
public class Vaga {
    private int id;
    private int numero;
    private boolean ocupada;
    
    public Vaga(int id, int numero) {
        this.id = id;
        this.numero = numero;
        this.ocupada = false; // Por padrão, a vaga nasce livre
    }
    
    public int getId() { return id; }
    public int getNumero() { return numero; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }
}
