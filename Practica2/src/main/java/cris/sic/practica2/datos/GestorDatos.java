package cris.sic.practica2.datos;

import cris.sic.practica2.modelo.Partida;
import cris.sic.practica2.modelo.Piloto;

/**
 * Administrador central de datos y persistencia del juego.
 * Gestiona el almacenamiento activo en memoria mediante vectores nativos de Java
 * y sincroniza de forma transparente con el disco local cifrado a través de GestorArchivos.
 */
public class GestorDatos {

    private static GestorDatos instancia;

    private static final int CAPACIDAD_INICIAL = 2; // Capacidad inicial para soporte de expansión dinámica

    private Piloto[] pilotos;
    private int contadorPilotos;

    private Partida[] partidas;
    private int contadorPartidas;

    /**
     * Constructor principal. Inicializa los vectores en memoria e invoca automáticamente
     * la carga de datos cifrados desde disco (.txt) para restaurar el estado previo.
     */
    public GestorDatos() {
        this.pilotos = new Piloto[CAPACIDAD_INICIAL];
        this.contadorPilotos = 0;
        this.partidas = new Partida[CAPACIDAD_INICIAL];
        this.contadorPartidas = 0;

        // Restaurar estado persistido en disco al iniciar la aplicación
        cargarDatosDesdeDisco();
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
    // MÉTODOS DE SINCRONIZACIÓN CON DISCO
    // ==========================================

    /**
     * Carga y descifra los registros de pilotos y partidas almacenados en disco (.txt),
     * reconstruyendo los vectores de memoria activa.
     */
    public synchronized void cargarDatosDesdeDisco() {
        // Cargar pilotos desde pilotos.txt
        Piloto[] pilotosCargados = GestorArchivos.cargarPilotos();
        if (pilotosCargados != null && pilotosCargados.length > 0) {
            for (Piloto p : pilotosCargados) {
                if (p != null) {
                    if (contadorPilotos >= pilotos.length) {
                        redimensionarPilotos();
                    }
                    pilotos[contadorPilotos++] = p;
                }
            }
            System.out.println("[PERSISTENCIA] " + contadorPilotos + " pilotos restaurados y descifrados desde disco.");
        }

        // Cargar partidas desde partidas.txt
        Partida[] partidasCargadas = GestorArchivos.cargarPartidas();
        if (partidasCargadas != null && partidasCargadas.length > 0) {
            for (Partida p : partidasCargadas) {
                if (p != null) {
                    if (contadorPartidas >= partidas.length) {
                        redimensionarPartidas();
                    }
                    partidas[contadorPartidas++] = p;
                }
            }
            System.out.println("[PERSISTENCIA] " + contadorPartidas + " partidas restauradas y descifradas desde disco.");
        }
    }

    /**
     * Guarda el estado actual del vector de pilotos en disco con cifrado simétrico.
     */
    public synchronized void guardarPilotosEnDisco() {
        GestorArchivos.guardarPilotos(obtenerPilotos());
    }

    /**
     * Guarda el estado actual del vector de partidas en disco con cifrado simétrico.
     */
    public synchronized void guardarPartidasEnDisco() {
        GestorArchivos.guardarPartidas(obtenerPartidas());
    }

    // ==========================================
    // MÉTODOS PARA GESTIÓN DE PILOTOS
    // ==========================================

    /**
     * Inserta un nuevo piloto en el arreglo y lo persiste cifrado en disco.
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

        // Sincronizar inmediatamente con disco (.txt cifrado)
        guardarPilotosEnDisco();
        return true;
    }

    /**
     * Elimina un piloto del vector de memoria por su nombre y actualiza el archivo en disco.
     *
     * @param nombre Nombre del piloto a eliminar
     * @return true si fue encontrado y eliminado, false en caso contrario
     */
    public synchronized boolean eliminarPiloto(String nombre) {
        if (nombre == null) return false;
        int indice = -1;
        for (int i = 0; i < contadorPilotos; i++) {
            if (pilotos[i].getNombre().equalsIgnoreCase(nombre.trim())) {
                indice = i;
                break;
            }
        }

        if (indice == -1) {
            return false;
        }

        // Desplazar elementos hacia la izquierda
        for (int i = indice; i < contadorPilotos - 1; i++) {
            pilotos[i] = pilotos[i + 1];
        }
        pilotos[contadorPilotos - 1] = null;
        contadorPilotos--;

        // Sincronizar actualización con disco
        guardarPilotosEnDisco();
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
     * Inserta una nueva partida jugada en el vector de partidas y la persiste cifrada en disco.
     * Actualiza automáticamente el punteo máximo del piloto involucrado tanto en memoria como en disco.
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
            // Guardar cambios en el piloto (récord actualizado)
            guardarPilotosEnDisco();
        }

        // Persistir partidas en disco (.txt cifrado)
        guardarPartidasEnDisco();
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
