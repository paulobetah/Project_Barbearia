package com.jhon.barbearia.exception;

/**
 * Exceção base para erros de regra de negócio.
 * Por ser {@link RuntimeException} (unchecked), os métodos
 * não precisam declarar {@code throws} na assinatura.
 */
public class NegocioException extends RuntimeException {

    public NegocioException(String mensagem) {
        super(mensagem);
    }

    public NegocioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
