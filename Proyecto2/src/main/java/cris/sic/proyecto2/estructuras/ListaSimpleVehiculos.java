package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Lista enlazada simple implementada desde cero para almacenar los vehículos asociados a un residente.
 * 
 * Reglas de negocio obligatorias:
 * - Capacidad máxima: 3 vehículos por residente.
 * - Cero colecciones de java.util (sin ArrayList, LinkedList, etc.).
 * - Cero arreglos nativos (T[]) para almacenamiento de datos.
 */
public class ListaSimpleVehiculos {

    public static final int CAPACIDAD_MAXIMA = 3;

    private NodoSimple cabeza;
    private int tamaño;

    public ListaSimpleVehiculos() {
        this.cabeza = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un nuevo vehículo al final de la lista.
     * Valida que no se exceda el límite máximo de 3 vehículos por residente
     * y que no exista un vehículo con la misma placa ya registrado para este residente.
     *
     * @param vehiculo Vehículo a registrar
     * @return true si se insertó con éxito; false si excede la capacidad o la placa está repetida
     */
    public boolean insertar(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return false;
        }
        if (this.tamaño >= CAPACIDAD_MAXIMA) {
            return false;
        }
        if (buscarPorPlaca(vehiculo.getPlaca()) != null) {
            return false; // Placa ya existente en este residente
        }

        NodoSimple nuevoNodo = new NodoSimple(vehiculo);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            NodoSimple actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevoNodo);
        }
        tamaño++;
        return true;
    }

    /**
     * Busca un vehículo por su número de placa (insensible a mayúsculas/minúsculas).
     *
     * @param placa Placa del vehículo a localizar
     * @return Vehiculo encontrado o null si no existe
     */
    public Vehiculo buscarPorPlaca(String placa) {
        if (placa == null || cabeza == null) {
            return null;
        }
        NodoSimple actual = cabeza;
        while (actual != null) {
            Vehiculo v = actual.getDato();
            if (v != null && v.getPlaca() != null && v.getPlaca().equalsIgnoreCase(placa.trim())) {
                return v;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Elimina un vehículo por placa únicamente si su estado actual es FUERA.
     *
     * @param placa Placa del vehículo a eliminar
     * @return true si se eliminó; false si no se encontró o si el vehículo no está en estado FUERA
     */
    public boolean eliminar(String placa) {
        if (placa == null || cabeza == null) {
            return false;
        }

        // Caso 1: El elemento a eliminar es la cabeza
        if (cabeza.getDato() != null && cabeza.getDato().getPlaca().equalsIgnoreCase(placa.trim())) {
            if (cabeza.getDato().getEstado() != EstadoVehiculo.FUERA) {
                return false; // Regla de negocio: no eliminar si no está FUERA
            }
            cabeza = cabeza.getSiguiente();
            tamaño--;
            return true;
        }

        // Caso 2: El elemento está en nodos posteriores
        NodoSimple anterior = cabeza;
        NodoSimple actual = cabeza.getSiguiente();

        while (actual != null) {
            if (actual.getDato() != null && actual.getDato().getPlaca().equalsIgnoreCase(placa.trim())) {
                if (actual.getDato().getEstado() != EstadoVehiculo.FUERA) {
                    return false; // Regla de negocio: no eliminar si no está FUERA
                }
                anterior.setSiguiente(actual.getSiguiente());
                tamaño--;
                return true;
            }
            anterior = actual;
            actual = actual.getSiguiente();
        }
        return false;
    }

    /**
     * Verifica si todos los vehículos de esta lista están en estado FUERA.
     * Requisito indispensable para permitir la modificación de condición de socio o eliminación de residente.
     *
     * @return true si todos los vehículos están FUERA (o si la lista no tiene vehículos)
     */
    public boolean estanTodosFuera() {
        NodoSimple actual = cabeza;
        while (actual != null) {
            Vehiculo v = actual.getDato();
            if (v != null && v.getEstado() != EstadoVehiculo.FUERA) {
                return false;
            }
            actual = actual.getSiguiente();
        }
        return true;
    }

    /**
     * Verifica si al menos un vehículo se encuentra dentro del flujo del residencial
     * (EN_COLA_ENTRADA, ESTACIONADO o EN_COLA_SALIDA).
     *
     * @return true si tiene algún vehículo activo dentro del residencial
     */
    public boolean tieneVehiculosDentro() {
        return !estanTodosFuera();
    }

    public NodoSimple getCabeza() {
        return cabeza;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return cabeza == null || tamaño == 0;
    }

    public boolean puedeAgregarMas() {
        return tamaño < CAPACIDAD_MAXIMA;
    }
}
