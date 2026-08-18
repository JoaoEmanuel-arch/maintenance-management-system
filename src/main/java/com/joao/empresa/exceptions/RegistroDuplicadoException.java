package com.joao.empresa.exceptions;

/* o erro vai ficando cada vez mais específico
  Todo RegistroDuplicadoException é um erro de persistência,
  mas nem todo erro de persistência é duplicidade */

public class RegistroDuplicadoException extends PersistenciaException {

  public RegistroDuplicadoException(String mensagem, Throwable causa) {
    super(mensagem, causa);
  }

}
