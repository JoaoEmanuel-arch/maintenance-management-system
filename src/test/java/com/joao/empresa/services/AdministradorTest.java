package com.joao.empresa.services;

import com.joao.empresa.builders.AdministradorBuilder;
import com.joao.empresa.model.Administrador;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class AdministradorTest {

    @Test
    public void quandoAtualizarUsuarioForChamadoDeveAtualizarOsDadosDoAdministradorCadastrado(){

        GestaoUsuario gestaoUsuario = new GestaoUsuario();
        Administrador adminNovo = AdministradorBuilder.builder().build();
        Administrador adminAlterado = AdministradorBuilder.builder().comNome("Laura Moraes").
                                      comDepartamento("Artes").build();
        gestaoUsuario.cadastrarUsuario(adminNovo);

        gestaoUsuario.atualizarUsuario(adminAlterado);

        assertAll(
                () -> assertEquals(adminAlterado.getNome(), adminNovo.getNome()),
                () -> assertEquals(adminAlterado.getEmail(), adminNovo.getEmail()),
                () -> assertEquals(adminAlterado.getSenha(), adminNovo.getSenha()),
                () -> assertEquals(adminAlterado.getDepartamento(), adminNovo.getDepartamento())
        );

    }

}
