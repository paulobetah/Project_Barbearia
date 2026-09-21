package com.jhon.barbearia.config;

import com.jhon.barbearia.repository.ClienteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final ClienteRepository clienteRepository;
    private final LoginRateLimiter loginRateLimiter;

    public SecurityConfig(ClienteRepository clienteRepository, LoginRateLimiter loginRateLimiter) {
        this.clienteRepository = clienteRepository;
        this.loginRateLimiter = loginRateLimiter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/cadastro", "/recuperar-senha", "/api/agendamentos/horarios-disponiveis",
                        "/css/**", "/js/**", "/image/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers("/gestao/**").hasAnyRole("ADMIN", "PROPRIETARIO", "BARBEIRO")
                .requestMatchers("/agendamento/**", "/meus-agendamentos/**", "/perfil/**").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .successHandler((request, response, authentication) -> {
                    String email = authentication.getName();

                    // Limpa o rate limiter após login bem-sucedido
                    loginRateLimiter.resetar(email);

                    // Armazena DTO na sessão (não a entidade JPA)
                    clienteRepository.findByEmailIgnoreCase(email).ifPresent(cliente ->
                            request.getSession().setAttribute("usuarioLogado", UsuarioLogadoDTO.from(cliente)));

                    boolean gestor = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_PROPRIETARIO"));
                    response.sendRedirect(gestor ? "/admin" : "/");
                })
                .failureHandler((request, response, exception) -> {
                    // Registra tentativa falha no rate limiter
                    String email = request.getParameter("email");
                    if (email != null && !email.isBlank()) {
                        loginRateLimiter.registrarFalha(email);
                    }

                    // Verifica se ficou bloqueado após esta tentativa
                    if (email != null && loginRateLimiter.isBloqueado(email)) {
                        long minutos = loginRateLimiter.minutosRestantes(email);
                        response.sendRedirect("/login?erro=true&bloqueado=" + minutos);
                    } else {
                        response.sendRedirect("/login?erro=true");
                    }
                })
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/sair")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        // Filtro que recarrega dados do usuário do banco a cada requisição
        http.addFilterAfter(new UsuarioSessaoFilter(clienteRepository), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
