package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Residente;

/**
 * Nodo de doble enlace para la estructura lineal ListaDobleResidentes.
 * Permite navegación bidireccional mediante referencias anterior y siguiente.
 * 
 * Restricciones estrictas:
 * - Cero colecciones de java.util.
 * - Cero arreglos nativos T[] para almacenamiento.
 * 
 * @author cris_sic
 */
public class NodoDoble {

    private Residente dato;
    private NodoDoble siguiente;
    private NodoDoble anterior;

    public NodoDoble(Residente dato) {
        this.dato = dato;
        this.siguiente = null;
        this.anterior = null;
    }

    public NodoDoble(Residente dato, NodoDoble siguiente, NodoDoble anterior) {
        this.dato = dato;
        this.siguiente = siguiente;
        this.anterior = anterior;
    }

    public Residente getDato() {
        return dato;
    }

    public void setDato(Residente dato) {
        this.dato = dato;
    }

    public NodoDoble getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDoble siguiente) {
        this.siguiente = siguiente;
    }

    public NodoDoble getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoDoble anterior) {
        this.anterior = anterior;
    }
}
