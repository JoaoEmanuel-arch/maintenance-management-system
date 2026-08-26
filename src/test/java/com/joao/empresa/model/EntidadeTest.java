package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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


}
