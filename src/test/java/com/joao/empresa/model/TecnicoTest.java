package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

}
