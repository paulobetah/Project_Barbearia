package com.jhon.barbearia.config;

import com.jhon.barbearia.domain.Cliente;
import java.io.Serializable;

/**
 * DTO imutável para armazenar na HttpSession.
 * Substitui o uso direto da entidade JPA {@link Cliente} na sessão,
 * evitando problemas de detached entity e dados desatualizados.
 */
public record UsuarioLogadoDTO(
        Long id,
        String nome,
        String email,
        String papel,
        boolean ativo,
        boolean admin,
        boolean equipe
) implements Serializable {

    /**
     * Cria um DTO a partir de uma entidade Cliente.
     */
    public static UsuarioLogadoDTO from(Cliente cliente) {
        boolean isAdmin = "ADMIN".equalsIgnoreCase(cliente.getPapel())
                || "PROPRIETARIO".equalsIgnoreCase(cliente.getPapel());
        boolean isEquipe = isAdmin || "BARBEIRO".equalsIgnoreCase(cliente.getPapel());

        return new UsuarioLogadoDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getPapel(),
                Boolean.TRUE.equals(cliente.getAtivo()),
                isAdmin,
                isEquipe
        );
    }
}
