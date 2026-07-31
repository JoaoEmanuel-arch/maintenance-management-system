package com.joao.empresa.model;

public abstract class Entidade {

    private Integer id;

    protected Entidade(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    // alterar o id da entidade que estava nulo para o id que veio do banco
    public void definirId(Integer id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser um número positivo."
            );
        }

        if (this.id != null) {
            throw new IllegalStateException(
                    "A entidade já possui um ID."
            );
        }

        this.id = id;
    }

    @Override
    public boolean equals(Object objeto) {

        if (this == objeto) {
            return true;
        }

        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }

        Entidade outraEntidade = (Entidade) objeto;

        return id != null && id.equals(outraEntidade.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Entidade{" +
                "id=" + id +
                '}';
    }
}
