package main.java.com.joao.empresa.model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class Tecnico extends Usuario {

    private String especialidade;
    private String certificacoes;

    private Set<Manutencao> manutencoesResponsaveis = new LinkedHashSet<>();

    public Tecnico(int id, String nome, String email, String senha, String especialidade, String certificacoes) {
        super(id, nome, email, senha, TipoUsuario.TECNICO);
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

    public Set<Manutencao> getManutencoesResponsaveis() {
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
    public boolean equals(Object o) {
        if (!(o instanceof Tecnico tecnico)) return false;
        return Objects.equals(especialidade, tecnico.especialidade) && Objects.equals(certificacoes, tecnico.certificacoes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(especialidade, certificacoes);
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
