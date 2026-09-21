package cris.sic.practica2.datos;

import cris.sic.practica2.modelo.Partida;
import cris.sic.practica2.modelo.Piloto;

/**
 * Administrador central de persistencia en memoria del juego.
 * Gestiona el almacenamiento de pilotos y partidas utilizando exclusivamente
 * arreglos/vectores nativos de Java con crecimiento dinámico para evitar
 * excepciones ArrayIndexOutOfBoundsException.
 */
public class GestorDatos {

    private static GestorDatos instancia;

    private static final int CAPACIDAD_INICIAL = 2; // Capacidad inicial pequeña para probar expansión dinámica

    private Piloto[] pilotos;
    private int contadorPilotos;

    private Partida[] partidas;
    private int contadorPartidas;

    public GestorDatos() {
        this.pilotos = new Piloto[CAPACIDAD_INICIAL];
        this.contadorPilotos = 0;
        this.partidas = new Partida[CAPACIDAD_INICIAL];
        this.contadorPartidas = 0;
    }

    /**
     * Obtiene la instancia única de GestorDatos (Patrón Singleton).
     *
     * @return instancia global de GestorDatos
     */
    public static synchronized GestorDatos getInstancia() {
        if (instancia == null) {
            instancia = new GestorDatos();
        }
        return instancia;
    }

    // ==========================================
    // MÉTODOS PARA GESTIÓN DE PILOTOS
    // ==========================================

    /**
     * Inserta un nuevo piloto en el arreglo.
     * Si el arreglo está lleno, se redimensiona automáticamente al doble de tamaño.
     *
     * @param piloto Objeto piloto a insertar
     * @return true si se insertó con éxito, false si ya existe o es inválido
     */
    public synchronized boolean insertarPiloto(Piloto piloto) {
        if (piloto == null || piloto.getNombre() == null || piloto.getNombre().trim().isEmpty()) {
            return false;
        }

        // Validar que no exista un piloto con el mismo nombre (insensible a mayúsculas)
        if (existePiloto(piloto.getNombre().trim())) {
            return false;
        }

        // Redimensionar si se alcanzó el límite actual del vector
        if (contadorPilotos >= pilotos.length) {
            redimensionarPilotos();
        }

        pilotos[contadorPilotos] = piloto;
        contadorPilotos++;
        return true;
    }

    /**
     * Busca un piloto por su nombre.
     *
     * @param nombre Nombre del piloto a buscar
     * @return Objeto Piloto si fue encontrado, null en caso contrario
     */
    public Piloto buscarPiloto(String nombre) {
        if (nombre == null) {
            return null;
        }
        for (int i = 0; i < contadorPilotos; i++) {
            if (pilotos[i].getNombre().equalsIgnoreCase(nombre.trim())) {
                return pilotos[i];
            }
        }
        return null;
    }

    /**
     * Verifica si un piloto ya está registrado por su nombre.
     *
     * @param nombre Nombre a verificar
     * @return true si existe, false de lo contrario
     */
    public boolean existePiloto(String nombre) {
        return buscarPiloto(nombre) != null;
    }

    /**
     * Retorna una copia exacta de los pilotos registrados hasta el momento.
     *
     * @return Arreglo Piloto[] con los elementos actuales
     */
    public Piloto[] obtenerPilotos() {
        Piloto[] copia = new Piloto[contadorPilotos];
        for (int i = 0; i < contadorPilotos; i++) {
            copia[i] = pilotos[i];
        }
        return copia;
    }

    public int getContadorPilotos() {
        return contadorPilotos;
    }

    /**
     * Imprime en consola la lista completa de pilotos registrados.
     */
    public void listarPilotos() {
        System.out.println("========== LISTADO DE PILOTOS REGISTRADOS (" + contadorPilotos + ") ==========");
        if (contadorPilotos == 0) {
            System.out.println("No hay pilotos registrados.");
            return;
        }
        for (int i = 0; i < contadorPilotos; i++) {
            Piloto p = pilotos[i];
            System.out.printf("[%d] Nombre: %-15s | Nave: %-15s | Punteo Máximo: %d%n",
                    (i + 1), p.getNombre(), p.getTipoNave(), p.getPunteoMaximo());
        }
    }

