package com.joao.empresa.builders;

import com.joao.empresa.model.Empresa;

public class EmpresaBuilder {

    private Integer id = 1;
    private String nome = "Gerdau Açominas";
    private String cnpj = "999929714";
    private String endereco = "Ouro Branco";
    private String segmento = "Produtora de Aço";
    private Empresa.Status status = Empresa.Status.ATIVADA;

    public static EmpresaBuilder builder() {
        return new EmpresaBuilder(); // instancia para usar os próprios métodos
    }

    // por padrão são aqueles valores, mas se eu quiser mudar chamo esses aqui específicos
    public EmpresaBuilder comId(Integer id) {
        this.id = id;
        return this;
    }

    public EmpresaBuilder semId() {
        this.id = null;
        return this;
    }

    public EmpresaBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public EmpresaBuilder comCnpj(String cnpj) {
        this.cnpj = cnpj;
        return this;
    }

    public EmpresaBuilder comEndereco(String endereco) {
        this.endereco = endereco;
        return this;
    }

    public EmpresaBuilder comSegmento(String segmento) {
        this.segmento = segmento;
        return this;
    }

    public EmpresaBuilder comStatus(Empresa.Status status) {
        this.status = status;
        return this;
    }

    // e no final crio o objeto propriamente dito
    public Empresa build() {
        return new Empresa(
                id,
                nome,
                cnpj,
                endereco,
                segmento,
                status
        );
    }
}