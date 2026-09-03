package com.joao.empresa.builders;

import com.joao.empresa.model.Administrador;

// Padrão de criação de objetos
public class AdministradorBuilder {

    //Se ninguém falar nada, esses são os valores já inicializados
    private Integer id = 1;
    private String nome = "João Emanuel";
    private String email = "pnjoao@gmail.com";
    private String departamento = "Tecnologia da Informação";

    public static AdministradorBuilder builder() { // construtor estático (pode ser chamado sem instanciar)
        return new AdministradorBuilder(); // salva o construtor na variável builder, a cada chamada cria um diferente
    }

    // esses métodos sobrescrevem o valor padrão
    public AdministradorBuilder comId(Integer id) {
        this.id = id;
        return this;
    }

    public AdministradorBuilder semId() {
        this.id = null;
        return this;
    }

    public AdministradorBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public AdministradorBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public AdministradorBuilder comDepartamento(String departamento) {
        this.departamento = departamento;
        return this;
    }

    public Administrador build() {
        return new Administrador(
                id,
                nome,
                email,
                departamento
        );
    }

}


