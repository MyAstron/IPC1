package cris.sic.proyecto2.estructuras;

import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.EstadoEspacio;
import cris.sic.proyecto2.modelo.TipoEspacio;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Estructura de datos Lista Enlazada Circular implementada desde cero para el parqueo de ResiPark.
 * 
 * Reglas de negocio y especificación:
 * - Área Socios: 75 espacios (Filas A, B, C; 25 espacios cada una).
 * - Área General: 75 espacios (Filas E, F, G, H, I; 15 espacios cada una). Sin fila D.
 * - Cero uso de colecciones de java.util o arreglos nativos T[] para almacenamiento de datos.
 * - Recorrido circular: La búsqueda de un espacio libre inicia en el nodo siguiente al último asignado
 *   y da como máximo una vuelta completa antes de reportar el área llena.
 * - Métodos sincronizados para garantizar seguridad en entornos multihilo.
 * 
 * @author cris_sic
 */
public class ListaCircularParqueo {

    private final TipoEspacio tipoArea;
    private final int capacidad;
    private NodoCircular cabeza;
    private NodoCircular ultimoAsignado;
    private int ocupados;

    /**
     * Constructor que inicializa y encadena automáticamente los 75 espacios circulares del área designada.
     *
     * @param tipoArea Tipo de área a construir (SOCIO o GENERAL)
     */
    public ListaCircularParqueo(TipoEspacio tipoArea) {
        this.tipoArea = (tipoArea != null) ? tipoArea : TipoEspacio.GENERAL;
        this.capacidad = 75;
        this.cabeza = null;
        this.ultimoAsignado = null;
        this.ocupados = 0;
        inicializarEspacios();
    }

    /**
     * Construye la lista enlazada circular de 75 espacios usando únicamente nodos y punteros.
     */
    private void inicializarEspacios() {
        NodoCircular ultimoNodo = null;

        if (this.tipoArea == TipoEspacio.SOCIO) {
            // Filas A, B, C (3 filas x 25 columnas = 75 espacios)
            char[] filasSocios = {'A', 'B', 'C'};
            for (char f : filasSocios) {
                for (int col = 1; col <= 25; col++) {
                    String id = "" + f + col;
                    EspacioParqueo espacio = new EspacioParqueo(id, f, col, TipoEspacio.SOCIO);
                    NodoCircular nuevoNodo = new NodoCircular(espacio);
                    if (cabeza == null) {
                        cabeza = nuevoNodo;
                        ultimoNodo = nuevoNodo;
                    } else {
                        ultimoNodo.setSiguiente(nuevoNodo);
                        ultimoNodo = nuevoNodo;
                    }
                }
            }
        } else {
            // Filas E, F, G, H, I (5 filas x 15 columnas = 75 espacios). No existe fila D.
            char[] filasGeneral = {'E', 'F', 'G', 'H', 'I'};
            for (char f : filasGeneral) {
                for (int col = 1; col <= 15; col++) {
                    String id = "" + f + col;
                    EspacioParqueo espacio = new EspacioParqueo(id, f, col, TipoEspacio.GENERAL);
                    NodoCircular nuevoNodo = new NodoCircular(espacio);
                    if (cabeza == null) {
                        cabeza = nuevoNodo;
                        ultimoNodo = nuevoNodo;
                    } else {
                        ultimoNodo.setSiguiente(nuevoNodo);
                        ultimoNodo = nuevoNodo;
                    }
                }
            }
        }

        // Cierre circular del anillo: el último nodo apunta a la cabeza
        if (ultimoNodo != null && cabeza != null) {
            ultimoNodo.setSiguiente(cabeza);
        }
    }

    /**
     * Asigna un espacio libre al vehículo evaluando secuencialmente desde el PRIMER nodo
     * de la lista (cabeza: A1 en Socios, E1 en General) para ocupar siempre el primer espacio disponible.
     *
     * @param vehiculo Vehículo que solicita estacionarse
     * @return EspacioParqueo asignado, o null si el área está completamente llena
     */
    public synchronized EspacioParqueo asignarEspacio(Vehiculo vehiculo) {
        if (vehiculo == null || estaLlena() || cabeza == null) {
            return null;
        }

        // Búsqueda secuencial obligatoria desde el PRIMER nodo de la lista (cabeza)
        NodoCircular actual = cabeza;
        int pasos = 0;

        while (pasos < capacidad && actual != null) {
            if (actual.getDato() != null && actual.getDato().estaLibre()) {
                // Primer espacio libre encontrado desde el inicio
                EspacioParqueo espacio = actual.getDato();
                espacio.ocupar(vehiculo);
                ultimoAsignado = actual;
                ocupados++;
                return espacio;
            }
            actual = actual.getSiguiente();
            pasos++;
        }

        // No se encontró espacio disponible tras recorrer toda el área
        return null;
    }

