package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.exceptions.EquipamentoJaCadastradoException;
import main.java.com.joao.empresa.exceptions.EquipamentoNaManutencaoException;
import main.java.com.joao.empresa.exceptions.EquipamentoNaoEncontradoException;
import main.java.com.joao.empresa.model.Equipamento;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GestaoEquipamento {

    private GestaoManutencao gestaoManutencao;
    private Set<Equipamento> equipamentos = new HashSet<>();

    // Injeção de dependência: construtor recebe a referência para eu acessar
    // os métodos da manutenção (aqui, acessar as listas de equipamento em manutenção).
    public GestaoEquipamento(GestaoManutencao gestaoManutencao) {
        this.gestaoManutencao = gestaoManutencao;
    }

    public Equipamento buscarPorId(int id){
        return (Equipamento) equipamentos.stream().
                filter(eqp -> eqp.getId() == id).
                findFirst().
                orElseThrow(() ->
                        new EquipamentoNaoEncontradoException("Equipamento com o ID: " + id + "não encontrado."));
    }

    //método interno para usar sem ter que lançar exceção
    private Equipamento buscarPorIdSemExcecao(int id){
        return (Equipamento) equipamentos.stream().
                filter(eqp -> eqp.getId() == id).
                findFirst().
                orElse(null);
    }

    public void cadastrarEquipamento(Equipamento eqp){
        if(buscarPorIdSemExcecao(eqp.getId()) != null){
            throw new EquipamentoJaCadastradoException("Já existe um equipamento com o id: " + eqp.getId());
        }
        equipamentos.add(eqp);
    }

    public Set<Equipamento> listarEquipamentos(){
        return Collections.unmodifiableSet(equipamentos); // devolve uma visão somente-leitura do conjunto, impedindo alterações externas
    }

    public void atualizarEquipamento(Equipamento alterado){
        Equipamento existente = buscarPorId(alterado.getId());

        if (alterado.getNome() != null) {
            existente.setNome(alterado.getNome());
        }
        if (alterado.getCodigoPatrimonio() != null) {
            existente.setCodigoPatrimonio(alterado.getCodigoPatrimonio());
        }
        if (alterado.getDataAquisicao() != null) {
            existente.setDataAquisicao(alterado.getDataAquisicao());
        }
    }

    public void excluirEquipamento(int id) { //só exclui se não tiver manutenção aberta com ele

        Equipamento existente = buscarPorId(id); // vejo se existe, caso contrário já lança a exceção

        if (gestaoManutencao.existeManutencaoDoEquipamento(id)) {
            throw new EquipamentoNaManutencaoException(
                    "Não é possível excluir. Equipamento possui manutenção associada.");
        }

        equipamentos.remove(existente);
    }

}
