package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.model.Administrador;
import com.joao.empresa.model.Gestor;
import com.joao.empresa.model.Tecnico;
import com.joao.empresa.model.Usuario;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @BeforeAll
    static void configurarBancoDeTeste() {

        System.setProperty(
                "db.url",
                "jdbc:mysql://localhost:3306/manutencao_test_db"
        );

        System.setProperty(
                "db.user",
                "root"
        );

        System.setProperty(
                "db.password",
                "root"
        );
    }

    @BeforeEach
    void setUp() throws Exception {

        usuarioDAO = new UsuarioDAO();

        limparBanco();
    }

    @Test
    void salvar_quandoForAdministrador_devePersistirUsuarioEDadosEspecificos()
            throws Exception {

        Administrador administrador =
                new Administrador(
                        "João",
                        "admin@email.com",
                        "Tecnologia"
                );

        assertNull(administrador.getId());

        usuarioDAO.salvar(administrador);

        assertNotNull(administrador.getId());

        assertTrue(administrador.getId() > 0);

        // vou ver se salvou mesmo fazendo um join entre pai (usuario) e filho (administrador)
        // vai me dar uma tebela com o relacionamento e eu comparo os campos
        try (Connection conn = ConnectionFactory.getConnection();

             PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                        SELECT
                                            u.nome,
                                            u.email,
                                            u.tipo_usuario,
                                            a.departamento
                                        FROM usuario u
                                        JOIN administrador a
                                            ON a.usuario_id = u.id
                                        WHERE u.id = ?
                                        """
                        )
        ) {

            stmt.setInt(1, administrador.getId());

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "João",
                                rs.getString("nome")
                        ),

                        () -> assertEquals(
                                "admin@email.com",
                                rs.getString("email")
                        ),

                        () -> assertEquals(
                                "ADMINISTRADOR",
                                rs.getString(
                                        "tipo_usuario"
                                )
                        ),

                        () -> assertEquals(
                                "Tecnologia",
                                rs.getString(
                                        "departamento"
                                )
                        )
                );
            }
        }
    }

    @Test
    void salvar_quandoForGestor_devePersistirUsuarioEDadosEspecificos()
            throws Exception {

        Gestor gestor =
                new Gestor(
                        "Maria",
                        "gestor@email.com",
                        "Manutenção"
                );

        usuarioDAO.salvar(gestor);

        assertNotNull(gestor.getId());

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT
                                    u.tipo_usuario,
                                    g.area_responsavel
                                FROM usuario u
                                JOIN gestor g
                                    ON g.usuario_id = u.id
                                WHERE u.id = ?
                                """
                        )
        ) {

            stmt.setInt(
                    1,
                    gestor.getId()
            );

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "GESTOR",
                                rs.getString(
                                        "tipo_usuario"
                                )
                        ),

                        () -> assertEquals(
                                "Manutenção",
                                rs.getString(
                                        "area_responsavel"
                                )
                        )
                );
            }
        }
    }

    @Test
    void salvar_quandoForTecnico_devePersistirUsuarioEDadosEspecificos()
            throws Exception {

        Tecnico tecnico =
                new Tecnico(
                        "Carlos",
                        "tecnico@email.com",
                        "Mecânica"
                );

        usuarioDAO.salvar(tecnico);

        assertNotNull(tecnico.getId());

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT
                                    u.tipo_usuario,
                                    t.especialidade
                                FROM usuario u
                                JOIN tecnico t
                                    ON t.usuario_id = u.id
                                WHERE u.id = ?
                                """
                        )
        ) {

            stmt.setInt(1, tecnico.getId());

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "TECNICO",
                                rs.getString(
                                        "tipo_usuario"
                                )
                        ),

                        () -> assertEquals(
                                "Mecânica",
                                rs.getString(
                                        "especialidade"
                                )
                        )
                );
            }
        }
    }

    @Test
    void salvar_quandoEmailJaExistir_deveLancarRegistroDuplicadoException()
            throws Exception {

        inserirAdministradorDiretamente(
                "João",
                "duplicado@email.com",
                "Tecnologia"
        );

        Gestor gestor =
                new Gestor(
                        "Maria",
                        "duplicado@email.com",
                        "Operações"
                );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> usuarioDAO.salvar(
                        gestor
                )
        );

        assertNull(gestor.getId());
    }

    @Test
    void salvar_quandoTipoNaoForSuportado_deveExecutarRollback()
            throws Exception {

        // esse usuario aqui não é adm, nem gestor e nem tecnico
        // o usuario foi inserido, mas nos dados específicos não suporta
        Usuario usuarioNaoSuportado =
                new Usuario(
                        "Usuário estranho",
                        "estranho@email.com",
                        Usuario.TipoUsuario.ADMINISTRADOR
                ) {
                    @Override
                    public void atualizarEspecifico(
                            Usuario alterado
                    ) {
                    }
                };

        assertThrows(
                IllegalArgumentException.class,
                () -> usuarioDAO.salvar(
                        usuarioNaoSuportado
                )
        );

        assertNull(usuarioNaoSuportado.getId());

        assertEquals(0, contarUsuarios());
    }

    @Test
    void buscarPorId_quandoForAdministrador_deveReconstruirAdministrador()
            throws Exception {

        int id =
                inserirAdministradorDiretamente(
                        "João",
                        "admin@email.com",
                        "Tecnologia"
                );

        Usuario resultado =
                usuarioDAO.buscarPorId(id);

        assertInstanceOf(
                Administrador.class,
                resultado
        );

        Administrador administrador =
                (Administrador) resultado;

        assertAll(
                () -> assertEquals(
                        id,
                        administrador.getId()
                ),

                () -> assertEquals(
                        "João",
                        administrador.getNome()
                ),

                () -> assertEquals(
                        "admin@email.com",
                        administrador.getEmail()
                ),

                () -> assertEquals(
                        Usuario.TipoUsuario.ADMINISTRADOR,
                        administrador.getTipo()
                ),

                () -> assertEquals(
                        "Tecnologia",
                        administrador.getDepartamento()
                )
        );
    }

    @Test
    void buscarPorId_quandoForGestor_deveReconstruirGestor()
            throws Exception {

        int id =
                inserirGestorDiretamente(
                        "Maria",
                        "gestor@email.com",
                        "Operações"
                );

        Usuario resultado =
                usuarioDAO.buscarPorId(id);

        assertInstanceOf(
                Gestor.class,
                resultado
        );

        Gestor gestor =
                (Gestor) resultado;

        assertAll(
                () -> assertEquals(
                        id,
                        gestor.getId()
                ),

                () -> assertEquals(
                        "Maria",
                        gestor.getNome()
                ),

                () -> assertEquals(
                        "Operações",
                        gestor.getAreaResponsavel()
                )
        );
    }

    @Test
    void buscarPorId_quandoForTecnico_deveReconstruirTecnico()
            throws Exception {

        int id =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "tecnico@email.com",
                        "Elétrica"
                );

        Usuario resultado =
                usuarioDAO.buscarPorId(id);

        assertInstanceOf(
                Tecnico.class,
                resultado
        );

        Tecnico tecnico =
                (Tecnico) resultado;

        assertAll(
                () -> assertEquals(
                        id,
                        tecnico.getId()
                ),

                () -> assertEquals(
                        "Carlos",
                        tecnico.getNome()
                ),

                () -> assertEquals(
                        "Elétrica",
                        tecnico.getEspecialidade()
                )
        );
    }

    @Test
    void buscarPorId_quandoUsuarioNaoExistir_deveRetornarNull() {

        Usuario resultado =
                usuarioDAO.buscarPorId(
                        999999
                );

        assertNull(resultado);
    }

    @Test
    void listar_quandoExistiremUsuarios_deveRetornarTodosComSeusSubtipos()
            throws Exception {

        inserirAdministradorDiretamente(
                "Administrador",
                "admin@email.com",
                "Tecnologia"
        );

        inserirGestorDiretamente(
                "Gestor",
                "gestor@email.com",
                "Operações"
        );

        inserirTecnicoDiretamente(
                "Técnico",
                "tecnico@email.com",
                "Mecânica"
        );

        List<Usuario> usuarios =
                usuarioDAO.listar();

        assertEquals(
                3,
                usuarios.size()
        );

        assertAll(
                () -> assertInstanceOf(
                        Administrador.class,
                        usuarios.get(0)
                ),

                () -> assertInstanceOf(
                        Gestor.class,
                        usuarios.get(1)
                ),

                () -> assertInstanceOf(
                        Tecnico.class,
                        usuarios.get(2)
                )
        );
    }

    @Test
    void listar_quandoNaoExistiremUsuarios_deveRetornarListaVazia() {

        List<Usuario> usuarios =
                usuarioDAO.listar();

        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void atualizar_quandoForAdministrador_deveAtualizarDadosComunsEEspecificos()
            throws Exception {

        int id =
                inserirAdministradorDiretamente(
                        "Nome antigo",
                        "antigo@email.com",
                        "Financeiro"
                );

        Administrador alterado =
                new Administrador(
                        id,
                        "Nome atualizado",
                        "novo@email.com",
                        "Tecnologia"
                );

        usuarioDAO.atualizar(
                alterado
        );

        Administrador resultado =
                (Administrador)
                        usuarioDAO.buscarPorId(id);

        assertAll(
                () -> assertEquals(
                        "Nome atualizado",
                        resultado.getNome()
                ),

                () -> assertEquals(
                        "novo@email.com",
                        resultado.getEmail()
                ),

                () -> assertEquals(
                        "Tecnologia",
                        resultado.getDepartamento()
                )
        );
    }

    @Test
    void atualizar_quandoForGestor_deveAtualizarDadosComunsEEspecificos()
            throws Exception {

        int id =
                inserirGestorDiretamente(
                        "Nome antigo",
                        "gestor-antigo@email.com",
                        "Financeiro"
                );

        Gestor alterado =
                new Gestor(
                        id,
                        "Nome atualizado",
                        "gestor-novo@email.com",
                        "Operações"
                );

        usuarioDAO.atualizar(
                alterado
        );

        Gestor resultado =
                (Gestor)
                        usuarioDAO.buscarPorId(id);

        assertAll(
                () -> assertEquals(
                        "Nome atualizado",
                        resultado.getNome()
                ),

                () -> assertEquals(
                        "gestor-novo@email.com",
                        resultado.getEmail()
                ),

                () -> assertEquals(
                        "Operações",
                        resultado.getAreaResponsavel()
                )
        );
    }

    @Test
    void atualizar_quandoForTecnico_deveAtualizarDadosComunsEEspecificos()
            throws Exception {

        int id =
                inserirTecnicoDiretamente(
                        "Nome antigo",
                        "tec-antigo@email.com",
                        "Mecânica"
                );

        Tecnico alterado =
                new Tecnico(
                        id,
                        "Nome atualizado",
                        "tec-novo@email.com",
                        "Elétrica"
                );

        usuarioDAO.atualizar(
                alterado
        );

        Tecnico resultado =
                (Tecnico)
                        usuarioDAO.buscarPorId(id);

        assertAll(
                () -> assertEquals(
                        "Nome atualizado",
                        resultado.getNome()
                ),

                () -> assertEquals(
                        "tec-novo@email.com",
                        resultado.getEmail()
                ),

                () -> assertEquals(
                        "Elétrica",
                        resultado.getEspecialidade()
                )
        );
    }

    @Test
    void atualizar_quandoNovoEmailJaPertencerAOutroUsuario_deveLancarRegistroDuplicadoException()
            throws Exception {

        int primeiroId =
                inserirAdministradorDiretamente(
                        "Usuário A",
                        "usuarioA@email.com",
                        "Tecnologia"
                );

        inserirGestorDiretamente(
                "Usuário B",
                "usuarioB@email.com",
                "Operações"
        );

        Administrador alterado =
                new Administrador(
                        primeiroId,
                        "Usuário A",
                        "usuarioB@email.com",
                        "Tecnologia"
                );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> usuarioDAO.atualizar(
                        alterado
                )
        );
    }

    @Test
    void deletar_quandoAdministradorExistir_deveRemoverUsuarioESubtipoPorCascade()
            throws Exception {

        int id =
                inserirAdministradorDiretamente(
                        "Administrador",
                        "admin@email.com",
                        "Tecnologia"
                );

        usuarioDAO.deletar(id);

        assertAll(
                () -> assertEquals(
                        0,
                        contarPorId(
                                "usuario",
                                "id",
                                id
                        )
                ),

                () -> assertEquals(
                        0,
                        contarPorId(
                                "administrador",
                                "usuario_id",
                                id
                        )
                )
        );
    }

    @Test
    void deletar_quandoTecnicoPossuirManutencao_deveLancarIntegridadeReferencialException()
            throws Exception {

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Técnico",
                        "tecnico@email.com",
                        "Mecânica"
                );

        int empresaId = inserirEmpresaDiretamente();

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId
                );

        inserirManutencaoDiretamente(
                equipamentoId,
                tecnicoId
        );

        assertThrows( // lá no schema tem o delete restrict que protege a chave estrangeira
                IntegridadeReferencialException.class,
                () -> usuarioDAO.deletar(
                        tecnicoId
                )
        );
    }

    @Test
    void deletar_quandoUsuarioNaoExistir_deveLancarPersistenciaException() {

        assertThrows(
                PersistenciaException.class,
                () -> usuarioDAO.deletar(
                        999999
                )
        );
    }

    private int inserirUsuarioDiretamente(
            String nome,
            String email,
            String tipo
    ) throws Exception {

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                INSERT INTO usuario
                                    (
                                        nome,
                                        email,
                                        tipo_usuario
                                    )
                                VALUES
                                    (?, ?, ?)
                                """,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, tipo);

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }

    private int inserirAdministradorDiretamente(
            String nome,
            String email,
            String departamento
    ) throws Exception {

        int id = inserirUsuarioDiretamente(
                nome,
                email,
                "ADMINISTRADOR"
        );

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                INSERT INTO administrador
                                    (usuario_id, departamento)
                                VALUES
                                    (?, ?)
                                """
                        )
        ) {

            stmt.setInt(1, id);
            stmt.setString(2, departamento);

            stmt.executeUpdate();
        }

        return id;
    }

    private int inserirGestorDiretamente(
            String nome,
            String email,
            String areaResponsavel
    ) throws Exception {

        int id = inserirUsuarioDiretamente(
                nome,
                email,
                "GESTOR"
        );

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                INSERT INTO gestor
                                    (
                                        usuario_id,
                                        area_responsavel
                                    )
                                VALUES
                                    (?, ?)
                                """
                        )
        ) {

            stmt.setInt(1, id);
            stmt.setString(2, areaResponsavel);

            stmt.executeUpdate();
        }

        return id;
    }

    private int inserirTecnicoDiretamente(
            String nome,
            String email,
            String especialidade
    ) throws Exception {

        int id = inserirUsuarioDiretamente(
                nome,
                email,
                "TECNICO"
        );

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                INSERT INTO tecnico
                                    (
                                        usuario_id,
                                        especialidade
                                    )
                                VALUES
                                    (?, ?)
                                """
                        )
        ) {

            stmt.setInt(1, id);
            stmt.setString(2, especialidade);

            stmt.executeUpdate();
        }

        return id;
    }

    // devolve quantos usuários tem na tabela usuário
    private int contarUsuarios()
            throws Exception {

        try (Connection conn = ConnectionFactory.getConnection();

                Statement stmt = conn.createStatement();

                ResultSet rs =
                        stmt.executeQuery(
                                "SELECT COUNT(*) FROM usuario"
                        )
        ) {

            assertTrue(rs.next());

            return rs.getInt(1);
        }
    }

    private int contarPorId(
            String tabela,
            String coluna,
            int id
    ) throws Exception {

        String sql =
                "SELECT COUNT(*) FROM "
                        + tabela
                        + " WHERE "
                        + coluna
                        + " = ?";

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs =
                            stmt.executeQuery()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }



}