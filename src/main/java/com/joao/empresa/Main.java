package com.joao.empresa;

import com.joao.empresa.app.ConsoleApplication;
import com.joao.empresa.app.console.ConsoleInput;
import com.joao.empresa.app.console.EmpresaMenu;
import com.joao.empresa.app.console.EquipamentoMenu;
import com.joao.empresa.app.console.ManutencaoMenu;
import com.joao.empresa.app.console.UsuarioMenu;
import com.joao.empresa.dao.EmpresaDAO;
import com.joao.empresa.dao.EquipamentoDAO;
import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.dao.UsuarioDAO;
import com.joao.empresa.services.GestaoEmpresa;
import com.joao.empresa.services.GestaoEquipamento;
import com.joao.empresa.services.GestaoManutencao;
import com.joao.empresa.services.GestaoUsuario;

import java.util.Scanner;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        EmpresaDAO empresaDAO = new EmpresaDAO();
        EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
        ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

        GestaoUsuario gestaoUsuario = new GestaoUsuario(usuarioDAO);
        GestaoEmpresa gestaoEmpresa = new GestaoEmpresa(empresaDAO);
        GestaoEquipamento gestaoEquipamento = new GestaoEquipamento(
                equipamentoDAO,
                manutencaoDAO
        );
        GestaoManutencao gestaoManutencao = new GestaoManutencao(manutencaoDAO);

        ConsoleInput input = new ConsoleInput(new Scanner(System.in));

        UsuarioMenu usuarioMenu = new UsuarioMenu(gestaoUsuario, input);
        EmpresaMenu empresaMenu = new EmpresaMenu(gestaoEmpresa, input);
        EquipamentoMenu equipamentoMenu = new EquipamentoMenu(
                gestaoEquipamento,
                gestaoEmpresa,
                input
        );
        ManutencaoMenu manutencaoMenu = new ManutencaoMenu(
                gestaoManutencao,
                gestaoEquipamento,
                gestaoUsuario,
                input
        );

        ConsoleApplication application = new ConsoleApplication(
                input,
                usuarioMenu,
                empresaMenu,
                equipamentoMenu,
                manutencaoMenu
        );

        application.executar();
    }
}
