package com.joao.empresa.model;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.builders.TecnicoBuilder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ManutencaoTest {

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

    @Test
    void criarManutencao_quandoDescricaoForVazia_deveLancarExcecao() {

        Equipamento equipamento = EquipamentoBuilder.builder().build();

        Tecnico tecnico = TecnicoBuilder.builder().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "   ",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnico
                )
        );
    }

    @Test
    void reconstruirManutencao_quandoCustoForNegativo_deveLancarExcecao() {

        Equipamento equipamento = EquipamentoBuilder.builder().build();

        Tecnico tecnico = TecnicoBuilder.builder().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Manutencao(
                        1,
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        new BigDecimal("-100.00"),
                        LocalDate.of(2026, 8, 20),
                        null,
                        Manutencao.Status.ANDAMENTO,
                        equipamento,
                        tecnico
                )
        );
    }

    @Test
    void reconstruirManutencao_quandoEstiverEmAndamentoComDataFim_deveLancarExcecao() {

        Equipamento equipamento =
                EquipamentoBuilder.builder().build();

        Tecnico tecnico =
                TecnicoBuilder.builder().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Manutencao(
                        1,
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        BigDecimal.ZERO,
                        LocalDate.of(2026, 8, 20),
                        LocalDate.of(2026, 8, 21),
                        Manutencao.Status.ANDAMENTO,
                        equipamento,
                        tecnico
                )
        );
    }




}
