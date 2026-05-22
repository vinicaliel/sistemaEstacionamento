
public class Carro extends Veiculo {
    public Carro(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }
    
    @Override
    public double calcularValor(double valorBase) {
        return valorBase; // Carro paga 100% do valor
    }
    
    @Override
    public String getTipo() {
        return "Carro";
    }
}
