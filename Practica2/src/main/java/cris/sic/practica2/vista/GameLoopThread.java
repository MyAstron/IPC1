package cris.sic.practica2.vista;

/**
 * Hilo principal de renderizado y actualización del bucle de juego (Game Loop).
 * Mantiene una tasa de refresco constante de ~60 fotogramas por segundo (FPS)
 * mediante pausas controladas con Thread.sleep(16), invocando cíclicamente la actualización
 * física y el repintado del panel.
 * 
 * Cumple con la Subfase 4.4 del planificador.
 */
public class GameLoopThread extends Thread {

    private final PanelJuego panelJuego;
    private volatile boolean enEjecucion;
    private volatile boolean enPausa;

    // Frecuencia de actualización: ~60 FPS (aprox. 16.6 milisegundos por fotograma)
    public static final int FPS_OBJETIVO = 60;
    public static final long TIEMPO_FRAME_MS = 1000 / FPS_OBJETIVO; // 16 ms

    /**
     * Constructor del hilo de bucle de juego.
     *
     * @param panelJuego Panel de juego sobre el cual se orquestará el ciclo de actualización y repintado
     */
    public GameLoopThread(PanelJuego panelJuego) {
        super("GameLoopThread-60FPS");
        this.panelJuego = panelJuego;
        this.enEjecucion = false;
        this.enPausa = false;
    }

    @Override
    public void run() {
        enEjecucion = true;
        System.out.println("[GAME LOOP] Hilo de renderizado iniciado a ~60 FPS (sleep de " + TIEMPO_FRAME_MS + " ms).");

        while (enEjecucion) {
            long tiempoInicio = System.currentTimeMillis();

            if (!enPausa) {
                // 1. Actualizar el estado físico de la nave y elementos espaciales
                panelJuego.actualizarFisica();

                // 2. Solicitar el repintado de la superficie gráfica en el hilo de Swing
                panelJuego.repaint();
            }

            // Cálculo del tiempo restante para mantener ~60 FPS estables
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
            long tiempoDormir = TIEMPO_FRAME_MS - tiempoTranscurrido;

            try {
                if (tiempoDormir > 0) {
                    Thread.sleep(tiempoDormir);
                } else {
                    // Si el fotograma tomó más tiempo de lo debido, ceder brevemente la CPU
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                // Hilo interrumpido para detención controlada
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[GAME LOOP] Hilo de renderizado finalizado con éxito.");
    }

    /**
     * Inicia el hilo de renderizado si no se encuentra activo.
     */
    public synchronized void iniciarBucle() {
        if (!enEjecucion) {
            this.enEjecucion = true;
            this.start();
        }
    }

    /**
     * Detiene la ejecución del bucle de forma limpia y segura.
     */
    public synchronized void detenerBucle() {
        this.enEjecucion = false;
        this.interrupt();
    }

    /**
     * Pausa temporalmente las actualizaciones de física y renderizado del juego.
     */
    public void pausar() {
        this.enPausa = true;
    }

    /**
     * Reanuda la ejecución del juego si estaba en pausa.
     */
    public void reanudar() {
        this.enPausa = false;
    }

    /**
     * Alterna el estado de pausa del juego.
     *
     * @return nuevo estado de pausa (true si está pausado, false si está activo)
     */
    public boolean alternarPausa() {
        this.enPausa = !this.enPausa;
        return this.enPausa;
    }

    public boolean isEnEjecucion() {
        return enEjecucion;
    }

    public boolean isEnPausa() {
        return enPausa;
    }
}
