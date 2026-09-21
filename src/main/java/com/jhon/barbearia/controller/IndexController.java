package com.jhon.barbearia.controller;

import com.jhon.barbearia.service.BarbeiroEscalaService;
import com.jhon.barbearia.service.BarbeiroService;
import com.jhon.barbearia.service.ServicoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ServicoService servicoService;
    private final BarbeiroService barbeiroService;
    private final BarbeiroEscalaService barbeiroEscalaService;

    public IndexController(ServicoService servicoService,
                           BarbeiroService barbeiroService,
                           BarbeiroEscalaService barbeiroEscalaService) {
        this.servicoService = servicoService;
        this.barbeiroService = barbeiroService;
        this.barbeiroEscalaService = barbeiroEscalaService;
    }

    @GetMapping("/")
    public String abrirPaginaInicial(Model model) {
        model.addAttribute("listaServicos", servicoService.buscarTodos());
        model.addAttribute("listaBarbeiros", barbeiroService.buscarTodos());
        model.addAttribute("horariosFuncionamento", barbeiroEscalaService.buscarHorariosFuncionamento());
        return "index";
    }

    @GetMapping("/termos")
    public String termosDeUso() {
        return "termos";
    }

    @GetMapping("/privacidade")
    public String politicaDePrivacidade() {
        return "privacidade";
    }
}
