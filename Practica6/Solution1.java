import java.io.*;
import java.util.*;

enum TipoAsistente {
    ALUMNO,
    PROFESOR,
    INVITADO
}

enum AccionEvento {
    REGISTRO,
    ENTRADA,
    SALIDA,
    ENTRADA_MASIVA
}

enum EstadoEntrada {
    AUTORIZADO,
    NO_REGISTRADO,
    YA_DENTRO,
    AFORO_COMPLETO
}

enum EstadoSalida {
    AUTORIZADA,
    NO_REGISTRADO,
    NO_ESTA_DENTRO
}

final class AsistenteDTO {

    private final String id;
    private final String nombre;
    private final String tipo;

    public AsistenteDTO(
            String id,
            String nombre,
            String tipo) {

        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }
}

final class ResultadoEntradaDTO {

    private final int idAsistente;
    private final EstadoEntrada estado;

    public ResultadoEntradaDTO(
            int idAsistente,
            EstadoEntrada estado) {

        this.idAsistente = idAsistente;
        this.estado = estado;
    }

    public int getIdAsistente() {
        return idAsistente;
    }

    public EstadoEntrada getEstado() {
        return estado;
    }
}

final class ReporteDTO {

    private final int registrados;
    private final int disponibles;
    private final int aforo;
    private final int alumnos;
    private final int profesores;
    private final int invitados;
    private final double ocupacion;

    public ReporteDTO(
            int registrados,
            int disponibles,
            int aforo,
            int alumnos,
            int profesores,
            int invitados,
            double ocupacion) {

        this.registrados = registrados;
        this.disponibles = disponibles;
        this.aforo = aforo;
        this.alumnos = alumnos;
        this.profesores = profesores;
        this.invitados = invitados;
        this.ocupacion = ocupacion;
    }

    public int getRegistrados() {
        return registrados;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public int getAforo() {
        return aforo;
    }

    public int getAlumnos() {
        return alumnos;
    }

    public int getProfesores() {
        return profesores;
    }

    public int getInvitados() {
        return invitados;
    }

    public double getOcupacion() {
        return ocupacion;
    }
}

interface ControlAccesoService {

    int AFORO_MAXIMO = 10;

    boolean registrarAsistente(
            AsistenteDTO asistente);

    EstadoEntrada registrarEntrada(
            int idAsistente);

    EstadoSalida registrarSalida(
            int idAsistente);

    List<ResultadoEntradaDTO>
            registrarEntradasMasivas(
                    List<Integer> identificadores);

    ReporteDTO generarReporte();
}

final class Asistente {

    private final int id;
    private final String nombre;
    private final TipoAsistente tipo;

    public Asistente(int id, String nombre, TipoAsistente tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoAsistente getTipo() {
        return tipo;
    }
}

class ControlAccesoServiceImpl implements ControlAccesoService {

    private static final Map<Integer, Asistente> REGISTRADOS = new HashMap<>();
    private final Set<Integer> dentro = new HashSet<>();

    public ControlAccesoServiceImpl() {
        REGISTRADOS.clear();
    }

