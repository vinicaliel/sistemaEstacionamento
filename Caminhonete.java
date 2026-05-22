
public class Caminhonete extends Veiculo {
    public Caminhonete(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }
    
    @Override
    public double calcularValor(double valorBase) {
        return valorBase * 1.50; // Caminhonete paga 150% do valor
    }
    
    @Override
    public String getTipo() {
        return "Caminhonete";
    }
}
