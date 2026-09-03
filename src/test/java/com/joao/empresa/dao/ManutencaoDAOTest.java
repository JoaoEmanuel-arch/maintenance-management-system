package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.model.Equipamento;
import com.joao.empresa.model.Manutencao;
import com.joao.empresa.model.Tecnico;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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
                        0,
                        resultado
                                .getCusto()
                                .compareTo(BigDecimal.ZERO)
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

    @Test
    void atualizar_quandoManutencaoNaoExistir_deveLancarPersistenciaException()
            throws Exception {

        DadosRelacionamento dados =
                criarRelacionamentosPadrao(
                        "10101010101010",
                        "PAT-012",
                        "tecnico@email.com"
                );

        Equipamento equipamento =
                new Equipamento(
                        dados.equipamentoId(),
                        "Laminadora",
                        "PAT-012",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        dados.tecnicoId(),
                        "Técnico Teste",
                        "tecnico@email.com",
                        "Mecânica"
                );

        Manutencao inexistente =
                new Manutencao(
                        999999,
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Inexistente",
                        BigDecimal.ZERO,
                        LocalDate.of(2026, 8, 20),
                        null,
                        Manutencao.Status.ANDAMENTO,
                        equipamento,
                        tecnico
                );

        assertThrows(
                PersistenciaException.class,
                () -> manutencaoDAO.atualizar(
                        inexistente
                )
        );
    }

    @Test
    void atualizar_quandoNovoEquipamentoNaoExistir_deveLancarIntegridadeReferencialException()
            throws Exception {

        DadosRelacionamento dados =
                criarRelacionamentosPadrao(
                        "11111111111112",
                        "PAT-013",
                        "tecnico@email.com"
                );

        int manutencaoId =
                inserirManutencaoDiretamente(
                        dados.equipamentoId(),
                        dados.tecnicoId(),
                        "CORRETIVA",
                        "Original",
                        BigDecimal.ZERO,
                        "ANDAMENTO",
                        LocalDate.of(2026, 8, 20),
                        null
                );

        Equipamento equipamentoInexistente =
                new Equipamento(
                        999999,
                        "Inexistente",
                        "PAT-X",
                        LocalDate.of(2020, 1, 1)
                );

        Tecnico tecnico =
                new Tecnico(
                        dados.tecnicoId(),
                        "Técnico Teste",
                        "tecnico@email.com",
                        "Mecânica"
                );

        Manutencao alterada =
                new Manutencao(
                        manutencaoId,
                        Manutencao.TipoManutencao.CORRETIVA,
                        "Alterada",
                        BigDecimal.ZERO,
                        LocalDate.of(2026, 8, 20),
                        null,
                        Manutencao.Status.ANDAMENTO,
                        equipamentoInexistente,
                        tecnico
                );

        assertThrows(
                IntegridadeReferencialException.class,
                () -> manutencaoDAO.atualizar(
                        alterada
                )
        );
    }

    @Test
    void deletar_quandoManutencaoExistir_deveRemoverRegistro()
            throws Exception {

        DadosRelacionamento dados =
                criarRelacionamentosPadrao(
                        "12121212121212",
                        "PAT-014",
                        "tecnico@email.com"
                );

        int manutencaoId =
                inserirManutencaoDiretamente(
                        dados.equipamentoId(),
                        dados.tecnicoId(),
                        "CORRETIVA",
                        "Será removida",
                        BigDecimal.ZERO,
                        "ANDAMENTO",
                        LocalDate.of(2026, 8, 20),
                        null
                );

        manutencaoDAO.deletar(manutencaoId);

        assertEquals(
                0,
                contarManutencaoPorId(
                        manutencaoId
                )
        );
    }

    @Test
    void deletar_quandoManutencaoNaoExistir_deveLancarPersistenciaException() {

        assertThrows(
                PersistenciaException.class,
                () -> manutencaoDAO.deletar(
                        999999
                )
        );
    }

    @Test
    void existeManutencaoDoEquipamento_quandoNaoExistir_deveRetornarFalse()
            throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        "14141414141414"
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Sem manutenção",
                        "PAT-016"
                );

        assertFalse(
                manutencaoDAO
                        .existeManutencaoDoEquipamento(
                                equipamentoId
                        )
        );
    }

    private DadosRelacionamento criarRelacionamentosPadrao(
            String cnpj,
            String patrimonio,
            String email
    ) throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa Teste",
                        cnpj
                );

        int equipamentoId =
                inserirEquipamentoDiretamente(
                        empresaId,
                        "Laminadora",
                        patrimonio
                );

        int tecnicoId =
                inserirTecnicoDiretamente(
                        "Técnico Teste",
                        email,
                        "Mecânica"
                );

        return new DadosRelacionamento(
                equipamentoId,
                tecnicoId
        );
    }

    private int inserirEmpresaDiretamente(
            String nome,
            String cnpj
    ) throws Exception {

        String sql = """
                INSERT INTO empresa
                    (
                        nome,
                        cnpj,
                        endereco,
                        segmento,
                        status
                    )
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(1, nome);
            stmt.setString(2, cnpj);
            stmt.setString(
                    3,
                    "Ouro Branco"
            );
            stmt.setString(
                    4,
                    "Siderurgia"
            );
            stmt.setString(
                    5,
                    "ATIVADA"
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }

    private int inserirEquipamentoDiretamente(
            int empresaId,
            String nome,
            String codigoPatrimonio
    ) throws Exception {

        String sql = """
                INSERT INTO equipamento
                    (
                        nome,
                        codigo_patrimonio,
                        data_aquisicao,
                        empresa_id
                    )
                VALUES
                    (?, ?, ?, ?)
                """;

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(
                    1,
                    nome
            );

            stmt.setString(
                    2,
                    codigoPatrimonio
            );

            stmt.setDate(
                    3,
                    java.sql.Date.valueOf(
                            LocalDate.of(
                                    2020,
                                    1,
                                    1
                            )
                    )
            );

            stmt.setInt(
                    4,
                    empresaId
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }

    private int inserirTecnicoDiretamente(
            String nome,
            String email,
            String especialidade
    ) throws Exception {

        int usuarioId;

        String sqlUsuario = """
                INSERT INTO usuario
                    (
                        nome,
                        email,
                        tipo_usuario
                    )
                VALUES
                    (?, ?, ?)
                """;

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                sqlUsuario,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(
                    3,
                    "TECNICO"
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                assertTrue(rs.next());

                usuarioId = rs.getInt(1);
            }
        }

        String sqlTecnico = """
                INSERT INTO tecnico
                    (
                        usuario_id,
                        especialidade
                    )
                VALUES
                    (?, ?)
                """;

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                sqlTecnico
                        )
        ) {

            stmt.setInt(
                    1,
                    usuarioId
            );

            stmt.setString(
                    2,
                    especialidade
            );

            stmt.executeUpdate();
        }

        return usuarioId;
    }

    private int inserirManutencaoDiretamente(
            int equipamentoId,
            int tecnicoId,
            String tipo,
            String descricao,
            BigDecimal custo,
            String status,
            LocalDate dataInicio,
            LocalDate dataFim
    ) throws Exception {

        String sql = """
                INSERT INTO manutencao
                    (
                        tipo_manutencao,
                        data_inicio,
                        data_fim,
                        descricao,
                        custo,
                        status,
                        equipamento_id,
                        tecnico_id
                    )
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(
                    1,
                    tipo
            );

            stmt.setDate(
                    2,
                    java.sql.Date.valueOf(
                            dataInicio
                    )
            );

            if (dataFim != null) {

                stmt.setDate(
                        3,
                        java.sql.Date.valueOf(
                                dataFim
                        )
                );

            } else {

                stmt.setNull(
                        3,
                        java.sql.Types.DATE
                );
            }

            stmt.setString(
                    4,
                    descricao
            );

            stmt.setBigDecimal(
                    5,
                    custo
            );

            stmt.setString(
                    6,
                    status
            );

            stmt.setInt(
                    7,
                    equipamentoId
            );

            stmt.setInt(
                    8,
                    tecnicoId
            );

            stmt.executeUpdate();

            try (
                    ResultSet rs =
                            stmt.getGeneratedKeys()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }

    private int contarManutencaoPorId(
            int id
    ) throws Exception {

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT COUNT(*)
                                FROM manutencao
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, id);

            try (
                    ResultSet rs =
                            stmt.executeQuery()
            ) {

                assertTrue(rs.next());

                return rs.getInt(1);
            }
        }
    }

    private void limparBanco()
            throws Exception {

        try (
                Connection conn =
                        ConnectionFactory.getConnection();

                Statement stmt =
                        conn.createStatement()
        ) {

            stmt.executeUpdate(
                    "DELETE FROM manutencao"
            );

            stmt.executeUpdate(
                    "DELETE FROM equipamento"
            );

            stmt.executeUpdate(
                    "DELETE FROM administrador"
            );

            stmt.executeUpdate(
                    "DELETE FROM gestor"
            );

            stmt.executeUpdate(
                    "DELETE FROM tecnico"
            );

            stmt.executeUpdate(
                    "DELETE FROM usuario"
            );

            stmt.executeUpdate(
                    "DELETE FROM empresa"
            );
        }
    }

    private record DadosRelacionamento(
            int equipamentoId,
            int tecnicoId
    ) {
    }

}
