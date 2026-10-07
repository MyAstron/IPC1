package cris.sic.proyecto2.modelo;

import cris.sic.proyecto2.estructuras.ListaSimpleVehiculos;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Entidad modelo que representa a un Residente del complejo ResiPark.
 * 
 * Reglas de negocio clave:
 * - El identificador 'id' es único y no modificable tras la creación.
 * - Máximo 3 vehículos por residente almacenados en su ListaSimpleVehiculos.
 * - Regla de oro: No cambiar estado de socio si algún vehículo NO está en estado FUERA.
 */
public class Residente {

    private final String id;
    private String nombre;
    private String casaLote;
    private boolean esSocio;
    private final ListaSimpleVehiculos listaVehiculos;

    /**
     * Constructor principal de Residente.
     *
     * @param id       Identificador único (no modificable, sin '|')
     * @param nombre   Nombre completo del residente (sin '|')
     * @param casaLote Número o nomenclatura de casa/lote (sin '|')
     * @param esSocio  Indica si es socio del club residencial
     */
    public Residente(String id, String nombre, String casaLote, boolean esSocio) {
        if (!ValidadorTexto.esTextoValido(id) || ValidadorTexto.contienePipe(id)) {
            throw new IllegalArgumentException("ID de residente inválido o contiene el carácter '|'.");
        }
        this.id = id.trim();
        setNombre(nombre);
        setCasaLote(casaLote);
        this.esSocio = esSocio;
        this.listaVehiculos = new ListaSimpleVehiculos();
    }

    // ==========================================
    // REGLAS DE NEGOCIO DE VEHÍCULOS Y SOCIO
    // ==========================================

    /**
     * Evalúa si el residente aún puede registrar más vehículos (máximo 3).
     *
     * @return true si tiene menos de 3 vehículos registrados
     */
    public boolean puedeAgregarVehiculo() {
        return this.listaVehiculos.puedeAgregarMas();
    }

    /**
     * Asocia e inserta un vehículo a la lista del residente, estableciendo la referencia bidireccional.
     *
     * @param vehiculo Vehículo a registrar
     * @return true si fue agregado; false si se alcanzó el límite de 3 o la placa ya existe
     */
    public boolean agregarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) {
            return false;
        }
        if (!puedeAgregarVehiculo()) {
            return false;
        }
        boolean insertado = this.listaVehiculos.insertar(vehiculo);
        if (insertado) {
            vehiculo.setPropietario(this);
        }
        return insertado;
    }

    /**
     * Verifica si alguno de los vehículos del residente se encuentra dentro del flujo activo
     * (EN_COLA_ENTRADA, ESTACIONADO o EN_COLA_SALIDA).
     *
     * @return true si tiene vehículos activos en el residencial
     */
    public boolean tieneVehiculosDentro() {
        return this.listaVehiculos.tieneVehiculosDentro();
    }

    /**
     * Verifica si todos los vehículos registrados del residente están en estado FUERA.
     *
     * @return true si todos sus vehículos están fuera del residencial
     */
    public boolean estanTodosVehiculosFuera() {
        return this.listaVehiculos.estanTodosFuera();
    }

    /**
     * Valida si es seguro y conforme a las reglas cambiar la condición de socio del residente.
     * Regla de oro: No permitir cambio si algún vehículo no está FUERA.
     *
     * @return true si se puede modificar el estado de socio
     */
    public boolean puedeModificarCondicionSocio() {
        return estanTodosVehiculosFuera();
    }

    /**
     * Modifica el estado de socio respetando la regla de negocio.
     *
     * @param nuevoEstadoSocio Nuevo valor para la membresía de socio
     * @return true si el cambio se aplicó con éxito; false si fue denegado por tener vehículos dentro
     */
    public boolean cambiarEstadoSocio(boolean nuevoEstadoSocio) {
        if (this.esSocio == nuevoEstadoSocio) {
            return true;
        }
        if (!puedeModificarCondicionSocio()) {
            return false; // Rechazado por tener vehículos dentro
        }
        this.esSocio = nuevoEstadoSocio;
        return true;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public final void setNombre(String nombre) {
        if (!ValidadorTexto.esTextoValido(nombre) || ValidadorTexto.contienePipe(nombre)) {
            throw new IllegalArgumentException("Nombre de residente inválido o contiene el carácter '|'.");
        }
        this.nombre = nombre.trim();
    }

    public String getCasaLote() {
        return casaLote;
    }

    public final void setCasaLote(String casaLote) {
        if (!ValidadorTexto.esTextoValido(casaLote) || ValidadorTexto.contienePipe(casaLote)) {
            throw new IllegalArgumentException("Casa/Lote de residente inválido o contiene el carácter '|'.");
        }
        this.casaLote = casaLote.trim();
    }

    public boolean isEsSocio() {
        return esSocio;
    }

    /**
     * Setter tradicional para compatibilidad, validando la regla de integridad de vehículos fuera.
     */
    public void setEsSocio(boolean esSocio) {
        if (this.esSocio != esSocio && !puedeModificarCondicionSocio()) {
            throw new IllegalStateException("No se puede cambiar el estado de socio: el residente tiene vehículos activos en el parqueo o colas.");
        }
        this.esSocio = esSocio;
    }

    public ListaSimpleVehiculos getListaVehiculos() {
        return listaVehiculos;
    }

    public int getCantidadVehiculos() {
        return listaVehiculos.getTamaño();
    }

    @Override
    public String toString() {
        return "Residente [ID=" + id + ", Nombre=" + nombre + ", Casa/Lote=" + casaLote
                + ", Socio=" + (esSocio ? "SÍ" : "NO") + ", Vehículos=" + getCantidadVehiculos() + "/3]";
    }
}
