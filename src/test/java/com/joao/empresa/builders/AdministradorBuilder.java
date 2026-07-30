package com.joao.empresa.builders;

import com.joao.empresa.model.Administrador;

// Padrão de criação de objetos
public class AdministradorBuilder {

    //Se ninguém falar nada, esses são os valores já inicializados
    private int id = 1;
    private String nome = "João Emanuel";
    private String email = "pnjoao@gmail.com";
    private String senha = "ea34";
    private String departamento = "Tecnologia da Informação";

    public static AdministradorBuilder builder() { // construtor estático (pode ser chamado sem instanciar)
        return new AdministradorBuilder(); // salva o construtor na variável builder, a cada chamada cria um diferente
    }

    // esses métodos sobrescrevem o valor padrão
    public AdministradorBuilder comId(int id) {
        this.id = id;
        return this; // permite o encadeamento, devolve o próprio builder alterado
    }

    public AdministradorBuilder comNome(String nome) {
        this.nome = nome;
        return this;
    }

    public AdministradorBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public AdministradorBuilder comSenha(String senha) {
        this.senha = senha;
        return this;
    }

    public AdministradorBuilder comDepartamento(String departamento) {
        this.departamento = departamento;
        return this;
    }

    public Administrador build() { // o build me entrega o objeto pronto
        return new Administrador(id, nome, email, senha, departamento);
    }
}


