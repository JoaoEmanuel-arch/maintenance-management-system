package com.joao.empresa.model;

import com.joao.empresa.builders.EquipamentoBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmpresaTest {

    @Test
    void criarEmpresaNova_quandoDadosForemInformados_deveNascerSemId() {

        Empresa empresa = new Empresa(
                "Gerdau Açominas",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertAll(
                () -> assertNull(empresa.getId()),

                () -> assertEquals(
                        "Gerdau Açominas",
                        empresa.getNome()
                ),

                () -> assertEquals(
                        "123456789",
                        empresa.getCnpj()
                ),

                () -> assertEquals(
                        "Ouro Branco",
                        empresa.getEndereco()
                ),

                () -> assertEquals(
                        "Siderurgia",
                        empresa.getSegmento()
                ),

                () -> assertEquals(
                        Empresa.Status.ATIVADA,
                        empresa.getStatus()
                )
        );
    }

    @Test
    void reconstruirEmpresa_quandoVierDoBanco_deveManterId() {

        Empresa empresa = new Empresa(
                10,
                "Gerdau Açominas",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertEquals(10, empresa.getId());
    }

    @Test
    void adicionarEquipamento_deveAdicionarEquipamentoNaEmpresa() {

        Empresa empresa = new Empresa(
                1,
                "Gerdau Açominas",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .comId(1)
                        .build();

        empresa.adicionarEquipamento(equipamento);

        assertAll(
                () -> assertEquals(
                        1,
                        empresa.getEquipamentos().size()
                ),

                () -> assertTrue(
                        empresa.getEquipamentos()
                                .contains(equipamento)
                )
        );
    }

    @Test
    void adicionarEquipamento_quandoEquipamentoJaEstiverAdicionado_naoDeveDuplicar() {

        Empresa empresa = new Empresa(
                1,
                "Gerdau Açominas",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        Equipamento equipamento =
                EquipamentoBuilder.builder()
                        .comId(1)
                        .build();

        empresa.adicionarEquipamento(equipamento);
        empresa.adicionarEquipamento(equipamento);

        assertEquals(
                1,
                empresa.getEquipamentos().size()
        );
    }


}
