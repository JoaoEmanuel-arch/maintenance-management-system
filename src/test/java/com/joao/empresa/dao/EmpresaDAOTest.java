package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.exceptions.IntegridadeReferencialException;
import com.joao.empresa.exceptions.PersistenciaException;
import com.joao.empresa.exceptions.RegistroDuplicadoException;
import com.joao.empresa.model.Empresa;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmpresaDAOTest {

    private EmpresaDAO empresaDAO;

    // antes de todos os testes, não é pra usar o banco normal, e sim o de testes
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

    // antes de cada teste
    @BeforeEach
    void setUp() throws Exception {

        empresaDAO = new EmpresaDAO(); // cria o DAO que será testado

        limparBanco(); // apaga registros que eventualmente ficaram do teste anterior
    }

    @Test
    void salvar_quandoDadosForemValidos_devePersistirEmpresaEGerarId() throws Exception {

        Empresa empresa = new Empresa(
                "Gerdau Açominas",
                "11111111111111",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertNull(empresa.getId()); // antes de salvar o id tem que ser null

        empresaDAO.salvar(empresa);

        assertNotNull(empresa.getId()); // depois de salvar, recebeu id?
        assertTrue(empresa.getId() > 0); // ele é válido?

        // será que realmente foi salvo no banco?
        // vou procurar exatamente a empresa cujo ID acabou de ser gerado
        // eu abro uma conexão para verificar se foi salvo mesmo dentro do banco e comparo os resultados
        try (Connection conn = ConnectionFactory.getConnection();

             PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT nome,
                                       cnpj,
                                       endereco,
                                       segmento,
                                       status
                                FROM empresa
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, empresa.getId());

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next()); // a consulta encontrou pelo menos uma linha?

                assertAll(
                        () -> assertEquals(
                                "Gerdau Açominas",
                                rs.getString("nome")
                        ),

                        () -> assertEquals(
                                "11111111111111",
                                rs.getString("cnpj")
                        ),

                        () -> assertEquals(
                                "Ouro Branco",
                                rs.getString("endereco")
                        ),

                        () -> assertEquals(
                                "Siderurgia",
                                rs.getString("segmento")
                        ),

                        () -> assertEquals(
                                "ATIVADA",
                                rs.getString("status")
                        )
                );
            }
        }
    }

    @Test
    void salvar_quandoCnpjJaExistir_deveLancarRegistroDuplicadoException() throws Exception {

        inserirEmpresaDiretamente( // imaginar que a empresa já exista
                "Empresa existente",
                "22222222222222",
                Empresa.Status.ATIVADA
        );

        Empresa duplicada = new Empresa(
                "Outra empresa",
                "22222222222222",
                "São João del-Rei",
                "Tecnologia",
                Empresa.Status.ATIVADA
        );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> empresaDAO.salvar(duplicada)
        );

        assertNull(duplicada.getId()); // se o insert falhou por causar do registro duplicado
        // o objeto não pode ter recebido um ID falso
    }

    @Test
    void buscarPorId_quandoEmpresaExistir_deveReconstruirEmpresa()
            throws Exception {

        int id = inserirEmpresaDiretamente(
                "Empresa A",
                "33333333333333",
                Empresa.Status.DESATIVADA
        );

        Empresa resultado = empresaDAO.buscarPorId(id);

        assertNotNull(resultado); // encontrou alguma coisa?

        // compara todos os campos para ver se reconstruiu corretamente
        assertAll(
                () -> assertEquals(
                        id,
                        resultado.getId()
                ),

                () -> assertEquals(
                        "Empresa A",
                        resultado.getNome()
                ),

                () -> assertEquals(
                        "33333333333333",
                        resultado.getCnpj()
                ),

                () -> assertEquals(
                        "Ouro Branco",
                        resultado.getEndereco()
                ),

                () -> assertEquals(
                        "Siderurgia",
                        resultado.getSegmento()
                ),

                () -> assertEquals(
                        Empresa.Status.DESATIVADA,
                        resultado.getStatus()
                )
        );
    }

    @Test
    void buscarPorId_quandoEmpresaPossuirEquipamentos_deveCarregarEquipamentos() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa A",
                        "44444444444444",
                        Empresa.Status.ATIVADA
                );

        inserirEquipamentoDiretamente(
                empresaId,
                "Laminadora",
                "PAT-001"
        );

        inserirEquipamentoDiretamente(
                empresaId,
                "Empilhadeira",
                "PAT-002"
        );

        Empresa resultado = empresaDAO.buscarPorId(empresaId);

        assertNotNull(resultado);

        assertEquals(
                2,
                resultado
                        .getEquipamentos()
                        .size()
        );
    }

    @Test
    void listar_quandoExistiremEmpresas_deveRetornarEmpresasComSeusEquipamentos()
            throws Exception {

        int empresaComEquipamentos =
                inserirEmpresaDiretamente(
                        "Empresa A",
                        "55555555555555",
                        Empresa.Status.ATIVADA
                );

        inserirEquipamentoDiretamente(
                empresaComEquipamentos, // esse é o id
                "Laminadora",
                "PAT-003"
        );

        inserirEquipamentoDiretamente(
                empresaComEquipamentos,
                "Empilhadeira",
                "PAT-004"
        );

        inserirEmpresaDiretamente(
                "Empresa B",
                "66666666666666",
                Empresa.Status.DESATIVADA
        );

        List<Empresa> empresas = empresaDAO.listar();

        assertEquals(2, empresas.size());

        Empresa primeira =
                empresas.stream()
                        .filter(empresa -> empresa.getId().equals(empresaComEquipamentos))
                        .findFirst()
                        .orElseThrow();

        // pega a lista de empresas, passe um por uma, onde o id é igual a empresaComEquipamentos
        // pegue a primeira encontrada, se n encontrar nenhuma lança exceção

        // a empresa que deveria ter dois equipamentos veio com dois?
        assertEquals(2, primeira.getEquipamentos().size());
    }

    @Test
    void atualizar_quandoEmpresaExistir_devePersistirAlteracoes()
            throws Exception {

        int id = inserirEmpresaDiretamente(
                "Nome antigo",
                "77777777777777",
                Empresa.Status.ATIVADA
        );

        Empresa alterada = new Empresa(
                id,
                "Nome atualizado",
                "88888888888888",
                "Belo Horizonte",
                "Tecnologia",
                Empresa.Status.DESATIVADA
        );

        empresaDAO.atualizar(alterada);

        // ver lá no banco se alterou mesmo
        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT nome,
                                       cnpj,
                                       endereco,
                                       segmento,
                                       status
                                FROM empresa
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                assertTrue(rs.next());

                assertAll(
                        () -> assertEquals(
                                "Nome atualizado",
                                rs.getString("nome")
                        ),

                        () -> assertEquals(
                                "88888888888888",
                                rs.getString("cnpj")
                        ),

                        () -> assertEquals(
                                "Belo Horizonte",
                                rs.getString("endereco")
                        ),

                        () -> assertEquals(
                                "Tecnologia",
                                rs.getString("segmento")
                        ),

                        () -> assertEquals(
                                "DESATIVADA",
                                rs.getString("status")
                        )
                );
            }
        }
    }

    @Test
    void atualizar_quandoNovoCnpjJaPertencerAOutraEmpresa_deveLancarRegistroDuplicadoException()
            throws Exception {

        int primeiraId =
                inserirEmpresaDiretamente(
                        "Empresa A",
                        "99999999999991",
                        Empresa.Status.ATIVADA
                );

        inserirEmpresaDiretamente(
                "Empresa B",
                "99999999999992",
                Empresa.Status.ATIVADA
        );

        Empresa alterada = new Empresa(
                primeiraId,
                "Empresa A",
                "99999999999992",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertThrows(
                RegistroDuplicadoException.class,
                () -> empresaDAO.atualizar(alterada)
        );
    }

    @Test
    void atualizar_quandoEmpresaNaoExistir_deveLancarPersistenciaException() {

        Empresa empresa = new Empresa(
                999999,
                "Inexistente",
                "12312312312312",
                "Ouro Branco",
                "Siderurgia",
                Empresa.Status.ATIVADA
        );

        assertThrows(
                PersistenciaException.class,
                () -> empresaDAO.atualizar(empresa)
        );
    }

    @Test
    void deletar_quandoEmpresaExistir_deveRemoverRegistro() throws Exception {

        int id = inserirEmpresaDiretamente(
                "Empresa a remover",
                "10101010101010",
                Empresa.Status.ATIVADA
        );

        empresaDAO.deletar(id);

        // na hora que eu buscar, não pode ter nada no resultado
        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt =
                        conn.prepareStatement(
                                """
                                SELECT id
                                FROM empresa
                                WHERE id = ?
                                """
                        )
        ) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {

                assertFalse(rs.next());
            }
        }
    }

    @Test
    void deletar_quandoEmpresaPossuirEquipamento_deveLancarIntegridadeReferencialException() throws Exception {

        int empresaId =
                inserirEmpresaDiretamente(
                        "Empresa em uso",
                        "12121212121212",
                        Empresa.Status.ATIVADA
                );

        inserirEquipamentoDiretamente(
                empresaId,
                "Laminadora",
                "PAT-005"
        );

        assertThrows(
                IntegridadeReferencialException.class,
                () -> empresaDAO.deletar(empresaId)
        );
    }

    @Test
    void deletar_quandoEmpresaNaoExistir_deveLancarPersistenciaException() {

        assertThrows(
                PersistenciaException.class,
                () -> empresaDAO.deletar(999999)
        );
    }

    // é um salvar (não usa o salvar pq vai q ele tá quebrado) -> falar diretamente com o banco
    private int inserirEmpresaDiretamente(
            String nome,
            String cnpj,
            Empresa.Status status
    ) throws Exception {

        String sql = """
                INSERT INTO empresa
                    (nome, cnpj, endereco, segmento, status)
                VALUES
                    (?, ?, ?, ?, ?)
                """;

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt = conn.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            stmt.setString(1, nome);
            stmt.setString(2, cnpj);
            stmt.setString(3, "Ouro Branco");
            stmt.setString(4, "Siderurgia");
            stmt.setString(5, status.name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {

                assertTrue(rs.next());

                return rs.getInt(1); // retorna o id
            }
        }
    }

    private void inserirEquipamentoDiretamente(
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

        try (Connection conn = ConnectionFactory.getConnection();

                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            stmt.setString(2, codigoPatrimonio);
            stmt.setDate(3, java.sql.Date.valueOf(LocalDate.of(
                                    2020,
                                    1,
                                    1
                            )
                    )
            );

            stmt.setInt(4, empresaId);

            stmt.executeUpdate();
        }
    }


}
