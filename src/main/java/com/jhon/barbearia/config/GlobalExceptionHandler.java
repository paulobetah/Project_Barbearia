package com.jhon.barbearia.config;

import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.exception.RecursoNaoEncontradoException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Tratamento global de exceções para controllers MVC.
 * Captura exceções não tratadas individualmente pelos controllers
 * e redireciona com mensagens de erro amigáveis.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public String handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex, RedirectAttributes redirectAttributes) {
        log.warn("Recurso não encontrado: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(NegocioException.class)
    public String handleNegocio(NegocioException ex, RedirectAttributes redirectAttributes) {
        log.warn("Regra de negócio violada: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenerico(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("Erro inesperado capturado pelo GlobalExceptionHandler: ", ex);
        redirectAttributes.addFlashAttribute("erro", "Ocorreu um erro inesperado: " + ex.getMessage());
        return "redirect:/";
    }
}
