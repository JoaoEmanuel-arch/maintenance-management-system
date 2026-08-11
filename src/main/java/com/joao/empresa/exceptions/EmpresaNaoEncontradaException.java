package com.joao.empresa.exceptions;

public class EmpresaNaoEncontradaException extends RuntimeException{

    public EmpresaNaoEncontradaException(String mensagem){
        super(mensagem);
    }
}
