package com.joao.empresa.exceptions;

public class EquipamentoNaoEncontradoException extends RuntimeException{

    public EquipamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

}
