package main.java.com.joao.empresa.model;

public class Administrador extends Usuario {

    public enum NivelAcesso{
        TOTAL("Acesso total"),
        RESTRITO("Acesso restrito");

        private String descricao;

        NivelAcesso(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private NivelAcesso nivelAcesso;
    private String departamento;

    public Administrador(int id, String nome, String email, String senha, String departamento){
        super(id, nome, email, senha, TipoUsuario.ADMINISTRADOR);
        nivelAcesso = NivelAcesso.TOTAL;
        this.departamento = departamento;
    }

    public NivelAcesso getNivelAcesso() {
        return nivelAcesso;
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
        return super.toString() +
                " Administrador{" +
                "nivelAcesso=" + nivelAcesso +
                ", departamento='" + departamento + '\'' +
                '}';
    }

}
