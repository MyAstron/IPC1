package cris.sic.refugio;

import cris.sic.refugio.servicio.PersistenciaServicio;
import cris.sic.refugio.vista.LoginFrame;
import javax.swing.SwingUtilities;

// Punto de entrada principal de la aplicacion
public class Main {

    public static void main(String[] args) {
        // Cargar datos persistidos en memoria o inicializar valores por defecto
        PersistenciaServicio.cargarTodo();

        // Inicializacion de la interfaz grafica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            }
        });
    }
}
