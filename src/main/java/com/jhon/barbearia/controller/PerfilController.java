package com.jhon.barbearia.controller;

import com.jhon.barbearia.config.UsuarioLogadoDTO;
import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.service.ClienteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final ClienteService clienteService;

    public PerfilController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // Spring Security já garante que /perfil/** requer autenticação.

    @GetMapping("/perfil")
    public String abrirPerfil(HttpSession session, Model model) {
        UsuarioLogadoDTO usuarioLogado = (UsuarioLogadoDTO) session.getAttribute("usuarioLogado");

        // Carrega os dados frescos do banco (incluindo telefone, que o DTO não possui)
        clienteService.buscarPorId(usuarioLogado.id()).ifPresent(cliente ->
                model.addAttribute("cliente", cliente));

        return "perfil";
    }

    @PostMapping("/perfil/atualizar")
    public String atualizarPerfil(@RequestParam String nome,
                                  @RequestParam String email,
                                  @RequestParam(required = false) String telefone,
                                  @RequestParam(required = false) String novaSenha,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        UsuarioLogadoDTO usuarioLogado = (UsuarioLogadoDTO) session.getAttribute("usuarioLogado");

        try {
            Cliente atualizado = clienteService.atualizarPerfil(usuarioLogado.id(), nome, email, telefone, novaSenha);
            // Atualiza o DTO na sessão com os dados recém-salvos
            session.setAttribute("usuarioLogado", UsuarioLogadoDTO.from(atualizado));
            redirectAttributes.addFlashAttribute("sucesso", "Perfil atualizado com sucesso.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/perfil";
    }
}
