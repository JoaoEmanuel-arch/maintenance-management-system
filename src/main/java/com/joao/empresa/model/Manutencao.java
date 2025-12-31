package main.java.com.joao.empresa.model;

import java.time.LocalDate;
import java.util.Objects;

public class Manutencao extends Entidade {

    public enum TipoManutencao{
        PREVENTIVA("Manutencao preventiva"),
        CORRETIVA("Manutencao corretiva");

        private String descricao;

        TipoManutencao(String descricao){
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public enum Status{

        ANDAMENTO("Manutencao em andamento"),
        CONCLUIDA("Manutencao concluida"),
        CANCELADA("Manutencao cancelada");

        private String descricao;

        Status(String descricao){
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private LocalDate dataInicio, dataFim;
    private TipoManutencao tipoManutencao;
    private String descricao;
    private double custo;
    private Tecnico tecnicoResponsavel;
    private Equipamento equipamento;
    private Status status;

    public Manutencao(int id, TipoManutencao tipoManutencao, LocalDate dataInicio, String descricao, Tecnico tecnicoResponsavel, Equipamento equipamento) {
        super(id);
        this.tipoManutencao = tipoManutencao;
        this.dataInicio = dataInicio;
        this.descricao = descricao;
        this.tecnicoResponsavel = tecnicoResponsavel;
        this.equipamento = equipamento;
        status = Status.ANDAMENTO;
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public void setTipoManutencao(TipoManutencao tipoManutencao) {
        this.tipoManutencao = tipoManutencao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getCusto() {
        return custo;
    }

    public void setCusto(double custo) {
        this.custo = custo;
    }

    public Tecnico getTecnicoResponsavel() {
        return tecnicoResponsavel;
    }

    public void setTecnicoResponsavel(Tecnico tecnicoResponsavel) {
        this.tecnicoResponsavel = tecnicoResponsavel;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public void setEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() +
                "Manutencao{" +
                "dataInicio=" + dataInicio +
                ", dataFim=" + dataFim +
                ", tipoManutencao=" + tipoManutencao +
                ", descricao='" + descricao + '\'' +
                ", custo=" + custo +
                ", tecnicoResponsavel=" + tecnicoResponsavel +
                ", equipamento=" + equipamento +
                '}';
    }
}
