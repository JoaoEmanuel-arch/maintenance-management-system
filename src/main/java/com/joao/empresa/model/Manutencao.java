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
        this( // chama o segundo para reaproveitar a lógica de validação e criar o objeto
                null, // preenche automaticamente
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
            Integer id, // veio nulo do de cima, mas depois tem um método que preenche com o DAO
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

    private static void validarDadosBasicos(
            TipoManutencao tipoManutencao,
            String descricao,
            LocalDate dataInicio,
            Equipamento equipamento,
            Tecnico tecnicoResponsavel
    ) {
        if (tipoManutencao == null) {
            throw new IllegalArgumentException("O tipo da manutenção é obrigatório.");
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição da manutenção é obrigatória.");
        }

        if (dataInicio == null) {
            throw new IllegalArgumentException("A data inicial é obrigatória.");
        }

        if (equipamento == null) {
            throw new IllegalArgumentException("O equipamento é obrigatório.");
        }

        if (tecnicoResponsavel == null) {
            throw new IllegalArgumentException("O técnico responsável é obrigatório.");
        }
    }

    private static void validarCusto(BigDecimal custo) {
        if (custo == null) {
            throw new IllegalArgumentException("O custo é obrigatório.");
        }

        if (custo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O custo não pode ser negativo.");
        }
    }

    private static void validarDataFim(LocalDate dataFim, LocalDate dataInicio) {
        if (dataFim == null) {
            throw new IllegalArgumentException("A data final é obrigatória.");
        }

        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
    }

    private static void validarEstado(
            TipoManutencao tipoManutencao,
            String descricao,
            BigDecimal custo,
            LocalDate dataInicio,
            LocalDate dataFim,
            Status status,
            Equipamento equipamento,
            Tecnico tecnicoResponsavel
    ) {
        validarDadosBasicos(
                tipoManutencao,
                descricao,
                dataInicio,
                equipamento,
                tecnicoResponsavel
        );

        validarCusto(custo);

        if (status == null) {
            throw new IllegalArgumentException("O status da manutenção é obrigatório.");
        }

        if (dataFim != null) {
            validarDataFim(dataFim, dataInicio);
        }

        if (status == Status.ANDAMENTO && dataFim != null) {
            throw new IllegalArgumentException("Uma manutenção em andamento não pode possuir data final.");
        }

        if (status != Status.ANDAMENTO && dataFim == null) {
            throw new IllegalArgumentException("Uma manutenção concluída ou cancelada precisa possuir data final.");
        }
    }

    private void exigirEmAndamento() {
        if (status != Status.ANDAMENTO) {
            throw new IllegalStateException("Somente uma manutenção em andamento pode ser alterada.");
        }
    }

    // Atualiza apenas os dados permitidos enquanto a manutenção estiver em andamento. São os setters só q tudo de uma vez sóx
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
    public void finalizar(BigDecimal custo, LocalDate dataConclusao) {
        exigirEmAndamento();
        validarCusto(custo);
        validarDataFim(dataConclusao, dataInicio);

        this.custo = custo;
        this.dataFim = dataConclusao;
        this.status = Status.CONCLUIDA;
    }

    // O cancelamento também é uma operação completa.
    public void cancelar(LocalDate dataCancelamento) {
        exigirEmAndamento();
        validarDataFim(dataCancelamento, dataInicio);

        this.dataFim = dataCancelamento;
        this.status = Status.CANCELADA;
    }

    public TipoManutencao getTipoManutencao() {
        return tipoManutencao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getCusto() {
        return custo;
    }

    public Tecnico getTecnicoResponsavel() {
        return tecnicoResponsavel;
    }

    public Equipamento getEquipamento() {
        return equipamento;
    }

    public Status getStatus() {
        return status;
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
