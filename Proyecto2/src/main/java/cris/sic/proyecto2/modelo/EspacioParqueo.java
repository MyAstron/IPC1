package cris.sic.proyecto2.modelo;

/**
 * Entidad modelo que representa un espacio individual dentro del parqueo de ResiPark.
 * 
 * Reglas de negocio y especificación:
 * - Área Socios: Filas A, B, C con 25 espacios cada una (A1..A25, B1..B25, C1..C25 = 75 espacios).
 * - Área General: Filas E, F, G, H, I con 15 espacios cada una (E1..E15..I1..I15 = 75 espacios). No existe fila D.
 * - Cada espacio puede estar en estado LIBRE u OCUPADO por un Vehiculo.
 * 
 * @author cris_sic
 */
public class EspacioParqueo {

    private final String idEspacio;
    private final char fila;
    private final int numero;
    private final TipoEspacio tipoEspacio;
    private EstadoEspacio estado;
    private Vehiculo vehiculoEstacionado;

    /**
     * Constructor para instanciar un espacio de parqueo.
     *
     * @param idEspacio   Identificador único del espacio (ej. "A1", "E15")
     * @param fila        Carácter de la fila ('A'-'C' socios, 'E'-'I' general)
     * @param numero      Número consecutivo de espacio en la fila
     * @param tipoEspacio Tipo de área (SOCIO o GENERAL)
     */
    public EspacioParqueo(String idEspacio, char fila, int numero, TipoEspacio tipoEspacio) {
        this.idEspacio = (idEspacio != null) ? idEspacio.trim() : ("" + fila + numero);
        this.fila = Character.toUpperCase(fila);
        this.numero = numero;
        this.tipoEspacio = (tipoEspacio != null) ? tipoEspacio : TipoEspacio.GENERAL;
        this.estado = EstadoEspacio.LIBRE;
        this.vehiculoEstacionado = null;
    }

    /**
     * Asigna un vehículo al espacio de parqueo, cambiando su estado a OCUPADO.
     *
     * @param vehiculo Vehículo a estacionar
     * @return true si se ocupó con éxito; false si ya estaba ocupado o el vehículo es nulo
     */
    public boolean ocupar(Vehiculo vehiculo) {
        if (vehiculo == null || this.estado == EstadoEspacio.OCUPADO) {
            return false;
        }
        this.vehiculoEstacionado = vehiculo;
        this.estado = EstadoEspacio.OCUPADO;
        return true;
    }

    /**
     * Libera el espacio de parqueo, desvinculando el vehículo y dejándolo en estado LIBRE.
     *
     * @return El vehículo que estaba estacionado, o null si ya estaba libre
     */
    public Vehiculo liberar() {
        if (this.estado == EstadoEspacio.LIBRE) {
            return null;
        }
        Vehiculo saliente = this.vehiculoEstacionado;
        this.vehiculoEstacionado = null;
        this.estado = EstadoEspacio.LIBRE;
        return saliente;
    }

    public boolean estaLibre() {
        return this.estado == EstadoEspacio.LIBRE;
    }

    public boolean estaOcupado() {
        return this.estado == EstadoEspacio.OCUPADO;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public char getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public TipoEspacio getTipoEspacio() {
        return tipoEspacio;
    }

    public EstadoEspacio getEstado() {
        return estado;
    }

    public Vehiculo getVehiculoEstacionado() {
        return vehiculoEstacionado;
    }

    @Override
    public String toString() {
        if (estaOcupado() && vehiculoEstacionado != null) {
            return "[" + idEspacio + "] (" + tipoEspacio.getDescripcion() + ") - OCUPADO por: " + vehiculoEstacionado.getPlaca();
        } else {
            return "[" + idEspacio + "] (" + tipoEspacio.getDescripcion() + ") - LIBRE";
        }
    }
}
