package com.joao.empresa.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Cria conexão com o MySQL, mas permite trocar os dados sem alterar o código
public class ConnectionFactory {

    // Valores padrão
    private static final String URL_PADRAO =
            "jdbc:mysql://localhost:3306/manutencao_db";

    private static final String USER_PADRAO = "root";
    private static final String PASSWORD_PADRAO = "root";

    public static Connection getConnection() throws SQLException {

        // existe db.url passada pela JVM? se sim usa, se não usa URL_PADRAO
        String url = System.getProperty(
                "db.url",
                URL_PADRAO
        );

        String user = System.getProperty(
                "db.user",
                USER_PADRAO
        );

        String password = System.getProperty(
                "db.password",
                PASSWORD_PADRAO
        );

        // conecta no banco usando esses dados e me devolva uma connection
        return DriverManager.getConnection(
                url,
                user,
                password
        );
    }
}
