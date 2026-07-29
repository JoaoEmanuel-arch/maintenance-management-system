package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Equipamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipamentoDAO {

    public void salvar(Equipamento equipamento, int idEmpresa){

        String sql = """
            INSERT INTO equipamento
            (nome, codigo_patrimonio, data_aquisicao, empresa_id)
            VALUES (?, ?, ?, ?)
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getCodigoPatrimonio());
            stmt.setDate(3, Date.valueOf(equipamento.getDataAquisicao()));
            stmt.setInt(4, idEmpresa);

            stmt.executeUpdate();

            System.out.println("Equipamento salvo com sucesso");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar equipamento", e);
        }

    }

    public Equipamento buscarPorId(int id) {

        String sql = "SELECT * FROM equipamento WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()){

                String nome = rs.getString("nome");
                String codigoPatrimonio = rs.getString("codigo_patrimonio");
                Date dataAquisicao = rs.getDate("data_aquisicao");

                return new Equipamento(id, nome, codigoPatrimonio, dataAquisicao.toLocalDate());

            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar equipamento", e);
        }

        return null;
    }

    // busco os dados e crio os objetos. À cada objeto criado, eu o salvo na lista.
    public List<Equipamento> listar(){

        String sql = "SELECT * FROM equipamento";

        List<Equipamento> equipamentos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()){

            while (rs.next()){

                int id = rs.getInt("id"); // o id já foi gerado, já está lá no banco
                String nome = rs.getString("nome");
                String codigoPatrimonio = rs.getString("codigo_patrimonio");
                Date dataAquisicao = rs.getDate("data_aquisicao");

                equipamentos.add(new Equipamento(id, nome, codigoPatrimonio, dataAquisicao.toLocalDate()));

            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar equipamentos", e);
        }

        return equipamentos;
    }

    // Recebe o equipamento já alterado, pego as partes e jogo no update pra mudar dentro do banco
    public void atualizar(Equipamento equipamento){

        String sql = "UPDATE equipamento SET nome = ?, codigo_patrimonio = ?, data_aquisicao = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, equipamento.getNome());
            stmt.setString(2, equipamento.getCodigoPatrimonio());
            stmt.setDate(3, Date.valueOf(equipamento.getDataAquisicao()));
            stmt.setInt(4, equipamento.getId());

            stmt.executeUpdate();

            System.out.println("Equipamento atualizado!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar equipamento", e);
        }

    }

    public void deletar(int id){

        String sql = "DELETE FROM equipamento WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

            System.out.println("Equipamento deletado!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar equipamento", e);
        }

    }

}
