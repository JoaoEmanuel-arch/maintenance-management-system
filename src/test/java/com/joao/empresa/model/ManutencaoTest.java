package com.joao.empresa.model;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.builders.TecnicoBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ManutencaoTest {

    // verificar se na hora que criar um nova manutenção inicia com os valores corretos
    @Test
    void criarManutencao_quandoDadosForemValidos_deveIniciarEmAndamento() {

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .comId(1)
                        .build();

        Tecnico tecnico =
                TecnicoBuilder.builder()
                        .comId(1)
                        .build();

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnico
                );

        assertAll(
                () -> assertNull(manutencao.getId()), // não chamou DAO ainda

                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        BigDecimal.ZERO,
                        manutencao.getCusto()
                ),

                () -> assertNull(
                        manutencao.getDataFim() // não pode possui data final ainda
                )
        );
    }



}
