package com.joao.empresa.builders;

import com.joao.empresa.model.Empresa;

public class EmpresaBuilder {

    private int id = 1;
    private String nome = "Gerdau Açominas";
    private String cnpj = "999929714";
    private String endereco = "Ouro Branco";
    private String segmento = "Produtora de Aço";

    public static EmpresaBuilder builder() {
        return new EmpresaBuilder();
    }

    public EmpresaBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public EmpresaBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public EmpresaBuilder comCpnj(String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public EmpresaBuilder comEndereco(String endereco) {
        this.endereco = endereco;
        return this;
    }

    public EmpresaBuilder comSeguimento(String segmento) {
        this.segmento = segmento;
        return this;
    }

    public Empresa build() {
        return new Empresa(id, nome, cnpj, endereco, segmento);
    }

}
