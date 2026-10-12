import java.io.*;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

/*
 * =====================================================
 * TIPOS PROPORCIONADOS POR HACKERRANK
 * =====================================================
 */
enum TipoReembolso {
    ALIMENTOS,
    TRANSPORTE,
    HOSPEDAJE
}

enum ResultadoValidacion {
    APROBADO,
    SIN_COMPROBANTE,
    FUERA_DE_PERIODO,
    EXCEDE_LIMITE,
    REQUIERE_AUTORIZACION
}

final class ReembolsoInputDTO {
    private final TipoReembolso tipo;
    private final String folio;
    private final String empleado;
    private final double monto;
    private final LocalDate fechaGasto;
    private final boolean tieneComprobante;
    private final boolean autorizadoGerente;
    private final LocalDate fechaEvaluacion;

    public ReembolsoInputDTO(
            TipoReembolso tipo, String folio, String empleado, double monto,
            LocalDate fechaGasto, boolean tieneComprobante,
            boolean autorizadoGerente, LocalDate fechaEvaluacion) {
        this.tipo = tipo;
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public TipoReembolso getTipo() { return tipo; }
    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
    public LocalDate getFechaEvaluacion() { return fechaEvaluacion; }
}

final class ReembolsoOutputDTO {
    private final String folio;
    private final TipoReembolso tipo;
    private final ResultadoValidacion resultado;

    public ReembolsoOutputDTO(String folio, TipoReembolso tipo, ResultadoValidacion resultado) {
        this.folio = folio;
        this.tipo = tipo;
        this.resultado = resultado;
    }

    public String getFolio() { return folio; }
    public TipoReembolso getTipo() { return tipo; }
    public ResultadoValidacion getResultado() { return resultado; }
}

/*
 * =====================================================
 * 1 Y 2. JERARQUIA DE REEMBOLSOS Y CONSTRUCTORES
 * =====================================================
 */
abstract class Reembolso {
    
    // Atributos privados
    private String folio;
    private String empleado;
    private double monto;
    private LocalDate fechaGasto;
    private boolean tieneComprobante;
    private boolean autorizadoGerente;

    // Constructor principal
    public Reembolso(String folio, String empleado, double monto, LocalDate fechaGasto, boolean tieneComprobante, boolean autorizadoGerente) {
        this.folio = folio;
        this.empleado = empleado;
        this.monto = monto;
        this.fechaGasto = fechaGasto;
        this.tieneComprobante = tieneComprobante;
        this.autorizadoGerente = autorizadoGerente;
    }

    // Constructor sobrecargado
    public Reembolso(String folio, String empleado, double monto) {
        this(folio, empleado, monto, LocalDate.now(), false, false);
    }

    public abstract double getLimitePermitido();
    public abstract TipoReembolso getTipo();

