package com.jhon.barbearia.config;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Rate limiter em memória para proteger o login contra brute-force.
 * Bloqueia o email por {@value #MINUTOS_BLOQUEIO} minutos após {@value #MAX_TENTATIVAS} tentativas falhas.
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_TENTATIVAS = 5;
    private static final int MINUTOS_BLOQUEIO = 15;

    private final ConcurrentHashMap<String, TentativaLogin> tentativas = new ConcurrentHashMap<>();

    /**
     * Verifica se o email está bloqueado.
     */
    public boolean isBloqueado(String email) {
        TentativaLogin tentativa = tentativas.get(normalizar(email));
        if (tentativa == null) {
            return false;
        }
        if (tentativa.bloqueadoAte != null && tentativa.bloqueadoAte.isAfter(LocalDateTime.now())) {
            return true;
        }
        // Bloqueio expirou — limpa o registro
        if (tentativa.bloqueadoAte != null && !tentativa.bloqueadoAte.isAfter(LocalDateTime.now())) {
            tentativas.remove(normalizar(email));
            return false;
        }
        return false;
    }

    /**
     * Retorna quantos minutos restam de bloqueio, ou 0 se não estiver bloqueado.
     */
    public long minutosRestantes(String email) {
        TentativaLogin tentativa = tentativas.get(normalizar(email));
        if (tentativa == null || tentativa.bloqueadoAte == null) {
            return 0;
        }
        long minutos = java.time.Duration.between(LocalDateTime.now(), tentativa.bloqueadoAte).toMinutes();
        return Math.max(0, minutos + 1);
    }

    /**
     * Registra uma tentativa falha de login.
     */
    public void registrarFalha(String email) {
        tentativas.compute(normalizar(email), (key, tentativa) -> {
            if (tentativa == null) {
                tentativa = new TentativaLogin();
            }
            tentativa.contador++;
            tentativa.ultimaTentativa = LocalDateTime.now();

            if (tentativa.contador >= MAX_TENTATIVAS) {
                tentativa.bloqueadoAte = LocalDateTime.now().plusMinutes(MINUTOS_BLOQUEIO);
            }
            return tentativa;
        });
    }

    /**
     * Limpa as tentativas após login bem-sucedido.
     */
    public void resetar(String email) {
        tentativas.remove(normalizar(email));
    }

    private String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static class TentativaLogin {
        int contador;
        LocalDateTime ultimaTentativa;
        LocalDateTime bloqueadoAte;
    }
}
