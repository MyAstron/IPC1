package cris.sic.practica2.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representa una partida jugada en el simulador espacial.
 * Almacena el piloto participante, puntaje alcanzado, fecha de juego y enemigos destruidos.
 */
public class Partida {

    private String nombrePiloto;
    private int puntajeObtenido;
    private String fecha;
    private int enemigosDestruidos;

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Partida() {
        this.nombrePiloto = "";
        this.puntajeObtenido = 0;
        this.fecha = LocalDateTime.now().format(FORMATO_FECHA);
        this.enemigosDestruidos = 0;
    }

    public Partida(String nombrePiloto, int puntajeObtenido, int enemigosDestruidos) {
        this.nombrePiloto = nombrePiloto;
        this.puntajeObtenido = puntajeObtenido;
        this.fecha = LocalDateTime.now().format(FORMATO_FECHA);
        this.enemigosDestruidos = enemigosDestruidos;
    }

    public Partida(String nombrePiloto, int puntajeObtenido, String fecha, int enemigosDestruidos) {
        this.nombrePiloto = nombrePiloto;
        this.puntajeObtenido = puntajeObtenido;
        this.fecha = (fecha != null && !fecha.trim().isEmpty()) ? fecha : LocalDateTime.now().format(FORMATO_FECHA);
        this.enemigosDestruidos = enemigosDestruidos;
    }

    public String getNombrePiloto() {
        return nombrePiloto;
    }

    public void setNombrePiloto(String nombrePiloto) {
        this.nombrePiloto = nombrePiloto;
    }

    public int getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public void setPuntajeObtenido(int puntajeObtenido) {
        this.puntajeObtenido = puntajeObtenido;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getEnemigosDestruidos() {
        return enemigosDestruidos;
    }

    public void setEnemigosDestruidos(int enemigosDestruidos) {
        this.enemigosDestruidos = enemigosDestruidos;
    }

    @Override
    public String toString() {
        return "Partida{" +
                "nombrePiloto='" + nombrePiloto + '\'' +
                ", puntajeObtenido=" + puntajeObtenido +
                ", fecha='" + fecha + '\'' +
                ", enemigosDestruidos=" + enemigosDestruidos +
                '}';
    }
}
