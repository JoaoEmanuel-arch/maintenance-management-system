package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Tecnico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoDAO {

    private final EquipamentoDAO equipamentoDAO = new EquipamentoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void salvar(Manutencao manutencao) {

        if (manutencao.getEquipamento().getId() == null) {
            throw new IllegalArgumentException("O equipamento precisa estar salvo.");
        }

        if (manutencao.getTecnicoResponsavel().getId() == null) {
            throw new IllegalArgumentException("O técnico precisa estar salvo.");
        }

        String sql = """
            INSERT INTO manutencao
            (tipo_manutencao, data_inicio, data_fim, descricao,
             custo, status, equipamento_id, tecnico_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            stmt.setString(1, manutencao.getTipoManutencao().name());
            stmt.setDate(2, Date.valueOf(manutencao.getDataInicio()));

            // uma manutenção em andamento pode possui uma data final nula ainda
            if (manutencao.getDataFim() != null) {
                stmt.setDate(3, Date.valueOf(manutencao.getDataFim())
                ); // se não for nula coloca a data na tabela
            } else {
                stmt.setNull(3, Types.DATE); // coloca um valor nulo em um parâmetro do tipo data
            }

            stmt.setString(4, manutencao.getDescricao());
            stmt.setDouble(5, manutencao.getCusto());
            stmt.setString(6, manutencao.getStatus().name());
            stmt.setInt(7, manutencao.getEquipamento().getId());
            stmt.setInt(8, manutencao.getTecnicoResponsavel().getId());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Nenhuma manutenção foi inserida.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new SQLException(
                            "O banco não retornou o ID da manutenção."
                    );
                }

                manutencao.definirId(generatedKeys.getInt(1));
            }

            System.out.println("Manutenção salva com ID " + manutencao.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar manutenção", e);
        }
    }

    public Manutencao buscarPorId(int id){

        String sql = "SELECT * FROM manutencao WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1, id);

            // aqui está dentro do try pra ser fechado automaticamente depois
            try(ResultSet rs = stmt.executeQuery()){

                // à cada tupla do resultado eu chamo a função. Só vai ter uma pq é id
                if(rs.next()){
                    return construirManutencao(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar manutenção", e);
        }

        return null;
    }

    // transforma uma linha do resultado em um objeto Manutencao
    private Manutencao construirManutencao(ResultSet rs) throws SQLException {

        int id = rs.getInt("id");

        Manutencao.TipoManutencao tipo =
                Manutencao.TipoManutencao.valueOf(
                        rs.getString("tipo_manutencao")
                ); // o JDBC retorna o valor do ENUM como uma String

        String descricao = rs.getString("descricao");
        double custo = rs.getDouble("custo");
        Date dataInicio = rs.getDate("data_inicio");
        Date dataFim = rs.getDate("data_fim");

        Manutencao.Status status =
                Manutencao.Status.valueOf(
                        rs.getString("status")
                );

        int equipamentoId = rs.getInt("equipamento_id");

        int tecnicoId = rs.getInt("tecnico_id");

        Equipamento equipamento = equipamentoDAO.buscarPorId(equipamentoId);

        Tecnico tecnicoResponsavel = (Tecnico) usuarioDAO.buscarPorId(tecnicoId);

        Manutencao manutencao = new Manutencao(
                id,
                tipo,
                descricao,
                custo,
                dataInicio.toLocalDate(),
                dataFim.toLocalDate(),
                status,
                equipamento,
                tecnicoResponsavel
        );

        return manutencao;
    }

    // manutenções associadas a um equipamento
    public List<Manutencao> buscarPorEquipamento(int equipamentoId) {

        String sql = """
                SELECT * FROM manutencao
                WHERE equipamento_id = ?
                """;

        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipamentoId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    manutencoes.add(construirManutencao(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar manutenções do equipamento", e);
        }

        return manutencoes;
    }

    // manutenções sob responsabilidade de um técnico específico
    public List<Manutencao> buscarPorTecnico(int tecnicoId) {

        String sql = """
                SELECT * FROM manutencao
                WHERE tecnico_id = ?
                """;

        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tecnicoId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    manutencoes.add(construirManutencao(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar manutenções do técnico", e);
        }

        return manutencoes;
    }

    public List<Manutencao> listar() {

        String sql = "SELECT * FROM manutencao";

        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                manutencoes.add(construirManutencao(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar manutenções", e);
        }

        return manutencoes;
    }

    // listar manuntencoes ativar ou finalizadas ou canceladas
    public List<Manutencao> listarPorStatus(Manutencao.Status status) {

        String sql = "SELECT * FROM manutencao WHERE status = ?";

        List<Manutencao> manutencoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    manutencoes.add(construirManutencao(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar manutenções por status", e);
        }

        return manutencoes;
    }

    public void atualizar(Manutencao manutencao) {

        String sql = """
                UPDATE manutencao
                SET tipo_manutencao = ?,
                    data_inicio = ?,
                    data_fim = ?,
                    descricao = ?,
                    custo = ?,
                    status = ?,
                    equipamento_id = ?,
                    tecnico_id = ?
                WHERE id = ?
                """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, manutencao.getTipoManutencao().name());

            stmt.setDate(2, Date.valueOf(manutencao.getDataInicio()));

            if (manutencao.getDataFim() != null) {
                stmt.setDate(
                        3,
                        Date.valueOf(manutencao.getDataFim())
                );
            } else {
                stmt.setNull(3, Types.DATE);
            }

            stmt.setString(4, manutencao.getDescricao());
            stmt.setDouble(5, manutencao.getCusto());
            stmt.setString(6, manutencao.getStatus().name());

            stmt.setInt(7, manutencao.getEquipamento().getId());

            stmt.setInt(8, manutencao.getTecnicoResponsavel().getId());

            stmt.setInt(9, manutencao.getId());

            stmt.executeUpdate();

            System.out.println("Manutenção atualizada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar manutenção", e);
        }
    }

    public void deletar(int id) {

        String sql = "DELETE FROM manutencao WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

            System.out.println("Manutenção deletada!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar manutenção", e);
        }
    }

    // exclusão de equipamento verifica se existe manutenção associada
    public boolean existeManutencaoDoEquipamento(int equipamentoId) {

        // não quero buscar todos os dados, apenas saber se existe
        String sql = """
            SELECT 1  
            FROM manutencao
            WHERE equipamento_id = ?
            LIMIT 1
            """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, equipamentoId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // retorna true se existir
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar manutenções do equipamento", e);
        }
    }

}
