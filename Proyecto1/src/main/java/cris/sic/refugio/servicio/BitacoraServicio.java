package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Bitacora;
import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.persistencia.BaseDatosMemoria;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// Servicio para registrar y persistir bitacoras de acciones, errores y operaciones CRUD por pestaña
public class BitacoraServicio {

    private static final String ARCHIVO_ACCIONES = "bitacora_acciones.txt";
    private static final String ARCHIVO_ERRORES = "bitacora_errores.txt";
    private static final String ARCHIVO_BITACORA_ANIMALES = "bitacora_animales.txt";
    private static final String ARCHIVO_BITACORA_ADOPTANTES = "bitacora_adoptantes.txt";
    private static final String ARCHIVO_BITACORA_SOLICITUDES = "bitacora_solicitudes.txt";
    private static final String ARCHIVO_BITACORA_RESCATES = "bitacora_rescates.txt";
    private static final String ARCHIVO_BITACORA_UBICACIONES = "bitacora_ubicaciones.txt";

    // Modulo para obtener la fecha y hora actual formateada
    private static String obtenerFechaHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Modulo para registrar una accion en memoria y en el archivo de texto
    public static void registrarAccion(String usuario, String modulo, String descripcion) {
        String fechaHora = obtenerFechaHoraActual();
        Bitacora bitacora = new Bitacora(fechaHora, usuario, modulo, "ACCION", descripcion, "");

        // Guardar en el arreglo estatico de memoria si hay espacio
        if (BaseDatosMemoria.contadorBitacoraAcciones < BaseDatosMemoria.MAX_BITACORA) {
            BaseDatosMemoria.bitacoraAcciones[BaseDatosMemoria.contadorBitacoraAcciones] = bitacora;
            BaseDatosMemoria.contadorBitacoraAcciones++;
        }

        // Persistir en el archivo de texto
        persistirBitacora(ARCHIVO_ACCIONES, bitacora);
    }

    // Modulo para registrar un error en memoria y en el archivo de texto
    public static void registrarError(String usuario, String modulo, String descripcion) {
        String fechaHora = obtenerFechaHoraActual();
        Bitacora bitacora = new Bitacora(fechaHora, usuario, modulo, "ERROR", descripcion, "");

        // Guardar en el arreglo estatico de memoria si hay espacio
        if (BaseDatosMemoria.contadorBitacoraErrores < BaseDatosMemoria.MAX_BITACORA) {
            BaseDatosMemoria.bitacoraErrores[BaseDatosMemoria.contadorBitacoraErrores] = bitacora;
            BaseDatosMemoria.contadorBitacoraErrores++;
        }

        // Persistir en el archivo de texto
        persistirBitacora(ARCHIVO_ERRORES, bitacora);
    }

    // Modulo para registrar un rechazo con motivo en memoria y persistir en el archivo de acciones
    public static void registrarRechazo(String usuario, String modulo, String descripcion, String motivoRechazo) {
        String fechaHora = obtenerFechaHoraActual();
        Bitacora bitacora = new Bitacora(fechaHora, usuario, modulo, "RECHAZO", descripcion, motivoRechazo);

        if (BaseDatosMemoria.contadorBitacoraAcciones < BaseDatosMemoria.MAX_BITACORA) {
            BaseDatosMemoria.bitacoraAcciones[BaseDatosMemoria.contadorBitacoraAcciones] = bitacora;
            BaseDatosMemoria.contadorBitacoraAcciones++;
        }

        persistirBitacora(ARCHIVO_ACCIONES, bitacora);
    }

