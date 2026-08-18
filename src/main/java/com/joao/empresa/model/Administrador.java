package com.joao.empresa.model;

public class Administrador extends Usuario {

    private String departamento;

    public Administrador(String nome, String email, String departamento) {
        super(nome, email, TipoUsuario.ADMINISTRADOR);
        this.departamento = departamento;
    }

    public Administrador(Integer id, String nome, String email, String departamento){
        super(id, nome, email, TipoUsuario.ADMINISTRADOR);
        this.departamento = departamento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public void atualizarEspecifico(Usuario alterado){
        Administrador adm = (Administrador) alterado; //cast para tratar o usuário como administrador, adentrando na subclasse

        if(adm.getDepartamento() != null){
            setDepartamento(adm.getDepartamento());
        }
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "departamento='" + departamento + '\'' +
                '}';
    }

}
