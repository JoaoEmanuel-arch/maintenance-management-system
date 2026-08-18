package com.joao.empresa.exceptions;

public class ManutencaoNaoEncontradaException extends RuntimeException {

    public ManutencaoNaoEncontradaException(String mensagem){
        super(mensagem);
    }

}
