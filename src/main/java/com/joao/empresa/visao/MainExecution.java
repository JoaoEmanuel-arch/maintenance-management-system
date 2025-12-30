package main.java.com.joao.empresa.visao;

import main.java.com.joao.empresa.model.*;
import main.java.com.joao.empresa.services.GestaoEmpresa;
import main.java.com.joao.empresa.services.GestaoEquipamento;
import main.java.com.joao.empresa.services.GestaoManutencao;
import main.java.com.joao.empresa.services.GestaoUsuario;

import java.time.LocalDate;

public class MainExecution {

    public static void main(String[] args) {

        System.out.println("SISTEMA DE GERENCIAMENTO DE MANUTENÇÕES");
        System.out.println("-------------------------------------------------");

        //instancia os cruds pra eu poder utilizar os métodos
        GestaoUsuario gestaoUsuario = new GestaoUsuario();
        GestaoManutencao gestaoManutencao = new GestaoManutencao();
        GestaoEmpresa gestaoEmpresa = new GestaoEmpresa();
        GestaoEquipamento gestaoEquipamento = new GestaoEquipamento(gestaoManutencao);

        Administrador adm1 = new Administrador(
                1, "João Emanuel", "joao@gmail.com", "jk5654", "DCCOMP"
        );

        gestaoUsuario.cadastrarUsuario(adm1);

        Administrador adm2 = new Administrador(
                2, "Elverton Fazzion", "fazzion@gmail.com", "jk5dd", "DCCOMP"
        );

        gestaoUsuario.cadastrarUsuario(adm2);

        Gestor gestor1 = new Gestor(
                3, "Raimundo", "rai@gmail.com", "galodoido", "Laminações"
        );

        gestaoUsuario.cadastrarUsuario(gestor1);

        Gestor gestor2 = new Gestor(
                4, "Solange", "sol@gmail.com", "galodoido", "Laminações"
        );

        gestaoUsuario.cadastrarUsuario(gestor2);

        Tecnico tec1 = new Tecnico(
                5, "Alexandre", "ale7879", "uieru", "Operador de ponte", "UFSJ"
        );

        gestaoUsuario.cadastrarUsuario(tec1);

        Tecnico tec2 = new Tecnico(
                6, "Leandro", "ale7879", "uieru", "Operador de ponte", "UFSJ"
        );

        gestaoUsuario.cadastrarUsuario(tec2);

        Empresa emp1 = new Empresa(
                7, "GERDAU AÇOMINAS", "gerdau@gmail.com", "Ouro Branco", "Produtora de aço"
        );

        gestaoEmpresa.cadastrarEmpresa(emp1);

        Empresa emp2 = new Empresa(
                8, "VALE", "gerdau@gmail.com", "Ouro Branco", "Produtora de aço"
        );

        gestaoEmpresa.cadastrarEmpresa(emp2);

        Equipamento eqp1 = new Equipamento(
                9, "Laminadora", "98390955#", LocalDate.of(2019, 11, 23)
        );

        gestaoEquipamento.cadastrarEquipamento(eqp1);

        Equipamento eqp2 = new Equipamento(
                10, "Cortadora", "98390955#", LocalDate.of(2019, 11, 23)
        );

        gestaoEquipamento.cadastrarEquipamento(eqp2);

        Manutencao mnt1 = new Manutencao(
                11, Manutencao.TipoManutencao.CORRETIVA, LocalDate.of(2025, 12, 29),
                "A base da laminadora quebrou, precisa de manutenção urgente, a base foi junto com a peça",
                (Tecnico) gestaoUsuario.buscarPorId(5), gestaoEquipamento.buscarPorId(9)
        );

        Manutencao mnt2 = new Manutencao(
                12, Manutencao.TipoManutencao.PREVENTIVA, LocalDate.of(2025, 12, 29),
                "A base da laminadora pode quebrar, precisa de manutenção urgente, a base foi junto com a peça",
                (Tecnico) gestaoUsuario.buscarPorId(6), gestaoEquipamento.buscarPorId(10)
        );

    }

}
