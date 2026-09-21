package cris.sic.practica2.modelo;

/**
 * Representa a un piloto en el juego espacial.
 * Contiene información de su nombre, tipo de nave asignada y punteo máximo obtenido.
 */
public class Piloto {

    private String nombre;
    private String tipoNave; // Explorador, Caza Estelar, Acorazado
    private int punteoMaximo;

    public Piloto() {
        this.nombre = "";
        this.tipoNave = "Explorador";
        this.punteoMaximo = 0;
    }

    public Piloto(String nombre, String tipoNave) {
        this.nombre = nombre;
        this.tipoNave = tipoNave;
        this.punteoMaximo = 0;
    }

    public Piloto(String nombre, String tipoNave, int punteoMaximo) {
        this.nombre = nombre;
        this.tipoNave = tipoNave;
        this.punteoMaximo = punteoMaximo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoNave() {
        return tipoNave;
    }

    public void setTipoNave(String tipoNave) {
        this.tipoNave = tipoNave;
    }

    public int getPunteoMaximo() {
        return punteoMaximo;
    }

    public void setPunteoMaximo(int punteoMaximo) {
        this.punteoMaximo = punteoMaximo;
    }

    /**
     * Actualiza el punteo máximo si el puntaje recibido es mayor al actual.
     *
     * @param nuevoPuntaje puntaje a comparar
     */
    public void actualizarPunteoMaximo(int nuevoPuntaje) {
        if (nuevoPuntaje > this.punteoMaximo) {
            this.punteoMaximo = nuevoPuntaje;
        }
    }

    @Override
    public String toString() {
        return "Piloto{" +
                "nombre='" + nombre + '\'' +
                ", tipoNave='" + tipoNave + '\'' +
                ", punteoMaximo=" + punteoMaximo +
                '}';
    }
}
