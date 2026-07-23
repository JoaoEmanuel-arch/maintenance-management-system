package com.joao.empresa.model;

import java.time.LocalDate;

import java.util.LinkedHashSet;
import java.util.Set;

public class Equipamento extends Entidade {

    private String nome;
    private String codigoPatrimonio;
    private LocalDate dataAquisicao; // tipo específico para datas

    private Set<Manutencao> historicoManutencoes = new LinkedHashSet<>();

    public Equipamento(int id, String nome, String codigoPatrimonio, LocalDate dataAquisicao) {
        super(id);
        this.nome = nome;
        this.codigoPatrimonio = codigoPatrimonio;
        this.dataAquisicao = dataAquisicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public Set<Manutencao> getHistoricoManutencoes() {
        return historicoManutencoes;
    }

    public void adicionarManutencao(Manutencao manutencao) {
        boolean add = historicoManutencoes.add(manutencao);
    }

    @Override
    public String toString() {
        return "Equipamento{" +
                "nome='" + nome + '\'' +
                ", codigoPatrimonio='" + codigoPatrimonio + '\'' +
                ", dataAquisicao=" + dataAquisicao +
                ", historicoManutencoes=" + historicoManutencoes +
                '}';
    }
}


