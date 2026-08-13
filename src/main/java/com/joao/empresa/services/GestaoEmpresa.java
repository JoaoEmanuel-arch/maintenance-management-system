package com.joao.empresa.services;

import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.exceptions.EmpresaJaCadastradaException;
import com.joao.empresa.exceptions.EmpresaNaoEncontradaException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.model.Empresa;
import java.util.List;

public class GestaoEmpresa {

    EmpresaDAO empresaDAO = new EmpresaDAO(); //faço isso aqui só pra usar os métodos dessa classe

    public Empresa buscarPorId(int id) {
        Empresa empresa = empresaDAO.buscarPorId(id);

        // esse caso de erro aqui é responsabilidade somente daqui de tratar
        if(empresa == null){
            throw new EmpresaNaoEncontradaException("Empresa com ID " + id + " não encontrada.");
        }

        return empresa;
    }

    public void cadastrarEmpresa(Empresa empresa) {

        if (empresa == null) {
            throw new IllegalArgumentException("A empresa a ser cadastrada não pode ser nula.");
        }

        try {

            empresaDAO.salvar(empresa);

        } catch (RegistroDuplicadoException e) {
            //recebo o erro que traduziu do banco, mas especifico ainda mais, mantendo a causa original
            throw new EmpresaJaCadastradaException(
                    "Já existe uma empresa cadastrada com o CNPJ "
                            + empresa.getCnpj()
                            + ".", e
            );
        }
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


