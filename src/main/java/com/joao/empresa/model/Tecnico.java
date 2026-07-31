package com.joao.empresa.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class Tecnico extends Usuario {

    private String especialidade;

    private Set<Manutencao> manutencoesResponsaveis = new LinkedHashSet<>();

    public Tecnico(String nome, String email, String senha, String especialidade) {
        super(nome, email, senha, TipoUsuario.TECNICO);
        this.especialidade = especialidade;
    }

    public Tecnico(Integer id, String nome, String email, String senha, String especialidade) {
        super(id, nome, email, senha, TipoUsuario.TECNICO);
        this.especialidade = especialidade;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
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

        if(tec.getEspecialidade() != null){
            setEspecialidade(tec.getEspecialidade());
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                "Tecnico{" +
                "especialidade='" + especialidade + '\'' +
                ", chamadosAtivos=" + manutencoesResponsaveis +
                '}';
    }
}
