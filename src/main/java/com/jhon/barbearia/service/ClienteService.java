package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteService {
    void salvar(Cliente cliente);

    Cliente atualizarPerfil(Long clienteId, String nome, String email, String telefone, String novaSenha);

    void redefinirSenha(String email, String novaSenha);

    List<Cliente> buscarTodos();

    Optional<Cliente> buscarPorEmail(String email);

    Optional<Cliente> buscarPorId(Long id);

    boolean autenticar(String email, String senha);

    void atualizarPermissao(Long id, String papel, boolean ativo);
}
