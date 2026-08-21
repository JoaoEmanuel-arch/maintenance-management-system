package com.joao.empresa.services;

import com.joao.empresa.dao.UsuarioDAO;
import com.joao.empresa.model.*;
import com.joao.empresa.exceptions.*;
import java.util.*;

/* Traduzir erros técnicos do DAO para erros que fazem sentido para o negócio


* MySQL 1062 -> SQLException -> TradutorSQLException -> RegistroDuplicadoException ->
GestaoUsuario -> UsuarioJaCadastradoException

* MySQL bloqueia FK -> IntegridadeReferencialException -> GestaoUsuario ->
EntidadeEmUsoException

 */

public class GestaoUsuario {

    private final UsuarioDAO usuarioDAO;

    public GestaoUsuario() {
        this(new UsuarioDAO());
    }

    public GestaoUsuario(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario buscarPorId(int id){
        Usuario usuario = usuarioDAO.buscarPorId(id);

        if(usuario == null){
            throw new UsuarioNaoEncontradoException("Usuario com ID " + id + " não encontrado.");
        }

        return usuario;
    }

    public void cadastrarUsuario(Usuario usuario) {

        // antes terminava em erro técnico, agora se o DAO lançar exceção eu trato aqui
        try {

            usuarioDAO.salvar(usuario);

        // vou especificando cada vez mais o erro, deixando de ser genérico
        } catch (RegistroDuplicadoException e) { // linguagem da persistência

            throw new UsuarioJaCadastradoException( // linguagem de domínio
                    "Já existe um usuário cadastrado com o e-mail "
                            + usuario.getEmail() + ".", e // preservo a exceção anterior como causa
            );
        }
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public void atualizarUsuario(Usuario alterado) {

        if (alterado == null) {
            throw new IllegalArgumentException(
                    "O usuário a ser atualizado não pode ser nulo."
            );
        }

        if (alterado.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar um usuário sem ID."
            );
        }

        Usuario existente = buscarPorId(alterado.getId());

        if (existente.getTipo() != alterado.getTipo()) {
            throw new IllegalArgumentException(
                    "Não é permitido alterar o tipo de um usuário existente."
            );
        }

        existente.atualizarDados(alterado);
        existente.atualizarEspecifico(alterado);

        try {

            usuarioDAO.atualizar(existente);

        } catch (RegistroDuplicadoException e) {

            // aqui é abstrato, dá sql exception só lá no dao, passa no tradutor, chega aqui a abstrai
            throw new UsuarioJaCadastradoException(
                    "Já existe outro usuário cadastrado com o e-mail "
                            + existente.getEmail() + ".", e
            );
        }

    }

    public void removerUsuario(int id) {

        buscarPorId(id);

        try {

            usuarioDAO.deletar(id);

        } catch (IntegridadeReferencialException e) {

            throw new EntidadeEmUsoException(
                    "Não é possível remover o usuário de ID "
                            + id
                            + " porque existem registros associados a ele.",
                    e
            );
        }
    }

}
