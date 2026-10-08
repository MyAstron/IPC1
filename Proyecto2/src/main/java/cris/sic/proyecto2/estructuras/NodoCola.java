package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Nodo de enlace para la estructura de flujo Cola FIFO de vehículos (ColaFIFO).
 * 
 * Restricciones estrictas:
 * - Cero colecciones de java.util.
 * - Cero arreglos nativos T[] para almacenamiento.
 * 
 * @author cris_sic
 */
public class NodoCola {

    private Vehiculo dato;
    private NodoCola siguiente;

    public NodoCola(Vehiculo dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public NodoCola(Vehiculo dato, NodoCola siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public Vehiculo getDato() {
        return dato;
    }

    public void setDato(Vehiculo dato) {
        this.dato = dato;
    }

    public NodoCola getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCola siguiente) {
        this.siguiente = siguiente;
    }
}
