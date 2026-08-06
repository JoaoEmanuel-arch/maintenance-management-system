package com.joao.empresa.model;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    private BigDecimal custo;
    private Tecnico tecnicoResponsavel;
    private Equipamento equipamento;
    private Status status;

    // Construtor usado para criar uma manutenção nova.
    // Uma manutenção nova: ainda não possui ID, começa em andamento, não possui data final
    // começa com custo zero. Por isso essas informações não são recebidas por parâmetro
    public Manutencao(
            TipoManutencao tipoManutencao,
            String descricao,
            LocalDate dataInicio,
            Equipamento equipamento,
            Tecnico tecnicoResponsavel
    ) {
        this(
                null,
                tipoManutencao,
                descricao,
                BigDecimal.ZERO,
                dataInicio,
                null,
                Status.ANDAMENTO,
                equipamento,
                tecnicoResponsavel
        );
    }

    // Construtor para o DAO reconstruir manuntenção que já existe no banco
    public Manutencao(
            Integer id,
            TipoManutencao tipoManutencao,
            String descricao,
            BigDecimal custo,
            LocalDate dataInicio,
            LocalDate dataFim,
            Status status,
            Equipamento equipamento,
            Tecnico tecnicoResponsavel
    ) {
        super(id);

        validarEstado(
                tipoManutencao,
                descricao,
                custo,
                dataInicio,
                dataFim,
                status,
                equipamento,
                tecnicoResponsavel
        );

        this.tipoManutencao = tipoManutencao;
        this.descricao = descricao;
        this.custo = custo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.equipamento = equipamento;
        this.tecnicoResponsavel = tecnicoResponsavel;
    }

    // Atualiza apenas os dados permitidos enquanto a manutenção estiver em andamento.
    public void atualizarDados(
            TipoManutencao tipoManutencao,
            String descricao,
            LocalDate dataInicio,
            Equipamento equipamento,
            Tecnico tecnicoResponsavel
    ) {
        exigirEmAndamento();

        validarDadosBasicos(
                tipoManutencao,
                descricao,
                dataInicio,
                equipamento,
                tecnicoResponsavel
        );

        this.tipoManutencao = tipoManutencao;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.equipamento = equipamento;
        this.tecnicoResponsavel = tecnicoResponsavel;
    }

    // A finalização é uma operação completa. Status, custo e data final são alterados juntos.
    public void finalizar(
            BigDecimal custo,
            LocalDate dataConclusao
    ) {
        exigirEmAndamento();
        validarCusto(custo);
        validarDataFim(dataConclusao, dataInicio);

        this.custo = custo;
        this.dataFim = dataConclusao;
        this.status = Status.CONCLUIDA;
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
