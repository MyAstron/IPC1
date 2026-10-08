package cris.sic.proyecto2.modelo;

/**
 * Enumeración que define los tipos de área de parqueo en el sistema ResiPark.
 * - SOCIO: Espacios exclusivos para residentes con membresía de socio (Filas A, B, C: 75 espacios).
 * - GENERAL: Espacios para residentes no socios y visitantes (Filas E, F, G, H, I: 75 espacios).
 * 
 * @author cris_sic
 */
public enum TipoEspacio {
    SOCIO("Área de Socios"),
    GENERAL("Área General / Visitantes");

    private final String descripcion;

    TipoEspacio(String descripcion) {
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
