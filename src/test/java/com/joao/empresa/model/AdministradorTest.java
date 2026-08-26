package com.joao.empresa.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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



}
