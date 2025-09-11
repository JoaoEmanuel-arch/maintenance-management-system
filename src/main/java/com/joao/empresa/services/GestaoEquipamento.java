package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Equipamento;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestaoEquipamento {

    private List<Equipamento> equipamentos = new ArrayList<>();

    public Equipamento buscarPorId(int id){
        for(Equipamento eqp : equipamentos){
            if(eqp.getId() == id){
                return eqp;
            }
        }
        return null;
    }

    public boolean cadastrarEquipamento(Equipamento eqp){
        if(buscarPorId(eqp.getId()) != null){
            return false;
        }
        return equipamentos.add(eqp);
    }

    public List<Equipamento> listarEquipamentos(){
        return Collections.unmodifiableList(equipamentos);
    }

    public void atualizarEquipamento(Equipamento alterado){
        Equipamento existente = buscarPorId(alterado.getId());

        if (existente == null){
            return;
        }

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

    public boolean excluirEquipamento(int id){
        return equipamentos.removeIf(eqp -> eqp.getId() == id);
    }



}
