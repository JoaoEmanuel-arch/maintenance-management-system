package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

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



}