    @Override
    public boolean registrarAsistente(AsistenteDTO dto) {
        if (dto == null || dto.getId() == null || dto.getNombre() == null || dto.getTipo() == null) {
            return false;
        }
        try {
            int id = Integer.parseInt(dto.getId().trim());
            String nombre = dto.getNombre().trim();
            if (id <= 0 || nombre.isEmpty()) {
                return false;
            }
            TipoAsistente tipo = TipoAsistente.valueOf(dto.getTipo().trim());

            if (REGISTRADOS.containsKey(id)) {
                return false;
            }

            Asistente nuevo = new Asistente(id, nombre, tipo);
            REGISTRADOS.put(id, nuevo);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public EstadoEntrada registrarEntrada(int idAsistente) {
        if (!REGISTRADOS.containsKey(idAsistente)) {
            return EstadoEntrada.NO_REGISTRADO;
        }
        if (dentro.contains(idAsistente)) {
            return EstadoEntrada.YA_DENTRO;
        }
        if (dentro.size() >= AFORO_MAXIMO) {
            return EstadoEntrada.AFORO_COMPLETO;
        }
        dentro.add(idAsistente);
        return EstadoEntrada.AUTORIZADO;
    }

    @Override
    public EstadoSalida registrarSalida(int idAsistente) {
        if (!REGISTRADOS.containsKey(idAsistente)) {
            return EstadoSalida.NO_REGISTRADO;
        }
        if (!dentro.contains(idAsistente)) {
            return EstadoSalida.NO_ESTA_DENTRO;
        }
        dentro.remove(idAsistente);
        return EstadoSalida.AUTORIZADA;
    }

    @Override
    public List<ResultadoEntradaDTO> registrarEntradasMasivas(List<Integer> identificadores) {
        List<ResultadoEntradaDTO> resultados = new ArrayList<>();
        if (identificadores == null) {
            return resultados;
        }

        for (Integer id : identificadores) {
            if (dentro.size() >= AFORO_MAXIMO) {
                break;
            }
            if (id != null) {
                EstadoEntrada estado = registrarEntrada(id);
                resultados.add(new ResultadoEntradaDTO(id, estado));
                if (dentro.size() >= AFORO_MAXIMO) {
                    break;
                }
            }
        }
        return resultados;
    }

    @Override
    public ReporteDTO generarReporte() {
        int totalRegistrados = REGISTRADOS.size();
        int aforoActual = dentro.size();
        int lugaresDisponibles = AFORO_MAXIMO - aforoActual;

        int conteoAlumnos = 0;
        int conteoProfesores = 0;
        int conteoInvitados = 0;

        for (Integer id : dentro) {
            Asistente a = REGISTRADOS.get(id);
            if (a != null) {
                switch (a.getTipo()) {
                    case ALUMNO:
                        conteoAlumnos++;
                        break;
                    case PROFESOR:
                        conteoProfesores++;
                        break;
                    case INVITADO:
                        conteoInvitados++;
                        break;
                }
            }
        }

        double porcentajeOcupacion = (aforoActual * 100.0) / AFORO_MAXIMO;

        return new ReporteDTO(
                totalRegistrados,
                lugaresDisponibles,
                aforoActual,
                conteoAlumnos,
                conteoProfesores,
                conteoInvitados,
                porcentajeOcupacion
        );
    }
}

public class Solution1 {

    public static void main(String[] args)
            throws Exception {

        Locale.setDefault(Locale.US);

        BufferedReader br =
                new BufferedReader(
                        new InputStreamReader(System.in));

        String asistentes =
                br.readLine();

        String operaciones =
                br.readLine();


        ControlAccesoService servicio =
                new ControlAccesoServiceImpl();


        if (asistentes != null
                && !asistentes.isBlank()
                && !asistentes.equals("-")) {

            String[] registros =
                    asistentes.split(";");


            for (String registro : registros) {

                String[] datos =
                        registro.split("\\|", 3);


                AsistenteDTO dto =
                        new AsistenteDTO(
                                datos[0].trim(),
                                datos[1].trim(),
                                datos[2].trim()
                        );


                servicio.registrarAsistente(dto);
            }
        }


        StringBuilder salida =
                new StringBuilder();


        if (operaciones != null
                && !operaciones.isBlank()
                && !operaciones.equals("-")) {

            String[] lista =
                    operaciones.split(";");


            for (String operacion : lista) {

                String[] datos =
                        operacion.split("\\|", 2);

                String accion =
                        datos[0].trim();


                if (accion.equals("ENTRADA")) {

                    int id =
                            Integer.parseInt(
                                    datos[1].trim());


                    EstadoEntrada estado =
                            servicio
                            .registrarEntrada(id);


                    salida.append("ENTRADA ")
                            .append(id)
                            .append(" ")
                            .append(estado)
                            .append("\n");
                }


                else if (accion.equals("SALIDA")) {

                    int id =
                            Integer.parseInt(
                                    datos[1].trim());


                    EstadoSalida estado =
                            servicio
                            .registrarSalida(id);


                    salida.append("SALIDA ")
                            .append(id)
                            .append(" ")
                            .append(estado)
                            .append("\n");
                }


                else if (accion.equals("MASIVA")) {

                    String[] ids =
                            datos[1].split(",");


                    List<Integer> identificadores =
                            new ArrayList<>();


                    for (String id : ids) {

                        identificadores.add(
                                Integer.parseInt(
                                        id.trim()));
                    }


                    List<ResultadoEntradaDTO>
                            resultados =
                            servicio
                            .registrarEntradasMasivas(
                                    identificadores);


                    if (resultados == null) {

                        throw new IllegalStateException(
                                "registrarEntradasMasivas "
                                + "no debe regresar null");
                    }


                    for (ResultadoEntradaDTO resultado
                            : resultados) {

                        salida.append("ENTRADA ")
                                .append(
                                    resultado
                                    .getIdAsistente())
                                .append(" ")
                                .append(
                                    resultado
                                    .getEstado())
                                .append("\n");
                    }
                }
            }
        }


        ReporteDTO reporte =
                servicio.generarReporte();


        if (reporte == null) {

            throw new IllegalStateException(
                    "generarReporte no debe regresar null");
        }


        salida.append(
                String.format(
                        "REPORTE %d %d %d %d %d %d %.2f",
                        reporte.getRegistrados(),
                        reporte.getDisponibles(),
                        reporte.getAforo(),
                        reporte.getAlumnos(),
                        reporte.getProfesores(),
                        reporte.getInvitados(),
                        reporte.getOcupacion()
                )
        );


        System.out.print(
                salida.toString());
    }
}