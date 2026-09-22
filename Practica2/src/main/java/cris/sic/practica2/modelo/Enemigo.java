package cris.sic.practica2.modelo;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

/**
 * Representa una nave enemiga en el Side-Scroller.
 * Se desplaza en su propio hilo de derecha a izquierda hacia la nave del jugador.
 * La colisión directa con la nave del jugador representa daño fatal (Game Over).
 * Destruirla con proyectiles láser otorga 20 puntos.
 */
public class Enemigo extends ElementoEspacial {

    public static final int PUNTOS_RECOMPENSA = 20;
    private int tickAnimacion;

    public Enemigo(int xInicial, int yInicial) {
        super("EnemigoThread-" + System.currentTimeMillis(), xInicial, yInicial, 48, 28, 4, 20);
        this.tickAnimacion = 0;
    }

    public Enemigo(int xInicial, int yInicial, int velocidad, int sleepTick) {
        super("EnemigoThread-" + System.currentTimeMillis(), xInicial, yInicial, 48, 28, velocidad, sleepTick);
        this.tickAnimacion = 0;
    }

    @Override
    protected void actualizarAnimacion() {
        tickAnimacion = (tickAnimacion + 1) % 40;
    }

    @Override
    public void dibujar(Graphics2D g2) {
        if (!activo) return;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. PROPULSOR ENERGÉTICO ENEMIGO (Hacia la derecha, porque vuela hacia la izquierda)
        int llamaLongitud = 10 + (tickAnimacion % 5) * 2;
        Polygon llama = new Polygon();
        llama.addPoint(x + ancho - 2, y + (alto / 2) - 5);
        llama.addPoint(x + ancho + llamaLongitud, y + (alto / 2));
        llama.addPoint(x + ancho - 2, y + (alto / 2) + 5);

        g2.setColor(new Color(255, 61, 0, 200)); // Llama roja alienígena
        g2.fill(llama);

        // Núcleo caliente de la llama
        Polygon llamaInterior = new Polygon();
        llamaInterior.addPoint(x + ancho - 2, y + (alto / 2) - 2);
        llamaInterior.addPoint(x + ancho + (llamaLongitud / 2), y + (alto / 2));
        llamaInterior.addPoint(x + ancho - 2, y + (alto / 2) + 2);
        g2.setColor(new Color(255, 235, 59, 230));
        g2.fill(llamaInterior);

        // 2. FUSELAJE ENEMIGO (Punta afilada orientada hacia la izquierda)
        Polygon fuselaje = new Polygon();
        fuselaje.addPoint(x, y + (alto / 2));                     // Punta frontal izquierda
        fuselaje.addPoint(x + 18, y + 2);                        // Ala superior
        fuselaje.addPoint(x + ancho - 4, y + 4);                 // Cola superior
        fuselaje.addPoint(x + ancho - 10, y + (alto / 2));       // Muesca motor
        fuselaje.addPoint(x + ancho - 4, y + alto - 4);          // Cola inferior
        fuselaje.addPoint(x + 18, y + alto - 2);                 // Ala inferior

        GradientPaint degradadoEnemigo = new GradientPaint(
                x, y, new Color(0xB7, 0x1C, 0x1C),
                x + ancho, y, new Color(0x4A, 0x14, 0x8C)
        );
        g2.setPaint(degradadoEnemigo);
        g2.fill(fuselaje);

        // Borde carmesí neón
        g2.setColor(new Color(0xFF, 0x17, 0x44));
        g2.draw(fuselaje);

        // Visor/Ojo bio-mecánico brillante
        g2.setColor(new Color(0x00, 0xE5, 0xFF));
        g2.fillOval(x + 12, y + (alto / 2) - 4, 10, 8);
        g2.setColor(Color.WHITE);
        g2.drawOval(x + 12, y + (alto / 2) - 4, 10, 8);
    }

    @Override
    public String getTipo() {
        return "Nave Enemiga";
    }

    public int getPuntosRecompensa() {
        return PUNTOS_RECOMPENSA;
    }
}
