package cris.sic.proyecto2.modelo;

/**
 * Enumeración que representa el estado de ocupación de un espacio de parqueo.
 * 
 * @author cris_sic
 */
public enum EstadoEspacio {
    LIBRE("Libre"),
    OCUPADO("Ocupado");

    private final String descripcion;

    EstadoEspacio(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
