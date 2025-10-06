package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestaoUsuario {

    private List<Usuario> usuarios = new ArrayList<>();

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

    public List<Usuario> listarUsuarios(){
        return Collections.unmodifiableList(usuarios);
    }

    public void atualizarUsuario(Usuario alterado){ //aqui já recebe o objeto usuário específico que navega na superclasse
        Usuario existente = buscarPorId(alterado.getId());

        if(existente != null){
            existente.atualizarDados(alterado); //eu já altero o objeto e não a classe
            existente.atualizarEspecifico(alterado);
        }
    }

}
