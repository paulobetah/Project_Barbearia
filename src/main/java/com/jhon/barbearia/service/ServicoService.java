package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Servico;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ServicoService {
    List<Servico> buscarTodos();

    Optional<Servico> buscarPorId(Long id);

    Servico salvar(Long id, String nome, BigDecimal preco, Integer duracaoMinutos);

    void excluir(Long id);
}
