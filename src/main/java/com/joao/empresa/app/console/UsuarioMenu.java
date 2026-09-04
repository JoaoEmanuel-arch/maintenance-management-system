package com.joao.empresa.app.console;

import com.joao.empresa.model.Administrador;
import com.joao.empresa.model.Gestor;
import com.joao.empresa.model.Tecnico;
import com.joao.empresa.model.Usuario;
import com.joao.empresa.services.GestaoUsuario;

import java.util.List;

public final class UsuarioMenu {

    private final GestaoUsuario gestaoUsuario;
    private final ConsoleInput input;

    public UsuarioMenu(GestaoUsuario gestaoUsuario, ConsoleInput input) {
        this.gestaoUsuario = gestaoUsuario;
        this.input = input;
    }

    public void executar() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleInput.titulo("USUÁRIOS");
            System.out.println("1 - Cadastrar usuário");
            System.out.println("2 - Listar usuários");
            System.out.println("3 - Buscar usuário por ID");
            System.out.println("4 - Atualizar usuário");
            System.out.println("5 - Remover usuário");
            System.out.println("0 - Voltar");

            switch (input.lerOpcao("Escolha uma opção: ", 0, 5)) {
                case 1 -> input.executarOperacao(this::cadastrar);
                case 2 -> input.executarOperacao(this::listar);
                case 3 -> input.executarOperacao(this::buscar);
                case 4 -> input.executarOperacao(this::atualizar);
                case 5 -> input.executarOperacao(this::remover);
                case 0 -> voltar = true;
                default -> throw new IllegalStateException("Opção inválida.");
            }
        }
    }

    private void cadastrar() {
        ConsoleInput.titulo("CADASTRAR USUÁRIO");
        Usuario.TipoUsuario tipo = lerTipoUsuario();
        String nome = input.lerTextoObrigatorio("Nome: ");
        String email = input.lerTextoObrigatorio("E-mail: ");

        gestaoUsuario.cadastrarUsuario(
                criarUsuario(tipo, null, nome, email, null)
        );
    }

    private void listar() {
        ConsoleInput.titulo("USUÁRIOS CADASTRADOS");
        List<Usuario> usuarios = gestaoUsuario.listarUsuarios();

        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
            return;
        }

        usuarios.forEach(this::imprimir);
    }

    private void buscar() {
        ConsoleInput.titulo("BUSCAR USUÁRIO");
        imprimir(gestaoUsuario.buscarPorId(input.lerId("ID do usuário: ")));
    }

    private void atualizar() {
        ConsoleInput.titulo("ATUALIZAR USUÁRIO");
        int id = input.lerId("ID do usuário: ");
        Usuario existente = gestaoUsuario.buscarPorId(id);

        imprimir(existente);
        System.out.println("Pressione ENTER para manter o valor atual.");

        String nome = ConsoleInput.manterSeVazio(
                input.lerTextoOpcional("Nome [" + existente.getNome() + "]: "),
                existente.getNome()
        );
        String email = ConsoleInput.manterSeVazio(
                input.lerTextoOpcional("E-mail [" + existente.getEmail() + "]: "),
                existente.getEmail()
        );

        gestaoUsuario.atualizarUsuario(
                criarUsuario(existente.getTipo(), id, nome, email, existente)
        );
    }

    private void remover() {
        ConsoleInput.titulo("REMOVER USUÁRIO");
        int id = input.lerId("ID do usuário: ");
        Usuario usuario = gestaoUsuario.buscarPorId(id);
        imprimir(usuario);

        if (input.confirmar("Confirma a remoção? (s/n): ")) {
            gestaoUsuario.removerUsuario(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private Usuario criarUsuario(
            Usuario.TipoUsuario tipo,
            Integer id,
            String nome,
            String email,
            Usuario existente
    ) {
        return switch (tipo) {
            case ADMINISTRADOR -> {
                String atual = existente instanceof Administrador adm
                        ? adm.getDepartamento()
                        : null;
                String departamento = lerCampoEspecifico("Departamento", atual);
                yield id == null
                        ? new Administrador(nome, email, departamento)
                        : new Administrador(id, nome, email, departamento);
            }
            case GESTOR -> {
                String atual = existente instanceof Gestor gestor
                        ? gestor.getAreaResponsavel()
                        : null;
                String area = lerCampoEspecifico("Área responsável", atual);
                yield id == null
                        ? new Gestor(nome, email, area)
                        : new Gestor(id, nome, email, area);
            }
            case TECNICO -> {
                String atual = existente instanceof Tecnico tecnico
                        ? tecnico.getEspecialidade()
                        : null;
                String especialidade = lerCampoEspecifico("Especialidade", atual);
                yield id == null
                        ? new Tecnico(nome, email, especialidade)
                        : new Tecnico(id, nome, email, especialidade);
            }
        };
    }

    private String lerCampoEspecifico(String campo, String atual) {
        if (atual == null) {
            return input.lerTextoObrigatorio(campo + ": ");
        }

        return ConsoleInput.manterSeVazio(
                input.lerTextoOpcional(campo + " [" + atual + "]: "),
                atual
        );
    }

    private Usuario.TipoUsuario lerTipoUsuario() {
        System.out.println("1 - Administrador");
        System.out.println("2 - Gestor");
        System.out.println("3 - Técnico");

        return switch (input.lerOpcao("Tipo: ", 1, 3)) {
            case 1 -> Usuario.TipoUsuario.ADMINISTRADOR;
            case 2 -> Usuario.TipoUsuario.GESTOR;
            case 3 -> Usuario.TipoUsuario.TECNICO;
            default -> throw new IllegalStateException("Tipo inválido.");
        };
    }

    private void imprimir(Usuario usuario) {
        String detalhe;

        if (usuario instanceof Administrador adm) {
            detalhe = "Departamento: " + adm.getDepartamento();
        } else if (usuario instanceof Gestor gestor) {
            detalhe = "Área: " + gestor.getAreaResponsavel();
        } else if (usuario instanceof Tecnico tecnico) {
            detalhe = "Especialidade: " + tecnico.getEspecialidade();
        } else {
            detalhe = "";
        }

        System.out.printf(
                "#%d | %s | %s | %s | %s%n",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTipo().getDescricao(),
                detalhe
        );
    }
}

