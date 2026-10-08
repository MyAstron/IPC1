package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.EspacioParqueo;

/**
 * Nodo para la estructura dinámica de Lista Enlazada Circular.
 * Almacena un espacio de parqueo y un puntero al nodo siguiente en el anillo circular.
 * 
 * Reglas de diseño:
 * - Cero uso de librerías de java.util.
 * - Manejo exclusivo de punteros y memoria dinámica.
 * 
 * @author cris_sic
 */
public class NodoCircular {

    private EspacioParqueo dato;
    private NodoCircular siguiente;

    public NodoCircular(EspacioParqueo dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public NodoCircular(EspacioParqueo dato, NodoCircular siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public EspacioParqueo getDato() {
        return dato;
    }

    public void setDato(EspacioParqueo dato) {
        this.dato = dato;
    }

    public NodoCircular getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCircular siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public String toString() {
        return (dato != null) ? dato.toString() : "NodoCircular[null]";
    }
}
