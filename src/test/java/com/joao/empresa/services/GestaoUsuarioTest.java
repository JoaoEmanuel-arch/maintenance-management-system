package com.joao.empresa.services;

import com.joao.empresa.builders.AdministradorBuilder;
import com.joao.empresa.exceptions.UsuarioJaCadastradoException;
import com.joao.empresa.exceptions.UsuarioNaoEncontradoException;
import com.joao.empresa.model.Usuario;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/* ---------------------------------------------------------------------------------------------------------------
* Para os métodos cadastrarUsuario, buscarPorId, listarUsuarios e removerUsuario vou testar com um subtipo qualquer
* de Usuario. Esses métodos são agnósticos de tipo, então para qualquer um que eu testar serve para todos. Porém,
* para o método atualizarUsuario eu vou precisar testar para cada subtipo, porque cada um tem regra própria,
* cada um altera campos diferentes e cada um reage diferente ao alterado.
------------------------------------------------------------------------------------------------------------------*/

public class GestaoUsuarioTest {

    // atributos disponíveis para todas as classes, se criasse no before each ia morrer ali mesmo
    private GestaoUsuario gestaoUsuario;
    private Usuario usuarioNovo;

    @BeforeEach
    public void antesDeCadaMetodoInstanciaOsObjetos() {
        gestaoUsuario = new GestaoUsuario();
        usuarioNovo = AdministradorBuilder.builder().comId(1).build(); // deixar explícito no teste que o id é 1
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoEExistirUsuarioComIdBuscadoDeveRetornarEsseUsuario(){
        gestaoUsuario.cadastrarUsuario(usuarioNovo);

        Usuario usuario = gestaoUsuario.buscarPorId(1);

        assertEquals(1, usuario.getId());
    }

    @Test
    public void quandoMetodoBuscarPorIdForChamadoENaoExistirUsuarioComOIdBuscadoDeveLancarExcecao(){

        assertThrows(UsuarioNaoEncontradoException.class, () ->{
            gestaoUsuario.buscarPorId(2);
        });
    }

    @Test
    public void quandoOMetodoCadastrarUsuarioForChamadoSeJaExistirUmUsuarioComOMesmoIdDeveLancarExcecao(){

        Usuario usuarioNovo2 = AdministradorBuilder.builder().build();
        gestaoUsuario.cadastrarUsuario(usuarioNovo);

        assertThrows(UsuarioJaCadastradoException.class, () -> {
            gestaoUsuario.cadastrarUsuario(usuarioNovo2);
        });
    }

    @Test
    public void quandoCadastrarUsuarioForChamadoSemConflitosDeIdDeveAdicionarUsuarioAoSistema(){

        gestaoUsuario.cadastrarUsuario(usuarioNovo);

        assertTrue(gestaoUsuario.listarUsuarios().contains(usuarioNovo));
    }

    @Test
    public void quandoOMetodoExcluirUsuarioForChamadoOUsuarioDeveSerExcluidoDoSistema(){

        gestaoUsuario.cadastrarUsuario(usuarioNovo);

        gestaoUsuario.removerUsuario(1);

        assertFalse(gestaoUsuario.listarUsuarios().contains(usuarioNovo));
    }

}
