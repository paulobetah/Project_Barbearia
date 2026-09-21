package com.jhon.barbearia.service;

import com.jhon.barbearia.config.LoginRateLimiter;
import com.jhon.barbearia.domain.Cliente;
import com.jhon.barbearia.repository.ClienteRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClienteUserDetailsService implements UserDetailsService {

    private final ClienteRepository clienteRepository;
    private final LoginRateLimiter loginRateLimiter;

    public ClienteUserDetailsService(ClienteRepository clienteRepository, LoginRateLimiter loginRateLimiter) {
        this.clienteRepository = clienteRepository;
        this.loginRateLimiter = loginRateLimiter;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Verifica se o email está bloqueado por excesso de tentativas
        if (loginRateLimiter.isBloqueado(email)) {
            long minutos = loginRateLimiter.minutosRestantes(email);
            throw new LockedException("Conta bloqueada por excesso de tentativas. Tente novamente em " + minutos + " minuto(s).");
        }

        Cliente cliente = clienteRepository.findByEmailIgnoreCase(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado."));
        String papel = cliente.getPapel() == null || cliente.getPapel().isBlank() ? "CLIENTE" : cliente.getPapel();

        // Spring Security usa ROLE_ por baixo dos panos quando chamamos roles().
        return User.withUsername(cliente.getEmail())
                .password(cliente.getSenha())
                .roles(papel)
                .disabled(Boolean.FALSE.equals(cliente.getAtivo()))
                .build();
    }
}
