package com.joao.empresa.exceptions;

// Alguma operação da camada de persistência falhou
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message) {
        super(message);
    }
}
