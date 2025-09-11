package main.java.com.joao.empresa.model;

import java.util.ArrayList;
import java.util.List;

public class Tecnico extends Usuario {

    private String especialidade;
    private String certificacoes;

    private List<Manutencao> manutencoesResponsaveis = new ArrayList<>(); // List é uma interface(contrato), por baixo pode ser implementado o arraylist

    public Tecnico(int id, String nome, String email, String senha, TipoUsuario tipoUsuario, String especialidade, String certificacoes) {
        super(id, nome, email, senha, TipoUsuario.TECNICO); // com o enum já fica facilmente definido para escolher
        this.especialidade = especialidade;
        this.certificacoes = certificacoes;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getCertificacoes() {
        return certificacoes;
    }

    public void setCertificacoes(String certificacoes) {
        this.certificacoes = certificacoes;
    }

    public List<Manutencao> getChamadosAtivos() {
        return manutencoesResponsaveis;
    }

    public void adicionarManutencao(Manutencao manutencao) {
        manutencoesResponsaveis.add(manutencao);
    }

    @Override
    public void atualizarEspecifico(Usuario alterado) {
        Tecnico tec = (Tecnico) alterado;

        if(tec.getCertificacoes() != null){
            setCertificacoes(tec.getCertificacoes());
        }
        if(tec.getEspecialidade() != null){
            setEspecialidade(tec.getEspecialidade());
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                "Tecnico{" +
                "especialidade='" + especialidade + '\'' +
                ", certificacoes='" + certificacoes + '\'' +
                ", chamadosAtivos=" + manutencoesResponsaveis +
                '}';
    }
}
