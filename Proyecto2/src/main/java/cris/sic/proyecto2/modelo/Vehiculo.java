package cris.sic.proyecto2.modelo;

import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Entidad modelo que representa un Vehículo dentro del sistema ResiPark.
 * Gestiona sus características físicas, su estado de flujo en el parqueo y
 * la referencia a su residente propietario.
 */
public class Vehiculo {

    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipo; // Automóvil, Motocicleta, Pickup
    private EstadoVehiculo estado;
    private Residente propietario;

    /**
     * Constructor para vehículos asociados a un residente.
     *
     * @param placa       Placa única del vehículo (3-10 caracteres, sin '|')
     * @param marca       Marca del fabricante
     * @param modelo      Línea o modelo del vehículo
     * @param color       Color representativo
     * @param tipo        Tipo de vehículo (Automóvil, Motocicleta, Pickup)
     * @param propietario Residente dueño del vehículo
     */
    public Vehiculo(String placa, String marca, String modelo, String color, String tipo, Residente propietario) {
        setPlaca(placa);
        setMarca(marca);
        setModelo(modelo);
        setColor(color);
        setTipo(tipo);
        this.estado = EstadoVehiculo.FUERA;
        this.propietario = propietario;
    }

    /**
     * Constructor para vehículos sin residente asignado de inicio (ej. visitantes o pruebas).
     *
     * @param placa  Placa única del vehículo
     * @param marca  Marca del fabricante
     * @param modelo Línea o modelo
     * @param color  Color
     * @param tipo   Tipo de vehículo
     */
    public Vehiculo(String placa, String marca, String modelo, String color, String tipo) {
        this(placa, marca, modelo, color, tipo, null);
    }

    // ==========================================
    // REGLAS DE NEGOCIO Y TRANSICIÓN DE ESTADOS
    // ==========================================

    /**
     * Cambia el estado del vehículo verificando la validez de la transición.
     *
     * @param nuevoEstado Estado al cual se desea cambiar
     * @return true si la transición fue exitosa; false si la transición viola el ciclo de vida
     */
    public boolean transicionarA(EstadoVehiculo nuevoEstado) {
        if (this.estado != null && this.estado.puedeTransicionarA(nuevoEstado)) {
            this.estado = nuevoEstado;
            return true;
        }
        return false;
    }

    /**
     * Intenta pasar el vehículo a la cola de entrada. Solo permitido si está FUERA.
     *
     * @return true si se actualizó el estado a EN_COLA_ENTRADA
     */
    public boolean entrarAColaEntrada() {
        return transicionarA(EstadoVehiculo.EN_COLA_ENTRADA);
    }

    /**
     * Intenta estacionar el vehículo en un espacio asignado. Solo permitido si está EN_COLA_ENTRADA.
     *
     * @return true si se actualizó el estado a ESTACIONADO
     */
    public boolean estacionar() {
        return transicionarA(EstadoVehiculo.ESTACIONADO);
    }

    /**
     * Intenta solicitar salida colocándose en la cola de salida. Solo permitido si está ESTACIONADO.
     *
     * @return true si se actualizó el estado a EN_COLA_SALIDA
     */
    public boolean entrarAColaSalida() {
        return transicionarA(EstadoVehiculo.EN_COLA_SALIDA);
    }

    /**
     * Completa el proceso de salida retirándose del residencial. Solo permitido si está EN_COLA_SALIDA.
     *
     * @return true si se actualizó el estado a FUERA
     */
    public boolean salirDelResidencial() {
        return transicionarA(EstadoVehiculo.FUERA);
    }

    /**
     * Indica si el vehículo pertenece a un residente socio del club.
     *
     * @return true si tiene propietario y este es socio
     */
    public boolean esDeSocio() {
        return this.propietario != null && this.propietario.isEsSocio();
    }

    // ==========================================
    // GETTERS Y SETTERS CON VALIDACIONES
    // ==========================================

    public String getPlaca() {
        return placa;
    }

    public final void setPlaca(String placa) {
        if (!ValidadorTexto.esPlacaValida(placa)) {
            throw new IllegalArgumentException("Placa inválida: debe tener entre 3 y 10 caracteres y no contener '|'.");
        }
        this.placa = placa.trim().toUpperCase();
    }

    public String getMarca() {
        return marca;
    }

    public final void setMarca(String marca) {
        if (!ValidadorTexto.esTextoValido(marca) || ValidadorTexto.contienePipe(marca)) {
            throw new IllegalArgumentException("Marca no válida o contiene el carácter prohibido '|'.");
        }
        this.marca = marca.trim();
    }

    public String getModelo() {
        return modelo;
    }

    public final void setModelo(String modelo) {
        if (!ValidadorTexto.esTextoValido(modelo) || ValidadorTexto.contienePipe(modelo)) {
            throw new IllegalArgumentException("Modelo no válido o contiene el carácter prohibido '|'.");
        }
        this.modelo = modelo.trim();
    }

    public String getColor() {
        return color;
    }

    public final void setColor(String color) {
        if (!ValidadorTexto.esTextoValido(color) || ValidadorTexto.contienePipe(color)) {
            throw new IllegalArgumentException("Color no válido o contiene el carácter prohibido '|'.");
        }
        this.color = color.trim();
    }

    public String getTipo() {
        return tipo;
    }

    public final void setTipo(String tipo) {
        if (!ValidadorTexto.esTipoVehiculoValido(tipo) || ValidadorTexto.contienePipe(tipo)) {
            throw new IllegalArgumentException("Tipo de vehículo inválido. Valores permitidos: Automóvil, Motocicleta, Pickup.");
        }
        this.tipo = tipo.trim();
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        this.estado = estado;
    }

    public Residente getPropietario() {
        return propietario;
    }

    public void setPropietario(Residente propietario) {
        this.propietario = propietario;
    }

    @Override
    public String toString() {
        String prop = (propietario != null) ? propietario.getId() : "Sin Propietario";
        return "[" + placa + "] " + marca + " " + modelo + " (" + tipo + ", " + color + ") - Estado: " + estado + " - Dueño: " + prop;
    }
}
