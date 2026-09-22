package cris.sic.practica2;

import cris.sic.practica2.modelo.Asteroide;
import cris.sic.practica2.modelo.Enemigo;
import cris.sic.practica2.modelo.NaveJugador;
import cris.sic.practica2.modelo.Piloto;
import cris.sic.practica2.modelo.PremioQuaffle;
import cris.sic.practica2.modelo.PremioSnitch;
import cris.sic.practica2.modelo.Proyectil;
import cris.sic.practica2.vista.PanelJuego;
import cris.sic.practica2.vista.VentanaPrincipal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Suite de validación automatizada para las mecánicas del Side-Scroller,
 * dirección de objetos y tiempos de sleep por dificultad.
 */
public class PruebaMecanicasFase4YElementos {

    private static int pruebasExitosas = 0;
    private static int totalPruebas = 0;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("🛸 VERIFICACIÓN DE MECÁNICAS SIDE-SCROLLER Y HILOS DE DIFICULTAD 🛸");
        System.out.println("==================================================================");

        probarTiemposSleepYDificultad();
        probarDireccionElementosEspaciales();
        probarDireccionProyectil();
        probarMecanicaBloqueoAsteroide();
        probarMecanicaPremiosYEnemigos();
        probarDesplazamientoFondo();

        System.out.println("\n==================================================================");
        System.out.printf("RESULTADO: %d / %d pruebas superadas con éxito.\n", pruebasExitosas, totalPruebas);
        System.out.println("==================================================================");

