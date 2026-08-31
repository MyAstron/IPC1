package cris.sic.refugio;

import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.vista.LoginFrame;
import javax.swing.SwingUtilities;

// Punto de entrada principal de la aplicacion
public class Main {

    public static void main(String[] args) {
        // Inicializar usuarios predeterminados en memoria al iniciar la aplicacion
        AutenticacionServicio.inicializarUsuariosPorDefecto();

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
