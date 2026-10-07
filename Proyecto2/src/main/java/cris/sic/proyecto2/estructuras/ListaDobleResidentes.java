package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Lista doblemente enlazada implementada desde cero para almacenar y gestionar los residentes de ResiPark.
 * 
 * Reglas de negocio obligatorias:
 * - Cero colecciones de java.util (sin LinkedList, ArrayList, etc.).
 * - Cero arreglos nativos (T[]) para almacenamiento de datos.
 * - Identificador único de residente (ID inmutable y sin duplicados).
 * - Regla de Oro: No se puede eliminar un residente ni modificar su condición de socio si alguno de sus
 *   vehículos se encuentra dentro del complejo (EN_COLA_ENTRADA, ESTACIONADO o EN_COLA_SALIDA).
 * 
 * @author cris_sic
 */
public class ListaDobleResidentes {

    private NodoDoble cabeza;
    private NodoDoble cola;
    private int tamaño;

    public ListaDobleResidentes() {
        this.cabeza = null;
        this.cola = null;
        this.tamaño = 0;
    }

    /**
     * Inserta un nuevo residente al final de la lista doblemente enlazada.
     * Valida que el ID no exista previamente en la estructura.
     *
     * @param residente Residente a insertar
     * @return true si se insertó exitosamente; false si es nulo o el ID ya existe
     */
    public boolean insertar(Residente residente) {
        if (residente == null || residente.getId() == null) {
            return false;
        }
        if (buscarPorId(residente.getId()) != null) {
            return false; // ID duplicado no permitido
        }

        NodoDoble nuevoNodo = new NodoDoble(residente);
        if (cabeza == null) {
            cabeza = nuevoNodo;
            cola = nuevoNodo;
        } else {
            cola.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(cola);
            cola = nuevoNodo;
        }
        tamaño++;
        return true;
    }

