package com.joao.empresa.builders;

import com.joao.empresa.model.Equipamento;
import java.time.LocalDate;

public class EquipamentoBuilder {

    private int id = 1;
    private String nome = "Laminadora";
    private String codigoPatrimonio = "34837243678";
    private LocalDate dataAquisicao = LocalDate.of(2010, 06, 22);

    public static EquipamentoBuilder builder() {
        return new EquipamentoBuilder();
    }

    public EquipamentoBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public EquipamentoBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public EquipamentoBuilder comCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
        return this;
    }

    public EquipamentoBuilder comDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
        return this;
    }

    public Equipamento build() {
        return new Equipamento(id, nome, codigoPatrimonio, dataAquisicao);
    }

}
