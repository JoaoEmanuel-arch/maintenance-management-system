package com.joao.empresa.services;

import com.joao.empresa.builders.EmpresaBuilder;
import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.model.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Teste unitário do Service não deve precisar de banco de dados. O DAO é mockado.

//avisa para preparar o ambiente que nessa classe de testes eu vou utilizar o mockito
@ExtendWith(MockitoExtension.class) //
public class GestaoEmpresaTest {

    @Mock
    private EmpresaDAO empresaDAO; // DAO falso, não abre conexão, não executa SQL e não acessa MySql
    // "Mockito, quando alguém chamar buscarPorId(1), finja que encontrou essa empresa"

    private GestaoEmpresa gestaoEmpresa;

    @BeforeEach // execute isso antes de cada teste
    void setUp() { // antes de cada teste cria uma nova gestãoEmpresa(pra pegar os métodos)
        gestaoEmpresa = new GestaoEmpresa(empresaDAO); // e injeta o DAO falso nela
    }

    @Test
    void buscarPorId_quandoEmpresaExistir_deveRetornarEmpresa() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1) // tem uma empresa no sistema com ID 1
                .build();

        when(empresaDAO.buscarPorId(1)) // programação do mock do banco de dados
                .thenReturn(empresa); // EmpresaDAO, se alguém perguntar pelo ID 1 responda com essa empresa

        Empresa resultado = gestaoEmpresa.buscarPorId(1);

        assertSame(empresa, resultado); // verifica se são os mesmos objetos na memória, não apenas o resultado

        // pergunta ao Mockito se a GestaoEmpresa realmente chamou empresaDAO.buscarPorId(1)
        verify(empresaDAO).buscarPorId(1);
    }

}