package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GestorTest {

    @Test
    void criarGestor_devePossuirTipoGestor() {

        Gestor gestor =
                new Gestor(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Manutenção"
                );

        assertEquals(Usuario.TipoUsuario.GESTOR, gestor.getTipo());
    }

    @Test
    void atualizarDados_quandoDadosForemInformados_deveAtualizarNomeEEmail() {

        Gestor existente =
                new Gestor(
                        1,
                        "João",
                        "antigo@email.com",
                        "Manutenção"
                );

        Gestor alterado =
                new Gestor(
                        1,
                        "João Emanuel",
                        "novo@email.com",
                        "Operações"
                );

        existente.atualizarDados(alterado);

        assertAll(
                () -> assertEquals(
                        "João Emanuel",
                        existente.getNome()
                ),

                () -> assertEquals(
                        "novo@email.com",
                        existente.getEmail()
                )
        );
    }

    @Test
    void atualizarDados_quandoNovoNomeEEmailForemNulos_deveManterDadosAtuais() {

        Gestor existente =
                new Gestor(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Manutenção"
                );

        Gestor alterado =
                new Gestor(
                        1,
                        null,
                        null,
                        "Operações"
                );

        existente.atualizarDados(alterado);

        assertAll(
                () -> assertEquals(
                        "João Emanuel",
                        existente.getNome()
                ),

                () -> assertEquals(
                        "joao@email.com",
                        existente.getEmail()
                )
        );
    }

    @Test
    void atualizarEspecifico_quandoAreaResponsavelForInformada_deveAtualizarArea() {

        Gestor existente =
                new Gestor(
                        1,
                        "João",
                        "joao@email.com",
                        "Manutenção"
                );

        Gestor alterado =
                new Gestor(
                        1,
                        "João",
                        "joao@email.com",
                        "Operações"
                );

        existente.atualizarEspecifico(alterado);

        assertEquals("Operações", existente.getAreaResponsavel());
    }



}
