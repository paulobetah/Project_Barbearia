package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Barbeiro;
import com.jhon.barbearia.exception.NegocioException;
import com.jhon.barbearia.repository.BarbeiroRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BarbeiroServiceImpl implements BarbeiroService {

    private final BarbeiroRepository barbeiroRepository;

    public BarbeiroServiceImpl(BarbeiroRepository barbeiroRepository) {
        this.barbeiroRepository = barbeiroRepository;
    }

    @Override
    public List<Barbeiro> buscarTodos() {
        return barbeiroRepository.findAll();
    }

    @Override
    public Optional<Barbeiro> buscarPorId(Long id) {
        return barbeiroRepository.findById(id);
    }

    @Override
    @Transactional
    public Barbeiro salvar(Long id, String nome, String especialidade, String foto) {
        Barbeiro barbeiro = id == null ? new Barbeiro() : barbeiroRepository.findById(id).orElse(new Barbeiro());
        barbeiro.setNome(nome);
        barbeiro.setEspecialidade(especialidade);
        barbeiro.setFoto(foto);
        return barbeiroRepository.save(barbeiro);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        try {
            barbeiroRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new NegocioException("Nao foi possivel excluir barbeiro com agendamentos vinculados.");
        }
    }
}
