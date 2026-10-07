package cris.sic.proyecto2.modelo;

import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Entidad modelo que representa a un Visitante del residencial ResiPark.
 * Registra información de paso asociada a un residente que autoriza la visita.
 * Nota de diseño: La información de los visitantes es puramente en memoria y no se persiste en disco.
 */
public class Visitante {

    private String nombre;
    private String placa;
    private String idResidenteVisita;
    private Vehiculo vehiculo;

    /**
     * Constructor principal de Visitante.
     *
     * @param nombre            Nombre completo del visitante
     * @param placa             Placa del vehículo del visitante
     * @param idResidenteVisita ID del residente que visita
     */
    public Visitante(String nombre, String placa, String idResidenteVisita) {
        setNombre(nombre);
        setPlaca(placa);
        setIdResidenteVisita(idResidenteVisita);
        // Creamos un vehículo asociado representativo para su flujo en la simulación
        this.vehiculo = new Vehiculo(this.placa, "Visitante", "Visitante", "Particular", "Automóvil", null);
    }

    /**
     * Constructor extendido que permite especificar los detalles del vehículo del visitante.
     *
     * @param nombre            Nombre completo del visitante
     * @param placa             Placa del vehículo
     * @param idResidenteVisita ID del residente que visita
     * @param marca             Marca del vehículo
     * @param modelo            Modelo del vehículo
     * @param color             Color del vehículo
     * @param tipo              Tipo (Automóvil, Motocicleta, Pickup)
     */
    public Visitante(String nombre, String placa, String idResidenteVisita,
                     String marca, String modelo, String color, String tipo) {
        setNombre(nombre);
        setPlaca(placa);
        setIdResidenteVisita(idResidenteVisita);
        this.vehiculo = new Vehiculo(this.placa, marca, modelo, color, tipo, null);
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        if (!ValidadorTexto.esTextoValido(nombre)) {
            throw new IllegalArgumentException("El nombre del visitante no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public String getPlaca() {
        return placa;
    }

    public final void setPlaca(String placa) {
        if (!ValidadorTexto.esPlacaValida(placa)) {
            throw new IllegalArgumentException("Placa de visitante inválida: debe tener entre 3 y 10 caracteres.");
        }
        this.placa = placa.trim().toUpperCase();
        if (this.vehiculo != null) {
            this.vehiculo.setPlaca(this.placa);
        }
    }

    public String getIdResidenteVisita() {
        return idResidenteVisita;
    }

    public final void setIdResidenteVisita(String idResidenteVisita) {
        if (!ValidadorTexto.esTextoValido(idResidenteVisita)) {
            throw new IllegalArgumentException("El ID del residente a visitar no puede ser nulo ni vacío.");
        }
        this.idResidenteVisita = idResidenteVisita.trim();
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    @Override
    public String toString() {
        return "Visitante [Nombre=" + nombre + ", Placa=" + placa
                + ", Visita a Residente ID=" + idResidenteVisita + "]";
    }
}
