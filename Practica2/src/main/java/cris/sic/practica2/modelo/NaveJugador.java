package cris.sic.practica2.modelo;

import cris.sic.practica2.vista.PanelJuego;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Representa la nave espacial del jugador en el simulador.
 * Implementa la arquitectura estricta solicitada por el enunciado y el PDF de la Práctica 2:
 * 
 * 1. Hilo de Movimiento del Jugador:
 *    - Fácil (Explorador): sleep() corto (12 ms) -> Movimiento ágil, fluido y rápido.
 *    - Normal (Caza Estelar): sleep() estándar (25 ms) -> Respuesta de vuelo balanceada.
 *    - Difícil (Acorazado): sleep() mayor (50 ms) -> Nave pesada/lenta para esquivar.
 * 
 * 2. Hilo de Disparo del Jugador y Cadencia:
 *    - Fácil (Explorador): Hilo con sleep(2000) (cadencia de disparo de 2.0 segundos).
 *    - Normal (Caza Estelar): Hilo con sleep(1000) (cadencia de disparo de 1.0 segundo).
 *    - Difícil (Acorazado): Hilo con sleep(300) (ráfaga rápida cada 0.3 segundos).
 * 
 * 3. Mecánica de Bloqueo por Asteroide (Bludger):
 *    - Inmovilización y desactivación de sistemas por 2 segundos ante colisión.
 */
public class NaveJugador {

    // Coordenadas y dimensiones
    private int x;
    private int y;
    private final int ancho;
    private final int alto;

    // Parámetros de vuelo y piloto
    private int velocidad; // Píxeles por paso (10, 7, 4)
    private Piloto piloto;
    private PanelJuego panelJuego;

    // Banderas de control direccional
    private boolean moviendoArriba;
    private boolean moviendoAbajo;
    private boolean moviendoIzquierda;
    private boolean moviendoDerecha;

    // Estado del jugador
    private volatile boolean bloqueado; // Verdadero si fue impactado por un asteroide (Bludger)
    private volatile boolean puedeDisparar;
    private volatile boolean disparandoContinuo;
    private volatile boolean enPausa;
    private volatile boolean enEjecucion;
    private int disparosRealizados;

    // Hilos dedicados de control del jugador
    private HiloMovimientoJugador hiloMovimiento;
    private HiloDisparoContinuo hiloDisparo;

    // Límites de pantalla actuales
    private int minX = 0;
    private int minY = 54;
    private int maxX = 1000;
    private int maxY = 600;

    // Animación visual del motor
    private int tickAnimacion;
    private int tickBloqueo;

    /**
     * Constructor de la nave del jugador.
     *
     * @param xInicial Posición inicial en X
     * @param yInicial Posición inicial en Y
     * @param piloto   Piloto seleccionado (define nave, tiempos de sleep y cadencia)
     */
    public NaveJugador(int xInicial, int yInicial, Piloto piloto) {
        this.x = xInicial;
        this.y = yInicial;
        this.ancho = 64;
        this.alto = 36;
        this.piloto = piloto;
        this.velocidad = (piloto != null) ? piloto.getVelocidadMovimiento() : 7;
        this.puedeDisparar = true;
        this.disparandoContinuo = false;
        this.bloqueado = false;
        this.enPausa = false;
        this.enEjecucion = true;
        this.disparosRealizados = 0;
        this.tickAnimacion = 0;
        this.tickBloqueo = 0;
        detenerMovimiento();
    }

    /**
     * Vincula el panel de juego e inicializa los hilos concurrentes de la nave.
     *
     * @param panelJuego Panel donde se renderiza la nave y se insertan los proyectiles
     */
    public synchronized void iniciarHilos(PanelJuego panelJuego) {
        this.panelJuego = panelJuego;
        this.enEjecucion = true;
        this.puedeDisparar = true;

        // Iniciar Hilo de Movimiento del Jugador con el sleep según dificultad
        if (hiloMovimiento == null || !hiloMovimiento.isAlive()) {
            hiloMovimiento = new HiloMovimientoJugador();
            hiloMovimiento.start();
        }

        // Iniciar Hilo de Ráfaga/Disparo Continuo
        if (hiloDisparo == null || !hiloDisparo.isAlive()) {
            hiloDisparo = new HiloDisparoContinuo();
            hiloDisparo.start();
        }
    }

