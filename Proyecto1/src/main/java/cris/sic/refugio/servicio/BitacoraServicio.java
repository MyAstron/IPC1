package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Bitacora;
import cris.sic.refugio.persistencia.BaseDatosMemoria;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// Servicio para registrar y persistir bitacoras de acciones y errores
public class BitacoraServicio {

    private static final String ARCHIVO_ACCIONES = "bitacora_acciones.txt";
    private static final String ARCHIVO_ERRORES = "bitacora_errores.txt";

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
}
