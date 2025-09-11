package main.java.com.joao.empresa.model;

import java.util.ArrayList;
import java.util.List;

public class Gestor extends Usuario {

    private String areaResponsavel;

    private List<Tecnico> equipeTecnicos = new ArrayList<Tecnico>();

    public Gestor(int id, String nome, String email, String senha, String areaResponsavel) {
        super(id, nome, email, senha, TipoUsuario.GESTOR);
        this.areaResponsavel = areaResponsavel;
    }

    public String getAreaResponsavel() {
        return areaResponsavel;
    }

    public void setAreaResponsavel(String areaResponsavel) {
        this.areaResponsavel = areaResponsavel;
    }

    public List<Tecnico> getEquipeTecnicos() {
        return equipeTecnicos;
    }

    public void adicionarTecnico(Tecnico tecnico) {
        equipeTecnicos.add(tecnico);
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
                "areaResponsavel='" + areaResponsavel + '\'' +
                ", chamadosAtivos=" + equipeTecnicos +
                '}';
    }
}
