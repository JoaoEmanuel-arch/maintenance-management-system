package com.joao.empresa.exceptions;

// a regra do sistema não permite.
// Ex.: não posso excluir esse equipamento porque existem manutenções associadas
public class EntidadeEmUsoException extends RuntimeException {

  public EntidadeEmUsoException(String mensagem) {
    super(mensagem);
  }

  public EntidadeEmUsoException(
          String mensagem,
          Throwable causa
  ) {
    super(mensagem, causa);
  }
}
