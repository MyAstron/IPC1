package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Nodo de enlace simple para la estructura enlazada ListaSimpleVehiculos.
 * Cumple con la restricción estricta de CERO colecciones de java.util.
 */
public class NodoSimple {

    private Vehiculo dato;
    private NodoSimple siguiente;

    public NodoSimple(Vehiculo dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public NodoSimple(Vehiculo dato, NodoSimple siguiente) {
        this.dato = dato;
        this.siguiente = siguiente;
    }

    public Vehiculo getDato() {
        return dato;
    }

    public void setDato(Vehiculo dato) {
        this.dato = dato;
    }

    public NodoSimple getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoSimple siguiente) {
        this.siguiente = siguiente;
    }
}
