package com.joao.empresa.app.console;

import com.joao.empresa.model.Empresa;
import com.joao.empresa.services.GestaoEmpresa;

import java.util.List;

public final class EmpresaMenu {

    private final GestaoEmpresa gestaoEmpresa;
    private final ConsoleInput input;

    public EmpresaMenu(GestaoEmpresa gestaoEmpresa, ConsoleInput input) {
        this.gestaoEmpresa = gestaoEmpresa;
        this.input = input;
    }

    public void executar() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleInput.titulo("EMPRESAS");
            System.out.println("1 - Cadastrar empresa");
            System.out.println("2 - Listar empresas");
            System.out.println("3 - Buscar empresa por ID");
            System.out.println("4 - Atualizar empresa");
            System.out.println("5 - Excluir empresa");
            System.out.println("0 - Voltar");

            switch (input.lerOpcao("Escolha uma opção: ", 0, 5)) {
                case 1 -> input.executarOperacao(this::cadastrar);
                case 2 -> input.executarOperacao(this::listar);
                case 3 -> input.executarOperacao(this::buscar);
                case 4 -> input.executarOperacao(this::atualizar);
                case 5 -> input.executarOperacao(this::excluir);
                case 0 -> voltar = true;
                default -> throw new IllegalStateException("Opção inválida.");
            }
        }
    }

    private void cadastrar() {
        ConsoleInput.titulo("CADASTRAR EMPRESA");

        gestaoEmpresa.cadastrarEmpresa(
                new Empresa(
                        input.lerTextoObrigatorio("Nome: "),
                        input.lerTextoObrigatorio("CNPJ: "),
                        input.lerTextoObrigatorio("Endereço: "),
                        input.lerTextoObrigatorio("Segmento: "),
                        lerStatus()
                )
        );
    }

    private void listar() {
        ConsoleInput.titulo("EMPRESAS CADASTRADAS");
        List<Empresa> empresas = gestaoEmpresa.listarEmpresas();

        if (empresas.isEmpty()) {
            System.out.println("Nenhuma empresa cadastrada.");
            return;
        }

        empresas.forEach(this::imprimir);
    }

    private void buscar() {
        ConsoleInput.titulo("BUSCAR EMPRESA");
        imprimir(gestaoEmpresa.buscarPorId(input.lerId("ID da empresa: ")));
    }

    private void atualizar() {
        ConsoleInput.titulo("ATUALIZAR EMPRESA");
        int id = input.lerId("ID da empresa: ");
        Empresa existente = gestaoEmpresa.buscarPorId(id);

        imprimir(existente);
        System.out.println("Pressione ENTER para manter o valor atual.");

        String nome = manter("Nome", existente.getNome());
        String cnpj = manter("CNPJ", existente.getCnpj());
        String endereco = manter("Endereço", existente.getEndereco());
        String segmento = manter("Segmento", existente.getSegmento());
        Empresa.Status status = lerStatusOpcional(existente.getStatus());

        gestaoEmpresa.atualizarEmpresa(
                new Empresa(id, nome, cnpj, endereco, segmento, status)
        );
    }

    private void excluir() {
        ConsoleInput.titulo("EXCLUIR EMPRESA");
        int id = input.lerId("ID da empresa: ");
        Empresa empresa = gestaoEmpresa.buscarPorId(id);
        imprimir(empresa);

        if (input.confirmar("Confirma a exclusão? (s/n): ")) {
            gestaoEmpresa.excluirEmpresa(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    public void listarResumido() {
        List<Empresa> empresas = gestaoEmpresa.listarEmpresas();

        if (empresas.isEmpty()) {
            throw new IllegalStateException(
                    "Cadastre uma empresa antes de cadastrar equipamentos."
            );
        }

        for (Empresa empresa : empresas) {
            System.out.printf(
                    "#%d - %s | CNPJ: %s%n",
                    empresa.getId(),
                    empresa.getNome(),
                    empresa.getCnpj()
            );
        }
    }

    private String manter(String campo, String atual) {
        return ConsoleInput.manterSeVazio(
                input.lerTextoOpcional(campo + " [" + atual + "]: "),
                atual
        );
    }

    private Empresa.Status lerStatus() {
        System.out.println("1 - Ativada");
        System.out.println("2 - Desativada");

        return input.lerOpcao("Status: ", 1, 2) == 1
                ? Empresa.Status.ATIVADA
                : Empresa.Status.DESATIVADA;
    }

    private Empresa.Status lerStatusOpcional(Empresa.Status atual) {
        System.out.println("Status atual: " + atual.getDescricao());

        return input.confirmar("Deseja alterar o status? (s/n): ")
                ? lerStatus()
                : atual;
    }

    private void imprimir(Empresa empresa) {
        System.out.printf(
                "#%d | %s | CNPJ: %s | %s | %s | %s%n",
                empresa.getId(),
                empresa.getNome(),
                empresa.getCnpj(),
                empresa.getEndereco(),
                empresa.getSegmento(),
                empresa.getStatus().getDescricao()
        );
    }
}

