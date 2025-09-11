package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Empresa;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class GestaoEmpresa {

    private List<Empresa> empresas = new ArrayList<>();

    public Empresa buscarPorId(int id){
        for(Empresa emp : empresas){
            if(emp.getId() == id){
                return emp;
            }
        }
        return null;
    }

    public boolean cadastrarEmpresa(Empresa emp){
        if(buscarPorId(emp.getId()) != null){
            return false;
        }
        return empresas.add(emp); //true
    }

    public List<Empresa> listarEmpresas(){
        return Collections.unmodifiableList(empresas); // retorna uma cópia da lista e não deixa ninguém alterar
    }

    public void atualizarEmpresa(Empresa alterada){
        Empresa existente = buscarPorId(alterada.getId());

        if (existente == null){
            return;
        }

        if(alterada.getNome() != null){
            existente.setNome(alterada.getNome());
        }

        if(alterada.getCnpj() != null){
            existente.setCnpj(alterada.getCnpj());
        }

        if(alterada.getEndereco() != null){
            existente.setEndereco(alterada.getEndereco());
        }

        if(alterada.getSegmento() != null){
            existente.setSegmento(alterada.getSegmento());
        }

    }

    public boolean excluirEmpresa(int id){
        return empresas.removeIf(emp -> emp.getId() == id);
    }

}

// COLOCAR OPTIONAL (quando eu aprender generics, depois de try catch)
