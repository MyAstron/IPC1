package cris.sic.proyecto2.modelo;

import cris.sic.proyecto2.estructuras.ListaCircularParqueo;

/**
 * Controlador central del Parqueo de ResiPark (150 espacios en total).
 * Coordina el Área de Socios (75 espacios) y el Área General (75 espacios)
 * implementando las reglas de asignación y desborde según el tipo de usuario.
 * 
 * Reglas de negocio:
 * 1. Residente Socio: Asigna primero en Área Socios; si está llena, desborda al Área General.
 * 2. Residente No Socio y Visitantes: Asigna estrictamente en Área General.
 * 3. Si no hay espacio en las áreas autorizadas, la asignación es rechazada (retorna null).
 * 
 * @author cris_sic
 */
public class ControladorParqueo {

    private final ListaCircularParqueo areaSocios;
    private final ListaCircularParqueo areaGeneral;

    public ControladorParqueo() {
        this.areaSocios = new ListaCircularParqueo(TipoEspacio.SOCIO);
        this.areaGeneral = new ListaCircularParqueo(TipoEspacio.GENERAL);
    }

    /**
     * Asigna un espacio al vehículo entrante según su condición de socio o visitante.
     *
     * @param vehiculo Vehículo a estacionar
     * @return EspacioParqueo asignado, o null si fue rechazado por falta de espacio
     */
    public synchronized EspacioParqueo asignarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return null;
        }

        boolean esSocio = false;
        if (vehiculo.getPropietario() != null) {
            esSocio = vehiculo.getPropietario().isEsSocio();
        }

        EspacioParqueo espacioAsignado = null;

        if (esSocio) {
            // Intento 1: Asignar en Área Socios
            if (!areaSocios.estaLlena()) {
                espacioAsignado = areaSocios.asignarEspacio(vehiculo);
            }
            // Intento 2 (Desborde): Si el área de socios está llena, pasa a Área General
            if (espacioAsignado == null && !areaGeneral.estaLlena()) {
                espacioAsignado = areaGeneral.asignarEspacio(vehiculo);
            }
        } else {
            // Residentes no socios y visitantes solo pueden ocupar Área General
            if (!areaGeneral.estaLlena()) {
                espacioAsignado = areaGeneral.asignarEspacio(vehiculo);
            }
        }

        if (espacioAsignado != null) {
            if (vehiculo.getEstado() == EstadoVehiculo.FUERA) {
                vehiculo.entrarAColaEntrada();
            }
            vehiculo.estacionar();
        }

        return espacioAsignado;
    }

    /**
     * Libera el espacio que ocupa un vehículo en cualquiera de las dos áreas.
     *
     * @param placa Placa del vehículo a retirar
     * @return EspacioParqueo liberado, o null si no se encontraba estacionado
     */
    public synchronized EspacioParqueo liberarVehiculoPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }

        EspacioParqueo liberado = areaSocios.liberarEspacioPorPlaca(placa);
        if (liberado == null) {
            liberado = areaGeneral.liberarEspacioPorPlaca(placa);
        }
        return liberado;
    }

    /**
     * Busca un espacio por su identificador en ambas áreas del parqueo.
     *
     * @param idEspacio Identificador (ej. "A10", "G5")
     * @return EspacioParqueo o null si no existe
     */
    public synchronized EspacioParqueo buscarPorId(String idEspacio) {
        if (idEspacio == null || idEspacio.trim().isEmpty()) {
            return null;
        }
        char fila = Character.toUpperCase(idEspacio.trim().charAt(0));
        if (fila == 'A' || fila == 'B' || fila == 'C') {
            return areaSocios.buscarPorId(idEspacio);
        } else {
            return areaGeneral.buscarPorId(idEspacio);
        }
    }

    /**
     * Busca en qué espacio se encuentra estacionado un vehículo dada su placa.
     *
     * @param placa Placa del vehículo
     * @return EspacioParqueo o null si no está estacionado
     */
    public synchronized EspacioParqueo buscarPorPlaca(String placa) {
        if (placa == null) {
            return null;
        }
        EspacioParqueo espacio = areaSocios.buscarPorPlaca(placa);
        if (espacio == null) {
            espacio = areaGeneral.buscarPorPlaca(placa);
        }
        return espacio;
    }

    public synchronized ListaCircularParqueo getAreaSocios() {
        return areaSocios;
    }

    public synchronized ListaCircularParqueo getAreaGeneral() {
        return areaGeneral;
    }

    public synchronized int getTotalCapacidad() {
        return areaSocios.getCapacidad() + areaGeneral.getCapacidad(); // 150
    }

    public synchronized int getTotalOcupados() {
        return areaSocios.getOcupados() + areaGeneral.getOcupados();
    }

    public synchronized int getTotalDisponibles() {
        return areaSocios.getDisponibles() + areaGeneral.getDisponibles();
    }
}
