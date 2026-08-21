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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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



}
