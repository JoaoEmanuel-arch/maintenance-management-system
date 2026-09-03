package com.joao.empresa.model;

import com.joao.empresa.builders.ManutencaoBuilder;
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

    @Test
    void reconstruirEquipamento_quandoVierDoBanco_deveManterId() {

        Equipamento equipamento =
                new Equipamento(
                        5,
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 5, 15)
                );

        assertEquals(5, equipamento.getId());
    }

    @Test
    void adicionarManutencao_deveAdicionarAoHistorico() {

        Equipamento equipamento =
                new Equipamento(
                        1,
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 5, 15)
                );

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        equipamento.adicionarManutencao(manutencao);

        assertAll(
                () -> assertEquals(
                        1,
                        equipamento
                                .getHistoricoManutencoes()
                                .size()
                ),

                () -> assertTrue(
                        equipamento
                                .getHistoricoManutencoes()
                                .contains(manutencao)
                )
        );
    }

    @Test
    void adicionarManutencao_quandoManutencaoJaEstiverNoHistorico_naoDeveDuplicar() {

        Equipamento equipamento =
                new Equipamento(
                        1,
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 5, 15)
                );

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        equipamento.adicionarManutencao(manutencao);

        equipamento.adicionarManutencao(manutencao);

        assertEquals(
                1,
                equipamento
                        .getHistoricoManutencoes()
                        .size() // o tamanho deve ser 1
        );
    }

}
