package com.joao.empresa.model;

public class Gestor extends Usuario {

    private String areaResponsavel;

    public Gestor(String nome, String email, String areaResponsavel) {
        super(nome, email, TipoUsuario.GESTOR);
        this.areaResponsavel = areaResponsavel;
    }

    public Gestor(Integer id, String nome, String email, String areaResponsavel) {
        super(id, nome, email, TipoUsuario.GESTOR);
        this.areaResponsavel = areaResponsavel;
    }

    public String getAreaResponsavel() {
        return areaResponsavel;
    }

    public void setAreaResponsavel(String areaResponsavel) {
        this.areaResponsavel = areaResponsavel;
    }

    @Override
    public void atualizarEspecifico(Usuario alterado) {
        Gestor gestor = (Gestor) alterado;

        if(gestor.getAreaResponsavel() != null){
            setAreaResponsavel(gestor.getAreaResponsavel());
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                "Gestor{" +
                "areaResponsavel='" + areaResponsavel +
                '}';
    }
}
