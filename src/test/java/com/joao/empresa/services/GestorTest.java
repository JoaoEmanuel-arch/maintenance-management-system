package com.joao.empresa.services;

import com.joao.empresa.builders.GestorBuilder;
import com.joao.empresa.model.Gestor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GestorTest {

    @Test
    public void quandoAtualizarUsuarioForChamadoDeveAtualizarOsDadosDoGestorCadastrado(){

        GestaoUsuario gestaoUsuario = new GestaoUsuario();
        Gestor gestorNovo = GestorBuilder.builder().build();
        Gestor gestorAlterado = GestorBuilder.builder().comNome("Laura Moraes").
                comAreaResponsavel("Artes").build();
        gestaoUsuario.cadastrarUsuario(gestorNovo);

        gestaoUsuario.atualizarUsuario(gestorAlterado);

        assertAll(
                () -> assertEquals(gestorAlterado.getNome(), gestorNovo.getNome()),
                () -> assertEquals(gestorAlterado.getEmail(), gestorNovo.getEmail()),
                () -> assertEquals(gestorAlterado.getSenha(), gestorNovo.getSenha()),
                () -> assertEquals(gestorAlterado.getAreaResponsavel(), gestorNovo.getAreaResponsavel())
        );

    }

}