    /**
     * Busca un residente por su ID único.
     *
     * @param id Identificador del residente a buscar
     * @return El Residente encontrado o null si no existe
     */
    public Residente buscarPorId(String id) {
        if (id == null || cabeza == null) {
            return null;
        }
        NodoDoble actual = cabeza;
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getId().equalsIgnoreCase(id.trim())) {
                return r;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Busca el nodo doble que contiene al residente con el ID indicado.
     *
     * @param id Identificador a localizar
     * @return NodoDoble correspondiente o null
     */
    public NodoDoble buscarNodoPorId(String id) {
        if (id == null || cabeza == null) {
            return null;
        }
        NodoDoble actual = cabeza;
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getId().equalsIgnoreCase(id.trim())) {
                return actual;
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Actualiza los datos de un residente existente.
     * Aplica la regla de oro si se intenta modificar la condición de socio: todos sus vehículos
     * deben estar en estado FUERA.
     *
     * @param id           ID del residente a actualizar
     * @param nuevoNombre  Nuevo nombre
     * @param nuevaCasa    Nueva casa o lote
     * @param nuevoEsSocio Nueva condición de membresía
     * @return true si se actualizó con éxito; false si no existe o se rechazó el cambio de membresía
     */
    public boolean actualizar(String id, String nuevoNombre, String nuevaCasa, boolean nuevoEsSocio) {
        Residente r = buscarPorId(id);
        if (r == null) {
            return false;
        }

        // Si cambia condición de socio, validar regla de oro
        if (r.isEsSocio() != nuevoEsSocio) {
            if (!r.cambiarEstadoSocio(nuevoEsSocio)) {
                return false; // Denegado por tener vehículos dentro
            }
        }

        r.setNombre(nuevoNombre);
        r.setCasaLote(nuevaCasa);
        return true;
    }

    /**
     * Elimina un residente de la lista por su ID.
     * Regla de Oro: Se deniega la eliminación si el residente posee algún vehículo dentro del residencial
     * (en cola de entrada, estacionado o en cola de salida).
     *
     * @param id Identificador del residente a eliminar
     * @return true si fue eliminado; false si no existe o si tiene vehículos dentro
     */
    public boolean eliminar(String id) {
        if (id == null || cabeza == null) {
            return false;
        }

        NodoDoble nodoEliminar = buscarNodoPorId(id);
        if (nodoEliminar == null) {
            return false;
        }

        Residente r = nodoEliminar.getDato();
        // Validación de la Regla de Oro
        if (r.tieneVehiculosDentro()) {
            return false; // No se puede eliminar si algún vehículo no está FUERA
        }

        // Caso 1: Es el único nodo en la lista
        if (cabeza == cola && cabeza == nodoEliminar) {
            cabeza = null;
            cola = null;
        }
        // Caso 2: Es la cabeza
        else if (nodoEliminar == cabeza) {
            cabeza = cabeza.getSiguiente();
            if (cabeza != null) {
                cabeza.setAnterior(null);
            }
        }
        // Caso 3: Es la cola
        else if (nodoEliminar == cola) {
            cola = cola.getAnterior();
            if (cola != null) {
                cola.setSiguiente(null);
            }
        }
        // Caso 4: Nodo intermedio
        else {
            NodoDoble ant = nodoEliminar.getAnterior();
            NodoDoble sig = nodoEliminar.getSiguiente();
            if (ant != null) {
                ant.setSiguiente(sig);
            }
            if (sig != null) {
                sig.setAnterior(ant);
            }
        }

        // Desconectar nodo eliminado
        nodoEliminar.setSiguiente(null);
        nodoEliminar.setAnterior(null);
        tamaño--;
        return true;
    }

    /**
     * Demuestra y valida la capacidad de navegación bidireccional de la lista.
     * Retorna una cadena con el recorrido hacia adelante (cabeza -> cola) y hacia atrás (cola -> cabeza).
     *
     * @return Representación textual del recorrido en ambos sentidos
     */
    public String recorrerAmbosSentidos() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- RECORRIDO HACIA ADELANTE (Cabeza a Cola) ---\n");
        NodoDoble actual = cabeza;
        int idx = 1;
        while (actual != null) {
            sb.append("  [").append(idx++).append("] ").append(actual.getDato()).append("\n");
            actual = actual.getSiguiente();
        }

        sb.append("--- RECORRIDO HACIA ATRÁS (Cola a Cabeza) ---\n");
        actual = cola;
        idx = tamaño;
        while (actual != null) {
            sb.append("  [").append(idx--).append("] ").append(actual.getDato()).append("\n");
            actual = actual.getAnterior();
        }
        return sb.toString();
    }

    /**
     * Localiza un vehículo en todo el residencial buscando a través de las listas de vehículos
     * de cada residente registrado.
     *
     * @param placa Placa a localizar
     * @return Vehiculo localizado o null si no está registrado
     */
    public Vehiculo buscarVehiculoGlobal(String placa) {
        if (placa == null || cabeza == null) {
            return null;
        }
        NodoDoble actual = cabeza;
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getListaVehiculos() != null) {
                Vehiculo v = r.getListaVehiculos().buscarPorPlaca(placa);
                if (v != null) {
                    return v;
                }
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    /**
     * Localiza el residente propietario de un vehículo dada su placa.
     *
     * @param placa Placa del vehículo
     * @return Residente propietario o null
     */
    public Residente buscarResidentePorPlaca(String placa) {
        if (placa == null || cabeza == null) {
            return null;
        }
        NodoDoble actual = cabeza;
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getListaVehiculos() != null) {
                if (r.getListaVehiculos().buscarPorPlaca(placa) != null) {
                    return r;
                }
            }
            actual = actual.getSiguiente();
        }
        return null;
    }

    public NodoDoble getCabeza() {
        return cabeza;
    }

    public NodoDoble getCola() {
        return cola;
    }

    public int getTamaño() {
        return tamaño;
    }

    public boolean estaVacia() {
        return cabeza == null || tamaño == 0;
    }
}
