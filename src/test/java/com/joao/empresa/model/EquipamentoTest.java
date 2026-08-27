package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EquipamentoTest {

    @Test
    void criarEquipamentoNovo_quandoDadosForemInformados_deveNascerSemId() {

        LocalDate dataAquisicao = LocalDate.of(2020, 5, 15);

        Equipamento equipamento =
                new Equipamento(
                        "Laminadora",
                        "PAT-001",
                        dataAquisicao
                );

        assertAll(
                () -> assertNull(
                        equipamento.getId()
                ),

                () -> assertEquals(
                        "Laminadora",
                        equipamento.getNome()
                ),

                () -> assertEquals(
                        "PAT-001",
                        equipamento.getCodigoPatrimonio()
                ),

                () -> assertEquals(
                        dataAquisicao,
                        equipamento.getDataAquisicao()
                ),

                () -> assertTrue(
                        equipamento
                                .getHistoricoManutencoes()
                                .isEmpty()
                )
        );
    }

}
