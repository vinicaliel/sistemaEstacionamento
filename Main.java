



import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Inicializando banco de dados...");
        DatabaseSetup.initializeDatabase();
        
        Scanner scanner = new Scanner(System.in);
        // Instanciando o estacionamento com 10 vagas
        Estacionamento estacionamento = new Estacionamento(10); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        int opcao = 0;
        
        while (opcao != 6) {
            System.out.println("\n===== SISTEMA DE ESTACIONAMENTO =====");
            System.out.println("1. Cadastrar Veículo");
            System.out.println("2. Registrar Entrada");
            System.out.println("3. Registrar Saída");
            System.out.println("4. Listar Veículos Estacionados");
            System.out.println("5. Histórico de Movimentações");
            System.out.println("6. Sair do Sistema");
            System.out.print("Escolha uma opção: ");
            
            try {
                String input = scanner.nextLine();
                if (input.trim().isEmpty()) continue;
                opcao = Integer.parseInt(input);
                
                switch (opcao) {
                    case 1:
                        System.out.println("\n-- Cadastro de Veículo --");
                        System.out.print("Placa: ");
                        String placa = scanner.nextLine();
                        System.out.print("Modelo: ");
                        String modelo = scanner.nextLine();
                        System.out.print("Cor: ");
                        String cor = scanner.nextLine();
                        System.out.println("Tipos permitidos: 1 - Carro, 2 - Moto, 3 - Caminhonete");
                        System.out.print("Tipo do Veículo (1/2/3): ");
                        int tipo = Integer.parseInt(scanner.nextLine());
                        
                        Veiculo v;
                        if (tipo == 1) {
                            v = new Carro(placa, modelo, cor);
                        } else if (tipo == 2) {
                            v = new Moto(placa, modelo, cor);
                        } else if (tipo == 3) {
                            v = new Caminhonete(placa, modelo, cor);
                        } else {
                            System.out.println("Tipo de veículo inválido. Operação cancelada.");
                            break;
                        }
                        
                        estacionamento.cadastrarVeiculo(v);
                        break;
                        
                    case 2:
                        System.out.println("\n-- Registrar Entrada --");
                        System.out.print("Placa do veículo cadastrado: ");
                        String placaEntrada = scanner.nextLine();
                        System.out.print("Número da Vaga Desejada (1 a 10): ");
                        int vaga = Integer.parseInt(scanner.nextLine());
                        System.out.print("Horário de Entrada (formato HH:mm, ex: 13:00) [Pressione Enter para horário ATUAL]: ");
                        String strDataEntrada = scanner.nextLine();
                        
                        LocalDateTime dataEntrada;
                        if (strDataEntrada.trim().isEmpty()) {
                            dataEntrada = LocalDateTime.now();
                        } else {
                            java.time.LocalTime tempoEntrada = java.time.LocalTime.parse(strDataEntrada, formatter);
                            dataEntrada = LocalDateTime.of(java.time.LocalDate.now(), tempoEntrada);
                        }
                        
                        estacionamento.registrarEntrada(placaEntrada, vaga, dataEntrada);
                        break;
                        
                    case 3:
                        System.out.println("\n-- Registrar Saída e Pagamento --");
                        System.out.print("Placa do veículo estacionado: ");
                        String placaSaida = scanner.nextLine();
                        System.out.print("Horário de Saída (formato HH:mm, ex: 14:44) [Pressione Enter para horário ATUAL]: ");
                        String strDataSaida = scanner.nextLine();
                        
                        LocalDateTime dataSaida;
                        if (strDataSaida.trim().isEmpty()) {
                            dataSaida = LocalDateTime.now();
                        } else {
                            java.time.LocalTime tempoSaida = java.time.LocalTime.parse(strDataSaida, formatter);
                            dataSaida = LocalDateTime.of(java.time.LocalDate.now(), tempoSaida);
                        }
                        
                        estacionamento.registrarSaida(placaSaida, dataSaida);
                        break;
                        
                    case 4:
                        estacionamento.listarVeiculosEstacionados();
                        break;
                        
                    case 5:
                        estacionamento.historicoMovimentacoes();
                        break;
                        
                    case 6:
                        System.out.println("\nSaindo do sistema. Até logo!");
                        break;
                        
                    default:
                        System.out.println("Opção inválida! Escolha um número de 1 a 6.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nErro: Por favor, insira um número válido.");
            } catch (DateTimeParseException e) {
                System.out.println("\nErro: Formato de data/hora inválido. Use dd/MM/yyyy HH:mm");
            } catch (IllegalArgumentException e) {
                // Captura as exceções de regras de negócio jogadas pelo Estacionamento
                System.out.println("\nErro de Regra de Negócio: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\nErro inesperado: " + e.getMessage());
            }
        }
        
        scanner.close();
    }
}
