package com.joao.empresa.services;

import com.joao.empresa.builders.EmpresaBuilder;
import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.exceptions.EmpresaJaCadastradaException;
import com.joao.empresa.exceptions.EmpresaNaoEncontradaException;
import com.joao.empresa.exceptions.EntidadeEmUsoException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.model.Empresa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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

    @Test
    void buscarPorId_quandoEmpresaNaoExistir_deveLancarExcecao() {

        when(empresaDAO.buscarPorId(1))
                .thenReturn(null); // se procurar empresa com id 1, finge que o banco não encontrou

        // espero que saia a exceção ao executar buscarPorId
        assertThrows(
                EmpresaNaoEncontradaException.class,
                () -> gestaoEmpresa.buscarPorId(1)
        );

        verify(empresaDAO).buscarPorId(1);
    }

    @Test
    void cadastrarEmpresa_quandoEmpresaForNula_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEmpresa.cadastrarEmpresa(null)
        );

        verifyNoInteractions(empresaDAO); // mockito garante que ninguém encostou no DAO
        // se a empresa já chegou null, não faz sentido chamar o salvar no banco.
        // A service tem que barrar antes -> o DAO não pode ser chamado.
    }

    @Test
    void cadastrarEmpresa_quandoDadosForemValidos_deveSalvarEmpresa() {

        Empresa empresa = EmpresaBuilder.builder()
                .semId()
                .build();

        gestaoEmpresa.cadastrarEmpresa(empresa);

        // verifica se depois de receber uma empresa válida a service mandou o DAO salvá-la
        verify(empresaDAO).salvar(empresa);
    }

    @Test
    void cadastrarEmpresa_quandoCnpjForDuplicado_deveTraduzirExcecao() {

        Empresa empresa = EmpresaBuilder.builder()
                .semId()
                .build();

        // criar exceção que está fingindo que veio da camada de persistência
        RegistroDuplicadoException causa =
                new RegistroDuplicadoException(
                        "Registro duplicado.",
                        new RuntimeException()
                );

        //Mockito, quando a service tentar empresaDAO.salvar(empresa), em vez de salvar,
        // lance RegistroDuplicadoException (causa)
        doThrow(causa)
                .when(empresaDAO)
                .salvar(empresa);

        /* MySQL: "Não posso inserir. UNIQUE CNPJ violado."
           DAO: "Transformei isso em RegistroDuplicadoException." */

        // esse teste pergunta se a service pegou uma exceção técnica de persistência e traduziu
        // para uma exceção que faz sentido para o negócio
        EmpresaJaCadastradaException exception =
                assertThrows(
                        EmpresaJaCadastradaException.class,
                        () -> gestaoEmpresa.cadastrarEmpresa(empresa)
                );

        // verifica se preservou a exceção original
        assertSame(causa, exception.getCause());

        verify(empresaDAO).salvar(empresa);
    }

    @Test
    void listarEmpresas_deveRetornarEmpresasFornecidasPeloDao() {

        List<Empresa> empresas = List.of(
                EmpresaBuilder.builder()
                        .comId(1)
                        .build(),

                EmpresaBuilder.builder()
                        .comId(2)
                        .comCnpj("123456789")
                        .build()
        );

        when(empresaDAO.listar())
                .thenReturn(empresas);

        List<Empresa> resultado =
                gestaoEmpresa.listarEmpresas();

        assertSame(empresas, resultado);

        verify(empresaDAO).listar(); // confirma se realmente usou o DAO
    }

    @Test
    void atualizarEmpresa_quandoEmpresaForNula_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEmpresa.atualizarEmpresa(null)
        );

        verifyNoInteractions(empresaDAO);
    }

    @Test
    void atualizarEmpresa_quandoEmpresaNaoPossuirId_deveLancarExcecao() {

        Empresa empresa = EmpresaBuilder.builder()
                .semId()
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEmpresa.atualizarEmpresa(empresa)
        );

        verifyNoInteractions(empresaDAO); // nem consulte o banco, pq a requisição já é inválida
    }

    // não existe -> para aqui X não chega em atualizar
    @Test
    void atualizarEmpresa_quandoEmpresaNaoExistir_deveLancarExcecaoENaoAtualizar() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1)
                .build();

        when(empresaDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                EmpresaNaoEncontradaException.class,
                () -> gestaoEmpresa.atualizarEmpresa(empresa)
        );

        verify(empresaDAO).buscarPorId(1);

        verify( // o mockito garante que o método atualizar() nunca foi chamado
                empresaDAO,
                never()
        ).atualizar(any()); // não importa qual empresa. não pode ter chamado atualizar com nenhuma
    }

    @Test
    void atualizarEmpresa_quandoEmpresaExistir_deveAtualizar() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1)
                .build();

        when(empresaDAO.buscarPorId(1))
                .thenReturn(empresa);

        gestaoEmpresa.atualizarEmpresa(empresa);

        // verifica se os dois métodos realmente foram chamados
        verify(empresaDAO).buscarPorId(1);
        verify(empresaDAO).atualizar(empresa);
    }

    @Test
    void atualizarEmpresa_quandoNovoCnpjPertencerAOutraEmpresa_deveTraduzirExcecao() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1)
                .build();

        when(empresaDAO.buscarPorId(1))
                .thenReturn(empresa);

        RegistroDuplicadoException causa =
                new RegistroDuplicadoException(
                        "Registro duplicado.",
                        new RuntimeException()
                );

        // quando tentar atualizar, simule que o banco encontrou duplicidade
        doThrow(causa)
                .when(empresaDAO)
                .atualizar(empresa);

        EmpresaJaCadastradaException exception =
                assertThrows(
                        EmpresaJaCadastradaException.class,
                        () -> gestaoEmpresa.atualizarEmpresa(empresa)
                );

        assertSame(causa, exception.getCause());

        verify(empresaDAO).buscarPorId(1);
        verify(empresaDAO).atualizar(empresa);
    }

    @Test
    void excluirEmpresa_quandoEmpresaNaoPossuirEquipamentos_deveExcluir() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1)
                .build();

        when(empresaDAO.buscarPorId(1))
                .thenReturn(empresa);

        gestaoEmpresa.excluirEmpresa(1);

        verify(empresaDAO).buscarPorId(1);
        verify(empresaDAO).deletar(1);
    }

    @Test
    void excluirEmpresa_quandoEmpresaPossuirEquipamentos_deveLancarExcecaoENaoExcluir() {

        Empresa empresa = EmpresaBuilder.builder()
                .comId(1)
                .build();

        empresa.adicionarEquipamento(EquipamentoBuilder.builder().build());

        when(empresaDAO.buscarPorId(1))
                .thenReturn(empresa);

        assertThrows(
                EntidadeEmUsoException.class,
                () -> gestaoEmpresa.excluirEmpresa(1)
        );

        verify(empresaDAO).buscarPorId(1);

        verify( // depois da exceção, ele realmente parou e não tentou apagar?
                empresaDAO,
                never()
        ).deletar(anyInt()); // não quero que deletar() tenha sido chamado com nenhum inteiro
    }



}