package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Barbeiro;
import java.util.List;
import java.util.Optional;

public interface BarbeiroService {
    List<Barbeiro> buscarTodos();

    Optional<Barbeiro> buscarPorId(Long id);

    Barbeiro salvar(Long id, String nome, String especialidade, String foto);

    void excluir(Long id);
}
