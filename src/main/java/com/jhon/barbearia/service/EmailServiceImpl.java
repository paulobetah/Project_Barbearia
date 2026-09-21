package com.jhon.barbearia.service;

import com.jhon.barbearia.domain.Agendamento;
import com.jhon.barbearia.domain.Servico;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Envio de e-mails de confirmação e cancelamento de agendamentos.
 * Se o SMTP não estiver configurado, o erro é logado silenciosamente
 * sem impedir o fluxo do agendamento.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void enviarConfirmacao(Agendamento agendamento) {
        try {
            String servicos = agendamento.getServicos().stream()
                    .map(Servico::getNome)
                    .collect(Collectors.joining(", "));

            String corpo = "Ola, " + agendamento.getCliente().getNome() + "!\n\n"
                    + "Seu agendamento foi confirmado com sucesso!\n\n"
                    + "Detalhes:\n"
                    + "  Barbeiro: " + agendamento.getBarbeiro().getNome() + "\n"
                    + "  Data: " + agendamento.getDataHora().format(FORMATO_DATA) + "\n"
                    + "  Horario: " + agendamento.getDataHora().format(FORMATO_HORA) + "\n"
                    + "  Servicos: " + servicos + "\n"
                    + "  Valor total: R$ " + agendamento.getValorTotal() + "\n\n"
                    + "Nos vemos em breve!\n"
                    + "Jhon Barbearia";

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(agendamento.getCliente().getEmail());
            mensagem.setSubject("Jhon Barbearia - Agendamento Confirmado");
            mensagem.setText(corpo);

            mailSender.send(mensagem);
        } catch (Exception e) {
            // Log silencioso — não impedir o agendamento se o e-mail falhar
            System.err.println("Falha ao enviar e-mail de confirmacao: " + e.getMessage());
        }
    }

    @Override
    public void enviarCancelamento(Agendamento agendamento) {
        try {
            String corpo = "Ola, " + agendamento.getCliente().getNome() + "!\n\n"
                    + "Seu agendamento para o dia "
                    + agendamento.getDataHora().format(FORMATO_DATA)
                    + " as " + agendamento.getDataHora().format(FORMATO_HORA)
                    + " com o barbeiro " + agendamento.getBarbeiro().getNome()
                    + " foi cancelado.\n\n"
                    + "Para reagendar, acesse nosso site.\n\n"
                    + "Jhon Barbearia";

            SimpleMailMessage mensagem = new SimpleMailMessage();
            mensagem.setTo(agendamento.getCliente().getEmail());
            mensagem.setSubject("Jhon Barbearia - Agendamento Cancelado");
            mensagem.setText(corpo);

            mailSender.send(mensagem);
        } catch (Exception e) {
            System.err.println("Falha ao enviar e-mail de cancelamento: " + e.getMessage());
        }
    }
}
