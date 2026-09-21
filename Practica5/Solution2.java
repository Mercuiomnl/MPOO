import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static String evaluarLicencia(String fechaActual, String fechaVencimiento, String tipoLicencia, int renovacionesPrevias) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            
            Date dateActual = sdf.parse(fechaActual);
            Date dateVenc = sdf.parse(fechaVencimiento);
            
            long diffMillis = dateVenc.getTime() - dateActual.getTime();
            long diasRest = Math.round((double) diffMillis / (1000L * 60 * 60 * 24));
            
            String estado = "";
            if (diasRest > 30) {
                estado = "VIGENTE";
            } else if (diasRest >= 0) {
                estado = "PROXIMA_A_VENCER";
            } else if (diasRest >= -90) {
                estado = "VENCIDA";
            } else {
                estado = "BLOQUEADA";
            }
            
            if (estado.equals("BLOQUEADA")) {
                return String.format(Locale.US, "BLOQUEADA %d 0.00 NO_DISPONIBLE", diasRest);
            }
            
            double costoBase = 0.0;
            int aniosSumar = 0;
            int mesesSumar = 0;
            
            switch (tipoLicencia) {
                case "BASICA":
                    costoBase = 1000.00;
                    aniosSumar = 1;
                    break;
                case "PROFESIONAL":
                    costoBase = 1500.00;
                    aniosSumar = 2;
                    break;
                case "EMPRESARIAL":
                    costoBase = 2500.00;
                    aniosSumar = 3;
                    break;
                case "TEMPORAL":
                    costoBase = 600.00;
                    mesesSumar = 6;
                    break;
            }
            
            if (estado.equals("VIGENTE")) {
                costoBase *= 0.90;
            } else if (estado.equals("VENCIDA")) {
                costoBase *= 1.20;
            }
            
            if (renovacionesPrevias > 3) {
                costoBase *= 0.95;
            }
            
            Calendar cal = Calendar.getInstance();
            if (estado.equals("VENCIDA")) {
                cal.setTime(dateActual);
            } else {
                cal.setTime(dateVenc);
            }
            
            if (aniosSumar > 0) {
                cal.add(Calendar.YEAR, aniosSumar);
            }
            if (mesesSumar > 0) {
                cal.add(Calendar.MONTH, mesesSumar);
            }
            
            String nuevaFecha = sdf.format(cal.getTime());
            
            return String.format(Locale.US, "%s %d %.2f %s", estado, diasRest, costoBase, nuevaFecha);
            
        } catch (Exception e) {
            return "ERROR";
        }
    }
}

public class Solution2 {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String fechaActual = bufferedReader.readLine().trim();
        String fechaVencimiento = bufferedReader.readLine().trim();
        String tipoLicencia = bufferedReader.readLine().trim();
        int renovacionesPrevias = Integer.parseInt(bufferedReader.readLine().trim());

        String result = Result.evaluarLicencia(fechaActual, fechaVencimiento, tipoLicencia, renovacionesPrevias);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}