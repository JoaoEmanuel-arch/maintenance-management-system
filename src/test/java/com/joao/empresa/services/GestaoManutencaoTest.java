package com.joao.empresa.services;

import com.joao.empresa.builders.ManutencaoBuilder;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import com.joao.empresa.model.Manutencao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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



}
