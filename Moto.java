
public class Moto extends Veiculo {
    public Moto(String placa, String modelo, String cor) {
        super(placa, modelo, cor);
    }
    
    @Override
    public double calcularValor(double valorBase) {
        return valorBase * 0.50; // Moto paga 50% do valor
    }
    
    @Override
    public String getTipo() {
        return "Moto";
    }
}
