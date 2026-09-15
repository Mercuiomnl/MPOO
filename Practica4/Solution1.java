import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static int[] detectarZonaAjuste(int[] vagones) {
        int n = vagones.length;
        
        if (n <= 1) {
            return new int[]{-1, -1, 0};
        }

        int inicio = -1;
        int fin = -1;

        for (int i = 0; i < n - 1; i++) {
            if (vagones[i] > vagones[i + 1]) {
                inicio = i;
                break;
            }
        }

        if (inicio == -1) {
            return new int[]{-1, -1, 0};
        }

        for (int i = n - 1; i > 0; i--) {
            if (vagones[i] < vagones[i - 1]) {
                fin = i;
                break;
            }
        }

        int minimo = vagones[inicio];
        int maximo = vagones[inicio];
        
        for (int i = inicio; i <= fin; i++) {
            if (vagones[i] < minimo) {
                minimo = vagones[i];
            }
            if (vagones[i] > maximo) {
                maximo = vagones[i];
            }
        }

        for (int i = 0; i < inicio; i++) {
            if (vagones[i] > minimo) {
                inicio = i;
                break;
            }
        }

        for (int i = n - 1; i > fin; i--) {
            if (vagones[i] < maximo) {
                fin = i;
                break;
            }
        }

        int longitud = fin - inicio + 1;
        
        int[] resultado = new int[3];
        resultado[0] = inicio;
        resultado[1] = fin;
        resultado[2] = longitud;
        
        return resultado;
    }
}

public class Solution1 {

    public static void main(String[] args) throws IOException {

        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(System.in));

        BufferedWriter bufferedWriter =
                new BufferedWriter(
                        new FileWriter(System.getenv("OUTPUT_PATH"))
                );

        String linea = bufferedReader.readLine();

        if (linea != null && !linea.trim().isEmpty()) {
            String[] datos = linea.trim().split("\\s+");

            int[] vagones = new int[datos.length];

            for (int i = 0; i < datos.length; i++) {
                vagones[i] = Integer.parseInt(datos[i]);
            }

            int[] result = Result.detectarZonaAjuste(vagones);

            for (int i = 0; i < result.length; i++) {
                bufferedWriter.write(String.valueOf(result[i]));

                if (i != result.length - 1) {
                    bufferedWriter.write(" ");
                }
            }
        } else {
            bufferedWriter.write("-1 -1 0");
        }

        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}