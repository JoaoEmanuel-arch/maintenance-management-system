package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Equipamento;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GestaoEquipamento {

    private GestaoManutencao gestaoManutencao;
    private Set<Equipamento> equipamentos = new HashSet<>();

    // Injeção de dependência: construtor recebe a referência para eu acessar
    // os métodos da manutenção
    public GestaoEquipamento(GestaoManutencao gestaoManutencao) {
        this.gestaoManutencao = gestaoManutencao;
    }

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

    public Set<Equipamento> listarEquipamentos(){
        return Collections.unmodifiableSet(equipamentos);
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

    public boolean excluirEquipamento(int id) { //só exclui se não tiver manutenção aberta com ele
        if (gestaoManutencao.existeManutencaoDoEquipamento(id)) {
            return false;
        }
        return equipamentos.removeIf(eqp -> eqp.getId() == id);
    }

}
