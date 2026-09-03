package com.joao.empresa.services;

import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.exceptions.*;
import com.joao.empresa.model.Empresa;
import java.util.List;

public class GestaoEmpresa {

    // o teste não conseguia falar: "GestaoEmpresa, finja que o DAO encontrou essa empresa"
    // porque a própria classe criava o DAO -> Injeção de dependência por construtor
    // Serve muito pra fazer um DAO falso nos testes (mockar)
    private final EmpresaDAO empresaDAO;

    public GestaoEmpresa() {
        this(new EmpresaDAO());
    }

    public GestaoEmpresa(EmpresaDAO empresaDAO) {
        this.empresaDAO = empresaDAO;
    }

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

    // o listar dá problema é só dentro do DAO mesmo, com as coisas do banco
    public List<Empresa> listarEmpresas(){
        return empresaDAO.listar();
    }

    public void atualizarEmpresa(Empresa alterada){

        if (alterada == null) {
            throw new IllegalArgumentException(
                    "A empresa a ser atualizada não pode ser nula."
            );
        }

        if (alterada.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar uma empresa sem ID."
            );
        }

        buscarPorId(alterada.getId());

        try {

            empresaDAO.atualizar(alterada);

        } catch (RegistroDuplicadoException e) { // cnpj está com unique
            // não pode atualizar uma empresa colocando nela um cnpj que já pertence a outra empresa.
            throw new EmpresaJaCadastradaException(
                    "Já existe outra empresa cadastrada com o CNPJ "
                            + alterada.getCnpj() + ".", e
            );
        }
    }

    public void excluirEmpresa(int id) {

        Empresa empresa = buscarPorId(id);

        if (!empresa.getEquipamentos().isEmpty()) {
            throw new EntidadeEmUsoException(
                    "Não é possível excluir a empresa de ID "
                            + id + " porque ela possui equipamentos cadastrados."
            );
        }

        try {

            empresaDAO.deletar(id);

        } catch (IntegridadeReferencialException e) {

            throw new EntidadeEmUsoException(
                    "Não é possível excluir a empresa de ID " + id +
                    " porque existem registros associados a ela.", e
            );
        }
    }

    /* O banco é a última linha de defesa. Mesmo se o service verificar antes,
    outro processo pode cadastrar e a FK bloquear, aí o service também trata
    problema de integridade vindo do banco.

    SERVICE
    verifica regra explicitamente (Segundo as regras da aplicação, não pode)


    BANCO
    FK garante a integridade de qualquer forma (Independentemente do que a aplicação fizer,
    eu não vou deixar meus dados ficarem inconsistentes)

     */
}


