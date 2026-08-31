package cris.sic.refugio;

import cris.sic.refugio.vista.JFrameMain;
import javax.swing.SwingUtilities;

// Punto de entrada principal de la aplicacion
public class Main {

    public static void main(String[] args) {
        // Inicializacion de la interfaz grafica en el hilo de despacho de eventos de Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrameMain frame = new JFrameMain();
                frame.setVisible(true);
            }
        });
    }
}
