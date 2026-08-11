package com.joao.empresa.exceptions;

public class UsuarioJaCadastradoException extends RuntimeException {

    public UsuarioJaCadastradoException(String mensagem) {
        super(mensagem);
    }

    public UsuarioJaCadastradoException(
            String mensagem,
            Throwable causa
    ) {
        super(mensagem, causa);
    }
}
