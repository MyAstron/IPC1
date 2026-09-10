package cris.sic.actividad5.controlador;

import cris.sic.actividad5.modelo.Curso;
import cris.sic.actividad5.modelo.TareaAcademica;

import java.util.ArrayList;

/**
 * Controlador de negocio y gestor de colecciones para Cursos y Tareas Academicas.
 * Centraliza las operaciones CRUD en memoria mediante arreglos dinamicos (ArrayList).
 */
public class GestorAcademico {

    // Coleccion dinamica en memoria para almacenar los cursos registrados
    private ArrayList<Curso> listaCursos;

    // Coleccion dinamica en memoria para almacenar las tareas academicas creadas
    private ArrayList<TareaAcademica> listaTareas;

    /**
     * Constructor que inicializa las listas en memoria vacias.
     */
    public GestorAcademico() {
        this.listaCursos = new ArrayList<>();
        this.listaTareas = new ArrayList<>();
    }

    // ==========================================
    // OPERACIONES SOBRE CURSOS
    // ==========================================

    /**
     * Registra un nuevo curso en la coleccion verificando que el codigo no se encuentre duplicado.
     * 
     * @param curso Objeto Curso a registrar
     * @return true si se registro satisfactoriamente, false si ya existe un curso con el mismo codigo
     */
    public boolean registrarCurso(Curso curso) {
        if (curso == null || curso.getCodigo() == null || curso.getCodigo().trim().isEmpty()) {
            return false;
        }

        // Validar unicidad de codigo
        if (buscarCursoPorCodigo(curso.getCodigo().trim()) != null) {
            return false; // Codigo duplicado
        }

        return listaCursos.add(curso);
    }

    /**
     * Busca y retorna un Curso a partir de su codigo identificador (comparacion sin distincion de mayusculas).
     * 
     * @param codigo Identificador del curso a localizar
     * @return El objeto Curso coincidente o null si no se encuentra
     */
    public Curso buscarCursoPorCodigo(String codigo) {
        if (codigo == null) return null;
        for (Curso c : listaCursos) {
            if (c.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retorna la lista completa de cursos registrados en el sistema.
     * @return ArrayList con los objetos Curso
     */
    public ArrayList<Curso> getCursos() {
        return listaCursos;
    }

    /**
     * Retorna la cantidad total de cursos registrados en la lista.
     * @return Numero entero de cursos
     */
    public int getTotalCursos() {
        return listaCursos.size();
    }

    // ==========================================
    // OPERACIONES SOBRE TAREAS ACADEMICAS
    // ==========================================

    /**
     * Registra una nueva tarea academica vinculada a un curso.
     * 
     * @param tarea Objeto TareaAcademica a incorporar en la coleccion
     * @return true si la insercion fue exitosa, false en caso contrario
     */
    public boolean registrarTarea(TareaAcademica tarea) {
        if (tarea == null || tarea.getCursoAsociado() == null) {
            return false;
        }
        return listaTareas.add(tarea);
    }

    /**
     * Retorna la lista completa de tareas academicas registradas.
     * @return ArrayList con los objetos TareaAcademica
     */
    public ArrayList<TareaAcademica> getTareas() {
        return listaTareas;
    }

    /**
     * Filtra y retorna las tareas academicas que corresponden a un curso especifico.
     * 
     * @param codigoCurso Codigo del curso objetivo
     * @return ArrayList con las tareas pertenecientes al curso
     */
    public ArrayList<TareaAcademica> obtenerTareasPorCurso(String codigoCurso) {
        ArrayList<TareaAcademica> filtradas = new ArrayList<>();
        if (codigoCurso == null) return filtradas;

        for (TareaAcademica t : listaTareas) {
            if (t.getCursoAsociado() != null && t.getCursoAsociado().getCodigo().equalsIgnoreCase(codigoCurso.trim())) {
                filtradas.add(t);
            }
        }
        return filtradas;
    }

    /**
     * Retorna la cantidad total de tareas academicas registradas.
     * @return Numero entero de tareas
     */
    public int getTotalTareas() {
        return listaTareas.size();
    }

    // ==========================================
    // METODOS DE GENERACION DE REPORTES
    // ==========================================

    /**
     * Recorre las colecciones de cursos y tareas para construir un reporte
     * consolidado en formato de texto legible, ideal para presentarse en el JTextArea.
     * 
     * @return Cadena con el reporte estructurado completo
     */
    public String generarReporteConsolidado() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("         REPORTE CONSOLIDADO DE CURSOS Y TAREAS ACADEMICAS       \n");
        sb.append("=================================================================\n\n");

        sb.append("RESUMEN GENERAL:\n");
        sb.append("  * Total de Cursos Registrados : ").append(listaCursos.size()).append("\n");
        sb.append("  * Total de Tareas Asignadas   : ").append(listaTareas.size()).append("\n\n");

        sb.append("-----------------------------------------------------------------\n");
        sb.append("DETALLE DE CURSOS REGISTRADOS:\n");
        sb.append("-----------------------------------------------------------------\n");

        if (listaCursos.isEmpty()) {
            sb.append("  (No hay cursos registrados en el sistema actualmente)\n\n");
        } else {
            for (int i = 0; i < listaCursos.size(); i++) {
                Curso c = listaCursos.get(i);
                sb.append(String.format(" [%d] Codigo: %-8s | Nombre: %-25s | Tutor: %s\n", 
                    (i + 1), c.getCodigo(), c.getNombre(), c.getTutor()));
            }
            sb.append("\n");
        }

        sb.append("-----------------------------------------------------------------\n");
        sb.append("DETALLE DE TAREAS ACADEMICAS ASIGNADAS:\n");
        sb.append("-----------------------------------------------------------------\n");

        if (listaTareas.isEmpty()) {
            sb.append("  (No hay tareas registradas en el sistema actualmente)\n\n");
        } else {
            for (int i = 0; i < listaTareas.size(); i++) {
                TareaAcademica t = listaTareas.get(i);
                String codCurso = (t.getCursoAsociado() != null) ? t.getCursoAsociado().getCodigo() : "N/A";
                sb.append(String.format(" [%d] Tarea: %-20s | Curso: %-8s | Entrega: %s\n",
                    (i + 1), t.getTitulo(), codCurso, t.getFechaEntrega()));
                sb.append("     Descripcion: ").append(t.getDescripcion()).append("\n");
            }
            sb.append("\n");
        }

        sb.append("=================================================================\n");
        sb.append("                      FIN DEL REPORTE                            \n");
        sb.append("=================================================================\n");

        return sb.toString();
    }
}
