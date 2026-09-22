package cris.sic.practica2.modelo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Representa el premio especial Contenedor Quaffle.
 * Se desplaza horizontalmente de derecha a izquierda en su propio hilo.
 * Al ser recogido por la nave del jugador, otorga 10 puntos de bonificación.
 * 
 * Cumple con la Subfase 6.3 y 7.3 del planificador.
 */
public class PremioQuaffle extends ElementoEspacial {

    public static final int PUNTOS_RECOMPENSA = 10;
    private int tickPulso;

    public PremioQuaffle(int xInicial, int yInicial) {
        super("QuaffleThread-" + System.currentTimeMillis(), xInicial, yInicial, 28, 28, 3, 20);
        this.tickPulso = 0;
    }

    public PremioQuaffle(int xInicial, int yInicial, int velocidad, int sleepTick) {
        super("QuaffleThread-" + System.currentTimeMillis(), xInicial, yInicial, 28, 28, velocidad, sleepTick);
        this.tickPulso = 0;
    }

    @Override
    protected void actualizarAnimacion() {
        tickPulso = (tickPulso + 1) % 30;
    }

    @Override
    public void dibujar(Graphics2D g2) {
        if (!activo) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Halo pulsante exterior
        int pulsoRadio = 2 + (tickPulso % 6);
        g2.setColor(new Color(255, 152, 0, 100));
        g2.fillOval(x - pulsoRadio, y - pulsoRadio, ancho + (pulsoRadio * 2), alto + (pulsoRadio * 2));

        // Esfera escarlata y dorada
        GradientPaint gradienteQuaffle = new GradientPaint(
                x, y, new Color(0xD3, 0x2F, 0x2F),
                x + ancho, y + alto, new Color(0x7F, 0x00, 0x00)
        );
        g2.setPaint(gradienteQuaffle);
        g2.fillOval(x, y, ancho, alto);

        // Borde dorado
        g2.setColor(new Color(0xFF, 0xD7, 0x00));
        g2.drawOval(x, y, ancho, alto);

        // Emblema central contenedor
        g2.setColor(new Color(0xFF, 0xEB, 0x3B));
        g2.fillOval(x + 8, y + 8, 12, 12);
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 10, y + 10, 4, 4);
    }

    @Override
    public String getTipo() {
        return "Contenedor Quaffle";
    }

    public int getPuntosRecompensa() {
        return PUNTOS_RECOMPENSA;
    }
}
