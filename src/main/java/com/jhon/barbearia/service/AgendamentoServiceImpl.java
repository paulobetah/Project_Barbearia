package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.BarbeiroEscala;
import com.jhon.barbearia.domain.Barbeiro;
import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.exception.HorarioIndisponivelException;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.exception.RecursoNaoEncontradoException;
import com.jhon.barbearia.repository.AgendamentoRepository;
import com.jhon.barbearia.repository.BarbeiroEscalaRepository;
import com.jhon.barbearia.repository.BarbeiroRepository;
import com.jhon.barbearia.repository.ServicoRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoServiceImpl implements AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final BarbeiroRepository barbeiroRepository;
    private final ServicoRepository servicoRepository;
    private final BarbeiroEscalaRepository barbeiroEscalaRepository;
    private final EmailService emailService;

    private static final Map<DayOfWeek, String> DIA_SEMANA_MAP = Map.of(
            DayOfWeek.MONDAY, "SEGUNDA",
            DayOfWeek.TUESDAY, "TERCA",
            DayOfWeek.WEDNESDAY, "QUARTA",
            DayOfWeek.THURSDAY, "QUINTA",
            DayOfWeek.FRIDAY, "SEXTA",
            DayOfWeek.SATURDAY, "SABADO",
            DayOfWeek.SUNDAY, "DOMINGO"
    );

    public AgendamentoServiceImpl(
            AgendamentoRepository agendamentoRepository,
            BarbeiroRepository barbeiroRepository,
            ServicoRepository servicoRepository,
            BarbeiroEscalaRepository barbeiroEscalaRepository,
            EmailService emailService
    ) {
        this.agendamentoRepository = agendamentoRepository;
        this.barbeiroRepository = barbeiroRepository;
        this.servicoRepository = servicoRepository;
        this.barbeiroEscalaRepository = barbeiroEscalaRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void realizarAgendamento(Agendamento novoAgendamento) {
        if (novoAgendamento.getCliente() == null || novoAgendamento.getCliente().getId() == null) {
            throw new NegocioException("Faca login antes de agendar.");
        }
        if (novoAgendamento.getDataHora() == null || !novoAgendamento.getDataHora().isAfter(LocalDateTime.now())) {
            throw new NegocioException("Escolha uma data e horario futuros.");
        }
        if (novoAgendamento.getServicos() == null || novoAgendamento.getServicos().isEmpty()) {
            throw new NegocioException("Selecione pelo menos um servico.");
        }

        Long barbeiroId = novoAgendamento.getBarbeiro().getId();
        Barbeiro barbeiro = barbeiroRepository.findById(barbeiroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Barbeiro nao encontrado."));

        List<Long> servicosIds = novoAgendamento.getServicos().stream()
                .map(Servico::getId)
                .toList();
        List<Servico> servicos = servicoRepository.findAllById(servicosIds);
        if (servicos.size() != servicosIds.size()) {
            throw new RecursoNaoEncontradoException("Um ou mais servicos selecionados nao existem.");
        }

        // Item 13: Validar escala do barbeiro
        validarEscalaDoBarbeiro(barbeiroId, novoAgendamento.getDataHora(), servicos);

        // Item 14: Verificar sobreposição considerando duração dos serviços
        validarSobreposicao(barbeiroId, novoAgendamento.getDataHora(), servicos);

        novoAgendamento.setBarbeiro(barbeiro);
        novoAgendamento.setServicos(servicos);
        novoAgendamento.setStatus("Agendado");
        agendamentoRepository.save(novoAgendamento);

        // Item 17: Enviar e-mail de confirmação
        emailService.enviarConfirmacao(novoAgendamento);
    }

    @Override
    @Transactional
    public void cancelar(Long agendamentoId, Long clienteId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado."));
        if (!agendamento.getCliente().getId().equals(clienteId)) {
            throw new NegocioException("Este agendamento nao pertence ao cliente logado.");
        }
        if (!agendamento.getDataHora().isAfter(LocalDateTime.now())) {
            throw new NegocioException("Nao e possivel cancelar um atendimento que ja passou.");
        }
        agendamento.setStatus("Cancelado");
        agendamentoRepository.save(agendamento);

        // Item 17: Enviar e-mail de cancelamento
        emailService.enviarCancelamento(agendamento);
    }

    @Override
    @Transactional
    public void concluir(Long agendamentoId) {
        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento nao encontrado."));
        if (!"Agendado".equals(agendamento.getStatus())) {
            throw new NegocioException("Apenas agendamentos com status 'Agendado' podem ser concluidos.");
        }
        agendamento.setStatus("Concluido");
        agendamentoRepository.save(agendamento);
    }

    @Override
    public List<Agendamento> buscarPorBarbeiro(Long barbeiroId) {
        return agendamentoRepository.findByBarbeiroId(barbeiroId);
    }

    @Override
    public List<Agendamento> buscarPorCliente(Long clienteId) {
        return agendamentoRepository.findByClienteIdOrderByDataHoraDesc(clienteId);
    }

    @Override
    public List<Agendamento> buscarTodosOrdenados() {
        return agendamentoRepository.findAllByOrderByDataHoraDesc();
    }

    @Override
    public Page<Agendamento> buscarTodosPaginado(int pagina, int tamanhoPagina) {
        return agendamentoRepository.findAll(
                PageRequest.of(pagina, tamanhoPagina, Sort.by(Sort.Direction.DESC, "dataHora"))
        );
    }

    @Override
    public List<Agendamento> buscarPorDia(LocalDate data) {
        return agendamentoRepository.findByDataHoraBetweenOrderByDataHoraAsc(
                data.atStartOfDay(),
                data.atTime(LocalTime.MAX)
        );
    }

    /**
     * Item 13: Valida se o barbeiro atende no dia/horário solicitado,
     * e se a duração total dos serviços cabe dentro da escala.
     */
    private void validarEscalaDoBarbeiro(Long barbeiroId, LocalDateTime dataHora, List<Servico> servicos) {
        String diaSemana = DIA_SEMANA_MAP.getOrDefault(dataHora.getDayOfWeek(), "");

        List<BarbeiroEscala> escalas = barbeiroEscalaRepository.findByBarbeiroId(barbeiroId).stream()
                .filter(e -> diaSemana.equalsIgnoreCase(e.getDiaSemana()))
                .toList();

        if (escalas.isEmpty()) {
            throw new HorarioIndisponivelException("O barbeiro nao atende neste dia.");
        }

        BarbeiroEscala escala = escalas.getFirst();
        LocalTime horario = dataHora.toLocalTime();

        if (horario.isBefore(escala.getHoraInicio()) || horario.isAfter(escala.getHoraFim().minusMinutes(1))) {
            throw new HorarioIndisponivelException("O barbeiro nao atende neste horario.");
        }

        // Verifica se a duração total cabe antes do fim do expediente
        int duracaoTotal = calcularDuracaoTotal(servicos);
        LocalTime fimAtendimento = horario.plusMinutes(duracaoTotal);
        if (fimAtendimento.isAfter(escala.getHoraFim())) {
            throw new HorarioIndisponivelException(
                    "Os servicos selecionados duram " + duracaoTotal + " minutos e nao cabem antes do fechamento.");
        }
    }

    /**
     * Item 14: Verifica se o novo agendamento sobrepõe outros existentes,
     * considerando a duração dos serviços.
     */
    private void validarSobreposicao(Long barbeiroId, LocalDateTime dataHora, List<Servico> servicos) {
        int duracaoNovo = calcularDuracaoTotal(servicos);
        LocalDateTime fimNovo = dataHora.plusMinutes(duracaoNovo);

        // Busca todos os agendamentos ativos do barbeiro no dia
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId,
                        dataHora.toLocalDate().atStartOfDay(),
                        dataHora.toLocalDate().atTime(LocalTime.MAX),
                        "Cancelado"
                );

        for (Agendamento existente : agendamentosDoDia) {
            int duracaoExistente = calcularDuracaoTotal(existente.getServicos());
            LocalDateTime inicioExistente = existente.getDataHora();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

            // Verifica sobreposição: novo começa antes do fim do existente E termina depois do início do existente
            if (dataHora.isBefore(fimExistente) && fimNovo.isAfter(inicioExistente)) {
                throw new HorarioIndisponivelException(
                        "Este horario conflita com outro agendamento existente. Por favor, escolha outro.");
            }
        }
    }

    private int calcularDuracaoTotal(List<Servico> servicos) {
        return servicos.stream()
                .mapToInt(s -> s.getDuracaoMinutos() != null ? s.getDuracaoMinutos() : 30)
                .sum();
    }
}
