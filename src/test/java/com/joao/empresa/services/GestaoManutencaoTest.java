package com.joao.empresa.services;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.builders.ManutencaoBuilder;
import com.joao.empresa.builders.TecnicoBuilder;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Tecnico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void atualizarManutencao_quandoManutencaoForNula_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoManutencao.atualizarManutencao(null)
        );

        verifyNoInteractions(manutencaoDAO);
    }

    @Test
    void atualizarManutencao_quandoManutencaoNaoPossuirId_deveLancarExcecao() {

        Manutencao alterada =
                ManutencaoBuilder.builder()
                        .semId()
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoManutencao.atualizarManutencao(alterada)
        );

        verifyNoInteractions(manutencaoDAO);
    }

    @Test
    void atualizarManutencao_quandoManutencaoNaoExistir_deveLancarExcecaoENaoAtualizar() {

        Manutencao alterada =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                ManutencaoNaoEncontradaException.class,
                () -> gestaoManutencao.atualizarManutencao(alterada)
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO, never()).atualizar(any());
    }

    @Test
    void atualizarManutencao_quandoDadosForemValidos_deveAtualizarObjetoExistente() {

        Tecnico tecnicoNovo =
                TecnicoBuilder.builder()
                        .comId(2)
                        .comEmail("tecnico2@email.com")
                        .build();

        Equipamento equipamentoNovo =
                EquipamentoBuilder.builder()
                        .comId(2)
                        .comCodigoPatrimonio("PAT-002")
                        .build();

        Manutencao existente =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        Manutencao alterada =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .comTipoManutencao(
                                Manutencao.TipoManutencao.PREVENTIVA
                        )
                        .comDescricao("Manutenção preventiva geral")
                        .comDataInicio(
                                LocalDate.of(2026, 2, 10)
                        )
                        .comTecnicoResponsavel(tecnicoNovo)
                        .comEquipamento(equipamentoNovo)
                        .build();

        when(manutencaoDAO.buscarPorId(1)).thenReturn(existente);

        gestaoManutencao.atualizarManutencao(alterada);

        assertAll(
                () -> assertEquals(
                        Manutencao.TipoManutencao.PREVENTIVA,
                        existente.getTipoManutencao()
                ),

                () -> assertEquals(
                        "Manutenção preventiva geral",
                        existente.getDescricao()
                ),

                () -> assertEquals(
                        LocalDate.of(2026, 2, 10),
                        existente.getDataInicio()
                ),

                () -> assertSame(
                        tecnicoNovo,
                        existente.getTecnicoResponsavel()
                ),

                () -> assertSame(
                        equipamentoNovo,
                        existente.getEquipamento()
                ),

                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        existente.getStatus()
                )
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO).atualizar(existente);
    }

    @Test
    void atualizarManutencao_quandoManutencaoJaEstiverFinalizada_deveLancarExcecaoENaoAtualizar() {

        Manutencao existente =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .concluida()
                        .build();

        Manutencao alterada =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .comDescricao("Nova descrição")
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(existente);

        // isso é verificado lá na entidade -> só ir voltando em camadas pra ver
        assertThrows(
                IllegalStateException.class,
                () -> gestaoManutencao.atualizarManutencao(alterada)
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO, never()).atualizar(any());
    }

    @Test
    void atualizarManutencao_quandoEquipamentoOuTecnicoNaoExistir_deveTraduzirExcecao() {

        Manutencao existente =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        Manutencao alterada =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .comDescricao("Descrição alterada")
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(existente);

        IntegridadeReferencialException causa =
                new IntegridadeReferencialException(
                        "Relacionamento inexistente.",
                        new RuntimeException()
                );

        doThrow(causa)
                .when(manutencaoDAO)
                .atualizar(existente);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> gestaoManutencao
                                .atualizarManutencao(alterada)
                );

        assertSame(causa, exception.getCause());

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO).atualizar(existente);
    }

    @Test
    void cancelarManutencao_quandoEstiverEmAndamento_deveCancelarEAtualizarNoDao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        gestaoManutencao.cancelarManutencao(1);

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.CANCELADA, // tudo isso muda na entidade mesmo
                        manutencao.getStatus()
                ),

                () -> assertNotNull(
                        manutencao.getDataFim()
                )
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO).atualizar(manutencao);
    }

    @Test
    void cancelarManutencao_quandoJaEstiverFinalizada_deveLancarExcecaoENaoAtualizar() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .concluida()
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        // exige estar em andamento
        assertThrows(
                IllegalStateException.class,
                () -> gestaoManutencao
                        .cancelarManutencao(1)
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO, never()).atualizar(any());
    }

    @Test
    void finalizarManutencao_quandoDadosForemValidos_deveConcluirEAtualizarNoDao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        BigDecimal custo = new BigDecimal("1500.00");

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        gestaoManutencao.finalizarManutencao(1, custo);

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.CONCLUIDA,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        custo,
                        manutencao.getCusto()
                ),

                () -> assertNotNull(
                        manutencao.getDataFim()
                )
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO).atualizar(manutencao);
    }

    @Test
    void finalizarManutencao_quandoCustoForNegativo_deveLancarExcecaoENaoAtualizar() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        when(manutencaoDAO.buscarPorId(1))
                .thenReturn(manutencao);

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoManutencao.finalizarManutencao(1, new BigDecimal("-1.00"))
        );

        verify(manutencaoDAO).buscarPorId(1);

        verify(manutencaoDAO, never()).atualizar(any());
    }



}
