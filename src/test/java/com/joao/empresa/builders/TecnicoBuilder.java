package com.joao.empresa.builders;

import com.joao.empresa.model.Tecnico;

public class TecnicoBuilder {

    private Integer id = 1;
    private String nome = "João Emanuel";
    private String email = "pnjoao@gmail.com";
    private String especialidade = "Tecnologia da Informação";

    public static TecnicoBuilder builder() {
        return new TecnicoBuilder();
    }

    public TecnicoBuilder comId(Integer id) {
        this.id = id;
        return this;
    }

    public TecnicoBuilder semId() {
        this.id = null;
        return this;
    }

    public TecnicoBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public TecnicoBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public TecnicoBuilder comEspecialidade(String especialidade) {
        this.especialidade = especialidade;
        return this;
    }

    public Tecnico build() {
        return new Tecnico(
                id,
                nome,
                email,
                especialidade
        );
    }
}