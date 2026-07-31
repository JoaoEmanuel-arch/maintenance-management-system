package com.joao.empresa.services;

import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.exceptions.EmpresaNaoEncontradaException;
import com.joao.empresa.model.Empresa;
import java.util.List;

public class GestaoEmpresa {

    EmpresaDAO empresaDAO = new EmpresaDAO();

    public Empresa buscarPorId(int id) {
        Empresa empresa = empresaDAO.buscarPorId(id);

        if(empresa == null){
            throw new EmpresaNaoEncontradaException("Empresa com ID " + id + " não encontrada.");
        }

        return empresa;
    }

    public void cadastrarEmpresa(Empresa empresa){
        empresaDAO.salvar(empresa);
    }

    public List<Empresa> listarEmpresas(){
        return empresaDAO.listar();
    }

    public void atualizarEmpresa(Empresa alterada){

        if (alterada.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar uma empresa sem ID."
            );
        }

        buscarPorId(alterada.getId());
        empresaDAO.atualizar(alterada);
    }

    public void excluirEmpresa(int id){
        buscarPorId(id);
        empresaDAO.deletar(id);
    }

}


