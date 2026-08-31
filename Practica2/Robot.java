import java.util.UUID;
import java.util.Objects;
import java.util.Random;

public class Robot {
    private UUID id;
    private String nombre;
    private Bateria bateria;
    private int armadura;
    
    public static int totalRobots = 0;
    private static Random random = new Random();

    public Robot(String nombre, int armaduraInicial) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.bateria = new Bateria(100.0);
        this.armadura = armaduraInicial;
        totalRobots++;
    }

    public void recibirDano(int cantidad) {
        this.armadura = Math.max(0, this.armadura - cantidad);
    }

    public void moverse(int distancia) {
        double consumo = distancia * (1.5 + random.nextDouble() * 2.0); 
        this.bateria.consumir(consumo);
    }

    public int atacar() {
        return 10 + random.nextInt(16); // Daño entre 10 y 25
    }
    
    public boolean estaOperativo() {
        return this.armadura > 0 && this.bateria.getNivel() > 0;
    }

    @Override
    public String toString() {
        return String.format("Robot[%s] Nombre: %s | Armadura: %d | Batería: %s", 
               id.toString().substring(0, 8), nombre, armadura, bateria);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Robot robot = (Robot) obj;
        return Objects.equals(id, robot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}