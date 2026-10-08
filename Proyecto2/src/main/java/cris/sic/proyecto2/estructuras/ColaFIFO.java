package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Estructura de datos Cola FIFO (First-In, First-Out) implementada desde cero.
 * Utilizada para modelar las colas de espera en la Garita de Entrada y Garita de Salida.
 * 
 * Reglas de negocio y diseño:
 * - Inserción al final (encolar) y extracción al frente (desencolar).
 * - Cero colecciones de java.util (sin Queue, ArrayDeque, etc.).
 * - Cero arreglos nativos T[] para almacenamiento de datos.
 * 
 * @author cris_sic
 */
public class ColaFIFO {

    private NodoCola frente;
    private NodoCola fin;
    private int tamaño;

    public ColaFIFO() {
        this.frente = null;
        this.fin = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un vehículo al final de la cola (operación enqueue).
     *
     * @param vehiculo Vehículo a encolar
     * @return true si se insertó; false si el vehículo es nulo
     */
    public synchronized boolean encolar(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return false;
        }

        NodoCola nuevoNodo = new NodoCola(vehiculo);
        if (estaVacia()) {
            frente = nuevoNodo;
            fin = nuevoNodo;
        } else {
            fin.setSiguiente(nuevoNodo);
            fin = nuevoNodo;
        }
        tamaño++;
        return true;
    }

    /**
     * Extrae y remueve el vehículo al frente de la cola (operación dequeue).
     *
     * @return El vehículo que estaba al frente o null si la cola está vacía
     */
    public synchronized Vehiculo desencolar() {
        if (estaVacia()) {
            return null;
        }

        Vehiculo vehiculoExtraido = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fin = null;
        }
        tamaño--;
        return vehiculoExtraido;
    }

    /**
     * Consulta el vehículo al frente de la cola sin removerlo (operación peek).
     *
     * @return Vehículo al frente o null si está vacía
     */
    public synchronized Vehiculo obtenerFrente() {
        if (estaVacia()) {
            return null;
        }
        return frente.getDato();
    }

    /**
     * Evalúa si la cola no contiene elementos.
     *
     * @return true si está vacía
     */
    public synchronized boolean estaVacia() {
        return frente == null || tamaño == 0;
    }

    /**
     * Retorna la cantidad de elementos encolados.
     *
     * @return Número de vehículos en cola
     */
    public synchronized int getTamaño() {
        return tamaño;
    }

    /**
     * Busca de forma no destructiva si un vehículo con la placa indicada está en la cola.
     *
     * @param placa Placa a localizar
     * @return true si la placa está presente en la cola
     */
    public synchronized boolean contienePlaca(String placa) {
        if (placa == null || estaVacia()) {
            return false;
        }
        NodoCola actual = frente;
        while (actual != null) {
            Vehiculo v = actual.getDato();
            if (v != null && v.getPlaca() != null && v.getPlaca().equalsIgnoreCase(placa.trim())) {
                return true;
            }
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Recorrido no destructivo que devuelve la representación en texto del estado de la cola
     * desde el frente (primero en salir) hasta el fin (último en llegar).
     *
     * @return Cadena con los vehículos encolados
     */
    public synchronized String recorrer() {
        if (estaVacia()) {
            return "(Cola vacía)";
        }
        StringBuilder sb = new StringBuilder();
        NodoCola actual = frente;
        int posicion = 1;
        while (actual != null) {
            sb.append("  [Pos ").append(posicion++).append("] ").append(actual.getDato());
            if (actual.getSiguiente() != null) {
                sb.append("\n");
            }
            actual = actual.getSiguiente();
        }
        return sb.toString();
    }

    public synchronized NodoCola getPrimerNodo() {
        return frente;
    }
}
