package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
    Usuario.atualizarDados()
            ↓
    nome/email

    Administrador.atualizarEspecifico()
            ↓
    departamento

    Sem GestaoUsuario. Sem DAO. Sem banco. Testando somente a entidade.
*/

public class AdministradorTest {

    @Test
    void criarAdministrador_devePossuirTipoAdministrador() {

        Administrador administrador =
                new Administrador(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Tecnologia"
                );

        assertEquals(Usuario.TipoUsuario.ADMINISTRADOR, administrador.getTipo());
    }

    @Test
    void atualizarDados_quandoDadosForemInformados_deveAtualizarNomeEEmail() {

        // isso é o método do usuário que ele herda
        Administrador existente =
                new Administrador(
                        1,
                        "João",
                        "antigo@email.com",
                        "Financeiro"
                );

        Administrador alterado =
                new Administrador(
                        1,
                        "João Emanuel",
                        "novo@email.com",
                        "Tecnologia"
                );

        existente.atualizarDados(alterado); // alterado é do usuario é so nome e-mail

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

        Administrador existente =
                new Administrador(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Tecnologia"
                );

        Administrador alterado =
                new Administrador(
                        1,
                        null,
                        null,
                        "Financeiro"
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
    void atualizarEspecifico_quandoDepartamentoForInformado_deveAtualizarDepartamento() {

        Administrador existente =
                new Administrador(
                        1,
                        "João",
                        "joao@email.com",
                        "Financeiro"
                );

        Administrador alterado =
                new Administrador(
                        1,
                        "João",
                        "joao@email.com",
                        "Tecnologia"
                );

        existente.atualizarEspecifico(alterado);

        assertEquals(
                "Tecnologia",
                existente.getDepartamento()
        );
    }

    @Test
    void atualizarEspecifico_quandoDepartamentoForNulo_deveManterDepartamentoAtual() {

        Administrador existente =
                new Administrador(
                        1,
                        "João",
                        "joao@email.com",
                        "Tecnologia"
                );

        Administrador alterado =
                new Administrador(
                        1,
                        "João",
                        "joao@email.com",
                        null
                );

        existente.atualizarEspecifico(alterado);

        assertEquals(
                "Tecnologia",
                existente.getDepartamento()
        );
    }

}

