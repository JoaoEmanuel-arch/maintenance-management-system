package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Empresa;
import com.joao.empresa.model.Equipamento;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EmpresaDAO {

    public void salvar(Empresa empresa) {

        String sql = "INSERT INTO empresa (nome, cnpj, endereco, segmento, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEndereco());
            stmt.setString(4, empresa.getSegmento());
            stmt.setString(5, empresa.getStatus().name());

            stmt.executeUpdate();

            System.out.println("Empresa salva com sucesso!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar empresa", e);
        }
    }

    public Empresa buscarPorId(int id) {

        String sql = "SELECT * FROM empresa WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) { // pra fechar esse result set automaticamente depois

                if (rs.next()) {

                    String nome = rs.getString("nome");
                    String cnpj = rs.getString("cnpj");
                    String endereco = rs.getString("endereco");
                    String segmento = rs.getString("segmento");

                    Empresa.Status status = Empresa.Status.valueOf(
                            rs.getString("status")
                    );

                    Empresa empresa = new Empresa(id, nome, cnpj, endereco, segmento, status);

                    Set<Equipamento> equipamentos = buscarEquipamentosDaEmpresa(id);

                    for (Equipamento equipamento : equipamentos) {
                        empresa.adicionarEquipamento(equipamento);
                    }

                    return empresa;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar empresa", e);
        }

        return null;
    }

    // Busca todos os equipamentos que pertencem à empresa.
    private Set<Equipamento> buscarEquipamentosDaEmpresa(int empresaId) {

        String sql = "SELECT * FROM equipamento WHERE empresa_id = ?";

        Set<Equipamento> equipamentos = new HashSet<>(); // não deixa duplicado

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String codigoPatrimonio = rs.getString("codigo_patrimonio");
                    Date dataAquisicao = rs.getDate("data_aquisicao");

                    Equipamento equipamento = new Equipamento(id, nome, codigoPatrimonio, dataAquisicao.toLocalDate());

                    equipamentos.add(equipamento);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar equipamentos da empresa", e);
        }

        return equipamentos;
    }

    public List<Empresa> listar() {

        String sql = "SELECT * FROM empresa";

        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String cnpj = rs.getString("cnpj");
                String endereco = rs.getString("endereco");
                String segmento = rs.getString("segmento");

                Empresa.Status status = Empresa.Status.valueOf(
                        rs.getString("status")
                );

                Empresa empresa = new Empresa(id, nome, cnpj, endereco, segmento, status);

                Set<Equipamento> equipamentos = buscarEquipamentosDaEmpresa(id);

                for (Equipamento equipamento : equipamentos) {
                    empresa.adicionarEquipamento(equipamento);
                }

                empresas.add(empresa);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar empresas", e);
        }

        return empresas;
    }

    public void atualizar(Empresa empresa) {

        // 3 aspas é só pra poder pular linha na string
        String sql = """ 
                UPDATE empresa
                SET nome = ?, cnpj = ?, endereco = ?, segmento = ?, status = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEndereco());
            stmt.setString(4, empresa.getSegmento());
            stmt.setString(5, empresa.getStatus().name());
            stmt.setInt(6, empresa.getId());

            stmt.executeUpdate();

            System.out.println("Empresa atualizada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar empresa", e);
        }
    }

    public void deletar(int id) {

        String sql = "DELETE FROM empresa WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

            System.out.println("Empresa deletada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar empresa", e);
        }
    }

}
