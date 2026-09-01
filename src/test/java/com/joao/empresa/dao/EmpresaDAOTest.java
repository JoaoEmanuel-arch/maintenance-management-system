package com.joao.empresa.dao;

import com.joao.empresa.database.ConnectionFactory;
import com.joao.empresa.model.Empresa;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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



}
