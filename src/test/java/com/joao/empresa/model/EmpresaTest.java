package com.joao.empresa.model;

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



}
