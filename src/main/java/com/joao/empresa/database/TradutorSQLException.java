package com.joao.empresa.database;

import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;

import java.sql.SQLException;

// Essa classe serve para traduzir em exceções personalizadas os erros disparados pelo banco de dados

// fica aqui dentro de database pq é aonde deve existir os detalhes específicos do banco de dados.
// final é pra bloquear a herança (não faz sentido alguém herdar isso aqui)
public final class TradutorSQLException {

  private static final int MYSQL_REGISTRO_DUPLICADO = 1062;
  private static final int MYSQL_REGISTRO_REFERENCIADO = 1451;
  private static final int MYSQL_REFERENCIA_INEXISTENTE = 1452;

  private TradutorSQLException() {// construtor private para não criar objetos disso aqui
 // pra que tem q ter isso aqui
  }

  // recebe a exceção original e o contexto (ex.: salvar usuário)
  public static PersistenciaException traduzir(SQLException excecao, String operacao) {

    return switch (excecao.getErrorCode()) { // pega o código do erro fornecido pelo banco
    // e cria a exceção e retorno a exceção personalizada criada

      case MYSQL_REGISTRO_DUPLICADO ->
              new RegistroDuplicadoException(
                      "Registro duplicado ao " + operacao + ".", excecao
              );

      case MYSQL_REGISTRO_REFERENCIADO,
           MYSQL_REFERENCIA_INEXISTENTE ->
              new IntegridadeReferencialException(
                      "Violação de integridade referencial ao " + operacao + ".", excecao
              );

      default ->
              new PersistenciaException(
                      "Erro de persistência ao " + operacao + ".", excecao
              );
    };
  }
}
