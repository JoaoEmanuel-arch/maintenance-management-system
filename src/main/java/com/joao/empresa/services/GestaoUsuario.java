package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.exceptions.UsuarioJaCadastradoException;
import main.java.com.joao.empresa.exceptions.UsuarioNaoEncontradoException;
import main.java.com.joao.empresa.model.Usuario;
import java.util.*;

public class GestaoUsuario {

    private Set<Usuario> usuarios = new HashSet<>();

    public Usuario buscarPorId(int id){
        return usuarios.stream()
                .filter(usr -> usr.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException("Usuario com ID " + id + " não encontrado.")
                );
    }

    private Usuario buscarPorIdSemExcecao(int id) {
        return usuarios.stream()
                .filter(usr -> usr.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void cadastrarUsuario(Usuario usr){
        if(buscarPorIdSemExcecao(usr.getId()) != null){
            throw new UsuarioJaCadastradoException("Já existe um usuário cadastrado com o ID: " + usr.getId());
        }
        usuarios.add(usr);
    }

    public Set<Usuario> listarUsuarios() {
        return Collections.unmodifiableSet(usuarios);
    }

    public void atualizarUsuario(Usuario alterado){
        Usuario existente = buscarPorId(alterado.getId()); // aqui já lança exceção

        existente.atualizarDados(alterado);
        existente.atualizarEspecifico(alterado);
    }

    public void removerUsuario(int id){
        Usuario usr = buscarPorId(id); // aqui lança exceção
        usuarios.remove(usr);
    }

}
