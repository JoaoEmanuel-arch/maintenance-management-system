package com.joao.empresa.builders;

import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Tecnico;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ManutencaoBuilder {

    private Integer id = 1;

    private Manutencao.TipoManutencao tipoManutencao =
            Manutencao.TipoManutencao.CORRETIVA;

    private String descricao =
            "Correia dentada da empilhadeira arrebentou";

    private BigDecimal custo = BigDecimal.ZERO;

    private LocalDate dataInicio =
            LocalDate.of(2026, 1, 27);

    private LocalDate dataFim = null;

    private Manutencao.Status status =
            Manutencao.Status.ANDAMENTO;

    private Tecnico tecnicoResponsavel =
            TecnicoBuilder.builder()
                    .comId(1)
                    .build();

    private Equipamento equipamento =
            EquipamentoBuilder.builder()
                    .comId(1)
                    .build();

    public static ManutencaoBuilder builder() {
        return new ManutencaoBuilder();
    }

    public ManutencaoBuilder comId(Integer id) {
        this.id = id;
        return this;
    }

    public ManutencaoBuilder semId() {
        this.id = null;
        return this;
    }

    public ManutencaoBuilder comTipoManutencao(
            Manutencao.TipoManutencao tipoManutencao
    ) {
        this.tipoManutencao = tipoManutencao;
        return this;
    }

    public ManutencaoBuilder comDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public ManutencaoBuilder comCusto(BigDecimal custo) {
        this.custo = custo;
        return this;
    }

    public ManutencaoBuilder comDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
        return this;
    }

    public ManutencaoBuilder comDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
        return this;
    }

    public ManutencaoBuilder comStatus(
            Manutencao.Status status
    ) {
        this.status = status;
        return this;
    }

    public ManutencaoBuilder comTecnicoResponsavel(
            Tecnico tecnicoResponsavel
    ) {
        this.tecnicoResponsavel = tecnicoResponsavel;
        return this;
    }

    public ManutencaoBuilder comEquipamento(
            Equipamento equipamento
    ) {
        this.equipamento = equipamento;
        return this;
    }

    public ManutencaoBuilder concluida() {
        this.status = Manutencao.Status.CONCLUIDA;
        this.dataFim = LocalDate.of(2026, 1, 28);
        this.custo = new BigDecimal("8500.00");
        return this;
    }

    public ManutencaoBuilder cancelada() {
        this.status = Manutencao.Status.CANCELADA;
        this.dataFim = LocalDate.of(2026, 1, 28);
        this.custo = BigDecimal.ZERO;
        return this;
    }

    public Manutencao build() {
        return new Manutencao(
                id,
                tipoManutencao,
                descricao,
                custo,
                dataInicio,
                dataFim,
                status,
                equipamento,
                tecnicoResponsavel
        );
    }
}