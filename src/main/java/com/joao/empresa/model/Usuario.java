package com.joao.empresa.model;

public abstract class Usuario extends Entidade{

    public enum TipoUsuario { // uma variável que só pode assumir esses valores:
        ADMINISTRADOR("Administrador"),
        TECNICO("Técnico"),
        GESTOR("Gestor"); // "apelidos"

        private String descricao;

        TipoUsuario(String descricao) {
            this.descricao = descricao; // associa cada string extra à cada valor
        }

        public String getDescricao() { // retorna somente a descrição
            return descricao;
        }

        @Override
        public String toString() {
            return descricao; // o java chama automaticamente quando o enum vira string, estética
        }
    }

    private String nome;
    private String email;
    private String senha;
    private TipoUsuario tipo;

    protected Usuario(String nome, String email, String senha, TipoUsuario tipo) {
        this(
                null,
                nome,
                email,
                senha,
                tipo
        );
    }

    protected Usuario(Integer id, String nome, String email, String senha, TipoUsuario tipo){
        super(id);
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public void atualizarDados(Usuario alterado){  // regra de negócio do próprio objeto
        if(alterado.getNome() != null){
            setNome(alterado.getNome());
        }
        if(alterado.getEmail() != null){
            setEmail(alterado.getEmail());
        }
    }

    public abstract void atualizarEspecifico(Usuario alterado); // cada usuário implementa suas atualizações específicas

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", tipo_usuario='" + tipo + '\'' +
                '}';
    }
}