    /**
     * Detiene de forma limpia los hilos asociados a la nave.
     */
    public synchronized void detenerHilos() {
        this.enEjecucion = false;
        this.disparandoContinuo = false;
        detenerMovimiento();

        if (hiloMovimiento != null) {
            hiloMovimiento.interrupt();
            hiloMovimiento = null;
        }
        if (hiloDisparo != null) {
            hiloDisparo.interrupt();
            hiloDisparo = null;
        }
    }

    /**
     * Actualiza los límites del panel para las restricciones de vuelo.
     */
    public void configurarLimites(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    // =========================================================================
    // HILO DE MOVIMIENTO DEL JUGADOR CON SLEEP POR DIFICULTAD
    // =========================================================================

    /**
     * Hilo independiente que controla el desplazamiento continuo del jugador.
     * Regula la fluidez y velocidad mediante pausas de Thread.sleep():
     * - Explorador: sleep(12) -> Súper fluido y veloz.
     * - Caza Estelar: sleep(25) -> Velocidad estándar.
     * - Acorazado: sleep(50) -> Desplazamiento pesado y lento.
     */
    private class HiloMovimientoJugador extends Thread {
        public HiloMovimientoJugador() {
            super("HiloMovimientoJugador-" + ((piloto != null) ? piloto.getTipoNave() : "Nave"));
            setDaemon(true);
        }

        @Override
        public void run() {
            while (enEjecucion) {
                if (!enPausa && !bloqueado) {
                    if (moviendoArriba || moviendoAbajo || moviendoIzquierda || moviendoDerecha) {
                        ejecutarPasoMovimiento();
                    }
                }

                // Cálculo del tiempo de sleep según dificultad del piloto
                long tiempoSleep = (piloto != null) ? piloto.getSleepMovimientoMs() : 25;
                try {
                    Thread.sleep(tiempoSleep);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * Aplica el desplazamiento físico en las coordenadas de la nave y comprueba límites.
     */
    private synchronized void ejecutarPasoMovimiento() {
        if (moviendoArriba) {
            y -= velocidad;
        }
        if (moviendoAbajo) {
            y += velocidad;
        }
        if (moviendoIzquierda) {
            x -= velocidad;
        }
        if (moviendoDerecha) {
            x += velocidad;
        }

        aplicarLimitesEstrictos(minX, minY, maxX, maxY);
    }

    /**
     * Método público compatible con la suite de pruebas unitarias y el bucle principal.
     */
    public void actualizar(int minX, int minY, int maxX, int maxY) {
        configurarLimites(minX, minY, maxX, maxY);

        // Si el hilo independiente no está activo (ej. en pruebas de Fase 4), mover de forma síncrona
        if (hiloMovimiento == null || !hiloMovimiento.isAlive()) {
            if (moviendoArriba) y -= velocidad;
            if (moviendoAbajo) y += velocidad;
            if (moviendoIzquierda) x -= velocidad;
            if (moviendoDerecha) x += velocidad;
        }

        aplicarLimitesEstrictos(minX, minY, maxX, maxY);
        tickAnimacion = (tickAnimacion + 1) % 60;
        if (bloqueado) {
            tickBloqueo = (tickBloqueo + 1) % 60;
        }
    }

    /**
     * Aplica la restricción estricta de bordes para impedir que la nave abandone el panel jugable.
     */
    private void aplicarLimitesEstrictos(int minX, int minY, int maxX, int maxY) {
        if (x < minX) {
            x = minX;
        } else if (x + ancho > maxX) {
            x = Math.max(minX, maxX - ancho);
        }

        if (y < minY) {
            y = minY;
        } else if (y + alto > maxY) {
            y = Math.max(minY, maxY - alto);
        }
    }

    public void moverHacia(int objetivoX, int objetivoY, int minX, int minY, int maxX, int maxY) {
        if (bloqueado || enPausa) return;

        int centroX = objetivoX - (ancho / 2);
        int centroY = objetivoY - (alto / 2);

        this.x = Math.max(minX, Math.min(centroX, maxX - ancho));
        this.y = Math.max(minY, Math.min(centroY, maxY - alto));
    }

    // =========================================================================
    // HILO DE DISPARO DEL JUGADOR CON SLEEP DE CADENCIA
    // =========================================================================

    /**
     * Dispara un proyectil láser respetando la cadencia según la dificultad:
     * - Explorador: sleep(2000) (2 segundos)
     * - Caza Estelar: sleep(1000) (1 segundo)
     * - Acorazado: sleep(300) (0.3 segundos)
     */
    public synchronized boolean disparar() {
        if (!puedeDisparar || bloqueado || enPausa || panelJuego == null) {
            return false;
        }

        // Crear e instanciar el proyectil en el cañón de la nave
        int proyX = x + ancho;
        int proyY = y + (alto / 2) - 3;
        Color colorLaser = obtenerColorPlasmaLaser();

        Proyectil nuevoProyectil = new Proyectil(proyX, proyY, colorLaser, maxX);
        panelJuego.agregarProyectil(nuevoProyectil);
        disparosRealizados++;

        // Iniciar recarga con el sleep de cadencia en un hilo independiente
        long tiempoRecarga = (piloto != null) ? piloto.getTiempoRecargaMs() : 1000;
        puedeDisparar = false;

        new Thread(() -> {
            try {
                Thread.sleep(tiempoRecarga);
            } catch (InterruptedException ignored) {
            } finally {
                puedeDisparar = true;
            }
        }, "HiloRecargaDisparo-" + System.currentTimeMillis()).start();

        return true;
    }

    /**
     * Hilo para soporte de disparo continuo si el jugador mantiene pulsada la barra espaciadora.
     */
    private class HiloDisparoContinuo extends Thread {
        public HiloDisparoContinuo() {
            super("HiloDisparoContinuo");
            setDaemon(true);
        }

        @Override
        public void run() {
            while (enEjecucion) {
                if (disparandoContinuo && !enPausa && !bloqueado && puedeDisparar) {
                    disparar();
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public void setDisparandoContinuo(boolean disparando) {
        this.disparandoContinuo = disparando;
        if (disparando && puedeDisparar) {
            disparar();
        }
    }

    private Color obtenerColorPlasmaLaser() {
        if (piloto == null) return Color.CYAN;
        String tipo = piloto.getTipoNave();
        if (Piloto.NAVE_ACORAZADO.equalsIgnoreCase(tipo)) {
            return new Color(0xFF, 0x17, 0x44); // Láser rojo carmesí
        } else if (Piloto.NAVE_CAZA_ESTELAR.equalsIgnoreCase(tipo)) {
            return new Color(0xFF, 0xD7, 0x00); // Láser amarillo dorado
        } else {
            return new Color(0x00, 0xE5, 0xFF); // Láser cian neón
        }
    }

    // =========================================================================
    // MECÁNICA DE BLOQUEO POR ASTEROIDE / BLUDGER (2 SEGUNDOS)
    // =========================================================================

    /**
     * Bloquea temporalmente los sistemas de la nave por la duración especificada
     * (2000 ms ante colisión con un Asteroide / Bludger).
     */
    public synchronized void bloquearPor(long milisegundos) {
        this.bloqueado = true;
        detenerMovimiento();
        this.disparandoContinuo = false;

        new Thread(() -> {
            try {
                Thread.sleep(milisegundos);
            } catch (InterruptedException ignored) {
            } finally {
                this.bloqueado = false;
            }
        }, "HiloBloqueoNave-" + System.currentTimeMillis()).start();
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    // =========================================================================
    // RENDERIZADO GRÁFICO 2D
    // =========================================================================

    public void dibujar(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        String tipoNave = (piloto != null) ? piloto.getTipoNave() : Piloto.NAVE_CAZA_ESTELAR;

        // 1. Dibuja llama del propulsor (hacia la izquierda, simulando avance hacia la derecha)
        if (!bloqueado) {
            dibujarPropulsor(g2, tipoNave);
        }

        // 2. Dibuja fuselaje según nave
        if (Piloto.NAVE_EXPLORADOR.equalsIgnoreCase(tipoNave)) {
            dibujarNaveExplorador(g2);
        } else if (Piloto.NAVE_ACORAZADO.equalsIgnoreCase(tipoNave)) {
            dibujarNaveAcorazado(g2);
        } else {
            dibujarNaveCazaEstelar(g2);
        }

        // 3. Efecto visual de paralización si está bloqueada por Asteroide (Bludger)
        if (bloqueado) {
            dibujarEfectoBloqueo(g2);
        }

        // 4. Etiqueta informativa del piloto sobre la nave
        if (piloto != null && piloto.getNombre() != null && !piloto.getNombre().isEmpty()) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 10));
            g2.setColor(new Color(255, 255, 255, 180));
            String etiqueta = piloto.getNombre();
            int textoAncho = g2.getFontMetrics().stringWidth(etiqueta);
            int textoX = x + (ancho - textoAncho) / 2;
            int textoY = y - 4;
            g2.drawString(etiqueta, textoX, textoY);
        }
    }

    private void dibujarPropulsor(Graphics2D g2, String tipoNave) {
        int oscilacion = (tickAnimacion % 6) * 3;
        int longitudLlama = 14 + oscilacion;
        if (moviendoDerecha) {
            longitudLlama += 8;
        }

        int fuegoX = x - longitudLlama;
        int centerY = y + (alto / 2);

        Polygon llamaExterior = new Polygon();
        llamaExterior.addPoint(x + 2, centerY - 8);
        llamaExterior.addPoint(fuegoX, centerY);
        llamaExterior.addPoint(x + 2, centerY + 8);

        if (Piloto.NAVE_EXPLORADOR.equalsIgnoreCase(tipoNave)) {
            g2.setColor(new Color(0, 229, 255, 200));
        } else if (Piloto.NAVE_ACORAZADO.equalsIgnoreCase(tipoNave)) {
            g2.setColor(new Color(255, 87, 34, 220));
        } else {
            g2.setColor(new Color(255, 193, 7, 210));
        }
        g2.fill(llamaExterior);

        int nucleoX = x - (longitudLlama / 2);
        Polygon llamaInterior = new Polygon();
        llamaInterior.addPoint(x + 2, centerY - 4);
        llamaInterior.addPoint(nucleoX, centerY);
        llamaInterior.addPoint(x + 2, centerY + 4);

        g2.setColor(new Color(255, 255, 255, 240));
        g2.fill(llamaInterior);
    }

    private void dibujarEfectoBloqueo(Graphics2D g2) {
        // Campo electro-magnético de aturdimiento
        int pulso = (tickBloqueo % 10) * 2;
        g2.setColor(new Color(255, 87, 34, 150));
        g2.drawOval(x - 6 - pulso / 2, y - 6 - pulso / 2, ancho + 12 + pulso, alto + 12 + pulso);

        g2.setColor(new Color(255, 235, 59, 200));
        g2.drawString("⚡ BLOQUEADO", x + 2, y - 12);
    }

    private void dibujarNaveExplorador(Graphics2D g2) {
        Polygon alas = new Polygon();
        alas.addPoint(x + ancho, y + alto / 2);
        alas.addPoint(x + 12, y + 2);
        alas.addPoint(x + 2, y + 10);
        alas.addPoint(x + 8, y + alto / 2);
        alas.addPoint(x + 2, y + alto - 10);
        alas.addPoint(x + 12, y + alto - 2);

        GradientPaint degradadoCian = new GradientPaint(
                x, y, new Color(0x00, 0x83, 0x8F),
                x + ancho, y, new Color(0x00, 0xE5, 0xFF)
        );
        g2.setPaint(degradadoCian);
        g2.fill(alas);

        g2.setColor(new Color(0x18, 0xFF, 0xFF));
        g2.draw(alas);

        g2.setColor(new Color(0xE0, 0xF7, 0xFA));
        g2.fillOval(x + ancho - 26, y + (alto / 2) - 4, 16, 8);
        g2.setColor(Color.WHITE);
        g2.drawOval(x + ancho - 26, y + (alto / 2) - 4, 16, 8);
    }

    private void dibujarNaveCazaEstelar(Graphics2D g2) {
        Polygon fuselaje = new Polygon();
        fuselaje.addPoint(x + ancho, y + alto / 2);
        fuselaje.addPoint(x + 16, y + 4);
        fuselaje.addPoint(x + 4, y + 10);
        fuselaje.addPoint(x + 12, y + alto / 2);
        fuselaje.addPoint(x + 4, y + alto - 10);
        fuselaje.addPoint(x + 16, y + alto - 4);

        GradientPaint degradadoAzul = new GradientPaint(
                x, y, new Color(0x0D, 0x47, 0xA1),
                x + ancho, y, new Color(0x1E, 0x88, 0xE5)
        );
        g2.setPaint(degradadoAzul);
        g2.fill(fuselaje);

        g2.setColor(new Color(0xFF, 0xD7, 0x00));
        g2.draw(fuselaje);

        g2.setColor(new Color(0xFF, 0xEB, 0x3B));
        g2.fillRect(x + 14, y + 2, 10, 2);
        g2.fillRect(x + 14, y + alto - 4, 10, 2);

        g2.setColor(new Color(0xFF, 0xEC, 0xB3));
        g2.fillOval(x + ancho - 28, y + (alto / 2) - 5, 18, 10);
        g2.setColor(new Color(0xFF, 0xD7, 0x00));
        g2.drawOval(x + ancho - 28, y + (alto / 2) - 5, 18, 10);
    }

    private void dibujarNaveAcorazado(Graphics2D g2) {
        Polygon blindaje = new Polygon();
        blindaje.addPoint(x + ancho, y + (alto / 2) - 6);
        blindaje.addPoint(x + ancho, y + (alto / 2) + 6);
        blindaje.addPoint(x + 22, y + alto - 2);
        blindaje.addPoint(x + 2, y + alto - 5);
        blindaje.addPoint(x + 6, y + alto / 2);
        blindaje.addPoint(x + 2, y + 5);
        blindaje.addPoint(x + 22, y + 2);

        GradientPaint degradadoTitanio = new GradientPaint(
                x, y, new Color(0x26, 0x32, 0x38),
                x + ancho, y, new Color(0x45, 0x5A, 0x64)
        );
        g2.setPaint(degradadoTitanio);
        g2.fill(blindaje);

        g2.setColor(new Color(0xE5, 0x39, 0x35));
        g2.draw(blindaje);
        g2.fillRect(x + 18, y + 8, 18, 4);
        g2.fillRect(x + 18, y + alto - 12, 18, 4);

        g2.setColor(new Color(0xFF, 0x52, 0x52));
        g2.fillRect(x + ancho - 22, y + (alto / 2) - 4, 14, 8);
        g2.setColor(Color.WHITE);
        g2.drawRect(x + ancho - 22, y + (alto / 2) - 4, 14, 8);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, ancho, alto);
    }

    public void detenerMovimiento() {
        this.moviendoArriba = false;
        this.moviendoAbajo = false;
        this.moviendoIzquierda = false;
        this.moviendoDerecha = false;
    }

    public void resetearPosicion(int nuevoX, int nuevoY) {
        this.x = nuevoX;
        this.y = nuevoY;
        detenerMovimiento();
    }

    public void pausar() {
        this.enPausa = true;
    }

    public void reanudar() {
        this.enPausa = false;
    }

    // Getters y Setters
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

    public void setVelocidad(int velocidad) {
        this.velocidad = velocidad;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    public void setPiloto(Piloto piloto) {
        this.piloto = piloto;
        if (piloto != null) {
            this.velocidad = piloto.getVelocidadMovimiento();
        }
    }

    public boolean isMoviendoArriba() {
        return moviendoArriba;
    }

    public void setArriba(boolean moviendoArriba) {
        this.moviendoArriba = moviendoArriba;
    }

    public boolean isMoviendoAbajo() {
        return moviendoAbajo;
    }

    public void setAbajo(boolean moviendoAbajo) {
        this.moviendoAbajo = moviendoAbajo;
    }

    public boolean isMoviendoIzquierda() {
        return moviendoIzquierda;
    }

    public void setIzquierda(boolean moviendoIzquierda) {
        this.moviendoIzquierda = moviendoIzquierda;
    }

    public boolean isMoviendoDerecha() {
        return moviendoDerecha;
    }

    public void setDerecha(boolean moviendoDerecha) {
        this.moviendoDerecha = moviendoDerecha;
    }

    public boolean isPuedeDisparar() {
        return puedeDisparar;
    }

    public boolean isDisparandoContinuo() {
        return disparandoContinuo;
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    public void setPanelJuego(PanelJuego panelJuego) {
        this.panelJuego = panelJuego;
    }

    public int getDisparosRealizados() {
        return disparosRealizados;
    }
}
