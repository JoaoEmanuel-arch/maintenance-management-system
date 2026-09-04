package com.joao.empresa.app;

import com.joao.empresa.app.console.ConsoleInput;
import com.joao.empresa.app.console.EmpresaMenu;
import com.joao.empresa.app.console.EquipamentoMenu;
import com.joao.empresa.app.console.ManutencaoMenu;
import com.joao.empresa.app.console.UsuarioMenu;

public final class ConsoleApplication {

    private final ConsoleInput input;
    private final UsuarioMenu usuarioMenu;
    private final EmpresaMenu empresaMenu;
    private final EquipamentoMenu equipamentoMenu;
    private final ManutencaoMenu manutencaoMenu;

    public ConsoleApplication(
            ConsoleInput input,
            UsuarioMenu usuarioMenu,
            EmpresaMenu empresaMenu,
            EquipamentoMenu equipamentoMenu,
            ManutencaoMenu manutencaoMenu
    ) {
        this.input = input;
        this.usuarioMenu = usuarioMenu;
        this.empresaMenu = empresaMenu;
        this.equipamentoMenu = equipamentoMenu;
        this.manutencaoMenu = manutencaoMenu;
    }

    public void executar() {
        cabecalho();

        boolean executando = true;

        while (executando) {
            menuPrincipal();

            switch (input.lerOpcao("Escolha uma opção: ", 0, 4)) {
                case 1 -> usuarioMenu.executar();
                case 2 -> empresaMenu.executar();
                case 3 -> equipamentoMenu.executar();
                case 4 -> manutencaoMenu.executar();
                case 0 -> executando = false;
                default -> throw new IllegalStateException("Opção inválida.");
            }
        }

        System.out.println();
        System.out.println("Sistema encerrado.");
    }

    private void cabecalho() {
        System.out.println("=".repeat(72));
        System.out.println("        SISTEMA DE GERENCIAMENTO DE MANUTENÇÕES - V1 JDBC");
        System.out.println("=".repeat(72));
    }

    private void menuPrincipal() {
        ConsoleInput.titulo("MENU PRINCIPAL");
        System.out.println("1 - Usuários");
        System.out.println("2 - Empresas");
        System.out.println("3 - Equipamentos");
        System.out.println("4 - Manutenções");
        System.out.println("0 - Sair");
    }
}