    // Solo getters, sin setters
    public String getFolio() { return folio; }
    public String getEmpleado() { return empleado; }
    public double getMonto() { return monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public boolean tieneComprobante() { return tieneComprobante; }
    public boolean estaAutorizadoGerente() { return autorizadoGerente; }
}

class ReembolsoAlimentos extends Reembolso {
    public ReembolsoAlimentos(String f, String e, double m, LocalDate fg, boolean tc, boolean ag) {
        super(f, e, m, fg, tc, ag);
    }
    @Override public double getLimitePermitido() { return 1000.0; }
    @Override public TipoReembolso getTipo() { return TipoReembolso.ALIMENTOS; }
}

class ReembolsoTransporte extends Reembolso {
    public ReembolsoTransporte(String f, String e, double m, LocalDate fg, boolean tc, boolean ag) {
        super(f, e, m, fg, tc, ag);
    }
    @Override public double getLimitePermitido() { return 1500.0; }
    @Override public TipoReembolso getTipo() { return TipoReembolso.TRANSPORTE; }
}

class ReembolsoHospedaje extends Reembolso {
    public ReembolsoHospedaje(String f, String e, double m, LocalDate fg, boolean tc, boolean ag) {
        super(f, e, m, fg, tc, ag);
    }
    @Override public double getLimitePermitido() { return 4000.0; }
    @Override public TipoReembolso getTipo() { return TipoReembolso.HOSPEDAJE; }
}

/*
 * =====================================================
 * 3. FACTORY METHOD
 * =====================================================
 */
abstract class RegistroReembolso {
    public abstract Reembolso crearReembolso(ReembolsoInputDTO input);
}

class RegistroAlimentos extends RegistroReembolso {
    @Override public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoAlimentos(input.getFolio(), input.getEmpleado(), input.getMonto(), input.getFechaGasto(), input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

class RegistroTransporte extends RegistroReembolso {
    @Override public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoTransporte(input.getFolio(), input.getEmpleado(), input.getMonto(), input.getFechaGasto(), input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

class RegistroHospedaje extends RegistroReembolso {
    @Override public Reembolso crearReembolso(ReembolsoInputDTO input) {
        return new ReembolsoHospedaje(input.getFolio(), input.getEmpleado(), input.getMonto(), input.getFechaGasto(), input.tieneComprobante(), input.estaAutorizadoGerente());
    }
}

/*
 * =====================================================
 * 4. CHAIN OF RESPONSIBILITY
 * =====================================================
 */
abstract class ValidadorReembolso {
    protected ValidadorReembolso siguiente;

    public void setSiguiente(ValidadorReembolso siguiente) {
        this.siguiente = siguiente;
    }

    public abstract ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion);

    protected ResultadoValidacion evaluarSiguiente(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (siguiente != null) {
            return siguiente.validar(reembolso, fechaEvaluacion);
        }
        return ResultadoValidacion.APROBADO;
    }
}

// 5. REGLAS
class ValidadorComprobante extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (!reembolso.tieneComprobante()) {
            return ResultadoValidacion.SIN_COMPROBANTE;
        }
        return evaluarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorPeriodo extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        long diasTranscurridos = ChronoUnit.DAYS.between(reembolso.getFechaGasto(), fechaEvaluacion);
        if (diasTranscurridos > 30) {
            return ResultadoValidacion.FUERA_DE_PERIODO;
        }
        return evaluarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorLimite extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > reembolso.getLimitePermitido()) {
            return ResultadoValidacion.EXCEDE_LIMITE;
        }
        return evaluarSiguiente(reembolso, fechaEvaluacion);
    }
}

class ValidadorGerencia extends ValidadorReembolso {
    @Override
    public ResultadoValidacion validar(Reembolso reembolso, LocalDate fechaEvaluacion) {
        if (reembolso.getMonto() > 2000 && !reembolso.estaAutorizadoGerente()) {
            return ResultadoValidacion.REQUIERE_AUTORIZACION;
        }
        return evaluarSiguiente(reembolso, fechaEvaluacion);
    }
}

/*
 * =====================================================
 * 6. METODO PRINCIPAL DEL RETO
 * =====================================================
 */
class Result {
    public static ReembolsoOutputDTO procesarReembolso(ReembolsoInputDTO input) {
        
        // 1. Factory Method 
        RegistroReembolso creador = null;
        switch (input.getTipo()) {
            case ALIMENTOS:
                creador = new RegistroAlimentos();
                break;
            case TRANSPORTE:
                creador = new RegistroTransporte();
                break;
            case HOSPEDAJE:
                creador = new RegistroHospedaje();
                break;
        }
        Reembolso reembolso = creador.crearReembolso(input);

        // 2. Construir la cadena de validadores 
        ValidadorReembolso validadorComprobante = new ValidadorComprobante();
        ValidadorReembolso validadorPeriodo = new ValidadorPeriodo();
        ValidadorReembolso validadorLimite = new ValidadorLimite();
        ValidadorReembolso validadorGerencia = new ValidadorGerencia();

        // Enlazar la cadena
        validadorComprobante.setSiguiente(validadorPeriodo);
        validadorPeriodo.setSiguiente(validadorLimite);
        validadorLimite.setSiguiente(validadorGerencia);

        // 3 y 4. Ejecutar desde el primero
        ResultadoValidacion resultado = validadorComprobante.validar(reembolso, input.getFechaEvaluacion());

        // 5. Retornar DTO
        return new ReembolsoOutputDTO(reembolso.getFolio(), reembolso.getTipo(), resultado);
    }
}

public class Solution {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String lineaFecha = br.readLine();
        if (lineaFecha == null || lineaFecha.trim().isEmpty()) return;
        LocalDate fechaEvaluacion = LocalDate.parse(lineaFecha.trim());

        String lineaSolicitud = br.readLine();
        String[] datos = lineaSolicitud.split("\\|");

        TipoReembolso tipo = TipoReembolso.valueOf(datos[0].trim());
        String folio = datos[1].trim();
        String empleado = datos[2].trim();
        double monto = Double.parseDouble(datos[3].trim());
        LocalDate fechaGasto = LocalDate.parse(datos[4].trim());
        boolean tieneComprobante = Boolean.parseBoolean(datos[5].trim());
        boolean autorizadoGerente = Boolean.parseBoolean(datos[6].trim());

        ReembolsoInputDTO input = new ReembolsoInputDTO(
                tipo, folio, empleado, monto, fechaGasto,
                tieneComprobante, autorizadoGerente, fechaEvaluacion
        );

        ReembolsoOutputDTO output = Result.procesarReembolso(input);

        System.out.println("REEMBOLSO " + output.getFolio());
        System.out.println("TIPO " + output.getTipo());
        System.out.println("RESULTADO " + output.getResultado());
    }
}