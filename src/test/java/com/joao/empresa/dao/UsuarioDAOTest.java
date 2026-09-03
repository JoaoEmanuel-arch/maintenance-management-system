package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
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



}