    /**
     * Libera un espacio de parqueo por su identificador único (ej: "A1", "E10").
     *
     * @param idEspacio Identificador del espacio
     * @return El espacio liberado, o null si no se encontró o ya estaba libre
     */
    public synchronized EspacioParqueo liberarEspacioPorId(String idEspacio) {
        if (idEspacio == null || cabeza == null || estaVacia()) {
            return null;
        }

        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad) {
            if (actual != null && actual.getDato() != null) {
                EspacioParqueo esp = actual.getDato();
                if (esp.getIdEspacio().equalsIgnoreCase(idEspacio.trim())) {
                    if (esp.estaOcupado()) {
                        esp.liberar();
                        ocupados--;
                        return esp;
                    }
                    return null; // Ya estaba libre
                }
            }
            if (actual != null) {
                actual = actual.getSiguiente();
            }
            pasos++;
        }
        return null;
    }

    /**
     * Libera el espacio donde se encuentra estacionado el vehículo con la placa indicada.
     *
     * @param placa Placa del vehículo a retirar
     * @return El espacio liberado, o null si no se encontró
     */
    public synchronized EspacioParqueo liberarEspacioPorPlaca(String placa) {
        if (placa == null || cabeza == null || estaVacia()) {
            return null;
        }

        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad) {
            if (actual != null && actual.getDato() != null) {
                EspacioParqueo esp = actual.getDato();
                if (esp.estaOcupado() && esp.getVehiculoEstacionado() != null) {
                    if (esp.getVehiculoEstacionado().getPlaca().equalsIgnoreCase(placa.trim())) {
                        esp.liberar();
                        ocupados--;
                        return esp;
                    }
                }
            }
            if (actual != null) {
                actual = actual.getSiguiente();
            }
            pasos++;
        }
        return null;
    }

    /**
     * Busca un espacio por su identificador único.
     *
     * @param idEspacio Identificador (ej. "B12")
     * @return El EspacioParqueo o null si no existe
     */
    public synchronized EspacioParqueo buscarPorId(String idEspacio) {
        if (idEspacio == null || cabeza == null) {
            return null;
        }
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad) {
            if (actual != null && actual.getDato() != null) {
                if (actual.getDato().getIdEspacio().equalsIgnoreCase(idEspacio.trim())) {
                    return actual.getDato();
                }
            }
            if (actual != null) {
                actual = actual.getSiguiente();
            }
            pasos++;
        }
        return null;
    }

    /**
     * Busca el espacio que contiene el vehículo con la placa indicada.
     *
     * @param placa Placa del vehículo
     * @return EspacioParqueo ocupado o null si no está estacionado en esta área
     */
    public synchronized EspacioParqueo buscarPorPlaca(String placa) {
        if (placa == null || cabeza == null || estaVacia()) {
            return null;
        }
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad) {
            if (actual != null && actual.getDato() != null && actual.getDato().estaOcupado()) {
                Vehiculo v = actual.getDato().getVehiculoEstacionado();
                if (v != null && v.getPlaca().equalsIgnoreCase(placa.trim())) {
                    return actual.getDato();
                }
            }
            if (actual != null) {
                actual = actual.getSiguiente();
            }
            pasos++;
        }
        return null;
    }

    public synchronized boolean estaLlena() {
        return ocupados >= capacidad;
    }

    public synchronized boolean estaVacia() {
        return ocupados == 0;
    }

    public synchronized int getCapacidad() {
        return capacidad;
    }

    public synchronized int getOcupados() {
        return ocupados;
    }

    public synchronized int getDisponibles() {
        return capacidad - ocupados;
    }

    public TipoEspacio getTipoArea() {
        return tipoArea;
    }

    public synchronized NodoCircular getCabeza() {
        return cabeza;
    }

    public synchronized NodoCircular getUltimoAsignado() {
        return ultimoAsignado;
    }

    public synchronized void setUltimoAsignadoPorId(String idEspacio) {
        if (idEspacio == null || idEspacio.trim().isEmpty() || idEspacio.equalsIgnoreCase("NULL")) {
            this.ultimoAsignado = null;
            return;
        }
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad && actual != null) {
            if (actual.getDato() != null && actual.getDato().getIdEspacio().equalsIgnoreCase(idEspacio.trim())) {
                this.ultimoAsignado = actual;
                return;
            }
            actual = actual.getSiguiente();
            pasos++;
        }
    }

    public synchronized void recalcularOcupados() {
        int count = 0;
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad && actual != null) {
            if (actual.getDato() != null && actual.getDato().estaOcupado()) {
                count++;
            }
            actual = actual.getSiguiente();
            pasos++;
        }
        this.ocupados = count;
    }

    public synchronized void vaciar() {
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad && actual != null) {
            if (actual.getDato() != null) {
                actual.getDato().liberar();
            }
            actual = actual.getSiguiente();
            pasos++;
        }
        this.ocupados = 0;
        this.ultimoAsignado = null;
    }

    /**
     * Genera un reporte en texto con el estado de todos los espacios del área en orden secuencial.
     *
     * @return Cadena formateada
     */
    public synchronized String recorrer() {
        if (cabeza == null) {
            return "(Área no inicializada)";
        }
        StringBuilder sb = new StringBuilder();
        NodoCircular actual = cabeza;
        int pasos = 0;
        while (pasos < capacidad) {
            if (actual != null && actual.getDato() != null) {
                sb.append("  ").append(actual.getDato());
                if (pasos < capacidad - 1) {
                    sb.append("\n");
                }
            }
            if (actual != null) {
                actual = actual.getSiguiente();
            }
            pasos++;
        }
        return sb.toString();
    }
}
