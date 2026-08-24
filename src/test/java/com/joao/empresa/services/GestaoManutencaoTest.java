package com.joao.empresa.services;

import com.joao.empresa.builders.ManutencaoBuilder;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import com.joao.empresa.model.Manutencao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GestaoManutencaoTest {

    @Mock
    private ManutencaoDAO manutencaoDAO;

    private GestaoManutencao gestaoManutencao;

    @BeforeEach
    void setUp() {
        gestaoManutencao = new GestaoManutencao(manutencaoDAO);
    }

    @Test
    void buscarPorId_quandoManutencaoExistir_deveRetornarManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        Manutencao resultado = gestaoManutencao.buscarPorId(1);

        assertSame(manutencao, resultado);

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void buscarPorId_quandoManutencaoNaoExistir_deveLancarExcecao() {

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                ManutencaoNaoEncontradaException.class,
                () -> gestaoManutencao.buscarPorId(1)
        );

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void buscarAtivasPorId_quandoManutencaoEstiverEmAndamento_deveRetornarManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        Manutencao resultado = gestaoManutencao.buscarAtivasPorId(1);

        assertSame(manutencao, resultado);

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void buscarAtivasPorId_quandoManutencaoNaoEstiverEmAndamento_deveLancarExcecao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .concluida()
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        assertThrows(
                ManutencaoNaoEncontradaException.class,
                () -> gestaoManutencao.buscarAtivasPorId(1)
        );

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void buscarFinalizadasPorId_quandoManutencaoEstiverConcluida_deveRetornarManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .concluida()
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        Manutencao resultado = gestaoManutencao.buscarFinalizadasPorId(1);

        assertSame(manutencao, resultado);

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void buscarFinalizadasPorId_quandoManutencaoEstiverEmAndamento_deveLancarExcecao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        assertThrows(
                ManutencaoNaoEncontradaException.class,
                () -> gestaoManutencao.buscarFinalizadasPorId(1)
        );

        verify(manutencaoDAO).buscarPorId(1);
    }

    @Test
    void cadastrarManutencao_quandoManutencaoForNula_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoManutencao.cadastrarManutencao(null)
        );

        verifyNoInteractions(manutencaoDAO);
    }

    @Test
    void cadastrarManutencao_quandoDadosForemValidos_deveSalvarManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .semId()
                        .build();

        gestaoManutencao.cadastrarManutencao(manutencao);

        verify(manutencaoDAO).salvar(manutencao);
    }

    @Test
    void cadastrarManutencao_quandoEquipamentoOuTecnicoNaoExistir_deveTraduzirExcecao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .semId()
                        .build();

        IntegridadeReferencialException causa =
                new IntegridadeReferencialException(
                        "Chave estrangeira inválida.",
                        new RuntimeException()
                );

        doThrow(causa)
                .when(manutencaoDAO)
                .salvar(manutencao);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> gestaoManutencao.cadastrarManutencao(manutencao)
                );

        assertSame(causa, exception.getCause());

        verify(manutencaoDAO).salvar(manutencao);
    }

    @Test
    void listarTodasManutencoes_deveRetornarResultadoDoDao() {

        List<Manutencao> manutencoes =
                List.of(
                        ManutencaoBuilder.builder()
                                .comId(1)
                                .build(),

                        ManutencaoBuilder.builder()
                                .comId(2)
                                .concluida()
                                .build()
                );

        when(manutencaoDAO.listar())
                .thenReturn(manutencoes);

        List<Manutencao> resultado = gestaoManutencao.listarTodasManutencoes();

        assertSame(manutencoes, resultado);

        verify(manutencaoDAO).listar();
    }

    @Test
    void listarManutencoesAtivas_deveConsultarStatusAndamento() {

        List<Manutencao> manutencoes = List.of(
                ManutencaoBuilder.builder()
                        .build()
        );

        when(manutencaoDAO.listarPorStatus(Manutencao.Status.ANDAMENTO))
                .thenReturn(manutencoes);

        List<Manutencao> resultado = gestaoManutencao.listarManutencoesAtivas();

        assertSame(manutencoes, resultado);

        verify(manutencaoDAO)
                .listarPorStatus(
                        Manutencao.Status.ANDAMENTO
                );
    }

    @Test
    void listarManutencoesConcluidas_deveConsultarStatusConcluida() {

        List<Manutencao> manutencoes = List.of(
                ManutencaoBuilder.builder()
                        .concluida()
                        .build()
        );

        when(manutencaoDAO.listarPorStatus(Manutencao.Status.CONCLUIDA)).
                thenReturn(manutencoes);

        List<Manutencao> resultado = gestaoManutencao.listarManutencoesConcluidas();

        assertSame(manutencoes, resultado);

        verify(manutencaoDAO)
                .listarPorStatus(
                        Manutencao.Status.CONCLUIDA
                );
    }

    // o segredo pra entender tudo certinho é pensar no caminho das camadas
    @Test
    void listarManutencoesCanceladas_deveConsultarStatusCancelada() {

        List<Manutencao> manutencoes = List.of(
                ManutencaoBuilder.builder()
                        .cancelada()
                        .build()
        );

        when(manutencaoDAO.listarPorStatus(Manutencao.Status.CANCELADA)).
                thenReturn(manutencoes);

        List<Manutencao> resultado = gestaoManutencao.listarManutencoesCanceladas();

        assertSame(manutencoes, resultado);

        verify(manutencaoDAO)
                .listarPorStatus(
                        Manutencao.Status.CANCELADA
                );
    }



}
