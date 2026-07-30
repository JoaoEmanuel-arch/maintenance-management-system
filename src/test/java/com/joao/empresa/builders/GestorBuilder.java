package com.joao.empresa.builders;

import com.joao.empresa.model.Gestor;

public class GestorBuilder {

    private int id = 1;
    private String nome = "João Emanuel";
    private String email = "pnjoao@gmail.com";
    private String senha = "ea34";
    private String areaResponsavel = "Tecnologia da Informação";

    public static GestorBuilder builder() {
        return new GestorBuilder();
    }

    public GestorBuilder comId(int id) {
        this.id = id;
        return this;
    }

    public GestorBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public GestorBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public GestorBuilder comSenha(String senha) {
        this.senha = senha;
        return this;
    }

    public GestorBuilder comAreaResponsavel(String areaResponsavel) {
        this.areaResponsavel = areaResponsavel;
        return this;
    }

    public Gestor build() {
        return new Gestor(id, nome, email, senha, areaResponsavel);
    }

}
