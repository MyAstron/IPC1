package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Evento;

/**
 * Nodo de enlace para la estructura de historial Pila LIFO de eventos (PilaEventos).
 * 
 * Restricciones estrictas:
 * - Cero colecciones de java.util.
 * - Cero arreglos nativos T[] para almacenamiento.
 * 
 * @author cris_sic
 */
public class NodoPila {

    private Evento dato;
    private NodoPila siguiente;

    public NodoPila(Evento dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public NodoPila(Evento dato, NodoPila siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public Evento getDato() {
        return dato;
    }

    public void setDato(Evento dato) {
        this.dato = dato;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
}
