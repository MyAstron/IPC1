package cris.sic.practica2.modelo;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Clase base abstracta para todos los elementos espaciales del Side-Scroller.
 * Se ejecutan en hilos independientes (extiende Thread) y cumplen estrictamente
 * con el patrón horizontal del enunciado:
 * Aparecen en el borde derecho (X máximo) y se desplazan horizontalmente
 * hacia el borde izquierdo (X mínimo).
 * 
 * Subclases concretas:
 * - Enemigo (Naves enemigas con colisión fatal)
 * - Asteroide (Bludger, obstáculo que paraliza por 2s)
 * - PremioQuaffle (Premio contenedor +10 pts)
 * - PremioSnitch (Premio raro +150 pts y barrido de enemigos)
 */
public abstract class ElementoEspacial extends Thread {

    protected int x;
    protected int y;
    protected int ancho;
    protected int alto;
    protected int velocidad;
    protected int sleepTick;

    protected volatile boolean activo;
    protected volatile boolean enPausa;

    /**
     * Constructor base de elemento espacial.
     *
     * @param nombreHilo Identificador del hilo
     * @param xInicial   Coordenada X inicial (Borde derecho / X máx)
     * @param yInicial   Coordenada Y inicial en el área jugable
     * @param ancho      Ancho en píxeles del elemento
     * @param alto       Alto en píxeles del elemento
     * @param velocidad  Píxeles que avanza hacia la izquierda en cada tick
     * @param sleepTick  Milisegundos de pausa del hilo entre cada avance
     */
    public ElementoEspacial(String nombreHilo, int xInicial, int yInicial, int ancho, int alto, int velocidad, int sleepTick) {
        super(nombreHilo);
        this.x = xInicial;
        this.y = yInicial;
        this.ancho = ancho;
        this.alto = alto;
        this.velocidad = Math.max(1, velocidad);
        this.sleepTick = Math.max(10, sleepTick);
        this.activo = true;
        this.enPausa = false;
        setDaemon(true);
    }

    @Override
    public void run() {
        // Se desplaza horizontalmente desde el borde derecho hacia la izquierda
        while (activo && (x + ancho > 0)) {
            if (!enPausa) {
                // Desplazamiento horizontal de derecha a izquierda (Side-Scroller horizontal)
                x -= velocidad;
                actualizarAnimacion();
            }

            try {
                Thread.sleep(sleepTick);
            } catch (InterruptedException e) {
                // Hilo interrumpido al destruir el elemento o limpiar la pantalla
                break;
            }
        }

        // Liberar al cruzar completamente el borde izquierdo (X <= 0)
        this.activo = false;
    }

    /**
     * Método hook para animaciones internas (alas de snitch, rotación de asteroide, etc.).
     */
    protected void actualizarAnimacion() {
        // Implementación opcional en subclases
    }

    /**
     * Renderizado gráfico 2D del elemento espacial en el panel.
     *
     * @param g2 Contexto gráfico 2D
     */
    public abstract void dibujar(Graphics2D g2);

    /**
     * Nombre o tipo del elemento espacial para telemetría e interacciones.
     */
    public abstract String getTipo();

    /**
     * Retorna el rectángulo de colisión exacto para intersectar con la nave o proyectiles.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    /**
     * Destruye el elemento e interrumpe de forma limpia su hilo de ejecución.
     */
    public void destruir() {
        this.activo = false;
        interrupt();
    }

    public void pausar() {
        this.enPausa = true;
    }

    public void reanudar() {
        this.enPausa = false;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isEnPausa() {
        return enPausa;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public int getVelocidad() {
        return velocidad;
    }

    public int getSleepTick() {
        return sleepTick;
    }
}
