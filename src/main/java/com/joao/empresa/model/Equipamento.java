package main.java.com.joao.empresa.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Equipamento extends Entidade {

    private String nome;
    private String codigoPatrimonio;
    private LocalDate dataAquisicao; // tipo específico para datas

    private List<Manutencao> historicoManutencoes = new ArrayList<>();

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

    public List<Manutencao> getHistorico() {
        return new ArrayList<>(historicoManutencoes); // retorna uma cópia do histórico, evitando alterações
    }

    public void adicionarManutencao(Manutencao manutencao) {
        historicoManutencoes.add(manutencao);
    }

    @Override
    public String toString() {
        return String.format(
                "Equipamento{id=%d, nome='%s', patrimonio='%s', dataAquisicao=%s, historico=%s}",
                getId(), nome, codigoPatrimonio, dataAquisicao, historicoManutencoes
        );
    }
}


