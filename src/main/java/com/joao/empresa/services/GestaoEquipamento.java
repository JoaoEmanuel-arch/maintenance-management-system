package com.joao.empresa.services;

import com.joao.empresa.dao.EquipamentoDAO;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.*;
import com.joao.empresa.model.Equipamento;
import java.util.List;

public class GestaoEquipamento {

    private EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
    private ManutencaoDAO manutencaoDAO = new ManutencaoDAO();
    private GestaoManutencao gestaoManutencao;

    public GestaoEquipamento() {
        this.gestaoManutencao = new GestaoManutencao();
    }

    // Injeção de dependência: construtor recebe a referência para eu acessar
    // os métodos da manutenção (aqui, acessar as listas de equipamento em manutenção).
    public GestaoEquipamento(GestaoManutencao gestaoManutencao) {
        this.gestaoManutencao = gestaoManutencao;
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
                                + " não encontrada."
                );
            }

        }
    }

    public List<Equipamento> listarEquipamentos() {
        return equipamentoDAO.listar();
    }

    public void atualizarEquipamento(Equipamento alterado) {

        if (alterado.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar um equipamento sem ID."
            );
        }

        buscarPorId(alterado.getId());
        equipamentoDAO.atualizar(alterado);
    }

    public void excluirEquipamento(int id) { //só exclui se não tiver manutenção aberta com ele

        buscarPorId(id); // vejo se existe, caso contrário já lança a exceção

        if (manutencaoDAO.existeManutencaoDoEquipamento(id)) {
            throw new EquipamentoNaManutencaoException(
                    "Não é possível excluir. Equipamento possui manutenção associada.");
        }

        equipamentoDAO.deletar(id);
    }

}
