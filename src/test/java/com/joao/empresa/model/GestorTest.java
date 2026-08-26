package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

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

}
