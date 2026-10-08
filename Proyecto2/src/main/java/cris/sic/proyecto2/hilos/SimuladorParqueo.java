package cris.sic.proyecto2.hilos;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Gestor y orquestador central de concurrencia para la simulación de ResiPark.
 * 
 * Componentes coordinados:
 * - 2 Garitas de Entrada concurrentes (GaritaEntrada1 y GaritaEntrada2).
 * - 1 Garita de Salida concurrente (GaritaSalida).
 * - Cola compartida de entrada y cola compartida de salida con despertar notifyAll().
 * - Parqueo (150 espacios) y Bitácora de eventos protegidos contra condiciones de carrera.
 * 
 * @author cris_sic
 */
public class SimuladorParqueo {

    private final ColaFIFO colaEntrada;
    private final ColaFIFO colaSalida;
    private final ControladorParqueo controladorParqueo;
    private final PilaEventos pilaEventos;

    private final GaritaEntrada garitaEntrada1;
    private final GaritaEntrada garitaEntrada2;
    private final GaritaSalida garitaSalida;

    /**
     * Constructor que inicializa todos los recursos compartidos y las 3 garitas de atención.
     *
     * @param controladorParqueo Controlador central del parqueo
     * @param pilaEventos        Bitácora histórica de eventos
     */
    public SimuladorParqueo(ControladorParqueo controladorParqueo, PilaEventos pilaEventos) {
        this.controladorParqueo = (controladorParqueo != null) ? controladorParqueo : new ControladorParqueo();
        this.pilaEventos = (pilaEventos != null) ? pilaEventos : new PilaEventos();
        this.colaEntrada = new ColaFIFO();
        this.colaSalida = new ColaFIFO();

        this.garitaEntrada1 = new GaritaEntrada("Garita de Entrada 1", colaEntrada, this.controladorParqueo, this.pilaEventos);
        this.garitaEntrada2 = new GaritaEntrada("Garita de Entrada 2", colaEntrada, this.controladorParqueo, this.pilaEventos);
        this.garitaSalida = new GaritaSalida("Garita de Salida", colaSalida, this.controladorParqueo, this.pilaEventos);
    }

    /**
     * Inicia los 3 hilos de atención en paralelo.
     */
    public synchronized void iniciarSimulacion() {
        garitaEntrada1.iniciar();
        garitaEntrada2.iniciar();
        garitaSalida.iniciar();
    }

    /**
     * Detiene con seguridad los 3 hilos de la simulación.
     */
    public synchronized void detenerSimulacion() {
        garitaEntrada1.detener();
        garitaEntrada2.detener();
        garitaSalida.detener();
    }

    /**
     * Pausa la atención en las 3 garitas.
     */
    public synchronized void pausarSimulacion() {
        garitaEntrada1.pausar();
        garitaEntrada2.pausar();
        garitaSalida.pausar();
    }

    /**
     * Reanuda la atención en las 3 garitas.
     */
    public synchronized void reanudarSimulacion() {
        garitaEntrada1.reanudar();
        garitaEntrada2.reanudar();
        garitaSalida.reanudar();
    }

    /**
     * Encola un vehículo en la entrada de forma sincronizada y despierta a los hilos de las garitas con notifyAll().
     *
     * @param vehiculo Vehículo a ingresar
     * @return true si se encoló con éxito; false si ya estaba en cola o estado no permitido
     */
    public boolean encolarVehiculoEntrada(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return false;
        }

        synchronized (colaEntrada) {
            if (colaEntrada.contienePlaca(vehiculo.getPlaca())) {
                return false; // Ya está en la cola de entrada
            }

            if (vehiculo.getEstado() == EstadoVehiculo.FUERA) {
                vehiculo.entrarAColaEntrada();
            }

            boolean encolado = colaEntrada.encolar(vehiculo);
            if (encolado) {
                colaEntrada.notifyAll(); // Despierta Garita 1 o Garita 2
            }
            return encolado;
        }
    }

    /**
     * Encola un vehículo en la salida de forma sincronizada y despierta a la Garita de Salida con notifyAll().
     *
     * @param vehiculo Vehículo estacionado que solicita salir
     * @return true si se encoló para salir; false si no estaba estacionado o ya estaba en cola
     */
    public boolean encolarVehiculoSalida(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return false;
        }

        synchronized (colaSalida) {
            if (colaSalida.contienePlaca(vehiculo.getPlaca())) {
                return false;
            }

            if (vehiculo.getEstado() == EstadoVehiculo.ESTACIONADO) {
                vehiculo.entrarAColaSalida();
            }

            boolean encolado = colaSalida.encolar(vehiculo);
            if (encolado) {
                colaSalida.notifyAll(); // Despierta Garita de Salida
            }
            return encolado;
        }
    }

    /**
     * Ajusta la velocidad de simulación en milisegundos para todas las garitas.
     *
     * @param tiempoMs Milisegundos de retardo por atención
     */
    public void setVelocidadAtencion(int tiempoMs) {
        garitaEntrada1.setTiempoAtencionMs(tiempoMs);
        garitaEntrada2.setTiempoAtencionMs(tiempoMs);
        garitaSalida.setTiempoAtencionMs(tiempoMs);
    }

    public void setListenerGlobal(GaritaListener listener) {
        garitaEntrada1.setListener(listener);
        garitaEntrada2.setListener(listener);
        garitaSalida.setListener(listener);
    }

    public ColaFIFO getColaEntrada() {
        return colaEntrada;
    }

    public ColaFIFO getColaSalida() {
        return colaSalida;
    }

    public ControladorParqueo getControladorParqueo() {
        return controladorParqueo;
    }

    public PilaEventos getPilaEventos() {
        return pilaEventos;
    }

    public GaritaEntrada getGaritaEntrada1() {
        return garitaEntrada1;
    }

    public GaritaEntrada getGaritaEntrada2() {
        return garitaEntrada2;
    }

    public GaritaSalida getGaritaSalida() {
        return garitaSalida;
    }
}
