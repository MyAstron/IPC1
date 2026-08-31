package cris.sic.refugio.modelo;

// Modelo para representar un registro de bitacora (eventos o errores)
public class Bitacora {
    private String fechaHora;
    private String usuario;
    private String modulo;
    private String tipoEvento;
    private String descripcion;
    private String motivoRechazo;

    public Bitacora() {}

    public Bitacora(String fechaHora, String usuario, String modulo, String tipoEvento, String descripcion, String motivoRechazo) {
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.modulo = modulo;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.motivoRechazo = motivoRechazo;
    }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }
}
