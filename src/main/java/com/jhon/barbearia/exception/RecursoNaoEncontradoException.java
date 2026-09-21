package com.jhon.barbearia.exception;

/**
 * Exceção para quando um recurso não é encontrado no banco de dados.
 * Exemplos: "Barbeiro não encontrado", "Agendamento não encontrado".
 */
public class RecursoNaoEncontradoException extends NegocioException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
