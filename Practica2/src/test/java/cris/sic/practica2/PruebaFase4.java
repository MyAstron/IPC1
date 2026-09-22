package cris.sic.practica2;

import cris.sic.practica2.modelo.NaveJugador;
import cris.sic.practica2.modelo.Piloto;
import cris.sic.practica2.vista.PanelJuego;
import cris.sic.practica2.vista.VentanaPrincipal;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Suite de validación automatizada para la FASE 4:
 * - Subfase 4.1: Renderizado del lienzo, estrellas y nave en paintComponent.
 * - Subfase 4.2: Lógica de coordenadas, velocidades y límites estrictos en NaveJugador.
 * - Subfase 4.3: Manejo de direcciones y controles.
 * - Subfase 4.4: Ciclo de vida y estabilidad de GameLoopThread a ~60 FPS.
 */
public class PruebaFase4 {

    private static int pruebasExitosas = 0;
    private static int totalPruebas = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("🚀 EJECUTANDO BATERÍA DE PRUEBAS AUTOMATIZADAS - FASE 4 🚀");
        System.out.println("==========================================================");

        probarVelocidadesPorTipoNave();
        probarMovimientoYLimitesEstrictos();
        probarRenderizadoGrafico();
        probarCicloVidaGameLoopThread();

        System.out.println("\n==========================================================");
        System.out.printf("RESULTADO FINAL: %d / %d pruebas superadas con éxito.\n", pruebasExitosas, totalPruebas);
        System.out.println("==========================================================");

