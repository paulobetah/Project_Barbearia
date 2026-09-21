package com.jhon.barbearia.config;

import com.jhon.barbearia.repository.ClienteRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que recarrega os dados do usuário logado a partir do banco a cada requisição.
 * Garante que mudanças de papel, status ou exclusão sejam refletidas imediatamente,
 * sem esperar a sessão expirar.
 *
 * Se o usuário foi desativado ou removido do banco, a sessão é invalidada
 * e ele é redirecionado para o login.
 */
public class UsuarioSessaoFilter extends OncePerRequestFilter {

    private final ClienteRepository clienteRepository;

    public UsuarioSessaoFilter(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("usuarioLogado") != null) {
                String email = auth.getName();

                var clienteOpt = clienteRepository.findByEmailIgnoreCase(email);

                if (clienteOpt.isEmpty() || Boolean.FALSE.equals(clienteOpt.get().getAtivo())) {
                    // Usuário foi removido ou desativado → força logout
                    session.invalidate();
                    SecurityContextHolder.clearContext();
                    response.sendRedirect("/login?erro=true");
                    return;
                }

                // Atualiza o DTO na sessão com dados frescos do banco
                session.setAttribute("usuarioLogado", UsuarioLogadoDTO.from(clienteOpt.get()));
            }
        }

        filterChain.doFilter(request, response);
    }
}
