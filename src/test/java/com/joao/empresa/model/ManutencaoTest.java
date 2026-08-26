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

    @Test
    void cancelar_quandoDataCancelamentoForAnteriorADataInicio_deveLancarExcecaoSemAlterarEstado() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comDataInicio(
                                LocalDate.of(2026, 8, 20)
                        )
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> manutencao.cancelar(
                        LocalDate.of(2026, 8, 19)
                )
        );

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        manutencao.getStatus()
                ),

                () -> assertNull(
                        manutencao.getDataFim()
                )
        );
    }

    @Test
    void cancelar_quandoManutencaoJaEstiverFinalizada_deveLancarExcecao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .concluida()
                        .build();

        // dentro da própria entidade manutenção ele exige que está em andamento
        assertThrows(
                IllegalStateException.class,
                () -> manutencao.cancelar(
                        LocalDate.of(2026, 8, 23)
                )
        );
    }

    @Test
    void atualizarDados_quandoManutencaoEstiverEmAndamento_deveAtualizarCamposPermitidos() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .build();

        Tecnico novoTecnico =
                TecnicoBuilder.builder()
                        .comId(2)
                        .comEmail("novo@email.com")
                        .build();

        Equipamento novoEquipamento =
                EquipamentoBuilder.builder()
                        .comId(2)
                        .comCodigoPatrimonio("PAT-002")
                        .build();

        LocalDate novaDataInicio =
                LocalDate.of(2026, 8, 21);

        manutencao.atualizarDados(
                Manutencao.TipoManutencao.PREVENTIVA,
                "Revisão preventiva",
                novaDataInicio,
                novoEquipamento,
                novoTecnico
        );

        assertAll(
                () -> assertEquals(
                        Manutencao.TipoManutencao.PREVENTIVA,
                        manutencao.getTipoManutencao()
                ),

                () -> assertEquals(
                        "Revisão preventiva",
                        manutencao.getDescricao()
                ),

                () -> assertEquals(
                        novaDataInicio,
                        manutencao.getDataInicio()
                ),

                () -> assertSame(
                        novoEquipamento,
                        manutencao.getEquipamento()
                ),

                () -> assertSame(
                        novoTecnico,
                        manutencao.getTecnicoResponsavel()
                ),

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
    void atualizarDados_quandoDadosForemInvalidos_deveLancarExcecaoSemAlterarManutencao() {

        Manutencao manutencao =
                ManutencaoBuilder.builder()
                        .comId(1)
                        .comDescricao("Descrição original")
                        .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> manutencao.atualizarDados(
                        Manutencao.TipoManutencao.PREVENTIVA,
                        "   ",
                        LocalDate.of(2026, 8, 21),
                        EquipamentoBuilder.builder().build(),
                        TecnicoBuilder.builder().build()
                )
        );

        assertEquals("Descrição original", manutencao.getDescricao());

    }




}
