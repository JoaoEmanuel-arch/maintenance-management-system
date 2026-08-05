package com.joao.empresa.model;

public abstract class Entidade {

    private Integer id;

    protected Entidade(Integer id) {
        if (id != null) {
            validarIdPositivo(id);
        }

        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    // alterar o id da entidade que estava nulo para o id que veio do banco
    public void definirId(Integer id) {
        validarIdPositivo(id);

        if (this.id != null) {
            throw new IllegalStateException(
                    "A entidade já possui um ID."
            );
        }

        this.id = id;
    }

    private static void validarIdPositivo(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "O ID deve ser um número positivo."
            );
        }
    }

    // dois objetos só representam a mesma entidade quando pertencem exatamente à mesma classe
    // e possuem o mesmo ID não nulo
    @Override
    public final boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }

        if (objeto == null || getClass() != objeto.getClass()) {
            return false;
        }

        Entidade outraEntidade = (Entidade) objeto;

        return id != null && id.equals(outraEntidade.id);
    }

    // retorna o hash da própria classe
    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

    // são finais para nenhuma subclasse sobreescrever e avacalhar o negócio

    @Override
    public String toString() {
        return "Entidade{" +
                "id=" + id +
                '}';
    }
}
