package cris.sic.proyecto2.modelo;

/**
 * Enumeración que representa los cuatro estados posibles de un vehículo en el sistema ResiPark.
 * 
 * Flujo de estados del ciclo de vida:
 * FUERA -> EN_COLA_ENTRADA -> ESTACIONADO -> EN_COLA_SALIDA -> FUERA
 */
public enum EstadoVehiculo {
    FUERA("Fuera del residencial"),
    EN_COLA_ENTRADA("En cola de entrada"),
    ESTACIONADO("Estacionado en parqueo"),
    EN_COLA_SALIDA("En cola de salida");

    private final String descripcion;

    EstadoVehiculo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Valida si la transición hacia un nuevo estado es consistente con las reglas de flujo del parqueo.
     *
     * @param nuevo Estado destino
     * @return true si la transición está permitida
     */
    public boolean puedeTransicionarA(EstadoVehiculo nuevo) {
        if (nuevo == null) {
            return false;
        }
        if (this == nuevo) {
            return true; // Permanecer en el mismo estado
        }
        switch (this) {
            case FUERA:
                // Solo puede ingresar a la cola de entrada
                return nuevo == EN_COLA_ENTRADA;
            case EN_COLA_ENTRADA:
                // Puede estacionarse o salir rechazado (volver a FUERA)
                return nuevo == ESTACIONADO || nuevo == FUERA;
            case ESTACIONADO:
                // Solo puede pasar a cola de salida
                return nuevo == EN_COLA_SALIDA;
            case EN_COLA_SALIDA:
                // Al ser atendido en garita de salida, vuelve a FUERA
                return nuevo == FUERA;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
