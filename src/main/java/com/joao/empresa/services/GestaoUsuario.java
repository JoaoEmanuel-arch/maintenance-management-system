package com.joao.empresa.services;

import com.joao.empresa.dao.UsuarioDAO;
import com.joao.empresa.model.*;
import com.joao.empresa.exceptions.*;
import java.util.*;

public class GestaoUsuario {

    private UsuarioDAO usuarioDAO = new UsuarioDAO(); // poder mexer no banco daqui mesmo

    public Usuario buscarPorId(int id){
        Usuario usuario = usuarioDAO.buscarPorId(id);

        if(usuario == null){
            throw new UsuarioNaoEncontradoException("Usuario com ID " + id + " não encontrado.");
        }

        return usuario;
    }

    public void cadastrarUsuario(Usuario usuario){
        usuarioDAO.salvar(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioDAO.listar();
    }

    public void atualizarUsuario(Usuario alterado) {

        if (alterado.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar um usuário sem ID."
            );
        }

        Usuario existente = buscarPorId(alterado.getId());

        existente.atualizarDados(alterado);
        existente.atualizarEspecifico(alterado);

        usuarioDAO.atualizar(existente);
    }

    public void removerUsuario(int id) {

        buscarPorId(id); // garante que existe, se n existir já lança a exceção

        usuarioDAO.deletar(id);
    }

}
