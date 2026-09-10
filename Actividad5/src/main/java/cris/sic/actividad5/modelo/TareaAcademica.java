package cris.sic.actividad5.modelo;

/**
 * Clase que modela una Tarea Academica asignada a un curso especifico.
 * Aplica el principio de encapsulamiento mediante modificadores de acceso privados y metodos getter/setter.
 */
public class TareaAcademica {

    // Referencia al objeto Curso al cual pertenece la tarea academica
    private Curso cursoAsociado;

    // Titulo descriptivo de la actividad o entrega (ej. "Practica 1", "Investigacion POO")
    private String titulo;

    // Descripcion detallada o instrucciones de la tarea
    private String descripcion;

    // Fecha limite de entrega en formato texto (ej. "15/09/2026")
    private String fechaEntrega;

    /**
     * Constructor por defecto.
     */
    public TareaAcademica() {
    }

    /**
     * Constructor completo para instanciar una tarea con todos sus atributos requeridos.
     * 
     * @param cursoAsociado Objeto Curso al que pertenece la tarea
     * @param titulo Titulo o nombre de la tarea
     * @param descripcion Detalle o enunciado de la asignacion
     * @param fechaEntrega Fecha limite para completar la tarea
     */
    public TareaAcademica(Curso cursoAsociado, String titulo, String descripcion, String fechaEntrega) {
        this.cursoAsociado = cursoAsociado;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
    }

    // ==========================================
    // METODOS GETTERS Y SETTERS (ENCAPSULAMIENTO)
    // ==========================================

    /**
     * Obtiene el curso asociado a esta tarea.
     * @return Objeto de tipo Curso
     */
    public Curso getCursoAsociado() {
        return cursoAsociado;
    }

    /**
     * Asigna o reasocia la tarea a un nuevo curso.
     * @param cursoAsociado Nuevo objeto Curso
     */
    public void setCursoAsociado(Curso cursoAsociado) {
        this.cursoAsociado = cursoAsociado;
    }

    /**
     * Obtiene el titulo de la tarea academica.
     * @return Titulo en formato String
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna el titulo de la tarea academica.
     * @param titulo Nuevo titulo de la tarea
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la descripcion o instrucciones de la tarea.
     * @return Descripcion en formato String
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Asigna la descripcion de la tarea.
     * @param descripcion Detalle o texto explicativo
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la fecha limite de entrega.
     * @return Fecha en formato String
     */
    public String getFechaEntrega() {
        return fechaEntrega;
    }

    /**
     * Asigna la fecha limite de entrega.
     * @param fechaEntrega Fecha de entrega
     */
    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    // ==========================================
    // METODOS DESCRIPTIVOS Y DE FORMATO
    // ==========================================

    /**
     * Genera un reporte detallado con toda la informacion de la tarea,
     * incluyendo el curso al que fue asignada.
     * 
     * @return Cadena con formato estructurado
     */
    public String getDetalles() {
        String infoCurso = (cursoAsociado != null) 
            ? cursoAsociado.getCodigo() + " - " + cursoAsociado.getNombre() 
            : "Sin curso asignado";

        return "------------------------------------\n"
             + " Tarea       : " + (titulo != null ? titulo : "Sin titulo") + "\n"
             + " Curso       : " + infoCurso + "\n"
             + " Entrega     : " + (fechaEntrega != null ? fechaEntrega : "No especificada") + "\n"
             + " Descripcion : " + (descripcion != null ? descripcion : "Sin descripcion") + "\n"
             + "------------------------------------";
    }

    /**
     * Representacion textual concisa de la tarea.
     * 
     * @return Cadena con el titulo y la fecha de entrega
     */
    @Override
    public String toString() {
        return titulo + " (Entrega: " + fechaEntrega + ")";
    }
}
