package com.joao.empresa.exceptions;

public class EmpresaNaoEncontradaException extends RuntimeException {

    public EmpresaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public EmpresaNaoEncontradaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
