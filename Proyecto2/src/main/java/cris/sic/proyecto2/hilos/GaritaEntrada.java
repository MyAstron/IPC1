package cris.sic.proyecto2.hilos;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Hilo concurrente que modela una Garita de Entrada en ResiPark (Garita 1 o Garita 2).
 * 
 * Reglas de concurrencia y diseño:
 * - Implementa Runnable para ser ejecutado por un Thread dedicado.
 * - Espera activa y segura en ciclo while sobre la cola compartida de entrada mediante wait().
 * - Protección contra condiciones de carrera al acceder al parqueo y la bitácora mediante synchronized.
 * - Simulación de tiempo de atención mediante Thread.sleep(tiempoAtencionMs).
 * - Notificación de progreso y cambios de estado mediante la interfaz GaritaListener.
 * 
 * @author cris_sic
 */
public class GaritaEntrada implements Runnable {

    private final String idGarita;
    private final ColaFIFO colaEntrada;
    private final ControladorParqueo controladorParqueo;
    private final PilaEventos pilaEventos;

    private volatile boolean enEjecucion;
    private volatile boolean pausado;
    private int tiempoAtencionMs;
    private Vehiculo vehiculoActual;
    private GaritaListener listener;
    private Thread hiloPropio;

    /**
     * Constructor principal de la Garita de Entrada.
     *
     * @param idGarita           Identificador único (ej: "Garita de Entrada 1")
     * @param colaEntrada        Recurso compartido Cola FIFO de entrada
     * @param controladorParqueo Recurso compartido Controlador del Parqueo
     * @param pilaEventos        Recurso compartido Bitácora histórica Pila LIFO
     */
    public GaritaEntrada(String idGarita, ColaFIFO colaEntrada, ControladorParqueo controladorParqueo, PilaEventos pilaEventos) {
        this.idGarita = (idGarita != null) ? idGarita : "Garita de Entrada";
        this.colaEntrada = colaEntrada;
        this.controladorParqueo = controladorParqueo;
        this.pilaEventos = pilaEventos;
        this.enEjecucion = false;
        this.pausado = false;
        this.tiempoAtencionMs = 1500; // 1.5 segundos por defecto
        this.vehiculoActual = null;
        this.listener = null;
        this.hiloPropio = null;
    }

    /**
     * Inicia la ejecución del hilo de la garita.
     */
    public synchronized void iniciar() {
        if (!enEjecucion) {
            enEjecucion = true;
            pausado = false;
            hiloPropio = new Thread(this, idGarita);
            hiloPropio.start();
        }
    }

    /**
     * Detiene la ejecución del hilo de forma segura.
     */
    public synchronized void detener() {
        enEjecucion = false;
        synchronized (colaEntrada) {
            colaEntrada.notifyAll();
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
        synchronized (colaEntrada) {
            colaEntrada.notifyAll();
        }
    }

    @Override
    public void run() {
        notificarEstado("En servicio (Esperando vehículos)");

        while (enEjecucion) {
            try {
                Vehiculo vehiculo = null;

                // 1. Extracción sincronizada de la cola compartida
                synchronized (colaEntrada) {
                    while (colaEntrada.estaVacia() && enEjecucion) {
                        notificarEstado("En espera (Cola vacía)");
                        colaEntrada.wait();
                    }

                    if (!enEjecucion) {
                        break;
                    }

                    if (pausado) {
                        Thread.sleep(200);
                        continue;
                    }

                    vehiculo = colaEntrada.desencolar();
                }

                if (vehiculo != null) {
                    this.vehiculoActual = vehiculo;
                    notificarEstado("Atendiendo vehículo: " + vehiculo.getPlaca());

                    // 2. Simulación de tiempo de atención
                    if (tiempoAtencionMs > 0) {
                        Thread.sleep(tiempoAtencionMs);
                    }

                    // 3. Asignación sincronizada en el parqueo
                    EspacioParqueo espacioAsignado;
                    synchronized (controladorParqueo) {
                        espacioAsignado = controladorParqueo.asignarVehiculo(vehiculo);
                    }

                    // 4. Registro de evento y actualización de estado
                    if (espacioAsignado != null) {
                        // Ingreso exitoso
                        String desc = "Vehículo " + vehiculo.getPlaca() + " asignado al espacio "
                                + espacioAsignado.getIdEspacio() + " (" + espacioAsignado.getTipoEspacio().getDescripcion() + ")";
                        Evento evento = new Evento("ASIGNACION", desc, idGarita);

                        synchronized (pilaEventos) {
                            pilaEventos.apilar(evento);
                        }

                        if (listener != null) {
                            listener.onVehiculoIngresado(idGarita, vehiculo, espacioAsignado, evento);
                        }
                    } else {
                        // Rechazo por falta de espacio
                        vehiculo.salirDelResidencial();
                        String desc = "Vehículo " + vehiculo.getPlaca() + " RECHAZADO (Sin espacios disponibles)";
                        Evento evento = new Evento("RECHAZO", desc, idGarita);

                        synchronized (pilaEventos) {
                            pilaEventos.apilar(evento);
                        }

                        if (listener != null) {
                            listener.onVehiculoRechazado(idGarita, vehiculo, evento);
                        }
                    }

                    this.vehiculoActual = null;
                }

            } catch (InterruptedException e) {
                // Interrupción segura al detener el hilo
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
