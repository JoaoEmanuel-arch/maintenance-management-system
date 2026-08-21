package com.joao.empresa.services;

import com.joao.empresa.builders.AdministradorBuilder;
import com.joao.empresa.builders.GestorBuilder;
import com.joao.empresa.builders.TecnicoBuilder;
import com.joao.empresa.dao.UsuarioDAO;
import com.joao.empresa.exceptions.EntidadeEmUsoException;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.exceptions.UsuarioJaCadastradoException;
import com.joao.empresa.exceptions.UsuarioNaoEncontradoException;
import com.joao.empresa.model.Administrador;
import com.joao.empresa.model.Gestor;
import com.joao.empresa.model.Tecnico;
import com.joao.empresa.model.Usuario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GestaoUsuarioTest {

    @Mock
    private UsuarioDAO usuarioDAO;

    private GestaoUsuario gestaoUsuario;

    @BeforeEach
    void setUp() {
        gestaoUsuario = new GestaoUsuario(usuarioDAO);
    }

    @Test
    void buscarPorId_quandoUsuarioExistir_deveRetornarUsuario() {

        Usuario usuario = AdministradorBuilder.builder()
                .comId(1)
                .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(usuario);

        Usuario resultado = gestaoUsuario.buscarPorId(1);

        assertSame(usuario, resultado);

        verify(usuarioDAO).buscarPorId(1);
    }

    @Test
    void buscarPorId_quandoUsuarioNaoExistir_deveLancarExcecao() {

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> gestaoUsuario.buscarPorId(1)
        );

        verify(usuarioDAO).buscarPorId(1);
    }

    @Test
    void cadastrarUsuario_quandoUsuarioForNulo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoUsuario.cadastrarUsuario(null)
        );

        verifyNoInteractions(usuarioDAO);
    }

    @Test
    void cadastrarUsuario_quandoEmailForDuplicado_deveTraduzirExcecao() {

        Usuario usuario = AdministradorBuilder.builder()
                .semId()
                .build();

        RegistroDuplicadoException causa =
                new RegistroDuplicadoException(
                        "Registro duplicado.",
                        new RuntimeException()
                );

        doThrow(causa)
                .when(usuarioDAO) // ele navega entre as tabelas filhas de usuario na herança
                .salvar(usuario);

        UsuarioJaCadastradoException exception =
                assertThrows(
                        UsuarioJaCadastradoException.class,
                        () -> gestaoUsuario.cadastrarUsuario(usuario)
                );

        assertSame(causa, exception.getCause());

        verify(usuarioDAO).salvar(usuario);
    }

    @Test
    void listarUsuarios_deveRetornarUsuariosFornecidosPeloDao() {

        List<Usuario> usuarios = List.of(
                AdministradorBuilder.builder()
                        .comId(1)
                        .build(),

                GestorBuilder.builder()
                        .comId(2)
                        .comEmail("gestor@email.com")
                        .build(),

                TecnicoBuilder.builder()
                        .comId(3)
                        .comEmail("tecnico@email.com")
                        .build()
        );

        when(usuarioDAO.listar())
                .thenReturn(usuarios);

        List<Usuario> resultado = gestaoUsuario.listarUsuarios();

        assertSame(usuarios, resultado);

        verify(usuarioDAO).listar();
    }

    @Test
    void atualizarUsuario_quandoUsuarioForNulo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoUsuario.atualizarUsuario(null)
        );

        verifyNoInteractions(usuarioDAO);
    }

    @Test
    void atualizarUsuario_quandoUsuarioNaoPossuirId_deveLancarExcecao() {

        Usuario usuario = AdministradorBuilder.builder()
                .semId()
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoUsuario.atualizarUsuario(usuario)
        );

        verifyNoInteractions(usuarioDAO);
    }

    @Test
    void atualizarUsuario_quandoUsuarioNaoExistir_deveLancarExcecaoENaoAtualizar() {

        Usuario alterado = AdministradorBuilder.builder()
                .comId(1)
                .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(null);

        assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> gestaoUsuario.atualizarUsuario(alterado)
        );

        verify(usuarioDAO).buscarPorId(1);

        verify(usuarioDAO, never()).atualizar(any());
    }

    @Test
    void atualizarUsuario_quandoTipoForAlterado_deveLancarExcecaoENaoAtualizar() {

        Usuario existente = AdministradorBuilder.builder()
                .comId(1)
                .build();

        Usuario alterado = GestorBuilder.builder()
                .comId(1)
                .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(existente);

        assertThrows(
                IllegalArgumentException.class,
                () -> gestaoUsuario.atualizarUsuario(alterado)
        );

        verify(usuarioDAO).buscarPorId(1);

        verify(usuarioDAO, never()).atualizar(any());
    }

    @Test
    void atualizarUsuario_quandoForAdministrador_deveAtualizarDadosComunsEEspecificos() {

        Administrador existente =
                AdministradorBuilder.builder()
                        .comId(1)
                        .comNome("Nome antigo")
                        .comEmail("antigo@email.com")
                        .comDepartamento("Financeiro")
                        .build();

        Administrador alterado =
                AdministradorBuilder.builder()
                        .comId(1)
                        .comNome("Nome novo")
                        .comEmail("novo@email.com")
                        .comDepartamento("Tecnologia")
                        .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(existente);

        gestaoUsuario.atualizarUsuario(alterado);

        assertEquals("Nome novo", existente.getNome());

        assertEquals("novo@email.com", existente.getEmail());

        assertEquals("Tecnologia", existente.getDepartamento());

        verify(usuarioDAO).buscarPorId(1);
        verify(usuarioDAO).atualizar(existente);
    }

    @Test
    void atualizarUsuario_quandoForGestor_deveAtualizarDadosComunsEEspecificos() {

        Gestor existente =
                GestorBuilder.builder()
                        .comId(1)
                        .comNome("Nome antigo")
                        .comEmail("antigo@email.com")
                        .comAreaResponsavel("Financeiro")
                        .build();

        Gestor alterado =
                GestorBuilder.builder()
                        .comId(1)
                        .comNome("Nome novo")
                        .comEmail("novo@email.com")
                        .comAreaResponsavel("Operações")
                        .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(existente);

        gestaoUsuario.atualizarUsuario(alterado);

        assertEquals("Nome novo", existente.getNome());

        assertEquals("novo@email.com", existente.getEmail());

        assertEquals("Operações", existente.getAreaResponsavel());

        verify(usuarioDAO).buscarPorId(1);
        verify(usuarioDAO).atualizar(existente);
    }

    @Test
    void atualizarUsuario_quandoForTecnico_deveAtualizarDadosComunsEEspecificos() {

        Tecnico existente =
                TecnicoBuilder.builder()
                        .comId(1)
                        .comNome("Nome antigo")
                        .comEmail("antigo@email.com")
                        .comEspecialidade("Mecânica")
                        .build();

        Tecnico alterado =
                TecnicoBuilder.builder()
                        .comId(1)
                        .comNome("Nome novo")
                        .comEmail("novo@email.com")
                        .comEspecialidade("Elétrica")
                        .build();

        when(usuarioDAO.buscarPorId(1))
                .thenReturn(existente);

        gestaoUsuario.atualizarUsuario(alterado);

        assertEquals("Nome novo", existente.getNome());

        assertEquals("novo@email.com", existente.getEmail());

        assertEquals("Elétrica", existente.getEspecialidade());

        verify(usuarioDAO).buscarPorId(1);
        verify(usuarioDAO).atualizar(existente);
    }

}
