package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
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

    @Test
    void salvar_quandoCodigoPatrimonioJaExistir_deveLancarRegistroDuplicadoException() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "22222222222222"
                );

        inserirEquipamentoDiretamente(
                empresaId,
                "Equipamento existente",
                "PAT-DUPLICADO"
        );

        Equipamento duplicado =
                new Equipamento(
                        "Outro equipamento",
                        "PAT-DUPLICADO",
                        LocalDate.of(
                                2021,
                                1,
                                1
                        )
                );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> equipamentoDAO.salvar(duplicado, empresaId)
        );

        assertNull(duplicado.getId());
    }

    @Test
    void salvar_quandoEmpresaNaoExistir_deveLancarIntegridadeReferencialException() {

        Equipamento equipamento =
                new Equipamento(
                        "Laminadora",
                        "PAT-002",
                        LocalDate.of(
                                2020,
                                5,
                                10
                        )
                );

        assertThrows(
                IntegridadeReferencialException.class,
                () -> equipamentoDAO.salvar(
                        equipamento,
                        999999
                )
        );

        assertNull(equipamento.getId());
    }

    @Test
    void buscarPorId_quandoEquipamentoExistir_deveReconstruirEquipamento() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "33333333333333"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Empilhadeira",
                        "PAT-003"
                );

        Equipamento resultado =
                equipamentoDAO.buscarPorId(
                        equipamentoId
                );

        assertNotNull(resultado);

        assertAll(
                () -> assertEquals(
                        equipamentoId,
                        resultado.getId()
                ),

                () -> assertEquals(
                        "Empilhadeira",
                        resultado.getNome()
                ),

                () -> assertEquals(
                        "PAT-003",
                        resultado.getCodigoPatrimonio()
                ),

                () -> assertEquals(
                        LocalDate.of(
                                2020,
                                1,
                                1
                        ),
                        resultado.getDataAquisicao()
                )
        );
    }

    @Test
    void buscarPorId_quandoEquipamentoNaoExistir_deveRetornarNull() {

        Equipamento resultado =
                equipamentoDAO.buscarPorId(
                        999999
                );

        assertNull(resultado);
    }

}
