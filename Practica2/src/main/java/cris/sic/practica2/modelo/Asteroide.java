package cris.sic.practica2.modelo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

/**
 * Representa un obstáculo espacial (Asteroide / Bludger).
 * Se desplaza de derecha a izquierda con rotación constante sobre su eje.
 * La colisión con la nave del jugador no es fatal, pero paraliza/bloquea
 * los sistemas de propulsión y disparo durante 2 segundos.
 * 
 * Cumple con la Subfase 6.3 y 7.3 del planificador.
 */
public class Asteroide extends ElementoEspacial {

    private double anguloRotacion;
    private final double velocidadRotacion;

    public Asteroide(int xInicial, int yInicial) {
        super("AsteroideThread-" + System.currentTimeMillis(), xInicial, yInicial, 38, 38, 3, 22);
        this.anguloRotacion = 0;
        this.velocidadRotacion = 0.05;
    }

    public Asteroide(int xInicial, int yInicial, int velocidad, int sleepTick) {
        super("AsteroideThread-" + System.currentTimeMillis(), xInicial, yInicial, 38, 38, velocidad, sleepTick);
        this.anguloRotacion = 0;
        this.velocidadRotacion = 0.05;
    }

    @Override
    protected void actualizarAnimacion() {
        anguloRotacion += velocidadRotacion;
    }

    @Override
    public void dibujar(Graphics2D g2) {
        if (!activo) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform transformOriginal = g2.getTransform();

        // Rotación continua sobre su centro
        int centroX = x + (ancho / 2);
        int centroY = y + (alto / 2);
        g2.rotate(anguloRotacion, centroX, centroY);

        // Estela de polvo espacial
        g2.setColor(new Color(120, 110, 100, 60));
        g2.fillOval(centroX + 2, centroY - 8, 20, 16);

        // Cuerpo rocoso irregular
        GradientPaint gradienteRoca = new GradientPaint(
                x, y, new Color(0x61, 0x61, 0x61),
                x + ancho, y + alto, new Color(0x21, 0x21, 0x21)
        );
        g2.setPaint(gradienteRoca);
        g2.fillOval(x, y, ancho, alto);

        // Borde rocoso
        g2.setColor(new Color(0x8D, 0x6E, 0x63));
        g2.drawOval(x, y, ancho, alto);

        // Cráteres de impacto
        g2.setColor(new Color(0x30, 0x30, 0x30));
        g2.fillOval(x + 8, y + 8, 10, 9);
        g2.fillOval(x + 20, y + 18, 12, 10);
        g2.fillOval(x + 10, y + 22, 6, 6);

        // Brillo solar en el borde del cráter
        g2.setColor(new Color(0xB0, 0xBE, 0xC5, 180));
        g2.drawArc(x + 8, y + 8, 10, 9, 45, 180);
        g2.drawArc(x + 20, y + 18, 12, 10, 45, 180);

        g2.setTransform(transformOriginal);
    }

    @Override
    public String getTipo() {
        return "Asteroide / Bludger";
    }
}
