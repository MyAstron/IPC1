package cris.sic.practica2.vista;

import cris.sic.practica2.modelo.Asteroide;
import cris.sic.practica2.modelo.Enemigo;
import cris.sic.practica2.modelo.PremioQuaffle;
import cris.sic.practica2.modelo.PremioSnitch;

import java.util.Random;

/**
 * Hilo generador continuo de elementos espaciales (Side-Scroller Spawner).
 * Se encarga de hacer aparecer en el borde derecho (X máx) a intervalos programados:
 * - Naves enemigas (Enemigo)
 * - Asteroides / Bludgers (Obstáculos)
 * - Contenedores Quaffle (Premios +10 pts)
 * - Snitch Espacial (Premio raro de alta velocidad)
 * 
 * Cada elemento instanciado arranca en su propio hilo y se desplaza hacia la izquierda.
 * 
 * Cumple con la Subfase 6.4 y los requisitos de Side-Scroller.
 */
public class SpawnerThread extends Thread {

    private final PanelJuego panelJuego;
    private final Random random;

    private volatile boolean enEjecucion;
    private volatile boolean enPausa;

    // Tiempos de control de generación en milisegundos
    private long ultimoSpawnEnemigo;
    private long ultimoSpawnAsteroide;
    private long ultimoSpawnQuaffle;
    private long ultimoSpawnSnitch;

    public SpawnerThread(PanelJuego panelJuego) {
        super("SpawnerThread-SideScroller");
        this.panelJuego = panelJuego;
        this.random = new Random();
        this.enEjecucion = false;
        this.enPausa = false;
        setDaemon(true);
    }

    @Override
    public void run() {
        enEjecucion = true;
        long tiempoBase = System.currentTimeMillis();
        ultimoSpawnEnemigo = tiempoBase;
        ultimoSpawnAsteroide = tiempoBase + 1000;
        ultimoSpawnQuaffle = tiempoBase + 2500;
        ultimoSpawnSnitch = tiempoBase + 8000;

        System.out.println("[SPAWNER] Generador espacial activo en el borde derecho (X máx).");

        while (enEjecucion) {
            if (!enPausa) {
                long ahora = System.currentTimeMillis();
                int maxX = panelJuego.getWidth() > 0 ? panelJuego.getWidth() : 1000;
                int maxY = panelJuego.getHeight() > 0 ? panelJuego.getHeight() : 600;
                int minY = PanelJuego.ALTO_HUD + 10;
                int rangoY = Math.max(50, (maxY - minY - 45));

                // 1. GENERAR ENEMIGO (Cada 2.0s - 3.0s)
                if (ahora - ultimoSpawnEnemigo >= 2200 + random.nextInt(800)) {
                    int spawnY = minY + random.nextInt(rangoY);
                    Enemigo enemigo = new Enemigo(maxX + 10, spawnY);
                    panelJuego.agregarElementoEspacial(enemigo);
                    enemigo.start();
                    ultimoSpawnEnemigo = ahora;
                }

                // 2. GENERAR ASTEROIDE / BLUDGER (Cada 4.0s - 5.5s)
                if (ahora - ultimoSpawnAsteroide >= 4200 + random.nextInt(1500)) {
                    int spawnY = minY + random.nextInt(rangoY);
                    Asteroide asteroide = new Asteroide(maxX + 15, spawnY);
                    panelJuego.agregarElementoEspacial(asteroide);
                    asteroide.start();
                    ultimoSpawnAsteroide = ahora;
                }

                // 3. GENERAR CONTENEDOR QUAFFLE (+10 pts) (Cada 5.0s - 7.0s)
                if (ahora - ultimoSpawnQuaffle >= 5500 + random.nextInt(2000)) {
                    int spawnY = minY + random.nextInt(rangoY);
                    PremioQuaffle quaffle = new PremioQuaffle(maxX + 15, spawnY);
                    panelJuego.agregarElementoEspacial(quaffle);
                    quaffle.start();
                    ultimoSpawnQuaffle = ahora;
                }

                // 4. GENERAR SNITCH ESPACIAL (Premio raro cada 18s - 25s)
                if (ahora - ultimoSpawnSnitch >= 18000 + random.nextInt(8000)) {
                    int spawnY = minY + 30 + random.nextInt(Math.max(30, rangoY - 60));
                    PremioSnitch snitch = new PremioSnitch(maxX + 20, spawnY);
                    panelJuego.agregarElementoEspacial(snitch);
                    snitch.start();
                    ultimoSpawnSnitch = ahora;
                }
            }

            try {
                Thread.sleep(200); // Chequeo periódico
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println("[SPAWNER] Generador espacial finalizado.");
    }

    public synchronized void iniciarSpawner() {
        if (!enEjecucion) {
            this.enEjecucion = true;
            this.start();
        }
    }

    public synchronized void detenerSpawner() {
        this.enEjecucion = false;
        this.interrupt();
    }

    public void pausar() {
        this.enPausa = true;
    }

    public void reanudar() {
        this.enPausa = false;
    }

    public boolean isEnEjecucion() {
        return enEjecucion;
    }

    public boolean isEnPausa() {
        return enPausa;
    }
}
