package main.java.com.joao.empresa.model;

import java.time.LocalDate;

import java.util.LinkedHashSet;
import java.util.Objects;
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
        historicoManutencoes.add(manutencao);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Equipamento that)) return false;
        return Objects.equals(nome, that.nome) && Objects.equals(codigoPatrimonio, that.codigoPatrimonio)
                && Objects.equals(dataAquisicao, that.dataAquisicao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, codigoPatrimonio, dataAquisicao);
    }

    @Override
    public String toString() {
        return String.format(
                "Equipamento{id=%d, nome='%s', patrimonio='%s', dataAquisicao=%s, historico=%s}",
                getId(), nome, codigoPatrimonio, dataAquisicao, historicoManutencoes
        );
    }
}


