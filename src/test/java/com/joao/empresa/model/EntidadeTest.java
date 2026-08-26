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

    @Test
    void definirId_quandoEntidadeJaPossuirId_deveLancarExcecaoSemAlterarId() {

        Empresa empresa = new Empresa(
                1,
                "Empresa Teste",
                "123456789",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertThrows(
                IllegalStateException.class,
                () -> empresa.definirId(2)
        );

        assertEquals(1, empresa.getId());
    }

    @Test
    void equals_quandoMesmaClasseEMesmoId_deveRetornarTrue() {

        // oq importa aqui é só a classe e o id pra dizer que são iguais
        // estou testando se os atributos não fazem diferença
        Empresa empresa1 = new Empresa(
                1,
                "Empresa A",
                "111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        Empresa empresa2 = new Empresa(
                1,
                "Empresa completamente diferente",
                "999",
                "São João del-Rei",
                "Tecnologia",
                Empresa.Status.DESATIVADA
        );

        assertEquals(empresa1, empresa2);

        assertEquals(empresa1.hashCode(), empresa2.hashCode());
    }

    @Test
    void equals_quandoIdsForemDiferentes_deveRetornarFalse() {

        // mesmo que os atributos sejam iguais, oq importa é a classe e o id
        Empresa empresa1 = new Empresa(
                1,
                "Empresa A",
                "111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        Empresa empresa2 = new Empresa(
                2,
                "Empresa A",
                "111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertNotEquals(empresa1, empresa2);
    }

    @Test
    void equals_quandoEntidadesNovasNaoPossuiremId_deveRetornarFalse() {

        Empresa empresa1 = new Empresa(
                "Empresa A",
                "111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        Empresa empresa2 = new Empresa(
                "Empresa A",
                "111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertNotEquals(empresa1, empresa2);
    }



}
