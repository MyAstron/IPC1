package cris.sic.refugio.modelo;

// Modelo para representar una solicitud de adopcion
public class Solicitud {
    private String codigo;
    private String codigoAnimal;
    private String codigoAdoptante;
    private String fecha;
    private String estado;

    public Solicitud() {}

    public Solicitud(String codigo, String codigoAnimal, String codigoAdoptante, String fecha, String estado) {
        this.codigo = codigo;
        this.codigoAnimal = codigoAnimal;
        this.codigoAdoptante = codigoAdoptante;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getCodigoAnimal() { return codigoAnimal; }
    public void setCodigoAnimal(String codigoAnimal) { this.codigoAnimal = codigoAnimal; }

    public String getCodigoAdoptante() { return codigoAdoptante; }
    public void setCodigoAdoptante(String codigoAdoptante) { this.codigoAdoptante = codigoAdoptante; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
