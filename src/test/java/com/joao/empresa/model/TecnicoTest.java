package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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



}
