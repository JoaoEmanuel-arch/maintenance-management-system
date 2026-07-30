package com.joao.empresa.services;

import com.joao.empresa.builders.TecnicoBuilder;
import com.joao.empresa.model.Tecnico;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TecnicoTest {

    @Test
    public void quandoAtualizarUsuarioForChamadoDeveAtualizarOsDadosDoTecnicoCadastrado(){

        GestaoUsuario gestaoUsuario = new GestaoUsuario();
        Tecnico tecnicoNovo = TecnicoBuilder.builder().build();
        Tecnico tecnicoAlterado = TecnicoBuilder.builder().comNome("Laura Moraes").
                comEspecialidade("Artes Plásticas").build();
        gestaoUsuario.cadastrarUsuario(tecnicoNovo);

        gestaoUsuario.atualizarUsuario(tecnicoAlterado);

        assertAll(
                () -> assertEquals(tecnicoAlterado.getNome(), tecnicoNovo.getNome()),
                () -> assertEquals(tecnicoAlterado.getEmail(), tecnicoNovo.getEmail()),
                () -> assertEquals(tecnicoAlterado.getSenha(), tecnicoNovo.getSenha()),
                () -> assertEquals(tecnicoAlterado.getEspecialidade(), tecnicoNovo.getEspecialidade())
        );
    }

}
