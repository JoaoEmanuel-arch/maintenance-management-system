package main.java.com.joao.empresa.model;

import java.util.Objects;

public abstract class Entidade {

    private int id;

    public Entidade(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) { // somente com base no id é mais seguro para o CRUD
        if (!(o instanceof Entidade entidade)) return false;
        return id == entidade.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Entidade{" +
                "id=" + id +
                '}';
    }
}
