package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

// Servicio para la generacion manual de reportes en formato HTML
public class ReporteHtmlServicio {

    // Modulo para obtener la fecha y hora actual formateada
    private static String obtenerFechaHora() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    // Modulo para generar el encabezado HTML y estilos CSS comunes
    private static String generarEncabezado(String titulo, String usuarioActivo) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n<html lang=\"es\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<title>").append(titulo).append("</title>\n");
        sb.append("<style>\n");
        sb.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 30px; background-color: #f4f6f9; color: #333; }\n");
        sb.append(".container { max-width: 950px; margin: auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }\n");
        sb.append("h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; margin-top: 0; }\n");
        sb.append(".meta { font-size: 0.95em; color: #7f8c8d; margin-bottom: 20px; }\n");
        sb.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 20px; }\n");
        sb.append("th, td { border: 1px solid #ddd; padding: 10px 12px; text-align: left; font-size: 0.95em; }\n");
        sb.append("th { background-color: #2980b9; color: white; }\n");
        sb.append("tr:nth-child(even) { background-color: #f8f9fa; }\n");
        sb.append(".badge-green { background-color: #d4edda; color: #155724; padding: 4px 8px; border-radius: 4px; font-weight: bold; }\n");
        sb.append(".badge-red { background-color: #f8d7da; color: #721c24; padding: 4px 8px; border-radius: 4px; font-weight: bold; }\n");
        sb.append(".badge-yellow { background-color: #fff3cd; color: #856404; padding: 4px 8px; border-radius: 4px; font-weight: bold; }\n");
        sb.append(".badge-blue { background-color: #d1ecf1; color: #0c5460; padding: 4px 8px; border-radius: 4px; font-weight: bold; }\n");
        sb.append(".grid-table { width: auto; margin: 20px auto; border-collapse: collapse; }\n");
        sb.append(".grid-cell { width: 90px; height: 70px; text-align: center; vertical-align: middle; border: 2px solid #bdc3c7; font-weight: bold; }\n");
        sb.append(".grid-libre { background-color: #d4edda; color: #155724; }\n");
        sb.append(".grid-ocupado { background-color: #f8d7da; color: #721c24; }\n");
        sb.append(".summary { margin-top: 20px; padding: 15px; background: #eaeded; border-radius: 6px; font-size: 0.95em; }\n");
        sb.append("</style>\n</head>\n<body>\n<div class=\"container\">\n");
        sb.append("<h1>").append(titulo).append("</h1>\n");
        sb.append("<div class=\"meta\">Refugio de Animales | Generado el: <b>").append(obtenerFechaHora())
          .append("</b> por el usuario: <b>").append(usuarioActivo).append("</b></div>\n");
        return sb.toString();
    }

    // Modulo para cerrar el archivo HTML
    private static String generarPie() {
        return "</div>\n</body>\n</html>";
    }

    // Modulo para generar el reporte de animales rescatados (filtrando los eliminados logicamente)
    public static String generarReporteAnimales(String usuarioActivo) {
        String nombreArchivo = "reporte_animales.html";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println(generarEncabezado("Reporte de Animales Rescatados", usuarioActivo));
            pw.println("<table>");
            pw.println("<thead><tr><th>Código</th><th>Nombre</th><th>Especie</th><th>Edad</th><th>Estado Clínico</th><th>Estado Adopción</th></tr></thead>");
            pw.println("<tbody>");

            int total = 0;
            int disponibles = 0;
            int adoptados = 0;

            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null && !a.getEstadoAdopcion().equals("ELIMINADO")) {
                    total++;
                    String badgeAdopcion = a.getEstadoAdopcion().equals("DISPONIBLE") ? "badge-green" : "badge-red";
                    if (a.getEstadoAdopcion().equals("DISPONIBLE")) disponibles++;
                    if (a.getEstadoAdopcion().equals("ADOPTADO")) adoptados++;

                    pw.println("<tr>");
                    pw.println("<td><b>" + a.getCodigo() + "</b></td>");
                    pw.println("<td>" + a.getNombre() + "</td>");
                    pw.println("<td>" + a.getEspecie() + "</td>");
                    pw.println("<td>" + a.getEdad() + " años</td>");
                    pw.println("<td>" + a.getEstadoClinico() + "</td>");
                    pw.println("<td><span class=\"" + badgeAdopcion + "\">" + a.getEstadoAdopcion() + "</span></td>");
                    pw.println("</tr>");
                }
            }

            pw.println("</tbody></table>");
            pw.println("<div class=\"summary\">");
            pw.println("<b>Resumen Estadístico:</b> Total de animales activos: " + total 
                    + " | Disponibles para adopción: " + disponibles 
                    + " | Adoptados: " + adoptados);
            pw.println("</div>");
            pw.println(generarPie());

            ReporteTextoServicio.generarReporteAnimalesTxt(usuarioActivo);
            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte HTML y TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte de animales: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte de adopciones y solicitudes
    public static String generarReporteAdopciones(String usuarioActivo) {
        String nombreArchivo = "reporte_adopciones.html";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println(generarEncabezado("Reporte de Adopciones y Solicitudes", usuarioActivo));
            pw.println("<table>");
            pw.println("<thead><tr><th>Código Solicitud</th><th>Código Animal</th><th>Código Adoptante</th><th>Fecha</th><th>Estado</th></tr></thead>");
            pw.println("<tbody>");

            int total = 0;
            int aprobadas = 0;
            int pendientes = 0;
            int rechazadas = 0;

            for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
                Solicitud s = BaseDatosMemoria.solicitudes[i];
                if (s != null) {
                    total++;
                    String badge = "badge-yellow";
                    if (s.getEstado().equals("APROBADA")) {
                        badge = "badge-green";
                        aprobadas++;
                    } else if (s.getEstado().equals("RECHAZADA")) {
                        badge = "badge-red";
                        rechazadas++;
                    } else {
                        pendientes++;
                    }

                    pw.println("<tr>");
                    pw.println("<td><b>" + s.getCodigo() + "</b></td>");
                    pw.println("<td>" + s.getCodigoAnimal() + "</td>");
                    pw.println("<td>" + s.getCodigoAdoptante() + "</td>");
                    pw.println("<td>" + s.getFecha() + "</td>");
                    pw.println("<td><span class=\"" + badge + "\">" + s.getEstado() + "</span></td>");
                    pw.println("</tr>");
                }
            }

            pw.println("</tbody></table>");
            pw.println("<div class=\"summary\">");
            pw.println("<b>Resumen Estadístico:</b> Total de solicitudes: " + total 
                    + " | Aprobadas: " + aprobadas 
                    + " | Pendientes: " + pendientes 
                    + " | Rechazadas: " + rechazadas);
            pw.println("</div>");
            pw.println(generarPie());

            ReporteTextoServicio.generarReporteAdopcionesTxt(usuarioActivo);
            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte HTML y TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte de adopciones: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte de ocupacion del refugio (matriz 5x5)
    public static String generarReporteOcupacion(String usuarioActivo) {
        String nombreArchivo = "reporte_ocupacion.html";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println(generarEncabezado("Reporte de Ocupación del Refugio (Matriz 5x5)", usuarioActivo));
            pw.println("<table class=\"grid-table\">");
            pw.println("<thead><tr><th></th><th>Columna 0</th><th>Columna 1</th><th>Columna 2</th><th>Columna 3</th><th>Columna 4</th></tr></thead>");
            pw.println("<tbody>");

            int celdasOcupadas = 0;
            int celdasTotales = BaseDatosMemoria.FILAS_REFUGIO * BaseDatosMemoria.COLUMNAS_REFUGIO;

            for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
                pw.println("<tr>");
                pw.println("<th>Fila " + f + "</th>");
                for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                    String animal = BaseDatosMemoria.ubicacionesRefugio[f][c];
                    if (animal == null || animal.trim().isEmpty()) {
                        pw.println("<td class=\"grid-cell grid-libre\">Libre</td>");
                    } else {
                        celdasOcupadas++;
                        pw.println("<td class=\"grid-cell grid-ocupado\">" + animal + "</td>");
                    }
                }
                pw.println("</tr>");
            }

            pw.println("</tbody></table>");
            int celdasLibres = celdasTotales - celdasOcupadas;
            double porcentaje = ((double) celdasOcupadas / celdasTotales) * 100.0;

            pw.println("<div class=\"summary\">");
            pw.println("<b>Resumen de Ocupación:</b> Total de Celdas: " + celdasTotales 
                    + " | Ocupadas: " + celdasOcupadas 
                    + " | Libres: " + celdasLibres 
                    + " | Porcentaje de Ocupación: " + String.format("%.2f", porcentaje) + "%");
            pw.println("</div>");
            pw.println(generarPie());

            ReporteTextoServicio.generarReporteOcupacionTxt(usuarioActivo);
            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte HTML y TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar reporte de ocupacion: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte de bitacora de acciones
    public static String generarReporteBitacoraAcciones(String usuarioActivo) {
        String nombreArchivo = "reporte_bitacora_acciones.html";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println(generarEncabezado("Reporte de Bitácora de Acciones", usuarioActivo));
            pw.println("<table>");
            pw.println("<thead><tr><th>Fecha y Hora</th><th>Usuario</th><th>Módulo</th><th>Tipo Evento</th><th>Descripción</th><th>Motivo Rechazo</th></tr></thead>");
            pw.println("<tbody>");

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

                        pw.println("<tr>");
                        pw.println("<td>" + fecha + "</td>");
                        pw.println("<td>" + user + "</td>");
                        pw.println("<td>" + mod + "</td>");
                        pw.println("<td><span class=\"badge-blue\">" + tipo + "</span></td>");
                        pw.println("<td>" + desc + "</td>");
                        pw.println("<td>" + (motivo.isEmpty() ? "-" : motivo) + "</td>");
                        pw.println("</tr>");
                    }
                }
            }

            pw.println("</tbody></table>");
            pw.println("<div class=\"summary\">");
            pw.println("<b>Total de eventos registrados en bitácora de acciones:</b> " + total);
            pw.println("</div>");
            pw.println(generarPie());

            ReporteTextoServicio.generarReporteBitacoraAccionesTxt(usuarioActivo);
            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte HTML y TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar bitacora de acciones: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }

    // Modulo para generar el reporte de bitacora de errores
    public static String generarReporteBitacoraErrores(String usuarioActivo) {
        String nombreArchivo = "reporte_bitacora_errores.html";
        try (PrintWriter pw = new PrintWriter(new FileWriter(nombreArchivo))) {
            pw.println(generarEncabezado("Reporte de Bitácora de Errores", usuarioActivo));
            pw.println("<table>");
            pw.println("<thead><tr><th>Fecha y Hora</th><th>Usuario</th><th>Módulo</th><th>Tipo Evento</th><th>Descripción</th></tr></thead>");
            pw.println("<tbody>");

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

                        pw.println("<tr>");
                        pw.println("<td>" + fecha + "</td>");
                        pw.println("<td>" + user + "</td>");
                        pw.println("<td>" + mod + "</td>");
                        pw.println("<td><span class=\"badge-red\">" + tipo + "</span></td>");
                        pw.println("<td>" + desc + "</td>");
                        pw.println("</tr>");
                    }
                }
            }

            pw.println("</tbody></table>");
            pw.println("<div class=\"summary\">");
            pw.println("<b>Total de errores registrados en bitácora:</b> " + total);
            pw.println("</div>");
            pw.println(generarPie());

            ReporteTextoServicio.generarReporteBitacoraErroresTxt(usuarioActivo);
            BitacoraServicio.registrarAccion(usuarioActivo, "Reportes", "Generación de reporte HTML y TXT: " + nombreArchivo);
            return "SUCCESS|" + nombreArchivo;
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Reportes", "Error al generar bitacora de errores: " + e.getMessage());
            return "ERROR|" + e.getMessage();
        }
    }
}
