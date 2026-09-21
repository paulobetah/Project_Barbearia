package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AgendamentoService {
    void realizarAgendamento(Agendamento agendamento);

    void cancelar(Long agendamentoId, Long clienteId);

    void concluir(Long agendamentoId);

    List<Agendamento> buscarPorBarbeiro(Long barbeiroId);

    List<Agendamento> buscarPorCliente(Long clienteId);

    List<Agendamento> buscarTodosOrdenados();

    Page<Agendamento> buscarTodosPaginado(int pagina, int tamanhoPagina);

    List<Agendamento> buscarPorDia(LocalDate data);
}
