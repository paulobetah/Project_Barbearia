package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.BarbeiroEscala;
import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.repository.AgendamentoRepository;
import com.jhon.barbearia.repository.BarbeiroEscalaRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BarbeiroEscalaServiceImpl implements BarbeiroEscalaService {

    private final BarbeiroEscalaRepository barbeiroEscalaRepository;
    private final AgendamentoRepository agendamentoRepository;

    /** Mapeamento de DayOfWeek para o valor armazenado no banco */
    private static final Map<DayOfWeek, String> DIA_SEMANA_MAP = Map.of(
            DayOfWeek.MONDAY, "SEGUNDA",
            DayOfWeek.TUESDAY, "TERCA",
            DayOfWeek.WEDNESDAY, "QUARTA",
            DayOfWeek.THURSDAY, "QUINTA",
            DayOfWeek.FRIDAY, "SEXTA",
            DayOfWeek.SATURDAY, "SABADO",
            DayOfWeek.SUNDAY, "DOMINGO"
    );

    /** Nomes em português para exibição */
    private static final LinkedHashMap<DayOfWeek, String> DIA_SEMANA_LABEL = new LinkedHashMap<>();
    static {
        DIA_SEMANA_LABEL.put(DayOfWeek.MONDAY, "Segunda-feira");
        DIA_SEMANA_LABEL.put(DayOfWeek.TUESDAY, "Terça-feira");
        DIA_SEMANA_LABEL.put(DayOfWeek.WEDNESDAY, "Quarta-feira");
        DIA_SEMANA_LABEL.put(DayOfWeek.THURSDAY, "Quinta-feira");
        DIA_SEMANA_LABEL.put(DayOfWeek.FRIDAY, "Sexta-feira");
        DIA_SEMANA_LABEL.put(DayOfWeek.SATURDAY, "Sábado");
        DIA_SEMANA_LABEL.put(DayOfWeek.SUNDAY, "Domingo");
    }

    public BarbeiroEscalaServiceImpl(BarbeiroEscalaRepository barbeiroEscalaRepository,
                                     AgendamentoRepository agendamentoRepository) {
        this.barbeiroEscalaRepository = barbeiroEscalaRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Override
    public List<BarbeiroEscala> buscarPorBarbeiro(Long barbeiroId) {
        return barbeiroEscalaRepository.findByBarbeiroId(barbeiroId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> gerarHorariosDisponiveis(Long barbeiroId, LocalDate data) {
        String diaSemana = DIA_SEMANA_MAP.getOrDefault(data.getDayOfWeek(), "");

        // Busca a escala do barbeiro para esse dia
        List<BarbeiroEscala> escalas = barbeiroEscalaRepository.findByBarbeiroId(barbeiroId).stream()
                .filter(e -> diaSemana.equalsIgnoreCase(e.getDiaSemana()))
                .toList();

        if (escalas.isEmpty()) {
            return List.of(); // Barbeiro não atende neste dia
        }

        BarbeiroEscala escala = escalas.getFirst();
        LocalTime inicio = escala.getHoraInicio();
        LocalTime fim = escala.getHoraFim();

        // Item 14: Busca agendamentos e bloqueia slots pela duração total dos serviços
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findByBarbeiroIdAndDataHoraBetweenAndStatusNot(
                        barbeiroId,
                        data.atStartOfDay(),
                        data.atTime(LocalTime.MAX),
                        "Cancelado"
                );

        Set<LocalTime> ocupados = new HashSet<>();
        for (Agendamento agendamento : agendamentosDoDia) {
            LocalTime inicioAgendamento = agendamento.getDataHora().toLocalTime();
            int duracaoTotal = calcularDuracaoTotal(agendamento.getServicos());

            // Bloqueia todos os slots cobertos pela duração
            LocalTime slot = inicioAgendamento;
            while (slot.isBefore(inicioAgendamento.plusMinutes(duracaoTotal)) && slot.isBefore(fim)) {
                ocupados.add(slot);
                slot = slot.plusMinutes(30);
            }
        }

        // Gera grade de 30 min, removendo ocupados e horários passados
        return Stream.iterate(inicio, horario -> horario.plusMinutes(30))
                .limit(40)
                .filter(horario -> horario.isBefore(fim))
                .filter(horario -> !ocupados.contains(horario))
                .filter(horario -> data.isAfter(LocalDate.now()) || horario.isAfter(LocalTime.now()))
                .map(horario -> horario.toString().substring(0, 5))
                .toList();
    }

    @Override
    public LinkedHashMap<String, String> buscarHorariosFuncionamento() {
        List<BarbeiroEscala> todasEscalas = barbeiroEscalaRepository.findAll();

        LinkedHashMap<String, String> horarios = new LinkedHashMap<>();

        for (var entry : DIA_SEMANA_LABEL.entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey();
            String label = entry.getValue();
            String diaBanco = DIA_SEMANA_MAP.get(dayOfWeek);

            List<BarbeiroEscala> escalaDoDia = todasEscalas.stream()
                    .filter(e -> diaBanco.equalsIgnoreCase(e.getDiaSemana()))
                    .toList();

            if (escalaDoDia.isEmpty()) {
                horarios.put(label, "Fechado");
            } else {
                LocalTime maisCedo = escalaDoDia.stream()
                        .map(BarbeiroEscala::getHoraInicio)
                        .min(LocalTime::compareTo)
                        .orElse(LocalTime.of(9, 0));
                LocalTime maisTarde = escalaDoDia.stream()
                        .map(BarbeiroEscala::getHoraFim)
                        .max(LocalTime::compareTo)
                        .orElse(LocalTime.of(18, 0));

                horarios.put(label, maisCedo.toString().substring(0, 5) + " - " + maisTarde.toString().substring(0, 5));
            }
        }

        return horarios;
    }

    private int calcularDuracaoTotal(List<Servico> servicos) {
        if (servicos == null || servicos.isEmpty()) {
            return 30; // fallback
        }
        return servicos.stream()
                .mapToInt(s -> s.getDuracaoMinutos() != null ? s.getDuracaoMinutos() : 30)
                .sum();
    }
}
