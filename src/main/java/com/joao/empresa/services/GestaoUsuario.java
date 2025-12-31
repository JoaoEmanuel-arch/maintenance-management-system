package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.exceptions.UsuarioJaCadastradoException;
import main.java.com.joao.empresa.exceptions.UsuarioNaoEncontradoException;
import main.java.com.joao.empresa.model.Empresa;
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

    public boolean cadastrarUsuario(Usuario usr){
        if(buscarPorIdSemExcecao(usr.getId()) != null){
            throw new UsuarioJaCadastradoException("Já existe um usuário cadastrado com o ID: " + usr.getId());
        }
        return usuarios.add(usr);
    }

    public Set<Usuario> listarUsuarios() {
        return Collections.unmodifiableSet(usuarios);
    }

    public boolean atualizarUsuario(Usuario alterado){ //aqui já recebe o objeto usuário específico que navega na superclasse
        Usuario existente = buscarPorId(alterado.getId());
        if(existente != null){
            existente.atualizarDados(alterado); //eu já altero o objeto e não a classe
            existente.atualizarEspecifico(alterado);
            return true;
        }
        return false;
    }

    public boolean removerUsuario(int id){
        Usuario usr = buscarPorId(id);
        if(usr != null){
            return usuarios.remove(usr);
        }
        return false;
    }

}
