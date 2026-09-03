package com.joao.empresa.services;

import com.joao.empresa.dao.EquipamentoDAO;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.*;
import com.joao.empresa.model.Equipamento;
import java.util.List;

public class GestaoEquipamento {

    private final EquipamentoDAO equipamentoDAO;
    private final ManutencaoDAO manutencaoDAO;

    public GestaoEquipamento() {
        this(
                new EquipamentoDAO(),
                new ManutencaoDAO()
        );
    }

    public GestaoEquipamento(EquipamentoDAO equipamentoDAO, ManutencaoDAO manutencaoDAO) {
        this.equipamentoDAO = equipamentoDAO;
        this.manutencaoDAO = manutencaoDAO;
    }

    public Equipamento buscarPorId(int id){
        Equipamento equipamento = equipamentoDAO.buscarPorId(id);

        if(equipamento == null){
            throw new EquipamentoNaoEncontradoException("Equipamento com ID " + id + " não encontrado.");
        }

        return equipamento;
    }

    public void cadastrarEquipamento(Equipamento equipamento, int idEmpresaDona){

        if (equipamento == null) {
            throw new IllegalArgumentException("O equipamento a ser cadastrado não pode ser nulo.");
        }

        try {

            equipamentoDAO.salvar(equipamento, idEmpresaDona);

        } catch (RegistroDuplicadoException e) {

            throw new EquipamentoJaCadastradoException(
                    "Já existe um equipamento cadastrado "
                            + "com o código de patrimônio "
                            + equipamento.getCodigoPatrimonio()
                            + ".",
                    e
            );

        } catch (IntegridadeReferencialException e) {

                throw new EmpresaNaoEncontradaException(
                        "Empresa com ID "
                                + idEmpresaDona
                                + " não encontrada.",
                        e
                );
        }

    }

    public List<Equipamento> listarEquipamentos() {
        return equipamentoDAO.listar();
    }

    public void atualizarEquipamento(Equipamento alterado) {

        if (alterado == null) {
            throw new IllegalArgumentException(
                    "O equipamento a ser atualizado não pode ser nulo."
            );
        }

        if (alterado.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar um equipamento sem ID."
            );
        }

        buscarPorId(alterado.getId());

        try {

            equipamentoDAO.atualizar(alterado);

        } catch (RegistroDuplicadoException e) {

            throw new EquipamentoJaCadastradoException(
                    "Já existe outro equipamento cadastrado "
                            + "com o código de patrimônio "
                            + alterado.getCodigoPatrimonio()
                            + ".",
                    e
            );
        }

    }

    public void excluirEquipamento(int id) {

        buscarPorId(id);

        // Regra da aplicação, já trava aqui
        if (manutencaoDAO.existeManutencaoDoEquipamento(id)) {

            throw new EntidadeEmUsoException(
                    "Não é possível excluir o equipamento de ID "
                            + id
                            + " porque existem manutenções associadas a ele."
            );
        }

        // Proteção do banco, se passar ali por algum motivo
        try {

            equipamentoDAO.deletar(id);

        } catch (IntegridadeReferencialException e) {

            throw new EntidadeEmUsoException(
                    "Não é possível excluir o equipamento de ID "
                            + id
                            + " porque existem registros associados a ele.",
                    e
            );
        }
    }

}
