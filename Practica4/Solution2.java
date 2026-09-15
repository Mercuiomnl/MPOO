import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static String cifrarMensaje(String mensaje, String alfabetoInterior, int posicionInicial, int intervaloRotacion) {
        char[] msj = mensaje.toCharArray();
        char[] alfabeto = alfabetoInterior.toCharArray();
        char[] resultado = new char[msj.length];
        
        int shift = posicionInicial;
        int procesados = 0;
        
        for (int i = 0; i < msj.length; i++) {
            if (msj[i] == ' ') {
                resultado[i] = ' ';
            } else {
                int idx = msj[i] - 'A';
                
                int actualShift = shift % 26;
                int innerIndex = (idx - actualShift + 26) % 26;
                
                resultado[i] = alfabeto[innerIndex];
                
                procesados++;
                
                if (procesados == intervaloRotacion) {
                    shift++;
                    procesados = 0;
                }
            }
        }
        
        return new String(resultado);
    }
}

public class Solution2 {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String mensaje = bufferedReader.readLine();
        String alfabetoInterior = bufferedReader.readLine();
        int posicionInicial = Integer.parseInt(bufferedReader.readLine().trim());
        int intervaloRotacion = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.cifrarMensaje(mensaje, alfabetoInterior, posicionInicial, intervaloRotacion);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}