package cris.sic.proyecto2.hilos;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Hilo concurrente que modela la Garita de Salida del sistema ResiPark.
 * 
 * Reglas de concurrencia y diseño:
 * - Implementa Runnable para ser gestionado por un Thread dedicado.
 * - Espera activa y segura en ciclo while sobre la cola compartida de salida mediante wait().
 * - Desocupación y liberación sincronizada del espacio correspondiente en el parqueo.
 * - Mutación del estado del vehículo hacia FUERA.
 * - Registro en tiempo real del evento en la Pila LIFO de eventos.
 * - Notificación de ciclo de vida a través de GaritaListener.
 * 
 * @author cris_sic
 */
public class GaritaSalida implements Runnable {

    private final String idGarita;
    private final ColaFIFO colaSalida;
    private final ControladorParqueo controladorParqueo;
    private final PilaEventos pilaEventos;

    private volatile boolean enEjecucion;
    private volatile boolean pausado;
    private int tiempoAtencionMs;
    private Vehiculo vehiculoActual;
    private GaritaListener listener;
    private Thread hiloPropio;

    /**
     * Constructor para la Garita de Salida.
     *
     * @param idGarita           Identificador (ej. "Garita de Salida")
     * @param colaSalida         Recurso compartido Cola FIFO de salida
     * @param controladorParqueo Recurso compartido Controlador del Parqueo
     * @param pilaEventos        Recurso compartido Bitácora histórica Pila LIFO
     */
    public GaritaSalida(String idGarita, ColaFIFO colaSalida, ControladorParqueo controladorParqueo, PilaEventos pilaEventos) {
        this.idGarita = (idGarita != null) ? idGarita : "Garita de Salida";
        this.colaSalida = colaSalida;
        this.controladorParqueo = controladorParqueo;
        this.pilaEventos = pilaEventos;
        this.enEjecucion = false;
        this.pausado = false;
        this.tiempoAtencionMs = 1500;
        this.vehiculoActual = null;
        this.listener = null;
        this.hiloPropio = null;
    }

    public synchronized void iniciar() {
        if (!enEjecucion) {
            enEjecucion = true;
            pausado = false;
            hiloPropio = new Thread(this, idGarita);
            hiloPropio.start();
        }
    }

    public synchronized void detener() {
        enEjecucion = false;
        synchronized (colaSalida) {
            colaSalida.notifyAll();
        }
        if (hiloPropio != null) {
            hiloPropio.interrupt();
        }
    }

    public synchronized void pausar() {
        this.pausado = true;
    }

    public synchronized void reanudar() {
        this.pausado = false;
        synchronized (colaSalida) {
            colaSalida.notifyAll();
        }
    }

    @Override
    public void run() {
        notificarEstado("En servicio (Esperando solicitudes de salida)");

        while (enEjecucion) {
            try {
                Vehiculo vehiculo = null;

                // 1. Extracción sincronizada de la cola de salida
                synchronized (colaSalida) {
                    while (colaSalida.estaVacia() && enEjecucion) {
                        notificarEstado("En espera (Sin salidas pendientes)");
                        colaSalida.wait();
                    }

                    if (!enEjecucion) {
                        break;
                    }

                    if (pausado) {
                        Thread.sleep(200);
                        continue;
                    }

                    vehiculo = colaSalida.desencolar();
                }

                if (vehiculo != null) {
                    this.vehiculoActual = vehiculo;
                    notificarEstado("Procesando salida del vehículo: " + vehiculo.getPlaca());

                    // 2. Simulación de tiempo de atención
                    if (tiempoAtencionMs > 0) {
                        Thread.sleep(tiempoAtencionMs);
                    }

                    // 3. Liberación sincronizada del espacio en el parqueo
                    EspacioParqueo espacioLiberado;
                    synchronized (controladorParqueo) {
                        espacioLiberado = controladorParqueo.liberarVehiculoPorPlaca(vehiculo.getPlaca());
                    }

                    // 4. Transición de estado del vehículo hacia FUERA
                    vehiculo.salirDelResidencial();

                    // 5. Registro de evento histórico
                    String desc;
                    if (espacioLiberado != null) {
                        desc = "Vehículo " + vehiculo.getPlaca() + " liberó el espacio "
                                + espacioLiberado.getIdEspacio() + " (" + espacioLiberado.getTipoEspacio().getDescripcion() + ") y salió del residencial";
                    } else {
                        desc = "Vehículo " + vehiculo.getPlaca() + " salió del residencial";
                    }

                    Evento evento = new Evento("SALIDA", desc, idGarita);

                    synchronized (pilaEventos) {
                        pilaEventos.apilar(evento);
                    }

                    if (listener != null) {
                        listener.onVehiculoSalida(idGarita, vehiculo, espacioLiberado, evento);
                    }

                    this.vehiculoActual = null;
                }

            } catch (InterruptedException e) {
                if (!enEjecucion) {
                    break;
                }
            }
        }

        notificarEstado("Garita fuera de servicio");
    }

    private void notificarEstado(String estado) {
        if (listener != null) {
            listener.onEstadoCambiado(idGarita, estado);
        }
    }

    public String getIdGarita() {
        return idGarita;
    }

    public boolean isEnEjecucion() {
        return enEjecucion;
    }

    public boolean isPausado() {
        return pausado;
    }

    public int getTiempoAtencionMs() {
        return tiempoAtencionMs;
    }

    public void setTiempoAtencionMs(int tiempoAtencionMs) {
        this.tiempoAtencionMs = Math.max(0, tiempoAtencionMs);
    }

    public Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    public void setListener(GaritaListener listener) {
        this.listener = listener;
    }

    public Thread getHiloPropio() {
        return hiloPropio;
    }
}
