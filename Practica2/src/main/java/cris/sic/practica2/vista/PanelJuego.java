package cris.sic.practica2.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Panel que albergará el lienzo de juego espacial (Side-Scroller).
 * Base estructural establecida en la Fase 2 para el bucle de juego e hilos de las Fases 4 a 7.
 */
public class PanelJuego extends JPanel {

    private final VentanaPrincipal ventanaPrincipal;
    private final JButton btnVolver;

    public PanelJuego(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout());
        setBackground(TemaEspacial.FONDO_ESPACIAL);

        // Encabezado del Juego
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelHeader.setBackground(TemaEspacial.FONDO_PANEL);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TemaEspacial.AMARILLO_ORO));

        JLabel lblTitulo = new JLabel("🎮 ZONA DE BATALLA ESPACIAL");
        lblTitulo.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        lblTitulo.setForeground(TemaEspacial.AMARILLO_ORO);
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // Centro: Área de juego / Lienzo
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS));
        panelCentro.setBackground(TemaEspacial.FONDO_ESPACIAL);

        panelCentro.add(Box.createVerticalGlue());

        JLabel lblInfo = new JLabel("Lienzo Gráfico del Simulador Espacial", SwingConstants.CENTER);
        lblInfo.setAlignmentX(CENTER_ALIGNMENT);
        lblInfo.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblInfo.setForeground(TemaEspacial.TEXTO_BLANCO);
        panelCentro.add(lblInfo);

        panelCentro.add(Box.createVerticalStrut(15));

        JLabel lblDetalle = new JLabel("(El renderizado 2D, bucle de 60 FPS, proyectiles y colisiones se activarán en las Fases 4-7)", SwingConstants.CENTER);
        lblDetalle.setAlignmentX(CENTER_ALIGNMENT);
        lblDetalle.setFont(TemaEspacial.FUENTE_TEXTO);
        lblDetalle.setForeground(TemaEspacial.TEXTO_SECUNDARIO);
        panelCentro.add(lblDetalle);

        panelCentro.add(Box.createVerticalGlue());
        add(panelCentro, BorderLayout.CENTER);

        // Barra Inferior con botón de retorno
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 20));
        panelInferior.setBackground(TemaEspacial.FONDO_PANEL);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, TemaEspacial.AZUL_OSCURO));

        btnVolver = new JButton("⬅️ Volver al Menú");
        TemaEspacial.aplicarEstiloBotonVolver(btnVolver);
        btnVolver.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_MENU));
        panelInferior.add(btnVolver);

        add(panelInferior, BorderLayout.SOUTH);
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }
}
