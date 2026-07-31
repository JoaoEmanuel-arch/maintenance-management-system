package com.joao.empresa.services;

import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.exceptions.ManutencaoNaoEncontradaException;
import com.joao.empresa.model.Manutencao;
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

        Manutencao manutencao = manutencaoDAO.buscarPorId(id);

        if (manutencao.getStatus() != Manutencao.Status.ANDAMENTO){ // esta comparando somente com a String retornada
            throw new ManutencaoNaoEncontradaException(
                    "Não existe manutenção ativa com ID " + id + "."
            );
        }

        return manutencao;
    }

    public Manutencao buscarFinalizadasPorId(int id){

        Manutencao manutencao = manutencaoDAO.buscarPorId(id);

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
        manutencaoDAO.salvar(manutencao);
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

        buscarAtivasPorId(alterada.getId()); //lança exceção
        manutencaoDAO.atualizar(alterada);
    }

    public void cancelarManutencao(int id){ // remove das manutenções ativas
        Manutencao manutencao = buscarAtivasPorId(id);
        manutencao.setStatus(Manutencao.Status.CANCELADA); // altero localmente assim
        manutencao.setDataFim(LocalDate.now());
        manutencaoDAO.atualizar(manutencao); // mando pro banco atualizar lá o novo status e data
    }

    public void finalizarManutencao(int id, double custo) { // encerra ativa e joga pra finalizadas
        Manutencao manutencao = buscarAtivasPorId(id);
        manutencao.setStatus(Manutencao.Status.CONCLUIDA);
        manutencao.setDataFim(LocalDate.now());
        manutencao.setCusto(custo);
        manutencaoDAO.atualizar(manutencao);
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