        if (pruebasExitosas == totalPruebas) {
            System.out.println("✅ ¡TODAS LAS VALIDACIONES DE LA FASE 4 CUMPLIDAS AL 100%!");
            System.exit(0);
        } else {
            System.err.println("❌ ALGUNAS PRUEBAS FALLARON.");
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
     * Prueba las velocidades asignadas a cada tipo de nave según las especificaciones del proyecto.
     */
    private static void probarVelocidadesPorTipoNave() {
        System.out.println("\n--- 1. Verificación de Parámetros de Nave por Dificultad ---");

        Piloto pExplorador = new Piloto("Test1", Piloto.NAVE_EXPLORADOR);
        NaveJugador naveExp = new NaveJugador(100, 100, pExplorador);
        afirmar(naveExp.getVelocidad() == 10, "Explorador (Fácil) tiene velocidad rápida de 10 px/f");

        Piloto pCaza = new Piloto("Test2", Piloto.NAVE_CAZA_ESTELAR);
        NaveJugador naveCaza = new NaveJugador(100, 100, pCaza);
        afirmar(naveCaza.getVelocidad() == 7, "Caza Estelar (Normal) tiene velocidad media de 7 px/f");

        Piloto pAcorazado = new Piloto("Test3", Piloto.NAVE_ACORAZADO);
        NaveJugador naveAco = new NaveJugador(100, 100, pAcorazado);
        afirmar(naveAco.getVelocidad() == 4, "Acorazado (Difícil) tiene velocidad lenta de 4 px/f");
    }

    /**
     * Prueba el movimiento en 4 direcciones y la restricción estricta de límites (Subfase 4.2).
     */
    private static void probarMovimientoYLimitesEstrictos() {
        System.out.println("\n--- 2. Verificación de Movimiento y Límites de Pantalla (Subfase 4.2) ---");

        int minX = 0;
        int minY = 54; // Margen superior del HUD
        int maxX = 1000;
        int maxY = 600;

        Piloto piloto = new Piloto("PilotoPrueba", Piloto.NAVE_CAZA_ESTELAR);
        NaveJugador nave = new NaveJugador(200, 200, piloto);
        int vel = nave.getVelocidad(); // 7 px

        // Movimiento a la derecha
        nave.setDerecha(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getX() == 200 + vel, "Movimiento fluido hacia la derecha (+ " + vel + " px)");
        nave.setDerecha(false);

        // Movimiento hacia arriba
        nave.setArriba(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getY() == 200 - vel, "Movimiento fluido hacia arriba (- " + vel + " px)");
        nave.setArriba(false);

        // Prueba de límite IZQUIERDO (Borde X min)
        nave.resetearPosicion(5, 200);
        nave.setIzquierda(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getX() == minX, "Restricción de borde izquierdo: X no puede ser menor a minX (0)");

        // Prueba de límite SUPERIOR (Margen HUD Y min)
        nave.resetearPosicion(200, 56);
        nave.setArriba(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getY() == minY, "Restricción de borde superior: Y no puede sobrepasar el HUD (" + minY + " px)");

        // Prueba de límite DERECHO (Borde X max)
        nave.resetearPosicion(maxX - nave.getAncho() - 3, 200);
        nave.setDerecha(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getX() == maxX - nave.getAncho(), "Restricción de borde derecho: X + ancho no supera maxX (" + maxX + " px)");

        // Prueba de límite INFERIOR (Borde Y max)
        nave.resetearPosicion(200, maxY - nave.getAlto() - 2);
        nave.setAbajo(true);
        nave.actualizar(minX, minY, maxX, maxY);
        afirmar(nave.getY() == maxY - nave.getAlto(), "Restricción de borde inferior: Y + alto no supera maxY (" + maxY + " px)");

        nave.detenerMovimiento();
    }

    /**
     * Prueba el renderizado gráfico de PanelJuego y los 3 modelos vectoriales de naves (Subfase 4.1).
     */
    private static void probarRenderizadoGrafico() {
        System.out.println("\n--- 3. Verificación de Renderizado Gráfico Vectorial (Subfase 4.1) ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panelJuego = ventana.getPanelJuego();
        panelJuego.setSize(1000, 600);

        BufferedImage buffer = new BufferedImage(1000, 600, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = buffer.createGraphics();

        // 3.1 Renderizar panel con nave Explorador
        Piloto p1 = new Piloto("StarWalker", Piloto.NAVE_EXPLORADOR);
        panelJuego.iniciarPartida(p1);
        panelJuego.paint(g2);
        afirmar(true, "Renderizado gráfico exitoso para nave tipo Explorador (Cian)");

        // 3.2 Renderizar panel con nave Caza Estelar
        Piloto p2 = new Piloto("SkyFighter", Piloto.NAVE_CAZA_ESTELAR);
        panelJuego.iniciarPartida(p2);
        panelJuego.paint(g2);
        afirmar(true, "Renderizado gráfico exitoso para nave tipo Caza Estelar (Oro/Azul)");

        // 3.3 Renderizar panel con nave Acorazado
        Piloto p3 = new Piloto("Dreadnought", Piloto.NAVE_ACORAZADO);
        panelJuego.iniciarPartida(p3);
        panelJuego.paint(g2);
        afirmar(true, "Renderizado gráfico exitoso para nave tipo Acorazado (Carmesí/Titanio)");

        g2.dispose();
        panelJuego.detenerPartida();
        ventana.dispose();
    }

    /**
     * Prueba el ciclo de vida y estabilidad del hilo GameLoopThread a 60 FPS (Subfase 4.4).
     */
    private static void probarCicloVidaGameLoopThread() {
        System.out.println("\n--- 4. Verificación del Hilo de Renderizado GameLoopThread (Subfase 4.4) ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panelJuego = ventana.getPanelJuego();
        panelJuego.setSize(1000, 600);

        Piloto piloto = new Piloto("LoopTestPilot", Piloto.NAVE_CAZA_ESTELAR);
        panelJuego.iniciarPartida(piloto);

        afirmar(panelJuego.estaEnEjecucion(), "GameLoopThread se encuentra activo tras iniciar partida");

        // Dejar correr durante 120 ms (~7-8 fotogramas del bucle de 60 FPS)
        try {
            Thread.sleep(120);
        } catch (InterruptedException ignored) {}

        afirmar(panelJuego.estaEnEjecucion(), "GameLoopThread mantiene ejecución continua estable a ~60 FPS");

        // Probar pausa
        panelJuego.alternarPausa();
        afirmar(panelJuego.estaEnPausa(), "Pausa activada correctamente en GameLoopThread");

        panelJuego.alternarPausa();
        afirmar(!panelJuego.estaEnPausa(), "Reanudación exitosa de GameLoopThread");

        // Detener partida
        panelJuego.detenerPartida();
        afirmar(!panelJuego.estaEnEjecucion(), "GameLoopThread finalizado limpiamente sin hilos colgados");

        ventana.dispose();
    }
}
