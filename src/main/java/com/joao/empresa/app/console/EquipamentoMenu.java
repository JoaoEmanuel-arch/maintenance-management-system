package com.joao.empresa.app.console;

import com.joao.empresa.model.Equipamento;
import com.joao.empresa.services.GestaoEmpresa;
import com.joao.empresa.services.GestaoEquipamento;

import java.time.LocalDate;
import java.util.List;

public final class EquipamentoMenu {

    private final GestaoEquipamento gestaoEquipamento;
    private final GestaoEmpresa gestaoEmpresa;
    private final ConsoleInput input;

    public EquipamentoMenu(
            GestaoEquipamento gestaoEquipamento,
            GestaoEmpresa gestaoEmpresa,
            ConsoleInput input
    ) {
        this.gestaoEquipamento = gestaoEquipamento;
        this.gestaoEmpresa = gestaoEmpresa;
        this.input = input;
    }

    public void executar() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleInput.titulo("EQUIPAMENTOS");
            System.out.println("1 - Cadastrar equipamento");
            System.out.println("2 - Listar equipamentos");
            System.out.println("3 - Buscar equipamento por ID");
            System.out.println("4 - Atualizar equipamento");
            System.out.println("5 - Excluir equipamento");
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
        ConsoleInput.titulo("CADASTRAR EQUIPAMENTO");

        String nome = input.lerTextoObrigatorio("Nome: ");
        String patrimonio = input.lerTextoObrigatorio("Código de patrimônio: ");
        LocalDate data = input.lerData("Data de aquisição (dd/MM/yyyy): ");

        System.out.println();
        System.out.println("Empresas disponíveis:");
        listarEmpresasResumido();
        int idEmpresa = input.lerId("ID da empresa proprietária: ");

        gestaoEquipamento.cadastrarEquipamento(
                new Equipamento(nome, patrimonio, data),
                idEmpresa
        );
    }

    private void listar() {
        ConsoleInput.titulo("EQUIPAMENTOS CADASTRADOS");
        List<Equipamento> equipamentos = gestaoEquipamento.listarEquipamentos();

        if (equipamentos.isEmpty()) {
            System.out.println("Nenhum equipamento cadastrado.");
            return;
        }

        equipamentos.forEach(this::imprimir);
    }

    private void buscar() {
        ConsoleInput.titulo("BUSCAR EQUIPAMENTO");
        imprimir(gestaoEquipamento.buscarPorId(input.lerId("ID do equipamento: ")));
    }

    private void atualizar() {
        ConsoleInput.titulo("ATUALIZAR EQUIPAMENTO");
        int id = input.lerId("ID do equipamento: ");
        Equipamento existente = gestaoEquipamento.buscarPorId(id);

        imprimir(existente);
        System.out.println("Pressione ENTER para manter o valor atual.");

        String nome = ConsoleInput.manterSeVazio(
                input.lerTextoOpcional("Nome [" + existente.getNome() + "]: "),
                existente.getNome()
        );
        String patrimonio = ConsoleInput.manterSeVazio(
                input.lerTextoOpcional(
                        "Patrimônio [" + existente.getCodigoPatrimonio() + "]: "
                ),
                existente.getCodigoPatrimonio()
        );
        LocalDate data = input.lerDataOpcional(
                "Data de aquisição [" + ConsoleInput.formatarData(existente.getDataAquisicao()) + "]: ",
                existente.getDataAquisicao()
        );

        gestaoEquipamento.atualizarEquipamento(
                new Equipamento(id, nome, patrimonio, data)
        );
    }

    private void excluir() {
        ConsoleInput.titulo("EXCLUIR EQUIPAMENTO");
        int id = input.lerId("ID do equipamento: ");
        Equipamento equipamento = gestaoEquipamento.buscarPorId(id);
        imprimir(equipamento);

        if (input.confirmar("Confirma a exclusão? (s/n): ")) {
            gestaoEquipamento.excluirEquipamento(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    public void listarResumido() {
        List<Equipamento> equipamentos = gestaoEquipamento.listarEquipamentos();

        if (equipamentos.isEmpty()) {
            throw new IllegalStateException(
                    "Cadastre um equipamento antes de cadastrar manutenções."
            );
        }

        for (Equipamento equipamento : equipamentos) {
            System.out.printf(
                    "#%d - %s | Patrimônio: %s%n",
                    equipamento.getId(),
                    equipamento.getNome(),
                    equipamento.getCodigoPatrimonio()
            );
        }
    }

    private void listarEmpresasResumido() {
        var empresas = gestaoEmpresa.listarEmpresas();

        if (empresas.isEmpty()) {
            throw new IllegalStateException(
                    "Cadastre uma empresa antes de cadastrar equipamentos."
            );
        }

        empresas.forEach(empresa -> System.out.printf(
                "#%d - %s | CNPJ: %s%n",
                empresa.getId(),
                empresa.getNome(),
                empresa.getCnpj()
        ));
    }

    private void imprimir(Equipamento equipamento) {
        System.out.printf(
                "#%d | %s | Patrimônio: %s | Aquisição: %s%n",
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getCodigoPatrimonio(),
                ConsoleInput.formatarData(equipamento.getDataAquisicao())
        );
    }
}

