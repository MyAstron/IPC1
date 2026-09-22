package cris.sic.practica2.modelo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

/**
 * Representa la codiciada Snitch Espacial (Premio raro de alta velocidad).
 * Se desplaza velozmente de derecha a izquierda con oscilación senoidal en el eje Y.
 * Al atraparla:
 * - Otorga 150 puntos de bonificación.
 * - Desata una onda de energía dorada que elimina a todos los enemigos en pantalla.
 * 
 * Cumple con la Subfase 6.3 y 7.3 del planificador.
 */
public class PremioSnitch extends ElementoEspacial {

    public static final int PUNTOS_RECOMPENSA = 150;

    private final int yBase;
    private double anguloOscilacion;
    private int tickAlas;

    public PremioSnitch(int xInicial, int yInicial) {
        super("SnitchThread-" + System.currentTimeMillis(), xInicial, yInicial, 24, 24, 6, 18);
        this.yBase = yInicial;
        this.anguloOscilacion = 0;
        this.tickAlas = 0;
    }

    public PremioSnitch(int xInicial, int yInicial, int velocidad, int sleepTick) {
        super("SnitchThread-" + System.currentTimeMillis(), xInicial, yInicial, 24, 24, velocidad, sleepTick);
        this.yBase = yInicial;
        this.anguloOscilacion = 0;
        this.tickAlas = 0;
    }

    @Override
    protected void actualizarAnimacion() {
        anguloOscilacion += 0.12;
        tickAlas = (tickAlas + 1) % 20;

        // Trayectoria errática y veloz (oscilación senoidal en Y)
        this.y = (int) (yBase + Math.sin(anguloOscilacion) * 16);
    }

    @Override
    public void dibujar(Graphics2D g2) {
        if (!activo) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centroX = x + (ancho / 2);
        int centroY = y + (alto / 2);

        // Aleteo animado
        int aleteoOffset = (tickAlas % 4 < 2) ? -8 : 4;

        // Ala izquierda / superior plateada
        Polygon alaSup = new Polygon();
        alaSup.addPoint(centroX - 4, centroY - 2);
        alaSup.addPoint(centroX - 16, centroY - 14 + aleteoOffset);
        alaSup.addPoint(centroX + 2, centroY - 8);
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fill(alaSup);
        g2.setColor(new Color(220, 230, 242));
        g2.draw(alaSup);

        // Ala derecha / inferior plateada
        Polygon alaInf = new Polygon();
        alaInf.addPoint(centroX - 4, centroY + 2);
        alaInf.addPoint(centroX - 16, centroY + 14 - aleteoOffset);
        alaInf.addPoint(centroX + 2, centroY + 8);
        g2.setColor(new Color(255, 255, 255, 220));
        g2.fill(alaInf);
        g2.setColor(new Color(220, 230, 242));
        g2.draw(alaInf);

        // Halo de resplandor dorado
        g2.setColor(new Color(255, 215, 0, 140));
        g2.fillOval(x - 4, y - 4, ancho + 8, alto + 8);

        // Esfera dorada brillante
        GradientPaint oroGradiente = new GradientPaint(
                x, y, new Color(0xFF, 0xF5, 0x9D),
                x + ancho, y + alto, new Color(0xFF, 0x8F, 0x00)
        );
        g2.setPaint(oroGradiente);
        g2.fillOval(x, y, ancho, alto);

        // Borde dorado neón
        g2.setColor(new Color(0xFF, 0xD7, 0x00));
        g2.drawOval(x, y, ancho, alto);

        // Destello brillante en el centro
        g2.setColor(Color.WHITE);
        g2.fillOval(x + 5, y + 5, 6, 6);
    }

    @Override
    public String getTipo() {
        return "Snitch Espacial";
    }

    public int getPuntosRecompensa() {
        return PUNTOS_RECOMPENSA;
    }
}
