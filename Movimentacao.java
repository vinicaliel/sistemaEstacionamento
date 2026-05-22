

import java.time.LocalDateTime;

public class Movimentacao {
    private int id;
    private Veiculo veiculo;
    private Vaga vaga;
    private LocalDateTime dataEntrada;
    private LocalDateTime dataSaida;
    private double valorPago;
    
    public Movimentacao(int id, Veiculo veiculo, Vaga vaga, LocalDateTime dataEntrada) {
        this.id = id;
        this.veiculo = veiculo;
        this.vaga = vaga;
        this.dataEntrada = dataEntrada;
    }
    
    public int getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public Vaga getVaga() { return vaga; }
    
    public LocalDateTime getDataEntrada() { return dataEntrada; }
    
    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }
    
    public double getValorPago() { return valorPago; }
    public void setValorPago(double valorPago) { this.valorPago = valorPago; }
}
