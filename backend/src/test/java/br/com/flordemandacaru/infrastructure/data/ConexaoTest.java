package br.com.flordemandacaru.infrastructure.data;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ConexaoTest {

    @Test
    void deveConectarAoPostgreSQL() throws SQLException {
        String senha = System.getenv("DB_PASSWORD");

        assertNotNull(senha, "Configure a variável DB_PASSWORD");

        try (Connection conexao = Conexao.conectar(senha)) {
            assertNotNull(conexao);
            assertFalse(conexao.isClosed());
            assertEquals(
                "flor_de_mandacaru",
                conexao.getCatalog()
            );
        }
    }
}
