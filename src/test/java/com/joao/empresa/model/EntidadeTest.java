package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntidadeTest {

    @Test
    void criarEntidade_quandoIdForNulo_devePermitir() {

        Empresa empresa = new Empresa(
                "Empresa Teste",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertNull(empresa.getId());
    }

    @Test
    void criarEntidade_quandoIdForPositivo_deveManterId() {

        Empresa empresa = new Empresa(
                1,
                "Empresa Teste",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertEquals(1, empresa.getId());
    }

    @Test
    void criarEntidade_quandoIdForZero_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Empresa(
                        0,
                        "Empresa Teste",
                        "123456789",
                        "Ouro Branco",
                        "Siderurgia",
                        Empresa.Status.ATIVADA
                )
        );
    }

    @Test
    void criarEntidade_quandoIdForNegativo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Empresa(
                        -1,
                        "Empresa Teste",
                        "123456789",
                        "Ouro Branco",
                        "Siderurgia",
                        Empresa.Status.ATIVADA
                )
        );
    }

    @Test
    void definirId_quandoEntidadeAindaNaoPossuirId_deveDefinirId() {

        Empresa empresa = new Empresa(
                "Empresa Teste",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        empresa.definirId(10);

        assertEquals(10, empresa.getId());
    }

    @Test
    void definirId_quandoIdForInvalido_deveLancarExcecao() {

        Empresa empresa = new Empresa(
                "Empresa Teste",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> empresa.definirId(0)
        );

        assertNull(empresa.getId());
    }



}
