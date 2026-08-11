package com.joao.empresa.dao;

import com.joao.empresa.model.*;
import com.joao.empresa.database.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public void salvar(Usuario usuario) {

        String sql = """
            INSERT INTO usuario
            (nome, email, tipo_usuario)
            VALUES (?, ?, ?)
            """;

        // abre somente a conexão primeiro pq vai usar em mais de um comando SQL
        try (Connection conn = ConnectionFactory.getConnection()) {

            // Não confirmar automaticamente cada comando, esperar eu mesmo confirmar com commit()
            conn.setAutoCommit(false);

            try {
                int idGerado;

                try (PreparedStatement stmt = conn.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS
                )) {

                    stmt.setString(1, usuario.getNome());
                    stmt.setString(2, usuario.getEmail());
                    stmt.setString(3, usuario.getTipo().name());

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas == 0) {
                        throw new SQLException("Nenhum usuário foi inserido.");
                    }

                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {

                        if (!generatedKeys.next()) {
                            throw new SQLException("O banco não retornou o ID do usuário.");
                        }

                        idGerado = generatedKeys.getInt(1);
                    }
                }

                // eu salvo os dados específicos agora passando a mesma conexão, sem ter que abrir outra
                // se der um erro nos filhos como que vou fazer com as alterações já feitas no pai ...
                salvarDadosEspecificos(conn, usuario, idGerado);

                // Confirma o INSERT em usuario e o INSERT na tabela específica.
                conn.commit();

                // O objeto só recebe o ID depois de o banco confirmar toda a operação nos filhos.
                usuario.definirId(idGerado);

                System.out.println("Usuário salvo com sucesso!");

            } catch (SQLException e) {

                executarRollback(conn, e);

                // Relança a exceção para o catch externo.
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário", e);
        }
    }

    // Depois eu salvo as subclasses com seus dados específicos no banco de dados,
    // com chaves primárias fazendo referência ao id do usuario pai
    // Não preciso fazer uma nova conexão, vem tudo por parâmetro
    private void salvarDadosEspecificos(Connection conn, Usuario usuario, int idGerado) throws SQLException {

        if (usuario instanceof Tecnico tecnico) { // se usuario for instância de tecnico
            String sql = "INSERT INTO tecnico (usuario_id, especialidade) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idGerado);
                stmt.setString(2, tecnico.getEspecialidade());

                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Erro ao salvar técnico", e);
            }
        }

        else if (usuario instanceof Gestor gestor) {
            String sql = "INSERT INTO gestor (usuario_id, area_responsavel) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idGerado);
                stmt.setString(2, gestor.getAreaResponsavel());

                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("Erro ao salvar gestor", e);
            }
        }

        else if (usuario instanceof Administrador administrador) {
            String sql = "INSERT INTO administrador (usuario_id, nivel_acesso, departamento) VALUES (?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, idGerado);
                stmt.setString(2, administrador.getDepartamento());

                stmt.executeUpdate();
            }
        }

        else {
            throw new SQLException("Tipo de usuário não suportado.");
        }
    }

    private void executarRollback(Connection conn, SQLException causaOriginal) {

        try {
            conn.rollback();

        } catch (SQLException erroNoRollback) {
            causaOriginal.addSuppressed(erroNoRollback);
        }
    }

    // primeiro busca na tabela usuario, depois que achar busca na tabela específica com o mesmo id
    public Usuario buscarPorId(int id) {

        // isso é como que eu pesquisaria no MySQL e o id vem do parâmetro
        String sql = "SELECT * FROM usuario WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) { // esse objeto que coloca o valor no sql

            // Substitui o ? pelo id
            stmt.setInt(1, id);

            // Executa SELECT
            ResultSet rs = stmt.executeQuery();

            // Se encontrou resultado (o primeiro é o cabeçalho, vem se tem tupla depois)
            // a ideia é criar o objeto com os dados que eu busquei na tabela
            if (rs.next()) {

                String nome = rs.getString("nome"); // pego o valor da coluna e salva aqui
                String email = rs.getString("email");

                // Converte String do banco → enum
                Usuario.TipoUsuario tipo =
                        Usuario.TipoUsuario.valueOf(
                                rs.getString("tipo_usuario")
                        );

                // aí com base no tipo de usuário, eu passo os atributos que já tem de usuário
                // e ele cria o objeto específico já na função. Pq o id é o mesmo
                switch (tipo) {
                    case TECNICO:
                        return buscarTecnico(id, nome, email);

                    case GESTOR:
                        return buscarGestor(id, nome, email);

                    case ADMINISTRADOR:
                        return buscarAdministrador(id, nome, email);

                    default:
                        throw new RuntimeException("Tipo de usuário inválido");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
        return null;
    }

    private Tecnico buscarTecnico(int id, String nome, String email) {

        String sql = "SELECT * FROM tecnico WHERE usuario_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) { // o primeiro que retornar na consulta (só terá um)

                String especialidade = rs.getString("especialidade");

                return new Tecnico(id, nome, email, especialidade);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar técnico", e);
        }

        return null;
    }

    private Gestor buscarGestor(int id, String nome, String email) {

        String sql = "SELECT * FROM gestor WHERE usuario_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String areaResponsavel = rs.getString("area_responsavel");

                return new Gestor(id, nome, email, areaResponsavel);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar gestor", e);
        }

        return null;
    }

    private Administrador buscarAdministrador(int id, String nome, String email) {

        String sql = "SELECT * FROM administrador WHERE usuario_id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                String departamento = rs.getString("departamento");

                return new Administrador(id, nome, email, departamento);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar administrador", e);
        }

        return null;
    }

    public List<Usuario> listar() {

        // eu junto em uma tabela todas as informarções de todos os usuários
        // depois na hora de montar os objetos eu só pego os valores por essa tabela
        String sql = """
            SELECT
                u.id,
                u.nome,
                u.email,
                u.tipo_usuario,

                t.especialidade,
                g.area_responsavel,
                a.departamento

            FROM usuario u

            LEFT JOIN tecnico t
                ON t.usuario_id = u.id

            LEFT JOIN gestor g
                ON g.usuario_id = u.id

            LEFT JOIN administrador a
                ON a.usuario_id = u.id

            ORDER BY u.id
            """;

        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String email = rs.getString("email");
                Usuario.TipoUsuario tipo = Usuario.TipoUsuario.valueOf(rs.getString("tipo_usuario"));

                Usuario usuario;

                // na hora de montar aqui, eu pego os valores vindo do join
                switch (tipo) {

                    case TECNICO:
                        usuario = new Tecnico(id, nome, email, rs.getString("especialidade"));
                        break;

                    case GESTOR:
                        usuario = new Gestor(id, nome, email, rs.getString("area_responsavel"));
                        break;

                    case ADMINISTRADOR:
                        usuario = new Administrador(id, nome, email, rs.getString("departamento"));
                        break;

                    default:
                        throw new IllegalStateException("Tipo de usuário inválido: " + tipo);
                }

                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }

        return usuarios;
    }

    // Recebe o usuário já alterado, pego as partes e jogo no update pra mudar dentro do banco
    public void atualizar(Usuario usuario) {

        String sql = """
            UPDATE usuario
            SET nome = ?,
                email = ?,
                tipo_usuario = ?
            WHERE id = ?
            """;

        try (Connection conn = ConnectionFactory.getConnection()) {

            conn.setAutoCommit(false);

            try {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setString(1, usuario.getNome());
                    stmt.setString(2, usuario.getEmail());
                    stmt.setString(3, usuario.getTipo().name());
                    stmt.setInt(4, usuario.getId());

                    int linhasAfetadas = stmt.executeUpdate();

                    if (linhasAfetadas == 0) {
                        throw new SQLException("Usuário com ID " + usuario.getId() + " não foi encontrado.");
                    }
                }

                atualizarDadosEspecificos(conn, usuario);

                conn.commit(); // confirma a operação para o JDBC

                System.out.println("Usuário atualizado!");

            } catch (SQLException e) {

                executarRollback(conn, e); // se esse ou um dos filhos deu errado, desfaz tudo

                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Erro ao atualizar usuário", e
            );
        }
    }

    private void atualizarDadosEspecificos(Connection conn, Usuario usuario) throws SQLException {

        if (usuario instanceof Tecnico tecnico) {

            String sql = """
                UPDATE tecnico
                SET especialidade = ?
                WHERE usuario_id = ?
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, tecnico.getEspecialidade());
                stmt.setInt(2, tecnico.getId());

                verificarAtualizacaoEspecifica(stmt, tecnico.getId(), "técnico");
            }

        } else if (usuario instanceof Gestor gestor) {

            String sql = """
                UPDATE gestor
                SET area_responsavel = ?
                WHERE usuario_id = ?
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, gestor.getAreaResponsavel());
                stmt.setInt(2, gestor.getId());

                verificarAtualizacaoEspecifica(stmt, gestor.getId(), "gestor");
            }

        } else if (
                usuario instanceof Administrador administrador
        ) {

            String sql = """
                UPDATE administrador
                SET departamento = ?
                WHERE usuario_id = ?
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, administrador.getDepartamento());
                stmt.setInt(2, administrador.getId());

                verificarAtualizacaoEspecifica(stmt, administrador.getId(), "administrador");
            }

        } else {
            throw new SQLException(
                    "Tipo de usuário não suportado."
            );
        }
    }

    private void verificarAtualizacaoEspecifica(PreparedStatement stmt, int id, String tipo) throws SQLException {

        int linhasAfetadas = stmt.executeUpdate();

        if (linhasAfetadas == 0) {
            throw new SQLException("Dados específicos do " + tipo
                            + " com ID " + id + " não foram encontrados."
            );
        }
    }

    public void deletar(int id){

        String sql = "DELETE FROM usuario WHERE id = ?";

        try(Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();

            System.out.println("Usuário deletado!");

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar usuário", e);
        }
    }
}