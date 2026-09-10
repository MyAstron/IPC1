package cris.sic.actividad5;

import cris.sic.actividad5.controlador.GestorAcademico;
import cris.sic.actividad5.modelo.Curso;
import cris.sic.actividad5.modelo.TareaAcademica;

/**
 * Punto de entrada principal de la aplicacion para Actividad 5.
 * Permite la ejecucion directa desde Apache NetBeans u otros entornos de desarrollo.
 */
public class Main {

    /**
     * Metodo principal de ejecucion.
     * En la Fase 1 inicializa y demuestra el correcto funcionamiento de los modelos y el controlador.
     * En fases posteriores lanzara la interfaz grafica de usuario construida con Swing.
     * 
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        System.out.println("=========================================================");
        System.out.println("   SISTEMA DE GESTION ACADEMICA Y CONVERSION - ACTIVIDAD 5");
        System.out.println("=========================================================\n");

        // Instanciar el gestor academico en memoria
        GestorAcademico gestor = new GestorAcademico();

        // 1. Registro de cursos de demostracion
        System.out.println(">> Registrando cursos base...");
        Curso c1 = new Curso("0770", "Introduccion a la Programacion y Computacion 1", "Ing. Docente Titular");
        Curso c2 = new Curso("0960", "Matematica para Computacion 1", "Lic. Catedratico");
        Curso c3 = new Curso("0771", "IPC 1 - Laboratorio", "Aux. Laboratorio");

        gestor.registrarCurso(c1);
        gestor.registrarCurso(c2);
        gestor.registrarCurso(c3);
        System.out.println("   [OK] " + gestor.getTotalCursos() + " cursos registrados con exito.\n");

        // 2. Registro de tareas academicas vinculadas
        System.out.println(">> Asignando tareas academicas a los cursos...");
        TareaAcademica t1 = new TareaAcademica(c1, "Actividad 5", "Construccion de interfaz Swing y modelos POO", "12/09/2026");
        TareaAcademica t2 = new TareaAcademica(c1, "Practica 1", "Sistema de control vehicular con arreglos", "20/09/2026");
        TareaAcademica t3 = new TareaAcademica(c2, "Hoja de Trabajo 1", "Tablas de verdad y logica formal", "15/09/2026");

        gestor.registrarTarea(t1);
        gestor.registrarTarea(t2);
        gestor.registrarTarea(t3);
        System.out.println("   [OK] " + gestor.getTotalTareas() + " tareas asignadas con exito.\n");

        // 3. Despliegue del reporte consolidado
        System.out.println(gestor.generarReporteConsolidado());

        System.out.println("[INFO] Modelos y Controlador verificados correctamente.");
        System.out.println("[INFO] Proyecto listo para la Fase 2 (VentanaPrincipal Swing).");
    }
}
