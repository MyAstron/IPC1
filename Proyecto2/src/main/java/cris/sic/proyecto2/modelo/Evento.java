package cris.sic.proyecto2.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad modelo que representa un evento registrado en la bitácora histórica del sistema ResiPark.
 * 
 * Cumple con los requerimientos:
 * - Atributos: fechaHora, tipoEvento, descripcion, garitaInvolucrada.
 * - Almacenado dinámicamente en una Pila LIFO no destructiva.
 * - Cero uso de colecciones de java.util para su estructura.
 * 
 * @author cris_sic
 */
public class Evento {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String fechaHora;
    private final String tipoEvento;
    private final String descripcion;
    private final String garitaInvolucrada;

    /**
     * Constructor principal con marca de tiempo automática.
     *
     * @param tipoEvento        Tipo o categoría del evento (ej: INGRESO, SALIDA, RECHAZO, ASIGNACIÓN)
     * @param descripcion       Detalle descriptivo del evento ocurrido
     * @param garitaInvolucrada Garita o módulo responsable (ej: "Garita de Entrada", "Garita de Salida", "Sistema")
     */
    public Evento(String tipoEvento, String descripcion, String garitaInvolucrada) {
        this.fechaHora = LocalDateTime.now().format(FORMATTER);
        this.tipoEvento = (tipoEvento != null) ? tipoEvento.trim() : "GENERAL";
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.garitaInvolucrada = (garitaInvolucrada != null) ? garitaInvolucrada.trim() : "SISTEMA";
    }

    /**
     * Constructor con fecha y hora explícita (útil para pruebas o registros con marca temporal fija).
     *
     * @param fechaHora         Cadena con fecha y hora formateada
     * @param tipoEvento        Tipo de evento
     * @param descripcion       Detalle descriptivo
     * @param garitaInvolucrada Garita involucrada
     */
    public Evento(String fechaHora, String tipoEvento, String descripcion, String garitaInvolucrada) {
        this.fechaHora = (fechaHora != null) ? fechaHora.trim() : LocalDateTime.now().format(FORMATTER);
        this.tipoEvento = (tipoEvento != null) ? tipoEvento.trim() : "GENERAL";
        this.descripcion = (descripcion != null) ? descripcion.trim() : "";
        this.garitaInvolucrada = (garitaInvolucrada != null) ? garitaInvolucrada.trim() : "SISTEMA";
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getGaritaInvolucrada() {
        return garitaInvolucrada;
    }

    @Override
    public String toString() {
        return "[" + fechaHora + "] [" + garitaInvolucrada + "] [" + tipoEvento + "] " + descripcion;
    }
}
