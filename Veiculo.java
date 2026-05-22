
public abstract class Veiculo {
    private String placa;
    private String modelo;
    private String cor;
    
    public Veiculo(String placa, String modelo, String cor) {
        this.placa = placa;
        this.modelo = modelo;
        this.cor = cor;
    }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    
    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }
    
    // Método abstrato ou polimórfico para calcular valor
    // O valor base é calculado em Movimentacao/Estacionamento (R$ 5 a 1ª hora, R$ 3 as seguintes)
    // Esse método vai aplicar o multiplicador específico de cada classe (polimorfismo)
    public abstract double calcularValor(double valorBase);
    
    public abstract String getTipo();
}
