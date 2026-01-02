package main.java.com.joao.empresa.services;

import main.java.com.joao.empresa.exceptions.ManutencaoJaCadastradaException;
import main.java.com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import main.java.com.joao.empresa.model.Manutencao;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class GestaoManutencao {

    private Set<Manutencao> manutencoesAtivas = new LinkedHashSet<>();

    private Set<Manutencao> manutencoesFinalizadas = new LinkedHashSet<>();

    public Manutencao buscarAtivasPorId(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id). //só passa os que forem true
                findFirst(). //retorna o primeiro
                orElseThrow(() ->
                        new ManutencaoNaoEncontradaException("Manutenção com ID " + id + " não encontrada.")
                );
    }

    private Manutencao buscarAtivasPorIdSemExcecao(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id).
                findFirst().
                orElse(null);
    }

    public Manutencao buscarFinalizadasPorId(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id). //só passa os que forem true
                        findFirst(). //retorna o primeiro
                        orElseThrow(() ->
                        new ManutencaoNaoEncontradaException("Manutenção com ID " + id + " não encontrada.")
                );
    }

    private Manutencao buscarFinalizadasPorIdSemExcecao(int id){
        return manutencoesAtivas.stream().
                filter(mnt -> mnt.getId() == id).
                findFirst().
                orElse(null);
    }

    public void cadastrarManutencao(Manutencao mnt) {
        if(buscarAtivasPorIdSemExcecao(mnt.getId()) != null){
            throw new ManutencaoJaCadastradaException("Já existe uma manutenção cadastrada com o ID " + mnt.getId());
        }
        manutencoesAtivas.add(mnt);
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

    public void atualizarManutencao(Manutencao alterada){ // recebo objeto somente com o campos que quero alterar, os demais ficam null
        Manutencao existente = buscarAtivasPorId(alterada.getId()); //lança exceção

        if(alterada.getTipoManutencao() != null){
            existente.setTipoManutencao(alterada.getTipoManutencao());
        }
        if(alterada.getDataInicio() != null){
            existente.setDataInicio(alterada.getDataInicio());
        }
        if(alterada.getDescricao() != null){
            existente.setDescricao(alterada.getDescricao());
        }
        if(alterada.getTecnicoResponsavel() != null){
            existente.setTecnicoResponsavel(alterada.getTecnicoResponsavel());
        }
        if(alterada.getEquipamento() != null){
            existente.setEquipamento(alterada.getEquipamento());
        }
    }

    public void cancelarManutencao(int id){ // remove das manutenções ativas
        Manutencao mnt = buscarAtivasPorId(id);
        mnt.setStatus(Manutencao.Status.CANCELADA);
        manutencoesAtivas.remove(mnt);
        manutencoesFinalizadas.add(mnt);
    }

    public void finalizarManutencao(int id) {
        Manutencao mnt = buscarAtivasPorId(id); // aqui já lança a exceção
        mnt.setStatus(Manutencao.Status.CONCLUIDA);
        manutencoesAtivas.remove(mnt);
        manutencoesFinalizadas.add(mnt);
        mnt.getEquipamento().adicionarManutencao(mnt); // joga pro histórico do equipamento
        mnt.getTecnicoResponsavel().adicionarManutencao(mnt); // joga pro histórico do técnico
    }

    public void excluirManutencao(int id){
        Manutencao mnt = buscarFinalizadasPorId(id);
        manutencoesFinalizadas.remove(mnt);
    }

}