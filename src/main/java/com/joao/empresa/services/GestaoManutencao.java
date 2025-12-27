package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import main.java.com.joao.empresa.model.Manutencao;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GestaoManutencao {

    private Set<Manutencao> manutencoesAtivas = new LinkedHashSet<>();

    private Set<Manutencao> manutencoesFinalizadas = new LinkedHashSet<>();

    // pensar que pode querer buscar nas finalizadas também
    public Manutencao buscarPorId(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id). //só passa os que forem true
                findFirst(). //retorna o primeiro
                orElseThrow(() ->
                        new ManutencaoNaoEncontradaException("Manutenção com ID " + id + " não encontrada.")
                );
    }

    private Manutencao buscarPorIdSemExcecao(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id).
                findFirst().
                orElse(null);
    }

    public boolean cadastrarManutencao(Manutencao mnt) {
        if (buscarPorId(mnt.getId()) != null) {
            return false;
        }
        return manutencoesAtivas.add(mnt);
    }

    public boolean existeManutencaoDoEquipamento(int idEquipamento) {
        return manutencoesAtivas.stream() // cada manutenção da lista entra no parâmetro da função anônima
                .anyMatch(m -> m.getEquipamento().getId() == idEquipamento)
                || manutencoesFinalizadas.stream()
                .anyMatch(m -> m.getEquipamento().getId() == idEquipamento);
    }

    public Set<Manutencao> listarManutencoes() {
        return Collections.unmodifiableSet(manutencoesAtivas);
    }

    public boolean excluirManutencao(int id){
        return manutencoesAtivas.removeIf(mnt -> mnt.getId() == id);
    }

    public void finalizarManutencao(int id) {
        Manutencao mnt = buscarPorId(id); // aqui já lança a exceção

        mnt.setStatus(Manutencao.Status.CONCLUIDA);
        manutencoesFinalizadas.add(mnt);
        mnt.getEquipamento().adicionarManutencao(mnt); // joga pro histórico do equipamento
        mnt.getTecnicoResponsavel().adicionarManutencao(mnt); // joga pro histórico do técnico
        excluirManutencao(id);
    }
}