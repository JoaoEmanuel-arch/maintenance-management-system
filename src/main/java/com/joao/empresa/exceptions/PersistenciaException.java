package com.joao.empresa.exceptions;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message,  Throwable causa) {
        super(message, causa);
    }
}
