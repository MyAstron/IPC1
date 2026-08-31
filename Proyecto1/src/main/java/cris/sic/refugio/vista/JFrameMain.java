package cris.sic.refugio.vista;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;

// Ventana principal del sistema, configurada manualmente utilizando Java Swing
public class JFrameMain extends JFrame {

    public JFrameMain() {
        // Configuracion basica del JFrame principal
        setTitle("Sistema de Gestion de Refugio de Animales");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Mensaje de bienvenida inicial para validar el esqueleto de la ventana principal
        JLabel lblBienvenida = new JLabel("Bienvenido al Refugio de Animales - Base Estructural", SwingConstants.CENTER);
        add(lblBienvenida, BorderLayout.CENTER);
    }
}
