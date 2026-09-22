package cris.sic.practica2.modelo;

/**
 * Representa a un piloto en el juego espacial.
 * Contiene información de su nombre, tipo de nave asignada, parámetros de combate
 * y punteo máximo obtenido.
 */
public class Piloto {

    public static final String NAVE_EXPLORADOR = "Explorador";
    public static final String NAVE_CAZA_ESTELAR = "Caza Estelar";
    public static final String NAVE_ACORAZADO = "Acorazado";

    private String nombre;
    private String tipoNave; // Explorador, Caza Estelar, Acorazado
    private int punteoMaximo;

    public Piloto() {
        this.nombre = "";
        this.tipoNave = NAVE_EXPLORADOR;
        this.punteoMaximo = 0;
    }

    public Piloto(String nombre, String tipoNave) {
        this.nombre = nombre;
        this.tipoNave = normalizarTipoNave(tipoNave);
        this.punteoMaximo = 0;
    }

    public Piloto(String nombre, String tipoNave, int punteoMaximo) {
        this.nombre = nombre;
        this.tipoNave = normalizarTipoNave(tipoNave);
        this.punteoMaximo = punteoMaximo;
    }

    private String normalizarTipoNave(String tipo) {
        if (tipo == null) return NAVE_EXPLORADOR;
        if (tipo.contains("Acorazado")) return NAVE_ACORAZADO;
        if (tipo.contains("Caza")) return NAVE_CAZA_ESTELAR;
        return NAVE_EXPLORADOR;
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
        this.tipoNave = normalizarTipoNave(tipoNave);
    }

    public int getPunteoMaximo() {
        return punteoMaximo;
    }

    public void setPunteoMaximo(int punteoMaximo) {
        this.punteoMaximo = punteoMaximo;
    }

    /**
     * Retorna el tiempo de recarga en milisegundos según la nave seleccionada:
     * - Explorador: 2000 ms (2.0s)
     * - Caza Estelar: 1000 ms (1.0s)
     * - Acorazado: 300 ms (0.3s)
     */
    public long getTiempoRecargaMs() {
        switch (tipoNave) {
            case NAVE_ACORAZADO:
                return 300;
            case NAVE_CAZA_ESTELAR:
                return 1000;
            case NAVE_EXPLORADOR:
            default:
                return 2000;
        }
    }

    /**
     * Retorna la velocidad de desplazamiento de la nave en píxeles:
     * - Explorador: Rápido (10 px)
     * - Caza Estelar: Medio (7 px)
     * - Acorazado: Lento (4 px)
     */
    public int getVelocidadMovimiento() {
        switch (tipoNave) {
            case NAVE_ACORAZADO:
                return 4;
            case NAVE_CAZA_ESTELAR:
                return 7;
            case NAVE_EXPLORADOR:
            default:
                return 10;
        }
    }

    /**
     * Retorna el nivel de dificultad asociado a la nave.
     */
    public String getNivelDificultad() {
        switch (tipoNave) {
            case NAVE_ACORAZADO:
                return "Difícil";
            case NAVE_CAZA_ESTELAR:
                return "Normal";
            case NAVE_EXPLORADOR:
            default:
                return "Fácil";
        }
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
                ", dificultad='" + getNivelDificultad() + '\'' +
                ", recarga=" + getTiempoRecargaMs() + "ms" +
                ", velocidad=" + getVelocidadMovimiento() + "px" +
                ", punteoMaximo=" + punteoMaximo +
                '}';
    }
}
