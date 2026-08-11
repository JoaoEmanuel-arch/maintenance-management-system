package com.joao.empresa.exceptions;

public class UsuarioNaoEncontradoException extends RuntimeException {

    public UsuarioNaoEncontradoException(String mensagem){
        super(mensagem);
    }

}
