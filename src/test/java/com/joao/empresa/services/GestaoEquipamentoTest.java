package com.joao.empresa.services;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.dao.EquipamentoDAO;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.*;
import com.joao.empresa.model.Equipamento;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// a explicação do basicão está tudo no GestaoEmpresaTest

public class GestaoEquipamentoTest {

    @Mock
    private EquipamentoDAO equipamentoDAO;

    @Mock
    private ManutencaoDAO manutencaoDAO;

    private GestaoEquipamento gestaoEquipamento;

    @BeforeEach
    void setUp() {
        gestaoEquipamento = // cria a gestão equipamento e injeta os daos nela
                new GestaoEquipamento(
                        equipamentoDAO,
                        manutencaoDAO
                );
    }

    @Test
    void buscarPorId_quandoEquipamentoExistir_deveRetornarEquipamento() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .comId(1)
                        .build();

        when(equipamentoDAO.buscarPorId(1))
                .thenReturn(equipamento);

        Equipamento resultado = gestaoEquipamento.buscarPorId(1);

        assertSame(equipamento, resultado);

        verify(equipamentoDAO).buscarPorId(1);
    }

    @Test
    void buscarPorId_quandoEquipamentoNaoExistir_deveLancarExcecao() {

        when(equipamentoDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> gestaoEquipamento.buscarPorId(1)
        );

        verify(equipamentoDAO).buscarPorId(1);
    }

    @Test
    void cadastrarEquipamento_quandoEquipamentoForNulo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEquipamento
                        .cadastrarEquipamento(null, 10)
        );

        verifyNoInteractions(equipamentoDAO, manutencaoDAO);
    }

    @Test
    void cadastrarEquipamento_quandoDadosForemValidos_deveSalvarEquipamento() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .semId()
                        .build();

        gestaoEquipamento.cadastrarEquipamento(equipamento, 10);

        verify(equipamentoDAO).salvar(equipamento, 10);

        verifyNoInteractions(manutencaoDAO);
    }

    @Test
    void cadastrarEquipamento_quandoCodigoPatrimonioForDuplicado_deveTraduzirExcecao() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .semId()
                        .build();

        RegistroDuplicadoException causa =
                new RegistroDuplicadoException(
                        "Registro duplicado.",
                        new RuntimeException()
                );

        doThrow(causa)
                .when(equipamentoDAO)
                .salvar(equipamento, 10);

        EquipamentoJaCadastradoException exception =
                assertThrows(
                        EquipamentoJaCadastradoException.class,
                        () -> gestaoEquipamento
                                .cadastrarEquipamento(equipamento, 10)
                );

        assertSame(causa, exception.getCause());

        verify(equipamentoDAO).salvar(equipamento, 10);
    }

    @Test
    void cadastrarEquipamento_quandoEmpresaNaoExistir_deveTraduzirExcecao() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .semId()
                        .build();

        IntegridadeReferencialException causa =
                new IntegridadeReferencialException(
                        "Empresa inexistente.",
                        new RuntimeException()
                );

        doThrow(causa)
                .when(equipamentoDAO)
                .salvar(equipamento, 99);

        EmpresaNaoEncontradaException exception =
                assertThrows(
                        EmpresaNaoEncontradaException.class,
                        () -> gestaoEquipamento
                                .cadastrarEquipamento(equipamento, 99)
                );

        assertSame(causa, exception.getCause());

        verify(equipamentoDAO).salvar(equipamento, 99);
    }

    @Test
    void listarEquipamentos_deveRetornarEquipamentosFornecidosPeloDao() {

        List<Equipamento> equipamentos =
                List.of(
                        EquipamentoBuilder.builder()
                                .comId(1)
                                .build(),

                        EquipamentoBuilder.builder()
                                .comId(2)
                                .comCodigoPatrimonio("PAT-002")
                                .build()
                );

        when(equipamentoDAO.listar())
                .thenReturn(equipamentos);

        List<Equipamento> resultado = gestaoEquipamento.listarEquipamentos();

        assertSame(equipamentos, resultado);

        verify(equipamentoDAO).listar();
    }

    @Test
    void atualizarEquipamento_quandoEquipamentoForNulo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEquipamento
                        .atualizarEquipamento(null)
        );

        verifyNoInteractions(equipamentoDAO, manutencaoDAO);
    }

    @Test
    void atualizarEquipamento_quandoEquipamentoNaoPossuirId_deveLancarExcecao() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .semId()
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoEquipamento
                        .atualizarEquipamento(equipamento)
        );

        verifyNoInteractions(equipamentoDAO, manutencaoDAO);
    }

    @Test
    void atualizarEquipamento_quandoEquipamentoNaoExistir_deveLancarExcecaoENaoAtualizar() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .comId(1)
                        .build();

        when(equipamentoDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                EquipamentoNaoEncontradoException.class,
                () -> gestaoEquipamento
                        .atualizarEquipamento(equipamento)
        );

        verify(equipamentoDAO).buscarPorId(1);

        verify(equipamentoDAO, never()).atualizar(any()); // não pode ter sido chamado com nenhum objeto
    }


}
