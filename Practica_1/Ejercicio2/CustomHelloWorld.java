import java.util.Scanner;

public class CustomHelloWorld {
    public static void main(String[]args){
        Scanner scanner = new Scanner(System.in);
        System.out.println("===CUSTOM HELLO WORLD===");
        System.out.print("Ingrese su nombre: ");
        String nombre = scanner.nextLine();
        System.out.println("Hola ," + nombre + "¡Que gusto saludarte!");
        scanner.close();
    }
    
}
