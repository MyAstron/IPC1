package cris.sic.actividad5.modelo;

/**
 * Clase que representa la entidad Curso en el dominio academico.
 * Aplica el principio de encapsulamiento con atributos privados y metodos de acceso.
 */
public class Curso {

    // Identificador unico o codigo del curso (ej. "0770", "IPC1")
    private String codigo;

    // Nombre descriptivo de la materia o asignatura (ej. "Introduccion a la Programacion")
    private String nombre;

    // Nombre completo del catedratico o tutor a cargo del curso
    private String tutor;

    /**
     * Constructor por defecto sin parametros.
     */
    public Curso() {
    }

    /**
     * Constructor con todos los parametros para inicializar el objeto Curso.
     * 
     * @param codigo Identificador unico del curso
     * @param nombre Nombre descriptivo del curso
     * @param tutor Nombre del docente o tutor asignado
     */
    public Curso(String codigo, String nombre, String tutor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tutor = tutor;
    }

    // ==========================================
    // METODOS GETTERS Y SETTERS (ENCAPSULAMIENTO)
    // ==========================================

    /**
     * Obtiene el codigo del curso.
     * @return Codigo unico en formato String
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Asigna o actualiza el codigo del curso.
     * @param codigo Nuevo codigo del curso
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Obtiene el nombre del curso.
     * @return Nombre del curso
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna o actualiza el nombre del curso.
     * @param nombre Nuevo nombre del curso
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el tutor asignado al curso.
     * @return Nombre del tutor o catedratico
     */
    public String getTutor() {
        return tutor;
    }

    /**
     * Asigna o actualiza el tutor asignado al curso.
     * @param tutor Nuevo tutor a cargo
     */
    public void setTutor(String tutor) {
        this.tutor = tutor;
    }

    // ==========================================
    // METODOS DESCRIPTIVOS Y DE FORMATO
    // ==========================================

    /**
     * Retorna una ficha estilizada con los datos completos del curso.
     * Utilizado para reportes y visualizacion en consola o interfaces graficas.
     * 
     * @return Cadena formateada con la informacion del curso
     */
    public String getInformacion() {
        return "====================================\n"
             + " Codigo : " + (codigo != null ? codigo : "N/A") + "\n"
             + " Curso  : " + (nombre != null ? nombre : "N/A") + "\n"
             + " Tutor  : " + (tutor != null ? tutor : "N/A") + "\n"
             + "====================================";
    }

    /**
     * Representacion amigable en cadena de texto del objeto Curso.
     * Ideal para ser renderizado dentro de componentes Swing como JComboBox o JList.
     * 
     * @return Cadena descriptiva: "CODIGO - NOMBRE"
     */
    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
