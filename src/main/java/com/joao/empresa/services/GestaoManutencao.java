package com.joao.empresa.services;

import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import com.joao.empresa.model.Manutencao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GestaoManutencao {

    ManutencaoDAO manutencaoDAO = new ManutencaoDAO();

    public Manutencao buscarPorId(int id) {

        Manutencao manutencao = manutencaoDAO.buscarPorId(id);

        if (manutencao == null) {
            throw new ManutencaoNaoEncontradaException("Manutenção com ID " + id + " não encontrada.");
        }

        return manutencao;
    }

    public Manutencao buscarAtivasPorId(int id) {

        Manutencao manutencao = buscarPorId(id); // o próprio método já lança e exceção correta

        if (manutencao.getStatus() != Manutencao.Status.ANDAMENTO) {
            throw new ManutencaoNaoEncontradaException(
                    "Não existe manutenção ativa com ID " + id + "."
            );
        }

        return manutencao;
    }

    public Manutencao buscarFinalizadasPorId(int id){

        Manutencao manutencao = buscarPorId(id);

        if (manutencao.getStatus() == Manutencao.Status.ANDAMENTO){ // não finalizou, está em andamento
            throw new ManutencaoNaoEncontradaException(
                    "Não existe manutenção finalizada com ID " + id + "."
            );
        }

        return manutencao;
    }

    // o próprio banco não deixa cadastrar duplicado. Ele lança erro, assim, justamente para não
    // aparecer aquele erro feio na cara do usuário, a gente trata esse erro sem quebrar o programa
    public void cadastrarManutencao(Manutencao manutencao) {

        if (manutencao == null) {
            throw new IllegalArgumentException(
                    "A manutenção a ser cadastrada não pode ser nula."
            );
        }

    }

    public List<Manutencao> listarTodasManutencoes() {
        return manutencaoDAO.listar();
    }

    public List<Manutencao> listarManutencoesAtivas() {
        return manutencaoDAO.listarPorStatus(Manutencao.Status.ANDAMENTO);
    }

    public List<Manutencao> listarManutencoesConcluidas() {
        return manutencaoDAO.listarPorStatus(Manutencao.Status.CONCLUIDA);
    }

    public List<Manutencao> listarManutencoesCanceladas() {
        return manutencaoDAO.listarPorStatus(Manutencao.Status.CANCELADA);
    }

    public void atualizarManutencao(Manutencao alterada){

        if (alterada.getId() == null) {
            throw new IllegalArgumentException(
                    "Não é possível atualizar uma manutenção sem ID."
            );
        }

        Manutencao existente = buscarAtivasPorId(alterada.getId()); //lança exceção

        // os métodos atualizar um objeto fica dentro do próprio objeto (equivale ao setter),
        // mas altera tudo de uma vez dentro do objeto, já fazendo a verificação
        existente.atualizarDados(
                alterada.getTipoManutencao(),
                alterada.getDescricao(),
                alterada.getDataInicio(),
                alterada.getEquipamento(),
                alterada.getTecnicoResponsavel()
        );

        manutencaoDAO.atualizar(alterada);
    }

    public void cancelarManutencao(int id){ // remove das manutenções ativas
        Manutencao manutencao = buscarAtivasPorId(id);

        manutencao.cancelar(LocalDate.now()); // lá dentro do objeto ele mesmo valida

        manutencaoDAO.atualizar(manutencao); // mando pro banco atualizar lá o novo status e data
    }

    // pra finalizar passa o custo e a data final
    public void finalizarManutencao(int id, BigDecimal custo) { // encerra ativa e joga pra finalizadas
        Manutencao manutencao = buscarAtivasPorId(id);

        manutencao.finalizar(custo, LocalDate.now());

        manutencaoDAO.atualizar(manutencao); // DAO é pra mexer com banco de dados
    }

    public void excluirManutencao(int id) { // excluir do sistema (finalizadas)

        Manutencao manutencao = buscarPorId(id);

        if (manutencao.getStatus() == Manutencao.Status.ANDAMENTO) {
            throw new IllegalStateException(
                    "Não é possível excluir uma manutenção em andamento."
            );
        }

        manutencaoDAO.deletar(id);
    }

}