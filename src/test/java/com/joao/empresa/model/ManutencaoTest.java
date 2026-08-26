package com.joao.empresa.model;

import com.joao.empresa.builders.EquipamentoBuilder;
import com.joao.empresa.builders.ManutencaoBuilder;
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

    @Test
    void reconstruirManutencao_quandoEstiverConcluidaSemDataFim_deveLancarExcecao() {

        Equipamento equipamento = EquipamentoBuilder.builder().build();

        Tecnico tecnico = TecnicoBuilder.builder().build();

        assertThrows(
                IllegalArgumentException.class,
                () -> new Manutencao(
                        1,
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        new BigDecimal("500.00"),
                        LocalDate.of(2026, 8, 20),
                        null,
                        Manutencao.Status.CONCLUIDA,
                        equipamento,
                        tecnico
                )
        );
    }

    @Test
    void finalizar_quandoDadosForemValidos_deveConcluirManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        BigDecimal custo = new BigDecimal("1500.00");

        LocalDate dataConclusao = LocalDate.of(2026, 8, 22);

        manutencao.finalizar(custo, dataConclusao);

        // verificar se os atributos foram atualizados corretamente
        assertAll(
                () -> assertEquals(
                        Manutencao.Status.CONCLUIDA,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        custo,
                        manutencao.getCusto()
                ),

                () -> assertEquals(
                        dataConclusao,
                        manutencao.getDataFim()
                )
        );
    }

    @Test
    void finalizar_quandoCustoForNegativo_deveLancarExcecaoSemAlterarEstado() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> manutencao.finalizar(
                        new BigDecimal("-1.00"),
                        LocalDate.of(2026, 8, 22)
                )
        );

        // ver se os estados não foram alterados
        assertAll(
                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        BigDecimal.ZERO,
                        manutencao.getCusto()
                ),

                () -> assertNull(
                        manutencao.getDataFim()
                )
        );
    }

    @Test
    void finalizar_quandoDataConclusaoForAnteriorADataInicio_deveLancarExcecaoSemAlterarEstado() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .comDataInicio(
                                LocalDate.of(2026, 8, 20)
                        )
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> manutencao.finalizar(
                        new BigDecimal("100.00"),
                        LocalDate.of(2026, 8, 19)
                )
        );

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        BigDecimal.ZERO,
                        manutencao.getCusto()
                ),

                () -> assertNull(
                        manutencao.getDataFim()
                )
        );
    }

    @Test
    void cancelar_quandoManutencaoEstiverEmAndamento_deveCancelar() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        LocalDate dataCancelamento = LocalDate.of(2026, 8, 22);

        manutencao.cancelar(dataCancelamento);

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.CANCELADA,
                        manutencao.getStatus()
                ),

                () -> assertEquals(
                        dataCancelamento,
                        manutencao.getDataFim()
                ),

                () -> assertEquals(
                        BigDecimal.ZERO,
                        manutencao.getCusto()
                )
        );
    }



}
