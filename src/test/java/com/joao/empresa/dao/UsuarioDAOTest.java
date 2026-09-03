package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Administrador;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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



}