package main.java.com.joao.empresa.model;

public class Gestor extends Usuario {

    private String areaResponsavel;

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
