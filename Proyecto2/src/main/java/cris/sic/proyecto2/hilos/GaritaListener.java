package cris.sic.proyecto2.hilos;

import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Interfaz observadora para recibir notificaciones de eventos generados por los hilos de las garitas.
 * Facilita el desacoplamiento entre los hilos de simulación y las capas de presentación (consola / Swing UI).
 * 
 * @author cris_sic
 */
public interface GaritaListener {

    /**
     * Invocado cuando un vehículo es atendido con éxito y asignado a un espacio de parqueo.
     *
     * @param idGarita        Identificador de la garita que atendió el ingreso
     * @param vehiculo        Vehículo ingresado
     * @param espacioAsignado Espacio de parqueo asignado
     * @param evento          Evento registrado en bitácora
     */
    void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento);

    /**
     * Invocado cuando un vehículo es rechazado por falta de espacios disponibles.
     *
     * @param idGarita Identificador de la garita
     * @param vehiculo Vehículo rechazado
     * @param evento   Evento de rechazo
     */
    void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento);

    /**
     * Invocado cuando un vehículo completa su trámite en la garita de salida y libera su espacio.
     *
     * @param idGarita        Identificador de la garita de salida
     * @param vehiculo        Vehículo saliente
     * @param espacioLiberado Espacio de parqueo desocupado
     * @param evento          Evento de salida
     */
    void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento);

    /**
     * Invocado cuando cambia el estado operativo de una garita (ej: EN ESPERA, ATENDIENDO).
     *
     * @param idGarita Identificador de la garita
     * @param estado   Mensaje descriptivo del estado
     */
    void onEstadoCambiado(String idGarita, String estado);
}
