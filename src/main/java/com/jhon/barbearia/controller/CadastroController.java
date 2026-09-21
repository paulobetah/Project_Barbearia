package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.service.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CadastroController {

    private final ClienteService clienteService;
    private final com.jhon.barbearia.config.LoginRateLimiter loginRateLimiter;

    public CadastroController(ClienteService clienteService, com.jhon.barbearia.config.LoginRateLimiter loginRateLimiter) {
        this.clienteService = clienteService;
        this.loginRateLimiter = loginRateLimiter;
    }

    @GetMapping("/cadastro")
    public String abrirTelaCadastro(Model model) {
        model.addAttribute("abrirAbaCadastro", true);
        return "login";
    }

    @PostMapping("/cadastro")
    public String salvarNovoCliente(Cliente novoCliente, Model model, jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (loginRateLimiter.isBloqueado("CADASTRO_" + ip)) {
            model.addAttribute("erro", "Muitas tentativas de cadastro. Tente novamente mais tarde.");
            model.addAttribute("abrirAbaCadastro", true);
            return "login";
        }
        
        try {
            clienteService.salvar(novoCliente);
            model.addAttribute("sucesso", "Cadastro realizado com sucesso. Faça seu login.");
            return "login";
        } catch (NegocioException e) {
            loginRateLimiter.registrarFalha("CADASTRO_" + ip);
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("abrirAbaCadastro", true);
            return "login";
        }
    }
}
