package cris.sic.practica2;

import cris.sic.practica2.modelo.NaveJugador;
import cris.sic.practica2.modelo.Piloto;
import cris.sic.practica2.modelo.Proyectil;
import cris.sic.practica2.vista.PanelJuego;
import cris.sic.practica2.vista.VentanaPrincipal;

import java.awt.Color;

/**
 * Suite de validación automatizada exhaustiva para la FASE 5:
 * - Subfase 5.1: Clase Proyectil en hilo independiente con movimiento de izquierda a derecha.
 * - Subfase 5.2: Acción de disparo asignada en NaveJugador (tecla Espacio y ráfaga continua).
 * - Subfase 5.3: Restricción de tiempo de recarga (sleep) según dificultad:
 *                * Explorador: 2000 ms
 *                * Caza Estelar: 1000 ms
 *                * Acorazado: 300 ms (Ráfaga rápida)
 * - Subfase 5.4: Autodestrucción y liberación de memoria al cruzar el borde derecho del panel.
 * - Punto de Espera y Prueba: Simulación de disparo sostenido comparando cadencia entre Explorador y Acorazado.
 */
public class PruebaFase5 {

    private static int pruebasExitosas = 0;
    private static int totalPruebas = 0;

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("🔥 EJECUTANDO PRUEBAS AUTOMATIZADAS - FASE 5: PROYECTILES E HILOS 🔥");
        System.out.println("==================================================================");

        probarSubfase51ClaseProyectil();
        probarSubfase52AccionDisparoNave();
        probarSubfase53TiemposRecargaYCadencia();
        probarSubfase54EliminacionYLiberacionMemoria();
        probarPuntoEsperaRafagaSostenida();

        System.out.println("\n==================================================================");
        System.out.printf("RESULTADO FASE 5: %d / %d pruebas superadas con éxito.\n", pruebasExitosas, totalPruebas);
        System.out.println("==================================================================");

