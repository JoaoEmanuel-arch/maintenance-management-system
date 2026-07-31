package com.joao.empresa.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Empresa extends Entidade {

    public enum Status{
        ATIVADA("Empresa ativada"),
        DESATIVADA("Empresa desativada");

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

    private String nome, cnpj, endereco, segmento;
    private Status status;

    private Set<Equipamento> equipamentos = new HashSet<>();

    // esse é pra salvar o objeto sem o id, pq ainda não foi gerado pelo banco
    public Empresa(String nome, String cnpj, String endereco, String segmento, Status status) {
        this(
                null,
                nome,
                cnpj,
                endereco,
                segmento,
                status
        );
    }

    // esse é pra reconstruir o objeto
    public Empresa(Integer id, String nome, String cnpj, String endereco, String segmento, Status status){
        super(id);
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.segmento = segmento;
        this.status = status;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Equipamento> getEquipamentos() {
        return equipamentos;
    }

    public void adicionarEquipamento(Equipamento equipamento){
        equipamentos.add(equipamento);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Empresa empresa)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(nome, empresa.nome) && Objects.equals(cnpj, empresa.cnpj) && Objects.equals(endereco, empresa.endereco) && Objects.equals(segmento, empresa.segmento) && status == empresa.status && Objects.equals(equipamentos, empresa.equipamentos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nome, cnpj, endereco, segmento, status, equipamentos);
    }

    @Override
    public String toString() {
        return super.toString() +
                "Empresa{" +
                "nome='" + nome + '\'' +
                ", cnpj=" + cnpj +
                ", endereco='" + endereco + '\'' +
                ", segmento='" + segmento + '\'' +
                '}';
    }
}
