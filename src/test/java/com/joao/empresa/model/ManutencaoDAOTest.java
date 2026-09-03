package com.joao.empresa.model;

import com.joao.empresa.dao.ManutencaoDAO;
import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ManutencaoDAOTest {

    private ManutencaoDAO manutencaoDAO;

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

        manutencaoDAO = new ManutencaoDAO();

        limparBanco();
    }

    @Test
    void salvar_quandoDadosForemValidos_devePersistirManutencaoEGerarId() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "11111111111111"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        "PAT-001"
                );

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Equipamento equipamento =
                new Equipamento(
                        equipamentoId,
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        tecnicoId,
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnico
                );

        assertNull(manutencao.getId());

        manutencaoDAO.salvar(manutencao);

        assertNotNull(manutencao.getId());
        assertTrue(manutencao.getId() > 0);

        try (Connection conn = ConnectionFactory.getConnection();

             PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT
                                    tipo_manutencao,
                                    data_inicio,
                                    data_fim,
                                    descricao,
                                    custo,
                                    status,
                                    equipamento_id,
                                    tecnico_id
                                FROM manutencao
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, manutencao.getId());

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "CORRETIVA",
                                rs.getString(
                                        "tipo_manutencao"
                                )
                        ),

                        () -> assertEquals(
                                LocalDate.of(
                                        2026,
                                        8,
                                        20
                                ),
                                rs.getDate(
                                        "data_inicio"
                                ).toLocalDate()
                        ),

                        () -> assertNull(
                                rs.getDate("data_fim")
                        ),

                        () -> assertEquals(
                                "Troca da correia",
                                rs.getString("descricao")
                        ),

                        () -> assertEquals(
                                new BigDecimal("0.00"),
                                rs.getBigDecimal("custo")
                        ),

                        () -> assertEquals(
                                "ANDAMENTO",
                                rs.getString("status")
                        ),

                        () -> assertEquals(
                                equipamentoId,
                                rs.getInt(
                                        "equipamento_id"
                                )
                        ),

                        () -> assertEquals(
                                tecnicoId,
                                rs.getInt("tecnico_id")
                        )
                );
            }
        }
    }

    @Test
    void salvar_quandoEquipamentoNaoPossuirId_deveLancarExcecaoSemAcessarPersistencia() {

        Equipamento equipamento =
                new Equipamento(
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        1,
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnico
                );

        assertThrows(
                IllegalStateException.class,
                () -> manutencaoDAO.salvar(manutencao)
        );

        assertNull(manutencao.getId());
    }

    @Test
    void salvar_quandoTecnicoNaoPossuirId_deveLancarExcecaoSemAcessarPersistencia() {

        Equipamento equipamento =
                new Equipamento(
                        1,
                        "Laminadora",
                        "PAT-001",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Troca da correia",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnico
                );

        assertThrows(
                IllegalStateException.class,
                () -> manutencaoDAO.salvar(
                        manutencao
                )
        );

        assertNull(manutencao.getId());
    }

    @Test
    void salvar_quandoEquipamentoNaoExistirNoBanco_deveLancarIntegridadeReferencialException()
            throws Exception {

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Equipamento equipamentoInexistente =
                new Equipamento(
                        999999,
                        "Inexistente",
                        "PAT-INEXISTENTE",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        tecnicoId,
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Teste",
                        LocalDate.of(2026, 8, 20),
                        equipamentoInexistente,
                        tecnico
                );

        assertThrows(
                IntegridadeReferencialException.class,
                () -> manutencaoDAO.salvar(manutencao)
        );

        assertNull(manutencao.getId());
    }

    @Test
    void salvar_quandoTecnicoNaoExistirNoBanco_deveLancarIntegridadeReferencialException()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "22222222222222"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        "PAT-002"
                );

        Equipamento equipamento =
                new Equipamento(
                        equipamentoId,
                        "Laminadora",
                        "PAT-002",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnicoInexistente =
                new Tecnico(
                        999999,
                        "Inexistente",
                        "inexistente@email.com",
                        "Mecânica"
                );

        Manutencao manutencao =
                new Manutencao(
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Teste",
                        LocalDate.of(2026, 8, 20),
                        equipamento,
                        tecnicoInexistente
                );

        assertThrows(
                IntegridadeReferencialException.class,
                () -> manutencaoDAO.salvar(
                        manutencao
                )
        );

        assertNull(manutencao.getId());
    }

}
