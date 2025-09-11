package main.java.com.joao.empresa.model;

import java.util.ArrayList;
import java.util.List;

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

    private List<Equipamento> equipamentos = new ArrayList<>();

    public Empresa(int id, String nome, String cnpj, String endereco, String segmento){
        super(id);
        this.nome = nome;
        this.cnpj = cnpj;
        this.endereco = endereco;
        this.segmento = segmento;
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

    public List<Equipamento> getEquipamentos() {
        return new ArrayList<>(equipamentos); // retorna uma cópia do array
    }

    public void adicionarEquipamento(Equipamento equipamento){
        equipamentos.add(equipamento);
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
