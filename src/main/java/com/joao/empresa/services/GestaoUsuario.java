package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Usuario;

import java.util.*;

public class GestaoUsuario {

    private Set<Usuario> usuarios = new HashSet<>();

    public Usuario buscarPorId(int id){
        for(Usuario usr : usuarios){
            if(usr.getId() == id){
                return usr;
            }
        }
        return null;
    }

    public boolean cadastrarUsuario(Usuario usr){
        if(buscarPorId(usr.getId()) != null){ // se já existe, não deixa cadastrar
            return false;
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
