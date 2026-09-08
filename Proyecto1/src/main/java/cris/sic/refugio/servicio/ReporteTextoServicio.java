package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// Servicio para la generacion manual de reportes en formato de texto plano (.txt)
public class ReporteTextoServicio {

    // Modulo para obtener la fecha y hora actual formateada
    private static String obtenerFechaHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Modulo para generar el encabezado comun de los reportes en texto plano
    private static void escribirEncabezado(PrintWriter pw, String titulo, String usuarioActivo) {
        pw.println("================================================================================");
        pw.println("               REFUGIO DE ANIMALES - SISTEMA DE GESTION");
        pw.println("               " + titulo.toUpperCase());
        pw.println("================================================================================");
        pw.println("Fecha y Hora de Generación : " + obtenerFechaHora());
        pw.println("Generado por el Usuario    : " + usuarioActivo);
        pw.println("--------------------------------------------------------------------------------");
        pw.println();
    }

    // Modulo para generar el reporte en archivo .txt del vector de animales rescatados
    public static String generarReporteAnimalesTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_animales.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte de Animales Rescatados (Vector Estático)", usuarioActivo);
            
            pw.printf("%-8s | %-20s | %-15s | %-6s | %-16s | %-12s%n", 
                      "CÓDIGO", "NOMBRE", "ESPECIE", "EDAD", "ESTADO CLÍNICO", "ADOPCIÓN");
            pw.println("--------------------------------------------------------------------------------");

