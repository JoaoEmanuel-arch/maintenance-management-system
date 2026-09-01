package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.model.Equipamento;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

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

    @Test
    void listar_quandoExistiremEquipamentos_deveRetornarTodos() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "44444444444444"
                );

        inserirEquipamentoDiretamente(
                empresaId,
                "Laminadora",
                "PAT-004"
        );

        inserirEquipamentoDiretamente(
                empresaId,
                "Empilhadeira",
                "PAT-005"
        );

        List<Equipamento> equipamentos = equipamentoDAO.listar();

        assertEquals(2, equipamentos.size());

        // passe por todos os equipamentos da lista, existe pelo menos um cujo
        // patrimônio seja PAT-004, anyMatch retorna true. Ver se eles estão lá na lista mesmo
        assertTrue(
                equipamentos.stream()
                        .anyMatch(
                                equipamento ->
                                        equipamento
                                                .getCodigoPatrimonio()
                                                .equals("PAT-004")
                        )
        );

        assertTrue(
                equipamentos.stream()
                        .anyMatch(
                                equipamento ->
                                        equipamento
                                                .getCodigoPatrimonio()
                                                .equals("PAT-005")
                        )
        );
    }

    @Test
    void listar_quandoNaoExistiremEquipamentos_deveRetornarListaVazia() {

        List<Equipamento> equipamentos = equipamentoDAO.listar();

        assertNotNull(equipamentos); // não deve retornar null
        assertTrue(equipamentos.isEmpty()); // e sim uma lista vazia
    }

    @Test
    void atualizar_quandoEquipamentoExistir_devePersistirAlteracoes() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "55555555555555"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Nome antigo",
                        "PAT-006"
                );

        Equipamento alterado =
                new Equipamento(
                        equipamentoId,
                        "Nome atualizado",
                        "PAT-007",
                        LocalDate.of(
                                2025,
                                8,
                                15
                        )
                );

        equipamentoDAO.atualizar(alterado);

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT nome,
                                       codigo_patrimonio,
                                       data_aquisicao
                                FROM equipamento
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, equipamentoId);

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "Nome atualizado",
                                rs.getString("nome")
                        ),

                        () -> assertEquals(
                                "PAT-007",
                                rs.getString(
                                        "codigo_patrimonio"
                                )
                        ),

                        () -> assertEquals(
                                LocalDate.of(
                                        2025,
                                        8,
                                        15
                                ),
                                rs.getDate(
                                        "data_aquisicao"
                                ).toLocalDate()
                        )
                );
            }
        }
    }

    @Test
    void atualizar_quandoNovoCodigoPatrimonioJaPertencerAOutroEquipamento_deveLancarRegistroDuplicadoException()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "66666666666666"
                );

        int primeiroId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Equipamento A",
                        "PAT-008"
                );

        inserirEquipamentoDiretamente(
                empresaId,
                "Equipamento B",
                "PAT-009"
        );

        Equipamento alterado =
                new Equipamento(
                        primeiroId,
                        "Equipamento A",
                        "PAT-009",
                        LocalDate.of(
                                2020,
                                1,
                                1
                        )
                );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> equipamentoDAO.atualizar(alterado)
        );
    }

    @Test
    void atualizar_quandoEquipamentoNaoExistir_deveLancarPersistenciaException() {

        Equipamento inexistente =
                new Equipamento(
                        999999,
                        "Inexistente",
                        "PAT-INEXISTENTE",
                        LocalDate.of(
                                2020,
                                1,
                                1
                        )
                );

        assertThrows(
                PersistenciaException.class,
                () -> equipamentoDAO.atualizar(inexistente)
        );
    }

}
