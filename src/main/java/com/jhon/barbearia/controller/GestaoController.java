package com.jhon.barbearia.controller;

import com.jhon.barbearia.service.AgendamentoService;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GestaoController {

    private final AgendamentoService agendamentoService;

    public GestaoController(AgendamentoService agendamentoService) {
        this.agendamentoService = agendamentoService;
    }

    @GetMapping("/gestao/agenda")
    public String agendaDoDia(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model
    ) {
        LocalDate dataConsulta = data == null ? LocalDate.now() : data;
        model.addAttribute("dataSelecionada", dataConsulta);
        model.addAttribute("agendamentos", agendamentoService.buscarPorDia(dataConsulta));
        return "gestao/agenda";
    }

    @PostMapping("/gestao/agenda/concluir")
    public String concluirAgendamento(@RequestParam Long agendamentoId,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                      RedirectAttributes redirectAttributes) {
        agendamentoService.concluir(agendamentoId);
        redirectAttributes.addFlashAttribute("sucesso", "Agendamento marcado como concluido.");
        return "redirect:/gestao/agenda?data=" + data;
    }
}
