package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Evento;

/**
 * Estructura de datos Pila LIFO (Last-In, First-Out) implementada desde cero.
 * Utilizada para la bitácora histórica de eventos en tiempo real de ResiPark.
 * 
 * Reglas de negocio y diseño:
 * - Inserción en el tope (apilar) y extracción desde el tope (desapilar).
 * - La consulta y visualización se realiza desde el evento más reciente (tope) hacia el más antiguo (base).
 * - Recorrido no destructivo para la interfaz gráfica.
 * - Estructura en memoria (no se persiste en disco).
 * - Cero colecciones de java.util (sin Stack, Deque, etc.).
 * - Cero arreglos nativos T[] para almacenamiento de datos.
 * 
 * @author cris_sic
 */
public class PilaEventos {

    private NodoPila tope;
    private int tamaño;

    public PilaEventos() {
        this.tope = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un nuevo evento en el tope de la pila (operación push).
     *
     * @param evento Evento ocurrido a registrar
     */
    public synchronized void apilar(Evento evento) {
        if (evento == null) {
            return;
        }
        NodoPila nuevoNodo = new NodoPila(evento, tope);
        tope = nuevoNodo;
        tamaño++;
    }

    /**
     * Extrae y remueve el evento más reciente del tope de la pila (operación pop).
     *
     * @return El evento en el tope o null si la pila está vacía
     */
    public synchronized Evento desapilar() {
        if (estaVacia()) {
            return null;
        }
        Evento eventoExtraido = tope.getDato();
        tope = tope.getSiguiente();
        tamaño--;
        return eventoExtraido;
    }

    /**
     * Consulta el evento más reciente en el tope sin removerlo (operación peek).
     *
     * @return El evento en el tope o null si está vacía
     */
    public synchronized Evento verTope() {
        if (estaVacia()) {
            return null;
        }
        return tope.getDato();
    }

    /**
     * Evalúa si la pila no contiene eventos registrados.
     *
     * @return true si la pila está vacía
     */
    public synchronized boolean estaVacia() {
        return tope == null || tamaño == 0;
    }

    /**
     * Retorna la cantidad total de eventos apilados.
     *
     * @return Número de eventos registrados
     */
    public synchronized int getTamaño() {
        return tamaño;
    }

    /**
     * Limpia y remueve todos los eventos de la pila.
     */
    public synchronized void vaciar() {
        tope = null;
        tamaño = 0;
    }

    /**
     * Recorrido NO destructivo desde el tope (evento más reciente) hacia la base (evento más antiguo).
     *
     * @return Representación en texto de todos los eventos ordenados cronológicamente inverso
     */
    public synchronized String recorrer() {
        if (estaVacia()) {
            return "(No hay eventos registrados en la bitácora)";
        }
        StringBuilder sb = new StringBuilder();
        NodoPila actual = tope;
        int posicion = 1;
        while (actual != null) {
            sb.append("  [").append(posicion++).append("] ").append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append("\n");
            }
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    /**
     * Acceso al primer nodo (tope) para renderizado y recorrido en componentes visuales.
     *
     * @return NodoPila en la cima
     */
    public synchronized NodoPila getPrimerNodo() {
        return tope;
    }
}
