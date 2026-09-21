package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;

/**
 * Serviço de envio de e-mails para notificações de agendamento.
 */
public interface EmailService {
    void enviarConfirmacao(Agendamento agendamento);

    void enviarCancelamento(Agendamento agendamento);
}
