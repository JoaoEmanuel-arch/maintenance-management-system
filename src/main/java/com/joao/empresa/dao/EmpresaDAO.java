package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.database.TradutorSQLException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.model.Empresa;
import com.joao.empresa.model.Equipamento;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EmpresaDAO {

    public void salvar(Empresa empresa) {

        String sql = "INSERT INTO empresa (nome, cnpj, endereco, segmento, status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql,
                     Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEndereco());
            stmt.setString(4, empresa.getSegmento());
            stmt.setString(5, empresa.getStatus().name());

            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException(
                        "Nenhuma empresa foi inserida."
                );
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {

                if (!generatedKeys.next()) {
                    throw new PersistenciaException(
                            "O banco não retornou o ID da empresa."
                    );
                }

                empresa.definirId(generatedKeys.getInt(1));
            }

            System.out.println(
                    "Empresa salva com ID " +
                            empresa.getId()
            );

        } catch (SQLException e) {
            throw TradutorSQLException.traduzir(e, "salvar empresa");
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
            throw new PersistenciaException("Erro ao buscar empresa", e);
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
            throw new PersistenciaException("Erro ao buscar equipamentos da empresa", e);
        }

        return equipamentos;
    }

    public List<Empresa> listar() {

        // retorna as empresas com seus equipamentos (podem ser null)
        // cada linha é um relacionamento, por isso a empresa pode repetir
        String sql = """
            SELECT
                e.id AS empresa_id,
                e.nome AS empresa_nome,
                e.cnpj AS empresa_cnpj,
                e.endereco AS empresa_endereco,
                e.segmento AS empresa_segmento,
                e.status AS empresa_status,

                eq.id AS equipamento_id,
                eq.nome AS equipamento_nome,
                eq.codigo_patrimonio AS equipamento_codigo_patrimonio,
                eq.data_aquisicao AS equipamento_data_aquisicao

            FROM empresa e

            LEFT JOIN equipamento eq
                ON eq.empresa_id = e.id

            ORDER BY e.id
            """;

        //como pode haver várias linhas com o mesmo id, eu salvo apenas uma aqui para não criar vários objetos
        Map<Integer, Empresa> empresasPorId = new LinkedHashMap<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int empresaId = rs.getInt("empresa_id");

                // eu tento pegar o objeto pronto lá no map, pra não criar outro
                Empresa empresa = empresasPorId.get(empresaId);

                // Só cria a empresa se ela ainda não foi criada
                if (empresa == null) {

                    empresa = new Empresa(
                            empresaId,
                            rs.getString("empresa_nome"),
                            rs.getString("empresa_cnpj"),
                            rs.getString("empresa_endereco"),
                            rs.getString("empresa_segmento"),
                            Empresa.Status.valueOf(
                                    rs.getString("empresa_status")
                            )
                    );

                    // salvo lá dentro para não ter criar outros objetos com o mesmo id
                    empresasPorId.put(empresaId, empresa);
                }

                int equipamentoId = rs.getInt("equipamento_id");

                // LEFT JOIN pode retornar NULL se a empresa
                // ainda não possuir nenhum equipamento
                if (!rs.wasNull()) {

                    Equipamento equipamento =
                            new Equipamento(
                                    equipamentoId,
                                    rs.getString("equipamento_nome"),
                                    rs.getString("equipamento_codigo_patrimonio"),
                                    rs.getDate("equipamento_data_aquisicao").toLocalDate()
                            );

                    empresa.adicionarEquipamento(equipamento);
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException("Erro ao listar empresas", e);
        }

        return new ArrayList<>(empresasPorId.values());
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
            throw new PersistenciaException("Erro ao atualizar empresa", e);
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
            throw new PersistenciaException("Erro ao deletar empresa", e);
        }
    }

}
