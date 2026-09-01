package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Equipamento;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class EquipamentoDAOTest {

    // qualquer coisa olha a explicação no EquipamentoDAOTest
    private EquipamentoDAO equipamentoDAO;

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

        equipamentoDAO = new EquipamentoDAO();

        limparBanco();
    }

    @Test
    void salvar_quandoDadosForemValidos_devePersistirEquipamentoEGerarId()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "11111111111111"
                );

        Equipamento equipamento =
                new Equipamento(
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 5, 10)
                );

        assertNull(equipamento.getId());

        equipamentoDAO.salvar(
                equipamento,
                empresaId
        );

        assertNotNull(equipamento.getId());
        assertTrue(equipamento.getId() > 0);

        try (Connection conn = ConnectionFactory.getConnection();

             PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT nome,
                                       codigo_patrimonio,
                                       data_aquisicao,
                                       empresa_id
                                FROM equipamento
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(
                    1,
                    equipamento.getId()
            );

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "Laminadora",
                                rs.getString("nome")
                        ),

                        () -> assertEquals(
                                "PAT-001",
                                rs.getString(
                                        "codigo_patrimonio"
                                )
                        ),

                        () -> assertEquals(
                                LocalDate.of(
                                        2020,
                                        5,
                                        10
                                ),
                                rs.getDate(
                                        "data_aquisicao"
                                ).toLocalDate()
                        ),

                        () -> assertEquals(
                                empresaId,
                                rs.getInt("empresa_id")
                        )
                );
            }
        }
    }




}
