package cris.sic.refugio.modelo;

// Modelo para representar un caso de rescate urgente
public class Rescate {
    private String codigo;
    private String direccionDescripcion;
    private String prioridad;
    private String estado;
    private String fecha;
    private String codigoAnimalVinculado;

    public Rescate() {}

    public Rescate(String codigo, String direccionDescripcion, String prioridad, String estado, String fecha, String codigoAnimalVinculado) {
        this.codigo = codigo;
        this.direccionDescripcion = direccionDescripcion;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fecha = fecha;
        this.codigoAnimalVinculado = codigoAnimalVinculado;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDireccionDescripcion() { return direccionDescripcion; }
    public void setDireccionDescripcion(String direccionDescripcion) { this.direccionDescripcion = direccionDescripcion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getCodigoAnimalVinculado() { return codigoAnimalVinculado; }
    public void setCodigoAnimalVinculado(String codigoAnimalVinculado) { this.codigoAnimalVinculado = codigoAnimalVinculado; }
}
