
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {
    public static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Criação das tabelas
            String sqlVeiculo = "CREATE TABLE IF NOT EXISTS veiculo (" +
                                "placa VARCHAR(20) PRIMARY KEY, " +
                                "modelo VARCHAR(100), " +
                                "cor VARCHAR(50), " +
                                "tipo VARCHAR(20))";
            stmt.execute(sqlVeiculo);

            String sqlVaga = "CREATE TABLE IF NOT EXISTS vaga (" +
                             "id SERIAL PRIMARY KEY, " +
                             "numero INT UNIQUE NOT NULL, " +
                             "ocupada BOOLEAN DEFAULT FALSE)";
            stmt.execute(sqlVaga);

            String sqlMovimentacao = "CREATE TABLE IF NOT EXISTS movimentacao (" +
                                     "id SERIAL PRIMARY KEY, " +
                                     "placa_veiculo VARCHAR(20) REFERENCES veiculo(placa), " +
                                     "numero_vaga INT REFERENCES vaga(numero), " +
                                     "data_entrada TIMESTAMP NOT NULL, " +
                                     "data_saida TIMESTAMP, " +
                                     "valor_pago DECIMAL(10, 2))";
            stmt.execute(sqlMovimentacao);

            // Popula as 10 vagas caso não existam
            for (int i = 1; i <= 10; i++) {
                String checkVaga = "INSERT INTO vaga (numero, ocupada) " +
                                   "SELECT " + i + ", FALSE " +
                                   "WHERE NOT EXISTS (SELECT 1 FROM vaga WHERE numero = " + i + ")";
                stmt.execute(checkVaga);
            }

            System.out.println("Banco de dados inicializado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
