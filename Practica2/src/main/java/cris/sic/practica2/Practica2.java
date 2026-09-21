package cris.sic.practica2;

import cris.sic.practica2.vista.VentanaPrincipal;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada principal de la Práctica 2: Simulador Espacial.
 * Inicializa y despliega la Ventana Principal de Swing con navegación CardLayout.
 */
public class Practica2 {

    public static void main(String[] args) {
        // Inicializar la interfaz gráfica en el hilo despachador de eventos (EDT)
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
