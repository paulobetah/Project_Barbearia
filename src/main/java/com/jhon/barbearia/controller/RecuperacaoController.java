package com.jhon.barbearia.controller;

import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.exception.RecursoNaoEncontradoException;
import com.jhon.barbearia.service.ClienteService;
import java.security.SecureRandom;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RecuperacaoController {

    private final JavaMailSender mailSender;
    private final ClienteService clienteService;
    private final com.jhon.barbearia.config.LoginRateLimiter loginRateLimiter;
    private final SecureRandom secureRandom = new SecureRandom();

    public RecuperacaoController(JavaMailSender mailSender, ClienteService clienteService, com.jhon.barbearia.config.LoginRateLimiter loginRateLimiter) {
        this.mailSender = mailSender;
        this.clienteService = clienteService;
        this.loginRateLimiter = loginRateLimiter;
    }

    @PostMapping("/recuperar-senha")
    public String recuperarSenha(@RequestParam String email, RedirectAttributes redirectAttributes, jakarta.servlet.http.HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        if (loginRateLimiter.isBloqueado("RECUPERAR_" + ip)) {
            redirectAttributes.addFlashAttribute("erro", "Muitas requisições. Tente novamente mais tarde.");
            return "redirect:/login";
        }
        
        try {
            Cliente cliente = clienteService.buscarPorEmail(email)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("E-mail não encontrado em nosso sistema."));
            String senhaTemporaria = gerarSenhaTemporaria();
            clienteService.redefinirSenha(email, senhaTemporaria);

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(email);
            mensagem.setSubject("Jhon Barbearia - Recuperacao de Senha");
            mensagem.setText("Ola, " + cliente.getNome() + "!\n\n"
                    + "Sua nova senha temporaria de acesso e: " + senhaTemporaria
                    + "\n\nFaca login com esta senha e altere-a em Meu Perfil.");

            mailSender.send(mensagem);
            redirectAttributes.addFlashAttribute("sucesso", "Um e-mail com sua nova senha foi enviado.");
        } catch (NegocioException e) {
            loginRateLimiter.registrarFalha("RECUPERAR_" + ip);
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/login";
    }

    private String gerarSenhaTemporaria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder senha = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            senha.append(caracteres.charAt(secureRandom.nextInt(caracteres.length())));
        }
        return senha.toString();
    }
}
