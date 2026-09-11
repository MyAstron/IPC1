package cris.sic.actividad5;

import cris.sic.actividad5.controlador.GestorAcademico;
import cris.sic.actividad5.modelo.Curso;
import cris.sic.actividad5.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la aplicacion para la Actividad 5.
 * Inicializa la aplicacion e invoca la interfaz grafica VentanaPrincipal en el hilo EDT de Swing.
 */
public class Main {

    /**
     * Metodo principal de ejecucion de la aplicacion.
     * 
     * @param args Argumentos de linea de comandos
     */
    public static void main(String[] args) {
        // Inicializar el controlador en memoria
        GestorAcademico gestor = new GestorAcademico();

        // Cursos iniciales de cortesia para facilitar la interaccion inmediata en la interfaz
        Curso c1 = new Curso("0770", "Introducción a la Programación 1", "Ing. Catedrático");
        Curso c2 = new Curso("0960", "Matemática para Computación 1", "Lic. Catedrático");
        gestor.registrarCurso(c1);
        gestor.registrarCurso(c2);

        // Lanzamiento seguro de la interfaz grafica en el Event Dispatch Thread (EDT) de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                VentanaPrincipal ventana = new VentanaPrincipal(gestor);
                ventana.setVisible(true);
            }
        });
    }
}
