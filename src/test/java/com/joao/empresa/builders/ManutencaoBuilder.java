package com.joao.empresa.builders;

import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Manutencao.*; // com esse aterisco importa tudo da classe manutenção automaticamente
import com.joao.empresa.model.Tecnico;
import java.time.LocalDate;

public class ManutencaoBuilder {

    private int id = 1;
    private TipoManutencao tipoManutencao = TipoManutencao.CORRETIVA;
    private LocalDate dataInicio = LocalDate.of(2026, 01, 27);
    private String descricao = "Correia dentada da empilhadeira arrebentou";
    private double custo = 8500.00;
    private Tecnico tecnicoResponsavel = TecnicoBuilder.builder().build();
    private Equipamento equipamento = EquipamentoBuilder.builder().build();

    public static ManutencaoBuilder builder() {
        return new ManutencaoBuilder();
    }

    public ManutencaoBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public ManutencaoBuilder comTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
        return this;
    }

    public ManutencaoBuilder comDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        return this;
    }

    public ManutencaoBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public ManutencaoBuilder comTecnicoResponsavel(Tecnico tecnicoResponsavel) {
        this.tecnicoResponsavel = tecnicoResponsavel;
        return this;
    }

    public ManutencaoBuilder comEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
        return this;
    }

    public Manutencao build() {
        return new Manutencao(id, tipoManutencao, dataInicio, descricao, tecnicoResponsavel, equipamento);
    }

}
