package cris.sic.practica2.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Panel del Menú Principal con los accesos directos requeridos por la especificación:
 * Jugar, Crear Piloto, Top de Puntajes, Historial y Salir.
 */
public class PanelMenuPrincipal extends JPanel {

    private final VentanaPrincipal ventanaPrincipal;

    private final JButton btnJugar;
    private final JButton btnCrearPiloto;
    private final JButton btnTopPuntajes;
    private final JButton btnHistorial;
    private final JButton btnSalir;

    public PanelMenuPrincipal(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout());
        setBackground(TemaEspacial.FONDO_ESPACIAL);

        // Contenedor Central con diseño en caja
        JPanel contenedorCentral = new JPanel();
        contenedorCentral.setLayout(new BoxLayout(contenedorCentral, BoxLayout.Y_AXIS));
        contenedorCentral.setOpaque(false);

        contenedorCentral.add(Box.createVerticalGlue());

        // Título del juego
        JLabel lblTitulo = new JLabel("🚀 SIMULADOR ESPACIAL 🚀", SwingConstants.CENTER);
        lblTitulo.setAlignmentX(CENTER_ALIGNMENT);
        lblTitulo.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        lblTitulo.setForeground(TemaEspacial.BORDE_CIAN);
        contenedorCentral.add(lblTitulo);

        contenedorCentral.add(Box.createVerticalStrut(10));

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Batalla Estelar 2D — USAC IPC1 Práctica 2", SwingConstants.CENTER);
        lblSubtitulo.setAlignmentX(CENTER_ALIGNMENT);
        lblSubtitulo.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblSubtitulo.setForeground(TemaEspacial.TEXTO_SECUNDARIO);
        contenedorCentral.add(lblSubtitulo);

        contenedorCentral.add(Box.createVerticalStrut(35));

        // Botón 1: JUGAR
        btnJugar = crearBotonMenu("🎮  Jugar", TemaEspacial.AZUL_PRIMARIO, TemaEspacial.TEXTO_BLANCO);
        btnJugar.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_JUEGO));
        contenedorCentral.add(btnJugar);
        contenedorCentral.add(Box.createVerticalStrut(15));

        // Botón 2: CREAR PILOTO
        btnCrearPiloto = crearBotonMenu("👨‍🚀  Crear Piloto", TemaEspacial.AZUL_OSCURO, TemaEspacial.TEXTO_BLANCO);
        btnCrearPiloto.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_CREAR_PILOTO));
        contenedorCentral.add(btnCrearPiloto);
        contenedorCentral.add(Box.createVerticalStrut(15));

        // Botón 3: TOP DE PUNTAJES
        btnTopPuntajes = crearBotonMenu("🏆  Top de Puntajes", TemaEspacial.FONDO_PANEL, TemaEspacial.AMARILLO_ORO);
        btnTopPuntajes.addActionListener(e -> {
            ventanaPrincipal.mostrarReportes(0);
        });
        contenedorCentral.add(btnTopPuntajes);
        contenedorCentral.add(Box.createVerticalStrut(15));

        // Botón 4: HISTORIAL
        btnHistorial = crearBotonMenu("📜  Historial", TemaEspacial.FONDO_PANEL, TemaEspacial.TEXTO_BLANCO);
        btnHistorial.addActionListener(e -> {
            ventanaPrincipal.mostrarReportes(1);
        });
        contenedorCentral.add(btnHistorial);
        contenedorCentral.add(Box.createVerticalStrut(15));

        // Botón 5: SALIR
        btnSalir = crearBotonMenu("❌  Salir", TemaEspacial.ROJO_PELIGRO, TemaEspacial.TEXTO_BLANCO);
        btnSalir.addActionListener(e -> accionSalir());
        contenedorCentral.add(btnSalir);

        contenedorCentral.add(Box.createVerticalGlue());

        add(contenedorCentral, BorderLayout.CENTER);

        // Pie de página
        JLabel lblFooter = new JLabel("© 2026 Universidad de San Carlos de Guatemala — IPC1", SwingConstants.CENTER);
        lblFooter.setFont(TemaEspacial.FUENTE_TEXTO);
        lblFooter.setForeground(TemaEspacial.TEXTO_SECUNDARIO);
        lblFooter.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblFooter, BorderLayout.SOUTH);
    }

    private JButton crearBotonMenu(String texto, Color fondo, Color textoColor) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(320, 48));
        btn.setPreferredSize(new Dimension(320, 48));
        TemaEspacial.aplicarEstiloBoton(btn, fondo, textoColor);
        return btn;
    }

    private void accionSalir() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea salir del Simulador Espacial?",
                "Confirmar Salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public JButton getBtnJugar() {
        return btnJugar;
    }

    public JButton getBtnCrearPiloto() {
        return btnCrearPiloto;
    }

    public JButton getBtnTopPuntajes() {
        return btnTopPuntajes;
    }

    public JButton getBtnHistorial() {
        return btnHistorial;
    }

    public JButton getBtnSalir() {
        return btnSalir;
    }
}
