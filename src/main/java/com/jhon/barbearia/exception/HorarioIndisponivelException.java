package com.jhon.barbearia.exception;

/**
 * Exceção para quando o horário solicitado já está ocupado.
 */
public class HorarioIndisponivelException extends NegocioException {

    public HorarioIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
