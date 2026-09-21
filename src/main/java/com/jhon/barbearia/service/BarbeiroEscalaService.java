package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.BarbeiroEscala;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

public interface BarbeiroEscalaService {

    List<BarbeiroEscala> buscarPorBarbeiro(Long barbeiroId);

    /**
     * Gera os horários disponíveis para agendamento com um barbeiro em determinada data.
     * Considera a escala do barbeiro e remove horários já ocupados.
     *
     * @return lista de horários no formato "HH:mm"
     */
    List<String> gerarHorariosDisponiveis(Long barbeiroId, LocalDate data);

    /**
     * Retorna os horários de funcionamento da barbearia para exibição na página inicial.
     * Agrega as escalas de todos os barbeiros para cada dia da semana.
     *
     * @return mapa ordenado: dia da semana → horário (ex: "Segunda-feira" → "09:00 - 21:00")
     */
    LinkedHashMap<String, String> buscarHorariosFuncionamento();
}
