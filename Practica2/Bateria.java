public class Bateria {
    private double nivel;

    public Bateria(double nivelInicial) {
        this.nivel = nivelInicial;
    }

    public void consumir(double cantidad) {
        this.nivel = Math.max(0, this.nivel - cantidad);
    }

    public double getNivel() {
        return nivel;
    }

    @Override
    public String toString() {
        return String.format("%.1f%%", nivel);
    }
}