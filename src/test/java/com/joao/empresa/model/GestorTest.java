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

}
