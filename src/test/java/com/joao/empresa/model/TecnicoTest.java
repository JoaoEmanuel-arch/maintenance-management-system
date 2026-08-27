package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TecnicoTest {

    @Test
    void criarTecnico_devePossuirTipoTecnico() {

        Tecnico tecnico =
                new Tecnico(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Mecânica"
                );

        assertEquals(Usuario.TipoUsuario.TECNICO, tecnico.getTipo());
    }

    // ver depois que eu acho que vou tirar esses arrays das entidades, é só fazer um join no dao
    @Test
    void criarTecnico_deveIniciarSemManutencoesResponsaveis() {

        Tecnico tecnico =
                new Tecnico(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Mecânica"
                );

        assertTrue(tecnico.getManutencoesResponsaveis().isEmpty());
    }

    @Test
    void atualizarDados_quandoDadosForemInformados_deveAtualizarNomeEEmail() {

        Tecnico existente =
                new Tecnico(
                        1,
                        "João",
                        "antigo@email.com",
                        "Mecânica"
                );

        Tecnico alterado =
                new Tecnico(
                        1,
                        "João Emanuel",
                        "novo@email.com",
                        "Elétrica"
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

        Tecnico existente =
                new Tecnico(
                        1,
                        "João Emanuel",
                        "joao@email.com",
                        "Mecânica"
                );

        Tecnico alterado =
                new Tecnico(
                        1,
                        null,
                        null,
                        "Elétrica"
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
    void atualizarEspecifico_quandoEspecialidadeForInformada_deveAtualizarEspecialidade() {

        Tecnico existente =
                new Tecnico(
                        1,
                        "João",
                        "joao@email.com",
                        "Mecânica"
                );

        Tecnico alterado =
                new Tecnico(
                        1,
                        "João",
                        "joao@email.com",
                        "Elétrica"
                );

        existente.atualizarEspecifico(alterado);

        assertEquals("Elétrica", existente.getEspecialidade());
    }

}
