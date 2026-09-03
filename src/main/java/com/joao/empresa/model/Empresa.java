package com.joao.empresa.model;

import java.util.HashSet;
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
    } // depois chama o de baixo para construir

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

    // o equals e hashcode virá da classe entidade. A determinação para objetos serem iguais
    // se baseia na classe e no id gerado pelo banco.

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
