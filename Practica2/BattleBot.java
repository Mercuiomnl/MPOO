public class BattleBot {
    public static void main(String[] args) {
        Robot bot1 = new Robot("Destructor", 100);
        Robot bot2 = new Robot("Aniquilador", 100);
        Robot bot3 = new Robot("Triturador", 120);

        System.out.println("Total de robots creados: " + Robot.totalRobots);
        
        System.out.println("\n--- Estado Inicial ---");
        System.out.println(bot1);
        System.out.println(bot2);
        System.out.println(bot3);

        System.out.println("\n--- Batalla ---");
        bot1.moverse(5);
        int dano = bot1.atacar();
        bot2.recibirDano(dano);
        System.out.println("Destructor atacó a Aniquilador causando " + dano + " de daño.");

        System.out.println("\n--- Estado Final ---");
        System.out.println(bot1);
        System.out.println(bot2);

        System.out.println("\n--- Referencias e Igualdad ---");
        Robot robotFavorito = bot1;
        Robot clon = new Robot("Destructor", 100);
        
        robotFavorito.recibirDano(50);
        System.out.println("Estado de bot1 tras dañar a robotFavorito: " + bot1.estaOperativo());
        
        System.out.println("bot1.equals(clon): " + bot1.equals(clon)); 
        System.out.println("bot1.equals(robotFavorito): " + bot1.equals(robotFavorito)); 
    }
}