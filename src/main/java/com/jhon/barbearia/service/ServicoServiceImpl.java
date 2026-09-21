package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Servico;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.repository.ServicoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoServiceImpl(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Override
    public List<Servico> buscarTodos() {
        return servicoRepository.findAll();
    }

    @Override
    public Optional<Servico> buscarPorId(Long id) {
        return servicoRepository.findById(id);
    }

    @Override
    @Transactional
    public Servico salvar(Long id, String nome, BigDecimal preco, Integer duracaoMinutos) {
        Servico servico = id == null ? new Servico() : servicoRepository.findById(id).orElse(new Servico());
        servico.setNome(nome);
        servico.setPreco(preco);
        servico.setDuracaoMinutos(duracaoMinutos != null ? duracaoMinutos : 30);
        return servicoRepository.save(servico);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        try {
            servicoRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new NegocioException("Nao foi possivel excluir servico com agendamentos vinculados.");
        }
    }
}
