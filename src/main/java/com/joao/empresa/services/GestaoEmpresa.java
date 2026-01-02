package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Empresa;
import main.java.com.joao.empresa.exceptions.EmpresaJaCadastradaException;
import main.java.com.joao.empresa.exceptions.EmpresaNaoEncontradaException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GestaoEmpresa {

    private Set<Empresa> empresas = new HashSet<>();

    public Empresa buscarPorId(int id) {
        return empresas.stream()
                .filter(emp -> emp.getId() == id) //filtra as empresas com o id
                .findFirst() // retorna o primeiro do filtro em optional
                .orElseThrow(() -> // retorna o valor do optional, caso contrário lança automaticamente uma exceção
                        new EmpresaNaoEncontradaException("Empresa com ID " + id + " não encontrada.")
                );
    }

    // Método auxiliar interno que busca sem lançar exceção -> pra quando a ausência é normal e não um erro
    private Empresa buscarPorIdSemExcecao(int id) {
        return empresas.stream()
                .filter(emp -> emp.getId() == id)
                .findFirst()
                .orElse(null);
    }

    //método interno
    private boolean existeCnpj(String cnpj){
        return empresas.stream().
                anyMatch(emp -> emp.getCnpj().equals(cnpj));
        // retorna true ou false se pelo menos um satisfaz a condição
    }

    public void cadastrarEmpresa(Empresa emp){
        // se o throw for executado, o método para imediatamente
        if(buscarPorIdSemExcecao(emp.getId()) != null){
            throw new EmpresaJaCadastradaException("Já existe uma empresa cadastrada com o ID: " + emp.getId());
        }
        if(existeCnpj(emp.getCnpj())){
            throw new EmpresaJaCadastradaException("CNPJ já cadastrado: " + emp.getCnpj());
        }
        empresas.add(emp);
    }

    public Set<Empresa> listarEmpresas(){
        return Collections.unmodifiableSet(empresas); // retorna uma cópia da lista e não deixa ninguém alterar
    }

    public void atualizarEmpresa(Empresa alterada){
        Empresa existente = buscarPorId(alterada.getId());

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

    public void excluirEmpresa(int id){
        Empresa emp = buscarPorId(id);
        empresas.remove(emp);
    }

}


