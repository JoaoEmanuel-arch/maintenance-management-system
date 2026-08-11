package com.joao.empresa.exceptions;

// não deixa fazer operações se tiver chave pendurada na outra
public class IntegridadeReferencialException extends PersistenciaException {

    public IntegridadeReferencialException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
