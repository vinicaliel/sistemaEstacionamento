



import java.sql.*;
import java.time.LocalDateTime;
import java.time.Duration;

public class Estacionamento {
    
    public Estacionamento(int totalVagas) {
        // A inicialização agora é feita pelo DatabaseSetup
    }
    
    // 1. Cadastrar veículos
    public void cadastrarVeiculo(Veiculo veiculo) {
        String sql = "INSERT INTO veiculo (placa, modelo, cor, tipo) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, veiculo.getPlaca());
            stmt.setString(2, veiculo.getModelo());
            stmt.setString(3, veiculo.getCor());
            stmt.setString(4, veiculo.getTipo());
            
            stmt.executeUpdate();
            System.out.println("Veículo cadastrado com sucesso!");
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) { // Código de erro de restrição unique do Postgres
                throw new IllegalArgumentException("Placa já cadastrada no sistema.");
            }
            throw new RuntimeException("Erro ao cadastrar veículo: " + e.getMessage(), e);
        }
    }
    
    private Veiculo buscarVeiculo(String placa) {
        String sql = "SELECT * FROM veiculo WHERE placa = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String modelo = rs.getString("modelo");
                String cor = rs.getString("cor");
                String tipo = rs.getString("tipo");
                
                if (tipo.equalsIgnoreCase("Carro")) {
                    return new Carro(placa, modelo, cor);
                } else if (tipo.equalsIgnoreCase("Moto")) {
                    return new Moto(placa, modelo, cor);
                } else if (tipo.equalsIgnoreCase("Caminhonete")) {
                    return new Caminhonete(placa, modelo, cor);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar veículo: " + e.getMessage(), e);
        }
    }
    
    // 2. Registrar entrada
    public void registrarEntrada(String placa, int numeroVaga, LocalDateTime dataEntrada) {
        Veiculo veiculo = buscarVeiculo(placa);
        if (veiculo == null) {
            throw new IllegalArgumentException("Veículo não cadastrado. Cadastre o veículo primeiro.");
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verifica se o veículo já está estacionado
            String checkEstacionado = "SELECT 1 FROM movimentacao WHERE placa_veiculo = ? AND data_saida IS NULL";
            try (PreparedStatement stmt = conn.prepareStatement(checkEstacionado)) {
                stmt.setString(1, placa);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    throw new IllegalArgumentException("Este veículo já está estacionado no momento.");
                }
            }
            
            // Verifica se a vaga existe e não está ocupada
            String checkVaga = "SELECT ocupada FROM vaga WHERE numero = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkVaga)) {
                stmt.setInt(1, numeroVaga);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    throw new IllegalArgumentException("A vaga informada não existe.");
                }
                if (rs.getBoolean("ocupada")) {
                    throw new IllegalArgumentException("Esta vaga já está ocupada por outro veículo.");
                }
            }
            
            conn.setAutoCommit(false);
            try {
                // Ocupa a vaga
                String updateVaga = "UPDATE vaga SET ocupada = TRUE WHERE numero = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateVaga)) {
                    stmt.setInt(1, numeroVaga);
                    stmt.executeUpdate();
                }
                
                // Registra movimentação
                String insertMov = "INSERT INTO movimentacao (placa_veiculo, numero_vaga, data_entrada) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertMov)) {
                    stmt.setString(1, placa);
                    stmt.setInt(2, numeroVaga);
                    stmt.setTimestamp(3, Timestamp.valueOf(dataEntrada));
                    stmt.executeUpdate();
                }
                
                conn.commit();
                System.out.println("Entrada registrada com sucesso na vaga " + numeroVaga + ".");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar entrada: " + e.getMessage(), e);
        }
    }
    
    // 3. Registrar saída e 4. Calcular cobrança
    public void registrarSaida(String placa, LocalDateTime dataSaida) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Busca a movimentação aberta
            String sqlMov = "SELECT id, numero_vaga, data_entrada FROM movimentacao WHERE placa_veiculo = ? AND data_saida IS NULL";
            int idMovimentacao = 0;
            int numeroVaga = 0;
            LocalDateTime dataEntrada = null;
            
            try (PreparedStatement stmt = conn.prepareStatement(sqlMov)) {
                stmt.setString(1, placa);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idMovimentacao = rs.getInt("id");
                    numeroVaga = rs.getInt("numero_vaga");
                    dataEntrada = rs.getTimestamp("data_entrada").toLocalDateTime();
                } else {
                    throw new IllegalArgumentException("Veículo não está estacionado ou não possui registro de entrada em aberto.");
                }
            }
            
            if (dataSaida.isBefore(dataEntrada)) {
                throw new IllegalArgumentException("A data de saída não pode ser anterior à data de entrada.");
            }
            
            // 4. Calcular cobrança
            long minutosEstacionado = Duration.between(dataEntrada, dataSaida).toMinutes();
            long horas = (long) Math.ceil(minutosEstacionado / 60.0);
            if (horas <= 0) horas = 1; 
            
            double valorBase = 5.00;
            if (horas > 1) {
                valorBase += (horas - 1) * 3.00;
            }
            
            Veiculo veiculo = buscarVeiculo(placa);
            double valorFinal = veiculo.calcularValor(valorBase);
            
            conn.setAutoCommit(false);
            try {
                // Atualiza movimentação
                String updateMov = "UPDATE movimentacao SET data_saida = ?, valor_pago = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateMov)) {
                    stmt.setTimestamp(1, Timestamp.valueOf(dataSaida));
                    stmt.setDouble(2, valorFinal);
                    stmt.setInt(3, idMovimentacao);
                    stmt.executeUpdate();
                }
                
                // Libera a vaga
                String updateVaga = "UPDATE vaga SET ocupada = FALSE WHERE numero = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateVaga)) {
                    stmt.setInt(1, numeroVaga);
                    stmt.executeUpdate();
                }
                
                conn.commit();
                
                System.out.println("\n========================================");
                System.out.println("          COMPROVANTE DE SAÍDA");
                System.out.println("========================================");
                System.out.println("Placa: " + placa);
                System.out.println("Tempo estacionado: " + horas + " hora(s)");
                System.out.println("Valor base cobrado: R$ " + String.format("%.2f", valorBase));
                System.out.println("Tipo do veículo: " + veiculo.getTipo());
                System.out.println("----------------------------------------");
                System.out.println("VALOR FINAL DEVIDO: R$ " + String.format("%.2f", valorFinal));
                System.out.println("========================================\n");
                
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao registrar saída: " + e.getMessage(), e);
        }
    }
    
    // 5. Listar veículos estacionados no momento
    public void listarVeiculosEstacionados() {
        System.out.println("\n--- Veículos Estacionados Atualmente ---");
        String sql = "SELECT m.numero_vaga, v.placa, v.modelo, v.tipo, m.data_entrada " +
                     "FROM movimentacao m " +
                     "JOIN veiculo v ON m.placa_veiculo = v.placa " +
                     "WHERE m.data_saida IS NULL " +
                     "ORDER BY m.numero_vaga";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            boolean encontrou = false;
            while (rs.next()) {
                System.out.printf("Vaga: %02d | Placa: %s | Modelo: %s | Tipo: %s | Entrada: %s\n",
                    rs.getInt("numero_vaga"),
                    rs.getString("placa"),
                    rs.getString("modelo"),
                    rs.getString("tipo"),
                    rs.getTimestamp("data_entrada").toLocalDateTime().toString()
                );
                encontrou = true;
            }
            if (!encontrou) {
                System.out.println("Não há nenhum veículo estacionado no momento.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar veículos: " + e.getMessage());
        }
        System.out.println("----------------------------------------");
    }
    
    // 6. Histórico de movimentações
    public void historicoMovimentacoes() {
        System.out.println("\n--- Histórico Geral de Movimentações ---");
        String sql = "SELECT m.id, m.placa_veiculo, m.numero_vaga, m.data_entrada, m.data_saida, m.valor_pago " +
                     "FROM movimentacao m " +
                     "ORDER BY m.data_entrada DESC";
                     
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            boolean temRegistros = false;
            while (rs.next()) {
                temRegistros = true;
                Timestamp dataSaidaTs = rs.getTimestamp("data_saida");
                String dataSaida = (dataSaidaTs != null) ? dataSaidaTs.toLocalDateTime().toString() : "Ainda Estacionado";
                double valorPago = rs.getDouble("valor_pago");
                
                System.out.printf("ID Movimentação: %d | Placa: %s | Vaga: %02d | Entrada: %s | Saída: %s | Valor Pago: R$ %.2f\n",
                    rs.getInt("id"),
                    rs.getString("placa_veiculo"),
                    rs.getInt("numero_vaga"),
                    rs.getTimestamp("data_entrada").toLocalDateTime().toString(),
                    dataSaida,
                    valorPago
                );
            }
            if (!temRegistros) {
                System.out.println("Nenhuma movimentação foi registrada ainda.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar histórico: " + e.getMessage());
        }
        System.out.println("----------------------------------------");
    }
}