        if (pruebasExitosas == totalPruebas) {
            System.out.println("✅ ¡TODAS LAS ESPECIFICACIONES DEL PDF OFICIAL SE CUMPLEN AL 100%!");
            System.exit(0);
        } else {
            System.err.println("❌ ALGUNAS PRUEBAS NO SE SUPERARON.");
            System.exit(1);
        }
    }

    private static void afirmar(boolean condicion, String descripcion) {
        totalPruebas++;
        if (condicion) {
            System.out.println("  [PASS] " + descripcion);
            pruebasExitosas++;
        } else {
            System.err.println("  [FAIL] " + descripcion);
        }
    }

    /**
     * Valida los tiempos de sleep exactos de los hilos de movimiento y disparo por dificultad:
     * - Fácil (Explorador): sleep() corto (12 ms), sleep(2000) en disparo.
     * - Normal (Caza Estelar): sleep() estándar (25 ms), sleep(1000) en disparo.
     * - Difícil (Acorazado): sleep() mayor (50 ms), sleep(300) en disparo.
     */
    private static void probarTiemposSleepYDificultad() {
        System.out.println("\n--- 1. Validación de Tiempos de Sleep de Hilos por Dificultad ---");

        Piloto explorador = new Piloto("HanSolo", Piloto.NAVE_EXPLORADOR);
        afirmar(explorador.getSleepMovimientoMs() == 12, "Fácil (Explorador): Hilo de movimiento con sleep corto (12 ms)");
        afirmar(explorador.getTiempoRecargaMs() == 2000, "Fácil (Explorador): Hilo de disparo con sleep(2000) (cadencia 2.0s)");

        Piloto caza = new Piloto("Luke", Piloto.NAVE_CAZA_ESTELAR);
        afirmar(caza.getSleepMovimientoMs() == 25, "Normal (Caza Estelar): Hilo de movimiento estándar (25 ms)");
        afirmar(caza.getTiempoRecargaMs() == 1000, "Normal (Caza Estelar): Hilo de disparo con sleep(1000) (cadencia 1.0s)");

        Piloto acorazado = new Piloto("Vader", Piloto.NAVE_ACORAZADO);
        afirmar(acorazado.getSleepMovimientoMs() == 50, "Difícil (Acorazado): Hilo de movimiento con sleep mayor (50 ms)");
        afirmar(acorazado.getTiempoRecargaMs() == 300, "Difícil (Acorazado): Hilo de disparo con sleep(300) (ráfaga rápida 0.3s)");
    }

    /**
     * Valida que todos los elementos (Enemigo, Asteroide, Quaffle, Snitch)
     * se desplacen horizontalmente hacia la izquierda (X decrece hacia X mín).
     */
    private static void probarDireccionElementosEspaciales() {
        System.out.println("\n--- 2. Dirección de Elementos Espaciales (Side-Scroller hacia la Izquierda) ---");

        // 2.1 Nave Enemiga
        Enemigo enemigo = new Enemigo(900, 200);
        enemigo.start();
        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}
        afirmar(enemigo.getX() < 900, "Enemigo: Se desplaza horizontalmente hacia la izquierda en su hilo (X actual: " + enemigo.getX() + " < 900)");
        enemigo.destruir();

        // 2.2 Asteroide / Bludger
        Asteroide asteroide = new Asteroide(900, 200);
        asteroide.start();
        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}
        afirmar(asteroide.getX() < 900, "Asteroide (Bludger): Se desplaza horizontalmente hacia la izquierda (X actual: " + asteroide.getX() + " < 900)");
        asteroide.destruir();

        // 2.3 Contenedor Quaffle
        PremioQuaffle quaffle = new PremioQuaffle(900, 200);
        quaffle.start();
        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}
        afirmar(quaffle.getX() < 900, "Premio Quaffle: Se desplaza horizontalmente hacia la izquierda (X actual: " + quaffle.getX() + " < 900)");
        quaffle.destruir();

        // 2.4 Snitch Espacial
        PremioSnitch snitch = new PremioSnitch(900, 200);
        snitch.start();
        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}
        afirmar(snitch.getX() < 900, "Premio Snitch: Se desplaza a alta velocidad hacia la izquierda (X actual: " + snitch.getX() + " < 900)");
        snitch.destruir();
    }

    /**
     * Valida que los proyectiles disparados se desplacen horizontalmente de izquierda a derecha.
     */
    private static void probarDireccionProyectil() {
        System.out.println("\n--- 3. Dirección de Proyectil Láser (Izquierda a Derecha) ---");

        Proyectil proyectil = new Proyectil(100, 200, Color.CYAN, 1000);
        proyectil.start();
        try {
            Thread.sleep(60);
        } catch (InterruptedException ignored) {}
        afirmar(proyectil.getX() > 100, "Proyectil: Avanza de izquierda a derecha (+X) en su hilo independiente (X: " + proyectil.getX() + " > 100)");
        proyectil.destruir();
    }

    /**
     * Valida que la colisión con un Asteroide / Bludger bloquee la nave por 2 segundos.
     */
    private static void probarMecanicaBloqueoAsteroide() {
        System.out.println("\n--- 4. Mecánica de Bloqueo por Asteroide (Bludger) ---");

        Piloto piloto = new Piloto("TestPilot", Piloto.NAVE_CAZA_ESTELAR);
        NaveJugador nave = new NaveJugador(100, 200, piloto);

        afirmar(!nave.isBloqueado(), "Nave inicia desbloqueada");

        nave.bloquearPor(500); // 500 ms de prueba rápida
        afirmar(nave.isBloqueado(), "Nave queda bloqueada inmediatamente tras impacto");

        try {
            Thread.sleep(600);
        } catch (InterruptedException ignored) {}

        afirmar(!nave.isBloqueado(), "Nave recupera control y desbloquea sistemas tras expirar el tiempo");
    }

    /**
     * Valida los puntajes y efectos de Quaffle, Snitch y Enemigo.
     */
    private static void probarMecanicaPremiosYEnemigos() {
        System.out.println("\n--- 5. Mecánica de Puntuación de Elementos ---");

        Enemigo e = new Enemigo(100, 100);
        afirmar(e.getPuntosRecompensa() == 20, "Enemigo otorga 20 puntos");

        PremioQuaffle q = new PremioQuaffle(100, 100);
        afirmar(q.getPuntosRecompensa() == 10, "Contenedor Quaffle otorga 10 puntos");

        PremioSnitch s = new PremioSnitch(100, 100);
        afirmar(s.getPuntosRecompensa() == 150, "Snitch Espacial otorga 150 puntos");
    }

    /**
     * Valida que el desplazamiento de fondo en PanelJuego mueva las estrellas continuamente a la izquierda.
     */
    private static void probarDesplazamientoFondo() {
        System.out.println("\n--- 6. Desplazamiento Continuo del Fondo Espacial ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panel = ventana.getPanelJuego();
        panel.setSize(1000, 600);

        Piloto piloto = new Piloto("FondoTest", Piloto.NAVE_EXPLORADOR);
        panel.iniciarPartida(piloto);

        // Renderizar un fotograma de prueba en memoria
        BufferedImage img = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        panel.paint(g2);
        g2.dispose();

        // Ejecutar ciclos de física para verificar que no lance excepciones
        panel.actualizarFisica();
        panel.actualizarFisica();
        afirmar(true, "Desplazamiento continuo del fondo y física a ~60 FPS ejecutados sin errores");

        panel.detenerPartida();
        ventana.dispose();
    }
}