            int total = 0;
            int disponibles = 0;
            int adoptados = 0;

            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null && !a.getEstadoAdopcion().equals("ELIMINADO")) {
                    total++;
                    if (a.getEstadoAdopcion().equals("DISPONIBLE")) disponibles++;
                    if (a.getEstadoAdopcion().equals("ADOPTADO")) adoptados++;

                    pw.printf("%-8s | %-20s | %-15s | %-6d | %-16s | %-12s%n",
                              a.getCodigo(),
                              truncar(a.getNombre(), 20),
                              truncar(a.getEspecie(), 15),
                              a.getEdad(),
                              truncar(a.getEstadoClinico(), 16),
                              a.getEstadoAdopcion());
                }
            }

            pw.println("--------------------------------------------------------------------------------");
            pw.println("RESUMEN ESTADÍSTICO:");
            pw.println(" - Total de Animales Activos       : " + total);
            pw.println(" - Disponibles para Adopción       : " + disponibles);
            pw.println(" - Adoptados                       : " + adoptados);
            pw.println(" - Capacidad Máxima del Vector     : " + BaseDatosMemoria.MAX_ANIMALES);
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte TXT de animales: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte en archivo .txt del vector de solicitudes y adopciones
    public static String generarReporteAdopcionesTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_adopciones.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte de Adopciones y Solicitudes (Vector Estático)", usuarioActivo);

            pw.printf("%-10s | %-14s | %-15s | %-12s | %-12s%n", 
                      "SOLICITUD", "CÓDIGO ANIMAL", "CÓDIGO ADOPTANTE", "FECHA", "ESTADO");
            pw.println("--------------------------------------------------------------------------------");

            int total = 0;
            int aprobadas = 0;
            int pendientes = 0;
            int rechazadas = 0;

            for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
                Solicitud s = BaseDatosMemoria.solicitudes[i];
                if (s != null) {
                    total++;
                    if (s.getEstado().equals("APROBADA")) aprobadas++;
                    else if (s.getEstado().equals("RECHAZADA")) rechazadas++;
                    else pendientes++;

                    pw.printf("%-10s | %-14s | %-16s | %-12s | %-12s%n",
                              s.getCodigo(),
                              s.getCodigoAnimal(),
                              s.getCodigoAdoptante(),
                              s.getFecha(),
                              s.getEstado());
                }
            }

            pw.println("--------------------------------------------------------------------------------");
            pw.println("RESUMEN ESTADÍSTICO:");
            pw.println(" - Total de Solicitudes            : " + total);
            pw.println(" - Solicitudes Aprobadas           : " + aprobadas);
            pw.println(" - Solicitudes Pendientes          : " + pendientes);
            pw.println(" - Solicitudes Rechazadas          : " + rechazadas);
            pw.println(" - Capacidad Máxima del Vector     : " + BaseDatosMemoria.MAX_SOLICITUDES);
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte TXT de adopciones: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte en archivo .txt de la matriz 5x5 de ubicaciones del refugio
    public static String generarReporteOcupacionTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_ocupacion.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte de Ocupación del Refugio (Matriz 5x5)", usuarioActivo);

            pw.println("REPRESENTACIÓN VISUAL DE LA MATRIZ DE CELDAS:");
            pw.println();
            pw.println("       Columna 0    Columna 1    Columna 2    Columna 3    Columna 4");
            pw.println("    +------------+------------+------------+------------+------------+");

            int celdasOcupadas = 0;
            int celdasTotales = BaseDatosMemoria.FILAS_REFUGIO * BaseDatosMemoria.COLUMNAS_REFUGIO;

            for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
                pw.printf("F%d  |", f);
                for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                    String animal = BaseDatosMemoria.ubicacionesRefugio[f][c];
                    if (animal == null || animal.trim().isEmpty()) {
                        pw.print("   LIBRE    |");
                    } else {
                        celdasOcupadas++;
                        pw.printf("  [%-7s] |", animal);
                    }
                }
                pw.println();
                pw.println("    +------------+------------+------------+------------+------------+");
            }

            int celdasLibres = celdasTotales - celdasOcupadas;
            double porcentaje = ((double) celdasOcupadas / celdasTotales) * 100.0;

            pw.println();
            pw.println("RESUMEN DE OCUPACIÓN DE LA MATRIZ:");
            pw.println(" - Dimensiones de la Matriz        : " + BaseDatosMemoria.FILAS_REFUGIO + " x " + BaseDatosMemoria.COLUMNAS_REFUGIO);
            pw.println(" - Total de Celdas Físicas         : " + celdasTotales);
            pw.println(" - Celdas Ocupadas                 : " + celdasOcupadas);
            pw.println(" - Celdas Libres / Disponibles     : " + celdasLibres);
            pw.printf(" - Porcentaje de Ocupación Refugio : %.2f%%%n", porcentaje);
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte TXT de ocupacion: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte en archivo .txt de la bitacora de acciones
    public static String generarReporteBitacoraAccionesTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_bitacora_acciones.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte de Bitácora de Acciones (Auditoría)", usuarioActivo);

            pw.printf("%-19s | %-10s | %-12s | %-8s | %-30s | %s%n",
                      "FECHA Y HORA", "USUARIO", "MÓDULO", "TIPO", "DESCRIPCIÓN", "MOTIVO RECHAZO");
            pw.println("--------------------------------------------------------------------------------");

            File f = new File("bitacora_acciones.txt");
            int total = 0;
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (linea.trim().isEmpty()) continue;
                        total++;
                        String[] p = linea.split("\\|", -1);
                        String fecha = (p.length > 0) ? p[0] : "";
                        String user = (p.length > 1) ? p[1] : "";
                        String mod = (p.length > 2) ? p[2] : "";
                        String tipo = (p.length > 3) ? p[3] : "";
                        String desc = (p.length > 4) ? p[4] : "";
                        String motivo = (p.length > 5) ? p[5] : "";

                        pw.printf("%-19s | %-10s | %-12s | %-8s | %-30s | %s%n",
                                  fecha, user, mod, tipo, truncar(desc, 30), motivo);
                    }
                }
            }

            pw.println("--------------------------------------------------------------------------------");
            pw.println("Total de eventos de acción registrados: " + total);
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte TXT de bitacora de acciones: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte en archivo .txt de la bitacora de errores
    public static String generarReporteBitacoraErroresTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_bitacora_errores.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte de Bitácora de Errores (Auditoría)", usuarioActivo);

            pw.printf("%-19s | %-10s | %-12s | %-8s | %s%n",
                      "FECHA Y HORA", "USUARIO", "MÓDULO", "TIPO", "DESCRIPCIÓN DEL ERROR");
            pw.println("--------------------------------------------------------------------------------");

            File f = new File("bitacora_errores.txt");
            int total = 0;
            if (f.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (linea.trim().isEmpty()) continue;
                        total++;
                        String[] p = linea.split("\\|", -1);
                        String fecha = (p.length > 0) ? p[0] : "";
                        String user = (p.length > 1) ? p[1] : "";
                        String mod = (p.length > 2) ? p[2] : "";
                        String tipo = (p.length > 3) ? p[3] : "";
                        String desc = (p.length > 4) ? p[4] : "";

                        pw.printf("%-19s | %-10s | %-12s | %-8s | %s%n",
                                  fecha, user, mod, tipo, desc);
                    }
                }
            }

            pw.println("--------------------------------------------------------------------------------");
            pw.println("Total de errores registrados: " + total);
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte TXT de bitacora de errores: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte general consolidado de todos los vectores estaticos y la matriz
    public static String generarReporteGeneralVectoresYMatrizTxt(String usuarioActivo) {
        String nombreArchivo = "reporte_general_vectores_matriz.txt";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            escribirEncabezado(pw, "Reporte General Consolidado de Vectores y Matriz", usuarioActivo);

            // 1. Matriz de Ubicaciones (5x5)
            pw.println("1. MATRIZ DE UBICACIONES DEL REFUGIO (5x5)");
            pw.println("================================================================================");
            pw.println("       Columna 0    Columna 1    Columna 2    Columna 3    Columna 4");
            pw.println("    +------------+------------+------------+------------+------------+");
            int celdasOcupadas = 0;
            int celdasTotales = BaseDatosMemoria.FILAS_REFUGIO * BaseDatosMemoria.COLUMNAS_REFUGIO;

            for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
                pw.printf("F%d  |", f);
                for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                    String animal = BaseDatosMemoria.ubicacionesRefugio[f][c];
                    if (animal == null || animal.trim().isEmpty()) {
                        pw.print("   LIBRE    |");
                    } else {
                        celdasOcupadas++;
                        pw.printf("  [%-7s] |", animal);
                    }
                }
                pw.println();
                pw.println("    +------------+------------+------------+------------+------------+");
            }
            int celdasLibres = celdasTotales - celdasOcupadas;
            double porcentaje = ((double) celdasOcupadas / celdasTotales) * 100.0;
            pw.printf("Resumen Matriz: Celdas Totales: %d | Ocupadas: %d | Libres: %d | Ocupación: %.2f%%%n%n",
                      celdasTotales, celdasOcupadas, celdasLibres, porcentaje);

            // 2. Vector de Animales
            pw.println("2. VECTOR ESTÁTICO DE ANIMALES RESCATADOS");
            pw.println("================================================================================");
            pw.printf("%-8s | %-20s | %-15s | %-6s | %-16s | %-12s%n", 
                      "CÓDIGO", "NOMBRE", "ESPECIE", "EDAD", "ESTADO CLÍNICO", "ADOPCIÓN");
            pw.println("--------------------------------------------------------------------------------");
            int totalAnimales = 0;
            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null && !a.getEstadoAdopcion().equals("ELIMINADO")) {
                    totalAnimales++;
                    pw.printf("%-8s | %-20s | %-15s | %-6d | %-16s | %-12s%n",
                              a.getCodigo(), truncar(a.getNombre(), 20), truncar(a.getEspecie(), 15),
                              a.getEdad(), truncar(a.getEstadoClinico(), 16), a.getEstadoAdopcion());
                }
            }
            pw.println("Total Animales Activos en Vector: " + totalAnimales + " / " + BaseDatosMemoria.MAX_ANIMALES);
            pw.println();

            // 3. Vector de Adoptantes
            pw.println("3. VECTOR ESTÁTICO DE ADOPTANTES REGISTRADOS");
            pw.println("================================================================================");
            pw.printf("%-10s | %-25s | %-16s | %-12s%n", "CÓDIGO", "NOMBRE", "DPI", "TELÉFONO");
            pw.println("--------------------------------------------------------------------------------");
            for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
                Adoptante ad = BaseDatosMemoria.adoptantes[i];
                if (ad != null) {
                    pw.printf("%-10s | %-25s | %-16s | %-12s%n",
                              ad.getCodigo(), truncar(ad.getNombre(), 25), ad.getDpi(), ad.getTelefono());
                }
            }
            pw.println("Total Adoptantes en Vector: " + BaseDatosMemoria.contadorAdoptantes + " / " + BaseDatosMemoria.MAX_ADOPTANTES);
            pw.println();

            // 4. Vector de Solicitudes
            pw.println("4. VECTOR ESTÁTICO DE SOLICITUDES DE ADOPCIÓN");
            pw.println("================================================================================");
            pw.printf("%-10s | %-14s | %-16s | %-12s | %-12s%n", 
                      "SOLICITUD", "CÓDIGO ANIMAL", "CÓDIGO ADOPTANTE", "FECHA", "ESTADO");
            pw.println("--------------------------------------------------------------------------------");
            for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
                Solicitud s = BaseDatosMemoria.solicitudes[i];
                if (s != null) {
                    pw.printf("%-10s | %-14s | %-16s | %-12s | %-12s%n",
                              s.getCodigo(), s.getCodigoAnimal(), s.getCodigoAdoptante(), s.getFecha(), s.getEstado());
                }
            }
            pw.println("Total Solicitudes en Vector: " + BaseDatosMemoria.contadorSolicitudes + " / " + BaseDatosMemoria.MAX_SOLICITUDES);
            pw.println();

            // 5. Vector de Rescates
            pw.println("5. VECTOR ESTÁTICO DE RESCATES URGENTES");
            pw.println("================================================================================");
            pw.printf("%-8s | %-10s | %-10s | %-12s | %-10s | %s%n",
                      "CÓDIGO", "PRIORIDAD", "ESTADO", "FECHA", "ANIMAL", "DIRECCIÓN / DESCRIPCIÓN");
            pw.println("--------------------------------------------------------------------------------");
            for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
                Rescate r = BaseDatosMemoria.rescates[i];
                if (r != null) {
                    pw.printf("%-8s | %-10s | %-10s | %-12s | %-10s | %s%n",
                              r.getCodigo(), r.getPrioridad(), r.getEstado(), r.getFecha(),
                              (r.getCodigoAnimalVinculado().isEmpty() ? "N/A" : r.getCodigoAnimalVinculado()),
                              r.getDireccionDescripcion());
                }
            }
            pw.println("Total Rescates en Vector: " + BaseDatosMemoria.contadorRescates + " / " + BaseDatosMemoria.MAX_RESCATES);
            pw.println();

            // 6. Vector de Usuarios
            pw.println("6. VECTOR ESTÁTICO DE USUARIOS DEL SISTEMA");
            pw.println("================================================================================");
            pw.printf("%-20s | %-15s%n", "NOMBRE DE USUARIO", "ROL ASIGNADO");
            pw.println("--------------------------------------------------------------------------------");
            for (int i = 0; i < BaseDatosMemoria.contadorUsuarios; i++) {
                Usuario u = BaseDatosMemoria.usuarios[i];
                if (u != null) {
                    pw.printf("%-20s | %-15s%n", u.getUsuario(), u.getRol());
                }
            }
            pw.println("Total Usuarios en Vector: " + BaseDatosMemoria.contadorUsuarios + " / " + BaseDatosMemoria.MAX_USUARIOS);
            pw.println();
            pw.println("================================================================================");
            pw.println("                         FIN DEL REPORTE CONSOLIDADO");
            pw.println("================================================================================");

            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte general consolidado TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte general consolidado TXT: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo auxiliar para truncar cadenas largas y evitar desajustes visuales en las columnas
    private static String truncar(String texto, int maxLen) {
        if (texto == null) return "";
        if (texto.length() <= maxLen) return texto;
        return texto.substring(0, maxLen - 3) + "...";
    }
}
