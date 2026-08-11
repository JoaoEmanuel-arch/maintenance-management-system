package com.joao.empresa.exceptions;

// erro de banco
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message,  Throwable causa) {
        super(message, causa);
    }
}
