package com.joao.empresa.exceptions;

public class EquipamentoJaCadastradoException extends RuntimeException {

    public EquipamentoJaCadastradoException(String mensagem) {
        super(mensagem);
    }

    public EquipamentoJaCadastradoException(
            String mensagem,
            Throwable causa
    ) {
        super(mensagem, causa);
    }
}