    /**
     * Duplica el tamaño del vector de pilotos para admitir nuevos registros.
     */
    private void redimensionarPilotos() {
        int nuevaCapacidad = pilotos.length * 2;
        Piloto[] nuevoArreglo = new Piloto[nuevaCapacidad];
        for (int i = 0; i < contadorPilotos; i++) {
            nuevoArreglo[i] = pilotos[i];
        }
        pilotos = nuevoArreglo;
    }

    // ==========================================
    // MÉTODOS PARA GESTIÓN DE PARTIDAS
    // ==========================================

    /**
     * Inserta una nueva partida jugada en el vector de partidas.
     * Actualiza automáticamente el punteo máximo del piloto involucrado.
     *
     * @param partida Partida jugada a registrar
     * @return true si se insertó con éxito, false si el parámetro es nulo
     */
    public synchronized boolean insertarPartida(Partida partida) {
        if (partida == null) {
            return false;
        }

        // Redimensionar si se alcanzó el límite actual del vector
        if (contadorPartidas >= partidas.length) {
            redimensionarPartidas();
        }

        partidas[contadorPartidas] = partida;
        contadorPartidas++;

        // Actualizar el punteo máximo del piloto si aplica
        Piloto piloto = buscarPiloto(partida.getNombrePiloto());
        if (piloto != null) {
            piloto.actualizarPunteoMaximo(partida.getPuntajeObtenido());
        }

        return true;
    }

    /**
     * Retorna una copia exacta de las partidas registradas hasta el momento.
     *
     * @return Arreglo Partida[] con los elementos actuales
     */
    public Partida[] obtenerPartidas() {
        Partida[] copia = new Partida[contadorPartidas];
        for (int i = 0; i < contadorPartidas; i++) {
            copia[i] = partidas[i];
        }
        return copia;
    }

    public int getContadorPartidas() {
        return contadorPartidas;
    }

    /**
     * Imprime en consola el listado de partidas guardadas.
     */
    public void listarPartidas() {
        System.out.println("========== HISTORIAL DE PARTIDAS (" + contadorPartidas + ") ==========");
        if (contadorPartidas == 0) {
            System.out.println("No hay partidas registradas en el historial.");
            return;
        }
        for (int i = 0; i < contadorPartidas; i++) {
            Partida p = partidas[i];
            System.out.printf("[%d] Piloto: %-15s | Puntaje: %-6d | Enemigos: %-4d | Fecha: %s%n",
                    (i + 1), p.getNombrePiloto(), p.getPuntajeObtenido(), p.getEnemigosDestruidos(), p.getFecha());
        }
    }

    /**
     * Duplica el tamaño del vector de partidas para admitir nuevos registros.
     */
    private void redimensionarPartidas() {
        int nuevaCapacidad = partidas.length * 2;
        Partida[] nuevoArreglo = new Partida[nuevaCapacidad];
        for (int i = 0; i < contadorPartidas; i++) {
            nuevoArreglo[i] = partidas[i];
        }
        partidas = nuevoArreglo;
    }

    // ==========================================
    // MÉTODOS DE CONSULTA Y ORDENAMIENTO (TOP)
    // ==========================================

    /**
     * Retorna el top de pilotos ordenados descendentemente por su puntaje máximo.
     * Implementa ordenamiento de burbuja manual sin librerías externas.
     *
     * @param limite Cantidad máxima de elementos a retornar (ej. 5 o 10)
     * @return Arreglo con los mejores pilotos ordenados
     */
    public Piloto[] obtenerTopPilotos(int limite) {
        Piloto[] copia = obtenerPilotos();
        int n = copia.length;

        // Ordenamiento burbuja descendente
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (copia[j].getPunteoMaximo() < copia[j + 1].getPunteoMaximo()) {
                    Piloto temp = copia[j];
                    copia[j] = copia[j + 1];
                    copia[j + 1] = temp;
                }
            }
        }

        int totalRetorno = Math.min(limite, n);
        Piloto[] top = new Piloto[totalRetorno];
        for (int i = 0; i < totalRetorno; i++) {
            top[i] = copia[i];
        }
        return top;
    }

    /**
     * Reinicia las estructuras de datos en memoria (útil para pruebas unitarias).
     */
    public synchronized void reiniciarDatos() {
        this.pilotos = new Piloto[CAPACIDAD_INICIAL];
        this.contadorPilotos = 0;
        this.partidas = new Partida[CAPACIDAD_INICIAL];
        this.contadorPartidas = 0;
    }
}
