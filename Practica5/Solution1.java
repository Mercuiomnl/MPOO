import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

class Result {

    public static String calcularEstancia(String tipoVehiculo, String fechaEntrada, String horaEntrada, String fechaSalida, String horaSalida) {
        try {
            tipoVehiculo = tipoVehiculo.trim();
            fechaEntrada = fechaEntrada.trim();
            horaEntrada = horaEntrada.trim();
            fechaSalida = fechaSalida.trim();
            horaSalida = horaSalida.trim();

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            sdf.setLenient(false);
            
            Date dateEntrada = sdf.parse(fechaEntrada + " " + horaEntrada);
            Date dateSalida = sdf.parse(fechaSalida + " " + horaSalida);
            
            if (dateSalida.compareTo(dateEntrada) <= 0) {
                return "INVALID";
            }
            
            long diffMillis = dateSalida.getTime() - dateEntrada.getTime();
            int horasCobradas = (int) ((diffMillis + 3599999) / 3600000);
            
            double tarifaHora = 0;
            double tarifaMax24h = 0; 
            
            switch (tipoVehiculo) {
                case "MOTOCICLETA":
                    tarifaHora = 15.0;
                    tarifaMax24h = 150.0; 
                    break;
                case "AUTOMOVIL":
                    tarifaHora = 25.0;
                    tarifaMax24h = 250.0; 
                    break;
                case "CAMIONETA":
                    tarifaHora = 30.0;    
                    tarifaMax24h = 300.0; 
                    break;
                case "ELECTRICO":
                    tarifaHora = 20.0;
                    tarifaMax24h = 200.0; 
                    break;
                default:
                    return "INVALID";
            }
            
            int bloques24 = horasCobradas / 24;
            int horasSobrantes = horasCobradas % 24;
            
            double costoSobrante = horasSobrantes * tarifaHora;
            if (costoSobrante > tarifaMax24h) {
                costoSobrante = tarifaMax24h;
            }
            
            double costoBase = (bloques24 * tarifaMax24h) + costoSobrante;
            
            Calendar calEntrada = Calendar.getInstance();
            calEntrada.setTime(dateEntrada);
            Calendar calSalida = Calendar.getInstance();
            calSalida.setTime(dateSalida);
            
            int diaEntrada = calEntrada.get(Calendar.DAY_OF_WEEK);
            int diaSalida = calSalida.get(Calendar.DAY_OF_WEEK);
            
            boolean esFinSemana = (diaEntrada == Calendar.SATURDAY || diaEntrada == Calendar.SUNDAY ||
                                   diaSalida == Calendar.SATURDAY || diaSalida == Calendar.SUNDAY);
                                   
            boolean esNocturna = (calEntrada.get(Calendar.HOUR_OF_DAY) >= 20 ||
                                  calSalida.get(Calendar.HOUR_OF_DAY) < 6 ||
                                  !fechaEntrada.equals(fechaSalida));
                                  
            if (esFinSemana) {
                costoBase *= 1.20;
            }
            if (esNocturna) {
                costoBase *= 1.15;
            }
            if (tipoVehiculo.equals("ELECTRICO")) {
                costoBase *= 0.90;
            }
            
            String tipoEstancia = "NORMAL";
            if (esFinSemana && esNocturna) {
                tipoEstancia = "MIXTA";
            } else if (esFinSemana) {
                tipoEstancia = "FIN_SEMANA";
            } else if (esNocturna) {
                tipoEstancia = "NOCTURNA";
            }
            
            return String.format(Locale.US, "%d %.2f %s", horasCobradas, costoBase, tipoEstancia);
            
        } catch (Exception e) {
            return "INVALID";
        }
    }
}

public class Solution1 {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String tipoVehiculo = bufferedReader.readLine();
        String fechaEntrada = bufferedReader.readLine();
        String horaEntrada = bufferedReader.readLine();
        String fechaSalida = bufferedReader.readLine();
        String horaSalida = bufferedReader.readLine();

        String result = Result.calcularEstancia(tipoVehiculo, fechaEntrada, horaEntrada, fechaSalida, horaSalida);

        bufferedWriter.write(result);
        bufferedWriter.newLine();

        bufferedReader.close();
        bufferedWriter.close();
    }
}