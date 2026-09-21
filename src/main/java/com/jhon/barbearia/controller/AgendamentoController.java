package com.jhon.barbearia.controller;

import com.jhon.barbearia.config.UsuarioLogadoDTO;
import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.Barbeiro;
import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.service.AgendamentoService;
import com.jhon.barbearia.service.BarbeiroService;
import com.jhon.barbearia.service.ServicoService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AgendamentoController {

    private final BarbeiroService barbeiroService;
    private final ServicoService servicoService;
    private final AgendamentoService agendamentoService;

    public AgendamentoController(
            BarbeiroService barbeiroService,
            ServicoService servicoService,
            AgendamentoService agendamentoService
    ) {
        this.barbeiroService = barbeiroService;
        this.servicoService = servicoService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/agendamento")
    public String abrirTelaAgendamento(@RequestParam Long barbeiroId, Model model) {
        return barbeiroService.buscarPorId(barbeiroId)
                .map(barbeiro -> {
                    model.addAttribute("barbeiro", barbeiro);
                    model.addAttribute("listaServicos", servicoService.buscarTodos());
                    return "agendamento";
                })
                .orElse("redirect:/");
    }

    // Spring Security já garante que /agendamento/** requer autenticação.
    // O UsuarioSessaoFilter mantém o DTO na sessão sempre atualizado.

    @PostMapping("/agendamento/salvar")
    public String salvarAgendamento(
            @RequestParam Long barbeiroId,
            @RequestParam List<Long> servicosIds,
            @RequestParam String data,
            @RequestParam String hora,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        UsuarioLogadoDTO usuarioLogado = (UsuarioLogadoDTO) session.getAttribute("usuarioLogado");

        try {
            Agendamento novoAgendamento = new Agendamento();

            Cliente cliente = new Cliente();
            cliente.setId(usuarioLogado.id());
            novoAgendamento.setCliente(cliente);

            Barbeiro barbeiro = new Barbeiro();
            barbeiro.setId(barbeiroId);
            novoAgendamento.setBarbeiro(barbeiro);

            List<Servico> servicosEscolhidos = new ArrayList<>();
            for (Long id : servicosIds) {
                Servico servico = new Servico();
                servico.setId(id);
                servicosEscolhidos.add(servico);
            }
            novoAgendamento.setServicos(servicosEscolhidos);
            novoAgendamento.setDataHora(LocalDateTime.parse(data + "T" + hora));

            agendamentoService.realizarAgendamento(novoAgendamento);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Seu horario foi agendado com sucesso.");
            return "redirect:/";
        } catch (DateTimeParseException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", "Data ou horario invalido.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("mensagemErro", e.getMessage());
        }

        return "redirect:/agendamento?barbeiroId=" + barbeiroId;
    }

    @GetMapping("/meus-agendamentos")
    public String abrirMeusAgendamentos(HttpSession session, Model model) {
        UsuarioLogadoDTO usuarioLogado = (UsuarioLogadoDTO) session.getAttribute("usuarioLogado");
        model.addAttribute("meusAgendamentos", agendamentoService.buscarPorCliente(usuarioLogado.id()));
        return "meus-agendamentos";
    }

    @PostMapping("/meus-agendamentos/cancelar")
    public String cancelarAgendamento(@RequestParam Long agendamentoId,
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        UsuarioLogadoDTO usuarioLogado = (UsuarioLogadoDTO) session.getAttribute("usuarioLogado");

        try {
            agendamentoService.cancelar(agendamentoId, usuarioLogado.id());
            redirectAttributes.addFlashAttribute("sucesso", "Agendamento cancelado.");
        } catch (NegocioException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/meus-agendamentos";
    }
}
