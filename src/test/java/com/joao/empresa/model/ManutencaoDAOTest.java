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
import java.util.List;

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

    @Test
    void buscarPorId_quandoManutencaoEmAndamentoExistir_deveReconstruirComDataFimNulaERelacionamentos()
            throws Exception {

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

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        int manutencaoId =
                inserirManutencaoDiretamente(
                        equipamentoId,
                        tecnicoId,
                        "CORRETIVA",
                        "Problema hidráulico",
                        new BigDecimal("0.00"),
                        "ANDAMENTO",
                        LocalDate.of(2026, 8, 20),
                        null
                );

        Manutencao resultado =
                manutencaoDAO.buscarPorId(
                        manutencaoId
                );

        assertNotNull(resultado);

        assertAll(
                () -> assertEquals(
                        manutencaoId,
                        resultado.getId()
                ),

                () -> assertEquals(
                        Manutencao.TipoManutencao.CORRETIVA,
                        resultado.getTipoManutencao()
                ),

                () -> assertEquals(
                        "Problema hidráulico",
                        resultado.getDescricao()
                ),

                () -> assertEquals(
                        BigDecimal.ZERO,
                        resultado.getCusto()
                ),

                () -> assertEquals(
                        Manutencao.Status.ANDAMENTO,
                        resultado.getStatus()
                ),

                () -> assertEquals(
                        LocalDate.of(
                                2026,
                                8,
                                20
                        ),
                        resultado.getDataInicio()
                ),

                () -> assertNull(
                        resultado.getDataFim()
                ),

                () -> assertEquals(
                        equipamentoId,
                        resultado
                                .getEquipamento()
                                .getId()
                ),

                () -> assertEquals(
                        "PAT-003",
                        resultado
                                .getEquipamento()
                                .getCodigoPatrimonio()
                ),

                () -> assertEquals(
                        tecnicoId,
                        resultado
                                .getTecnicoResponsavel()
                                .getId()
                ),

                () -> assertEquals(
                        "Mecânica",
                        resultado
                                .getTecnicoResponsavel()
                                .getEspecialidade()
                )
        );
    }

    @Test
    void buscarPorId_quandoManutencaoConcluidaExistir_deveReconstruirDataFimCustoEStatus()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "44444444444444"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        "PAT-004"
                );

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Elétrica"
                );

        int manutencaoId =
                inserirManutencaoDiretamente(
                        equipamentoId,
                        tecnicoId,
                        "PREVENTIVA",
                        "Revisão preventiva",
                        new BigDecimal("1500.00"),
                        "CONCLUIDA",
                        LocalDate.of(2026, 8, 20),
                        LocalDate.of(2026, 8, 22)
                );

        Manutencao resultado =
                manutencaoDAO.buscarPorId(
                        manutencaoId
                );

        assertNotNull(resultado);

        assertAll(
                () -> assertEquals(
                        Manutencao.Status.CONCLUIDA,
                        resultado.getStatus()
                ),

                () -> assertEquals(
                        new BigDecimal("1500.00"),
                        resultado.getCusto()
                ),

                () -> assertEquals(
                        LocalDate.of(
                                2026,
                                8,
                                22
                        ),
                        resultado.getDataFim()
                )
        );
    }

    @Test
    void buscarPorId_quandoManutencaoNaoExistir_deveRetornarNull() {

        Manutencao resultado =
                manutencaoDAO.buscarPorId(
                        999999
                );

        assertNull(resultado);
    }

    @Test
    void listar_quandoExistiremManutencoes_deveRetornarTodasOrdenadasPorId()
            throws Exception {

        DadosRelacionamento dados =
                criarRelacionamentosPadrao(
                        "55555555555555",
                        "PAT-005",
                        "tecnico1@email.com"
                );

        int primeiroId =
                inserirManutencaoDiretamente(
                        dados.equipamentoId(),
                        dados.tecnicoId(),
                        "CORRETIVA",
                        "Primeira",
                        BigDecimal.ZERO,
                        "ANDAMENTO",
                        LocalDate.of(2026, 8, 20),
                        null
                );

        int segundoId =
                inserirManutencaoDiretamente(
                        dados.equipamentoId(),
                        dados.tecnicoId(),
                        "PREVENTIVA",
                        "Segunda",
                        new BigDecimal("500.00"),
                        "CONCLUIDA",
                        LocalDate.of(2026, 8, 21),
                        LocalDate.of(2026, 8, 22)
                );

        List<Manutencao> resultado = manutencaoDAO.listar();

        assertAll(
                () -> assertEquals(
                        2,
                        resultado.size()
                ),

                () -> assertEquals(
                        primeiroId,
                        resultado.get(0).getId()
                ),

                () -> assertEquals(
                        segundoId,
                        resultado.get(1).getId()
                )
        );
    }

    @Test
    void listar_quandoNaoExistiremManutencoes_deveRetornarListaVazia() {

        List<Manutencao> resultado = manutencaoDAO.listar();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarPorEquipamento_deveRetornarSomenteManutencoesDoEquipamentoInformado()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "66666666666666"
                );

        int equipamento1 =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        "PAT-006"
                );

        int equipamento2 =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Empilhadeira",
                        "PAT-007"
                );

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        inserirManutencaoDiretamente(
                equipamento1,
                tecnicoId,
                "CORRETIVA",
                "Manutenção equipamento 1",
                BigDecimal.ZERO,
                "ANDAMENTO",
                LocalDate.of(2026, 8, 20),
                null
        );

        inserirManutencaoDiretamente(
                equipamento2,
                tecnicoId,
                "CORRETIVA",
                "Manutenção equipamento 2",
                BigDecimal.ZERO,
                "ANDAMENTO",
                LocalDate.of(2026, 8, 20),
                null
        );

        List<Manutencao> resultado =
                manutencaoDAO.listarPorEquipamento(
                        equipamento1
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertEquals(
                equipamento1,
                resultado
                        .get(0)
                        .getEquipamento()
                        .getId()
        );
    }

    @Test
    void listarPorTecnico_deveRetornarSomenteManutencoesDoTecnicoInformado()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "77777777777777"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        "PAT-008"
                );

        int tecnico1 =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        int tecnico2 =
                inserirTecnicoDiretamente(
                        "Pedro",
                        "pedro@email.com",
                        "Elétrica"
                );

        inserirManutencaoDiretamente(
                equipamentoId,
                tecnico1,
                "CORRETIVA",
                "Manutenção Carlos",
                BigDecimal.ZERO,
                "ANDAMENTO",
                LocalDate.of(2026, 8, 20),
                null
        );

        inserirManutencaoDiretamente(
                equipamentoId,
                tecnico2,
                "PREVENTIVA",
                "Manutenção Pedro",
                BigDecimal.ZERO,
                "ANDAMENTO",
                LocalDate.of(2026, 8, 21),
                null
        );

        List<Manutencao> resultado =
                manutencaoDAO.listarPorTecnico(
                        tecnico1
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertEquals(
                tecnico1,
                resultado
                        .get(0)
                        .getTecnicoResponsavel()
                        .getId()
        );
    }

    @Test
    void listarPorStatus_deveRetornarSomenteManutencoesComStatusInformado()
            throws Exception {

        DadosRelacionamento dados =
                criarRelacionamentosPadrao(
                        "88888888888888",
                        "PAT-009",
                        "tecnico@email.com"
                );

        inserirManutencaoDiretamente(
                dados.equipamentoId(),
                dados.tecnicoId(),
                "CORRETIVA",
                "Em andamento",
                BigDecimal.ZERO,
                "ANDAMENTO",
                LocalDate.of(2026, 8, 20),
                null
        );

        inserirManutencaoDiretamente(
                dados.equipamentoId(),
                dados.tecnicoId(),
                "PREVENTIVA",
                "Concluída",
                new BigDecimal("200.00"),
                "CONCLUIDA",
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 21)
        );

        inserirManutencaoDiretamente(
                dados.equipamentoId(),
                dados.tecnicoId(),
                "CORRETIVA",
                "Cancelada",
                BigDecimal.ZERO,
                "CANCELADA",
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 21)
        );

        List<Manutencao> resultado =
                manutencaoDAO.listarPorStatus(
                        Manutencao.Status.CONCLUIDA
                );

        assertEquals(
                1,
                resultado.size()
        );

        assertEquals(
                Manutencao.Status.CONCLUIDA,
                resultado.get(0).getStatus()
        );
    }

    @Test
    void listarPorStatus_quandoStatusForNulo_deveLancarExcecao() {

        assertThrows(
                IllegalArgumentException.class,
                () -> manutencaoDAO
                        .listarPorStatus(null)
        );
    }

    @Test
    void atualizar_quandoDadosForemValidos_devePersistirTodasAsAlteracoes()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "99999999999999"
                );

        int equipamentoAntigoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Equipamento antigo",
                        "PAT-010"
                );

        int equipamentoNovoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Equipamento novo",
                        "PAT-011"
                );

        int tecnicoAntigoId =
                inserirTecnicoDiretamente(
                        "Carlos",
                        "carlos@email.com",
                        "Mecânica"
                );

        int tecnicoNovoId =
                inserirTecnicoDiretamente(
                        "Pedro",
                        "pedro@email.com",
                        "Elétrica"
                );

        int manutencaoId =
                inserirManutencaoDiretamente(
                        equipamentoAntigoId,
                        tecnicoAntigoId,
                        "CORRETIVA",
                        "Descrição antiga",
                        BigDecimal.ZERO,
                        "ANDAMENTO",
                        LocalDate.of(2026, 8, 20),
                        null
                );

        Equipamento equipamentoNovo =
                new Equipamento(
                        equipamentoNovoId,
                        "Equipamento novo",
                        "PAT-011",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnicoNovo =
                new Tecnico(
                        tecnicoNovoId,
                        "Pedro",
                        "pedro@email.com",
                        "Elétrica"
                );

        Manutencao alterada =
                new Manutencao(
                        manutencaoId,
                        Manutencao.TipoManutencao.PREVENTIVA,
                        "Descrição atualizada",
                        new BigDecimal("2500.00"),
                        LocalDate.of(2026, 8, 21),
                        LocalDate.of(2026, 8, 25),
                        Manutencao.Status.CONCLUIDA,
                        equipamentoNovo,
                        tecnicoNovo
                );

        manutencaoDAO.atualizar(alterada);

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

            stmt.setInt(1, manutencaoId);

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "PREVENTIVA",
                                rs.getString(
                                        "tipo_manutencao"
                                )
                        ),

                        () -> assertEquals(
                                LocalDate.of(
                                        2026,
                                        8,
                                        21
                                ),
                                rs.getDate(
                                        "data_inicio"
                                ).toLocalDate()
                        ),

                        () -> assertEquals(
                                LocalDate.of(
                                        2026,
                                        8,
                                        25
                                ),
                                rs.getDate(
                                        "data_fim"
                                ).toLocalDate()
                        ),

                        () -> assertEquals(
                                "Descrição atualizada",
                                rs.getString("descricao")
                        ),

                        () -> assertEquals(
                                new BigDecimal("2500.00"),
                                rs.getBigDecimal("custo")
                        ),

                        () -> assertEquals(
                                "CONCLUIDA",
                                rs.getString("status")
                        ),

                        () -> assertEquals(
                                equipamentoNovoId,
                                rs.getInt(
                                        "equipamento_id"
                                )
                        ),

                        () -> assertEquals(
                                tecnicoNovoId,
                                rs.getInt(
                                        "tecnico_id"
                                )
                        )
                );
            }
        }
    }



}