    // Modulo auxiliar para escribir una linea en el archivo de bitacora correspondiente
    private static void persistirBitacora(String nombreArchivo, Bitacora bitacora) {
        try (FileWriter fw = new FileWriter(nombreArchivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            String linea = bitacora.getFechaHora() + "|"
                    + bitacora.getUsuario() + "|"
                    + bitacora.getModulo() + "|"
                    + bitacora.getTipoEvento() + "|"
                    + bitacora.getDescripcion() + "|"
                    + bitacora.getMotivoRechazo();
            pw.println(linea);
        } catch (Exception e) {
            System.err.println("Error al persistir bitacora en " + nombreArchivo + ": " + e.getMessage());
        }
    }

    // Modulo para registrar en la bitacora de la pestaña Animales con sus datos completos
    public static void registrarBitacoraAnimal(String usuario, String operacion, Animal animal) {
        if (animal == null) return;
        String fechaHora = obtenerFechaHoraActual();
        String linea = fechaHora + "|" + usuario + "|" + operacion + "|"
                + animal.getCodigo() + "|" + animal.getNombre() + "|" + animal.getEspecie() + "|"
                + animal.getEdad() + "|" + animal.getEstadoClinico() + "|" + animal.getEstadoAdopcion();
        escribirLineaArchivo(ARCHIVO_BITACORA_ANIMALES, linea);
    }

    // Modulo para registrar en la bitacora de la pestaña Adoptantes con sus datos completos
    public static void registrarBitacoraAdoptante(String usuario, String operacion, Adoptante adoptante) {
        if (adoptante == null) return;
        String fechaHora = obtenerFechaHoraActual();
        String linea = fechaHora + "|" + usuario + "|" + operacion + "|"
                + adoptante.getCodigo() + "|" + adoptante.getNombre() + "|" + adoptante.getDpi() + "|" + adoptante.getTelefono();
        escribirLineaArchivo(ARCHIVO_BITACORA_ADOPTANTES, linea);
    }

    // Modulo para registrar en la bitacora de la pestaña Solicitudes con sus datos completos
    public static void registrarBitacoraSolicitud(String usuario, String operacion, Solicitud solicitud) {
        if (solicitud == null) return;
        String fechaHora = obtenerFechaHoraActual();
        String linea = fechaHora + "|" + usuario + "|" + operacion + "|"
                + solicitud.getCodigo() + "|" + solicitud.getCodigoAnimal() + "|" + solicitud.getCodigoAdoptante() + "|"
                + solicitud.getFecha() + "|" + solicitud.getEstado();
        escribirLineaArchivo(ARCHIVO_BITACORA_SOLICITUDES, linea);
    }

    // Modulo para registrar en la bitacora de la pestaña Rescates con sus datos completos
    public static void registrarBitacoraRescate(String usuario, String operacion, Rescate rescate) {
        if (rescate == null) return;
        String fechaHora = obtenerFechaHoraActual();
        String linea = fechaHora + "|" + usuario + "|" + operacion + "|"
                + rescate.getCodigo() + "|" + rescate.getDireccionDescripcion() + "|" + rescate.getPrioridad() + "|"
                + rescate.getEstado() + "|" + rescate.getFecha() + "|" + rescate.getCodigoAnimalVinculado();
        escribirLineaArchivo(ARCHIVO_BITACORA_RESCATES, linea);
    }

    // Modulo para registrar en la bitacora de la pestaña Ubicaciones (Matriz 5x5)
    public static void registrarBitacoraUbicacion(String usuario, String operacion, int fila, int columna, String codigoAnimal) {
        String fechaHora = obtenerFechaHoraActual();
        String linea = fechaHora + "|" + usuario + "|" + operacion + "|" + fila + "|" + columna + "|" + (codigoAnimal == null ? "LIBRE" : codigoAnimal);
        escribirLineaArchivo(ARCHIVO_BITACORA_UBICACIONES, linea);
    }

    // Modulo auxiliar para escribir una linea directamente al archivo de bitacora especificado
    private static void escribirLineaArchivo(String nombreArchivo, String linea) {
        try (FileWriter fw = new FileWriter(nombreArchivo, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(linea);
        } catch (Exception e) {
            System.err.println("Error al escribir en bitacora " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