        if (pruebasExitosas == totalPruebas) {
            System.out.println("✅ ¡TODOS LOS REQUERIMIENTOS DE LA FASE 5 CUMPLIDOS AL 100%!");
            System.exit(0);
        } else {
            System.err.println("❌ ALGUNAS PRUEBAS DE LA FASE 5 FALLARON.");
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
     * Subfase 5.1: Proyectil extiende Thread, tiene coordenadas (x, y), velocidad
     * y avanza de izquierda a derecha (+X) en su propio hilo.
     */
    private static void probarSubfase51ClaseProyectil() {
        System.out.println("\n--- 1. Subfase 5.1: Clase Proyectil e Hilo Independiente ---");

        Proyectil p = new Proyectil(100, 250, Color.CYAN, 1000);
        afirmar(p instanceof Thread, "Proyectil es una subclase nativa de Thread");
        afirmar(p.getX() == 100 && p.getY() == 250, "Coordenadas iniciales (x=100, y=250) correctas");
        afirmar(p.getVelocidad() > 0, "Velocidad de proyectil positiva (velocidad=" + p.getVelocidad() + " px/frame)");
        afirmar(p.isActivo(), "Proyectil inicia en estado activo");

        // Iniciar hilo del proyectil y validar desplazamiento a la derecha
        p.start();
        try {
            Thread.sleep(60); // ~3-4 fotogramas
        } catch (InterruptedException ignored) {}

        afirmar(p.getX() > 100, "Desplazamiento horizontal verificado: X avanzó hacia la derecha (X=" + p.getX() + " > 100)");
        p.destruir();
        afirmar(!p.isActivo(), "Al destruir el proyectil, pasa a estado inactivo");
    }

    /**
     * Subfase 5.2: Acción de disparo en NaveJugador y soporte de ráfaga continua con tecla Espacio.
     */
    private static void probarSubfase52AccionDisparoNave() {
        System.out.println("\n--- 2. Subfase 5.2: Acción de Disparo en NaveJugador ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panel = ventana.getPanelJuego();
        panel.setSize(1000, 600);

        Piloto piloto = new Piloto("DisparoPilot", Piloto.NAVE_CAZA_ESTELAR);
        panel.iniciarPartida(piloto);
        NaveJugador nave = panel.getNaveJugador();

        int proyectilesIniciales = panel.getTotalProyectiles();
        afirmar(proyectilesIniciales == 0, "No hay proyectiles en pantalla al comenzar");

        // Disparo individual simulado (equivalente a pulsar Espacio)
        boolean disparoExitoso = nave.disparar();
        afirmar(disparoExitoso, "Acción nave.disparar() ejecutada con éxito");
        afirmar(panel.getTotalProyectiles() == 1, "Proyectil registrado en el vector nativo de PanelJuego");

        Proyectil proyDisparado = panel.obtenerProyectiles()[0];
        afirmar(proyDisparado != null && proyDisparado.isAlive(), "El proyectil disparado tiene su hilo concurrente en ejecución");
        afirmar(proyDisparado.getX() >= nave.getX() + nave.getAncho(), "El proyectil nace en la proa/cañón de la nave");

        panel.detenerPartida();
        ventana.dispose();
    }

    /**
     * Subfase 5.3: Restricción de tiempo de recarga (sleep) en la nave según el tipo de piloto:
     * - Explorador: 2000 ms
     * - Caza Estelar: 1000 ms
     * - Acorazado: 300 ms
     */
    private static void probarSubfase53TiemposRecargaYCadencia() {
        System.out.println("\n--- 3. Subfase 5.3: Restricción de Tiempo de Recarga (sleep) por Dificultad ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panel = ventana.getPanelJuego();
        panel.setSize(1000, 600);

        // 3.1 Piloto Acorazado (Difícil: 300 ms)
        Piloto pAcorazado = new Piloto("DreadTest", Piloto.NAVE_ACORAZADO);
        afirmar(pAcorazado.getTiempoRecargaMs() == 300, "Piloto Acorazado tiene configurada cadencia de 300 ms");
        panel.iniciarPartida(pAcorazado);
        NaveJugador naveAco = panel.getNaveJugador();

        afirmar(naveAco.disparar(), "Acorazado: Primer disparo permitido");
        afirmar(!naveAco.isPuedeDisparar(), "Acorazado: Cañón entra inmediatamente en recarga (puedeDisparar = false)");
        afirmar(!naveAco.disparar(), "Acorazado: Disparo bloqueado durante los 300 ms de recarga");

        try {
            Thread.sleep(350); // Esperar que expire el sleep(300)
        } catch (InterruptedException ignored) {}

        afirmar(naveAco.isPuedeDisparar(), "Acorazado: Cañón listo nuevamente tras 300 ms de recarga");
        afirmar(naveAco.disparar(), "Acorazado: Segundo disparo exitoso tras cumplirse la cadencia");

        // 3.2 Piloto Caza Estelar (Normal: 1000 ms)
        Piloto pCaza = new Piloto("CazaTest", Piloto.NAVE_CAZA_ESTELAR);
        afirmar(pCaza.getTiempoRecargaMs() == 1000, "Piloto Caza Estelar tiene configurada cadencia de 1000 ms");
        panel.iniciarPartida(pCaza);
        NaveJugador naveCaza = panel.getNaveJugador();

        afirmar(naveCaza.disparar(), "Caza Estelar: Primer disparo permitido");
        afirmar(!naveCaza.disparar(), "Caza Estelar: Disparo bloqueado inmediatamente después");

        try {
            Thread.sleep(400); // 400 ms < 1000 ms
        } catch (InterruptedException ignored) {}
        afirmar(!naveCaza.isPuedeDisparar(), "Caza Estelar: Sigue recargando a los 400 ms");

        // 3.3 Piloto Explorador (Fácil: 2000 ms)
        Piloto pExplorador = new Piloto("ExpTest", Piloto.NAVE_EXPLORADOR);
        afirmar(pExplorador.getTiempoRecargaMs() == 2000, "Piloto Explorador tiene configurada cadencia de 2000 ms (2.0s)");
        panel.iniciarPartida(pExplorador);
        NaveJugador naveExp = panel.getNaveJugador();

        afirmar(naveExp.disparar(), "Explorador: Primer disparo permitido");
        afirmar(!naveExp.disparar(), "Explorador: Bloqueado por recarga de 2.0s");

        panel.detenerPartida();
        ventana.dispose();
    }

    /**
     * Subfase 5.4: Autodestrucción de proyectiles al salir del borde derecho y liberación de memoria.
     */
    private static void probarSubfase54EliminacionYLiberacionMemoria() {
        System.out.println("\n--- 4. Subfase 5.4: Eliminación de Hilos de Proyectiles al Salir del Borde Derecho ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panel = ventana.getPanelJuego();
        panel.setSize(1000, 600);

        Piloto piloto = new Piloto("MemoryTestPilot", Piloto.NAVE_CAZA_ESTELAR);
        panel.iniciarPartida(piloto);

        // Crear un proyectil a pocos píxeles del borde derecho (X = 990, límite = 1000)
        Proyectil proyectilBorde = new Proyectil(990, 200, Color.CYAN, 1000);
        panel.agregarProyectil(proyectilBorde);
        afirmar(panel.getTotalProyectiles() == 1, "Proyectil agregado cerca del borde");
        afirmar(proyectilBorde.isActivo(), "Proyectil inicia activo");

        // Esperar unos milisegundos para que el hilo del proyectil cruce el borde X=1000
        try {
            Thread.sleep(80);
        } catch (InterruptedException ignored) {}

        afirmar(!proyectilBorde.isActivo(), "El hilo del proyectil se auto-desactiva al cruzar X máx (1000)");

        // Invocar el ciclo de física para que ejecute limpiarObjetosInactivos()
        panel.actualizarFisica();
        afirmar(panel.getTotalProyectiles() == 0, "El vector de proyectiles compacta y libera la referencia a null para el GC");

        panel.detenerPartida();
        ventana.dispose();
    }

    /**
     * Punto de Espera y Prueba: Mantener presionada la tecla de disparo (ráfaga sostenida)
     * con una nave Explorador y luego con un Acorazado, comprobando la cadencia de ráfaga.
     */
    private static void probarPuntoEsperaRafagaSostenida() {
        System.out.println("\n--- 5. Punto de Espera y Prueba: Simulación de Ráfaga Sostenida (Explorador vs Acorazado) ---");

        VentanaPrincipal ventana = new VentanaPrincipal();
        PanelJuego panel = ventana.getPanelJuego();
        panel.setSize(1000, 600);

        // 5.1 Ráfaga con Acorazado durante 1.0 segundo (esperamos ~3 o 4 proyectiles por cadencia de 300ms)
        Piloto pAcorazado = new Piloto("RafagaAcorazado", Piloto.NAVE_ACORAZADO);
        panel.iniciarPartida(pAcorazado);
        NaveJugador naveAco = panel.getNaveJugador();

        // Simular tecla Espacio sostenida
        naveAco.setDisparandoContinuo(true);
        try {
            Thread.sleep(1050); // 1.05 segundos de disparo continuo
        } catch (InterruptedException ignored) {}
        naveAco.setDisparandoContinuo(false);

        int disparosAcorazado = naveAco.getDisparosRealizados();
        System.out.println("  -> Disparos generados por Acorazado en ~1s (cadencia 300ms): " + disparosAcorazado);
        afirmar(disparosAcorazado >= 3 && disparosAcorazado <= 4,
                "Acorazado genera ráfaga rápida (3 a 4 disparos por segundo)");

        panel.detenerPartida();

        // 5.2 Ráfaga con Explorador durante 1.0 segundo (cadencia de 2000ms -> solo 1 disparo en 1s)
        Piloto pExplorador = new Piloto("RafagaExplorador", Piloto.NAVE_EXPLORADOR);
        panel.iniciarPartida(pExplorador);
        NaveJugador naveExp = panel.getNaveJugador();

        naveExp.setDisparandoContinuo(true);
        try {
            Thread.sleep(1050); // 1.05 segundos de disparo continuo
        } catch (InterruptedException ignored) {}
        naveExp.setDisparandoContinuo(false);

        int disparosExplorador = naveExp.getDisparosRealizados();
        System.out.println("  -> Disparos generados por Explorador en ~1s (cadencia 2000ms): " + disparosExplorador);
        afirmar(disparosExplorador == 1,
                "Explorador respeta su cadencia lenta de 2.0s (exactamente 1 disparo en 1s)");

        panel.detenerPartida();
        ventana.dispose();
    }
}
