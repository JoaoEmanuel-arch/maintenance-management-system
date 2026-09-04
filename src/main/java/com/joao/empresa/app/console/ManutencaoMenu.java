package com.joao.empresa.app.console;

import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Tecnico;
import com.joao.empresa.model.Usuario;
import com.joao.empresa.services.GestaoEquipamento;
import com.joao.empresa.services.GestaoManutencao;
import com.joao.empresa.services.GestaoUsuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class ManutencaoMenu {

    private final GestaoManutencao gestaoManutencao;
    private final GestaoEquipamento gestaoEquipamento;
    private final GestaoUsuario gestaoUsuario;
    private final ConsoleInput input;

    public ManutencaoMenu(
            GestaoManutencao gestaoManutencao,
            GestaoEquipamento gestaoEquipamento,
            GestaoUsuario gestaoUsuario,
            ConsoleInput input
    ) {
        this.gestaoManutencao = gestaoManutencao;
        this.gestaoEquipamento = gestaoEquipamento;
        this.gestaoUsuario = gestaoUsuario;
        this.input = input;
    }

    public void executar() {
        boolean voltar = false;

        while (!voltar) {
            ConsoleInput.titulo("MANUTENÇÕES");
            System.out.println("1 - Cadastrar manutenção");
            System.out.println("2 - Listar todas");
            System.out.println("3 - Listar em andamento");
            System.out.println("4 - Listar concluídas");
            System.out.println("5 - Listar canceladas");
            System.out.println("6 - Buscar por ID");
            System.out.println("7 - Atualizar manutenção em andamento");
            System.out.println("8 - Finalizar manutenção");
            System.out.println("9 - Cancelar manutenção");
            System.out.println("10 - Excluir manutenção finalizada/cancelada");
            System.out.println("0 - Voltar");

            switch (input.lerOpcao("Escolha uma opção: ", 0, 10)) {
                case 1 -> input.executarOperacao(this::cadastrar);
                case 2 -> input.executarOperacao(() -> listar(
                        gestaoManutencao.listarTodasManutencoes(),
                        "TODAS AS MANUTENÇÕES"
                ));
                case 3 -> input.executarOperacao(() -> listar(
                        gestaoManutencao.listarManutencoesAtivas(),
                        "MANUTENÇÕES EM ANDAMENTO"
                ));
                case 4 -> input.executarOperacao(() -> listar(
                        gestaoManutencao.listarManutencoesConcluidas(),
                        "MANUTENÇÕES CONCLUÍDAS"
                ));
                case 5 -> input.executarOperacao(() -> listar(
                        gestaoManutencao.listarManutencoesCanceladas(),
                        "MANUTENÇÕES CANCELADAS"
                ));
                case 6 -> input.executarOperacao(this::buscar);
                case 7 -> input.executarOperacao(this::atualizar);
                case 8 -> input.executarOperacao(this::finalizar);
                case 9 -> input.executarOperacao(this::cancelar);
                case 10 -> input.executarOperacao(this::excluir);
                case 0 -> voltar = true;
                default -> throw new IllegalStateException("Opção inválida.");
            }
        }
    }

    private void cadastrar() {
        ConsoleInput.titulo("CADASTRAR MANUTENÇÃO");

        Manutencao manutencao = new Manutencao(
                lerTipo(),
                input.lerTextoObrigatorio("Descrição: "),
                input.lerData("Data de início (dd/MM/yyyy): "),
                selecionarEquipamento(),
                selecionarTecnico()
        );

        gestaoManutencao.cadastrarManutencao(manutencao);
    }

    private void buscar() {
        ConsoleInput.titulo("BUSCAR MANUTENÇÃO");
        imprimir(gestaoManutencao.buscarPorId(input.lerId("ID da manutenção: ")));
    }

    private void atualizar() {
        ConsoleInput.titulo("ATUALIZAR MANUTENÇÃO");
        int id = input.lerId("ID da manutenção: ");
        Manutencao existente = gestaoManutencao.buscarAtivasPorId(id);

        imprimir(existente);
        System.out.println("Pressione ENTER para manter textos e data atuais.");

        Manutencao.TipoManutencao tipo = lerTipoOpcional(existente.getTipoManutencao());
        String descricao = ConsoleInput.manterSeVazio(
                input.lerTextoOpcional("Descrição [" + existente.getDescricao() + "]: "),
                existente.getDescricao()
        );
        LocalDate dataInicio = input.lerDataOpcional(
                "Data de início [" + ConsoleInput.formatarData(existente.getDataInicio()) + "]: ",
                existente.getDataInicio()
        );
        Equipamento equipamento = selecionarEquipamentoOpcional(existente.getEquipamento());
        Tecnico tecnico = selecionarTecnicoOpcional(existente.getTecnicoResponsavel());

        Manutencao alterada = new Manutencao(
                existente.getId(),
                tipo,
                descricao,
                existente.getCusto(),
                dataInicio,
                existente.getDataFim(),
                existente.getStatus(),
                equipamento,
                tecnico
        );

        gestaoManutencao.atualizarManutencao(alterada);
    }

    private void finalizar() {
        ConsoleInput.titulo("FINALIZAR MANUTENÇÃO");
        int id = input.lerId("ID da manutenção: ");
        Manutencao manutencao = gestaoManutencao.buscarAtivasPorId(id);
        imprimir(manutencao);

        BigDecimal custo = input.lerBigDecimal("Custo final: R$ ");

        if (input.confirmar("Confirma a finalização? (s/n): ")) {
            gestaoManutencao.finalizarManutencao(id, custo);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void cancelar() {
        ConsoleInput.titulo("CANCELAR MANUTENÇÃO");
        int id = input.lerId("ID da manutenção: ");
        Manutencao manutencao = gestaoManutencao.buscarAtivasPorId(id);
        imprimir(manutencao);

        if (input.confirmar("Confirma o cancelamento? (s/n): ")) {
            gestaoManutencao.cancelarManutencao(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void excluir() {
        ConsoleInput.titulo("EXCLUIR MANUTENÇÃO");
        int id = input.lerId("ID da manutenção: ");
        Manutencao manutencao = gestaoManutencao.buscarFinalizadasPorId(id);
        imprimir(manutencao);

        if (input.confirmar("Confirma a exclusão definitiva? (s/n): ")) {
            gestaoManutencao.excluirManutencao(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void listar(List<Manutencao> manutencoes, String titulo) {
        ConsoleInput.titulo(titulo);

        if (manutencoes.isEmpty()) {
            System.out.println("Nenhuma manutenção encontrada.");
            return;
        }

        manutencoes.forEach(this::imprimir);
    }

    private Equipamento selecionarEquipamento() {
        System.out.println();
        System.out.println("Equipamentos disponíveis:");
        listarEquipamentosResumido();
        return gestaoEquipamento.buscarPorId(input.lerId("ID do equipamento: "));
    }

    private Equipamento selecionarEquipamentoOpcional(Equipamento atual) {
        System.out.printf("Equipamento atual: #%d - %s%n", atual.getId(), atual.getNome());
        return input.confirmar("Deseja alterar o equipamento? (s/n): ")
                ? selecionarEquipamento()
                : atual;
    }

    private Tecnico selecionarTecnico() {
        System.out.println();
        System.out.println("Técnicos disponíveis:");
        listarTecnicosResumido();

        int id = input.lerId("ID do técnico: ");
        Usuario usuario = gestaoUsuario.buscarPorId(id);

        if (!(usuario instanceof Tecnico tecnico)) {
            throw new IllegalArgumentException(
                    "O usuário de ID " + id + " não é um técnico."
            );
        }

        return tecnico;
    }

    private Tecnico selecionarTecnicoOpcional(Tecnico atual) {
        System.out.printf("Técnico atual: #%d - %s%n", atual.getId(), atual.getNome());
        return input.confirmar("Deseja alterar o técnico? (s/n): ")
                ? selecionarTecnico()
                : atual;
    }

    private void listarEquipamentosResumido() {
        List<Equipamento> equipamentos = gestaoEquipamento.listarEquipamentos();

        if (equipamentos.isEmpty()) {
            throw new IllegalStateException(
                    "Cadastre um equipamento antes de cadastrar manutenções."
            );
        }

        equipamentos.forEach(equipamento -> System.out.printf(
                "#%d - %s | Patrimônio: %s%n",
                equipamento.getId(),
                equipamento.getNome(),
                equipamento.getCodigoPatrimonio()
        ));
    }

    private void listarTecnicosResumido() {
        List<Tecnico> tecnicos = gestaoUsuario.listarUsuarios()
                .stream()
                .filter(Tecnico.class::isInstance)
                .map(Tecnico.class::cast)
                .toList();

        if (tecnicos.isEmpty()) {
            throw new IllegalStateException(
                    "Cadastre um técnico antes de cadastrar manutenções."
            );
        }

        tecnicos.forEach(tecnico -> System.out.printf(
                "#%d - %s | Especialidade: %s%n",
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getEspecialidade()
        ));
    }

    private Manutencao.TipoManutencao lerTipo() {
        System.out.println("1 - Preventiva");
        System.out.println("2 - Corretiva");

        return input.lerOpcao("Tipo: ", 1, 2) == 1
                ? Manutencao.TipoManutencao.PREVENTIVA
                : Manutencao.TipoManutencao.CORRETIVA;
    }

    private Manutencao.TipoManutencao lerTipoOpcional(Manutencao.TipoManutencao atual) {
        System.out.println("Tipo atual: " + atual.getDescricao());
        return input.confirmar("Deseja alterar o tipo? (s/n): ")
                ? lerTipo()
                : atual;
    }

    private void imprimir(Manutencao manutencao) {
        System.out.println("-".repeat(72));
        System.out.printf(
                "#%d | %s | %s%n",
                manutencao.getId(),
                manutencao.getTipoManutencao().getDescricao(),
                manutencao.getStatus().getDescricao()
        );
        System.out.println("Descrição: " + manutencao.getDescricao());
        System.out.println("Início: " + ConsoleInput.formatarData(manutencao.getDataInicio()));
        System.out.println("Fim: " + ConsoleInput.formatarData(manutencao.getDataFim()));
        System.out.println("Custo: R$ " + manutencao.getCusto());
        System.out.printf(
                "Equipamento: #%d - %s%n",
                manutencao.getEquipamento().getId(),
                manutencao.getEquipamento().getNome()
        );
        System.out.printf(
                "Técnico: #%d - %s%n",
                manutencao.getTecnicoResponsavel().getId(),
                manutencao.getTecnicoResponsavel().getNome()
        );
    }
}

