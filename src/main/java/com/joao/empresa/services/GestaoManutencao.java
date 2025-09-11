package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.model.Manutencao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestaoManutencao {

    private List<Manutencao> manutencoesAtivas = new ArrayList<>();

    private List<Manutencao> manutencoesFinalizadas = new ArrayList<>();

    public Manutencao buscarPorId(int id) {
        for (Manutencao mnt : manutencoesAtivas) {
            if (mnt.getId() == id) {
                return mnt;
            }
        }
        return null;
    }

    public boolean cadastrarManutencao(Manutencao mnt) {
        if (buscarPorId(mnt.getId()) != null) {
            return false;
        }
        return manutencoesAtivas.add(mnt);
    }

    public List<Manutencao> listarManutencoes() {
        return Collections.unmodifiableList(manutencoesAtivas);
    }

    public boolean excluirManutencao(int id) {
        return manutencoesAtivas.removeIf(mnt -> mnt.getId() == id);
    }

    public boolean finalizarManutencao(int id) {
        Manutencao mnt = buscarPorId(id);
        if (mnt != null) {
            mnt.setStatus(Manutencao.Status.CONCLUIDA);
            manutencoesFinalizadas.add(mnt);
            mnt.getEquipamento().adicionarManutencao(mnt); // joga pro histórico do equipamento
            mnt.getTecnicoResponsavel().adicionarManutencao(mnt); // joga pro histórico do técnico
            excluirManutencao(id);
            return true;
        }
        return false;
    }
    // AQUI TÁ COM MUITAS RESPONSABILIDADES -> CRIAR O HISTORICO SERVICE
}