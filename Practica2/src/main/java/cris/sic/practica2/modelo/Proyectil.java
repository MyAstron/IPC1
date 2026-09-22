package cris.sic.practica2.modelo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Representa un proyectil láser disparado por la nave del jugador.
 * Se ejecuta en su propio hilo independiente (extiende Thread),
 * desplazándose en línea recta de izquierda a derecha (Side-Scroller)
 * con una tasa de refresco a ~60 FPS (sleep de 16 ms) y liberándose
 * automáticamente al salir por el borde derecho (X máx) del lienzo.
 * 
 * Cumple con la Fase 5 del planificador y requerimientos de Side-Scroller.
 */
public class Proyectil extends Thread {

    private int x;
    private int y;
    private final int ancho;
    private final int alto;
    private final int velocidad;
    private final Color colorPlasma;
    private final int limiteMaxX;

    private volatile boolean activo;
    private volatile boolean enPausa;

    /**
     * Constructor del proyectil láser.
     *
     * @param xInicial    Coordenada X de origen (proa de la nave)
     * @param yInicial    Coordenada Y de origen (centro vertical del cañón)
     * @param colorPlasma Color del haz de plasma según el tipo de nave
     * @param limiteMaxX  Límite derecho del panel para autodestrucción
     */
    public Proyectil(int xInicial, int yInicial, Color colorPlasma, int limiteMaxX) {
        super("ProyectilThread-" + System.currentTimeMillis());
        this.x = xInicial;
        this.y = yInicial;
        this.ancho = 18;
        this.alto = 6;
        this.velocidad = 14; // Velocidad de avance horizontal hacia la derecha
        this.colorPlasma = (colorPlasma != null) ? colorPlasma : Color.CYAN;
        this.limiteMaxX = limiteMaxX > 0 ? limiteMaxX : 1000;
        this.activo = true;
        this.enPausa = false;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (activo && x < limiteMaxX) {
            if (!enPausa) {
                // Desplazamiento horizontal de izquierda a derecha en el eje X
                x += velocidad;
            }

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                // Hilo interrumpido al destruir o limpiar proyectiles
                break;
            }
        }

        // Al cruzar el borde derecho del panel, desactivar para liberar memoria
        this.activo = false;
    }

    /**
     * Dibuja el proyectil con efecto de núcleo brillante y estela de energía.
     *
     * @param g2 Contexto gráfico 2D
     */
    public void dibujar(Graphics2D g2) {
        if (!activo) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Halo exterior de energía
        g2.setColor(new Color(colorPlasma.getRed(), colorPlasma.getGreen(), colorPlasma.getBlue(), 120));
        g2.fillRoundRect(x - 4, y - 2, ancho + 6, alto + 4, 8, 8);

        // Cuerpo principal del plasma
        g2.setColor(colorPlasma);
        g2.fillRoundRect(x, y, ancho, alto, 6, 6);

        // Núcleo caliente blanco centrado
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(x + 3, y + 1, ancho - 6, alto - 2, 4, 4);
    }

    /**
     * Retorna el rectángulo de colisión para interactuar con enemigos.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    /**
     * Destruye el proyectil e interrumpe su hilo de ejecución.
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

    public int getY() {
        return y;
    }
}
