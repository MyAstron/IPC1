package cris.sic.practica2.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

/**
 * Panel para la visualización de reportes, estadísticas, historial y gráficas JFreeChart.
 * Base estructural establecida en la Fase 2 para albergar tablas y gráficas en las Fases 8 y 9.
 */
public class PanelReportes extends JPanel {

    private final VentanaPrincipal ventanaPrincipal;
    private final JTabbedPane tabbedPane;
    private final JButton btnVolver;

    public PanelReportes(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout());
        setBackground(TemaEspacial.FONDO_ESPACIAL);

        // Encabezado
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panelHeader.setBackground(TemaEspacial.FONDO_PANEL);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TemaEspacial.BORDE_CIAN));

        JLabel lblTitulo = new JLabel("📊 REPORTES Y SALÓN DE LA FAMA");
        lblTitulo.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        lblTitulo.setForeground(TemaEspacial.BORDE_CIAN);
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // Pestañas para Top de Puntajes e Historial
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(TemaEspacial.FONDO_PANEL);
        tabbedPane.setForeground(TemaEspacial.TEXTO_BLANCO);
        tabbedPane.setFont(TemaEspacial.FUENTE_BOTON);

        // Pestaña 1: Top de Puntajes
        JPanel panelTop = crearPanelPestana("🏆 TOP DE MEJORES PUNTAJES",
                "(La tabla de líderes y la gráfica estadística de JFreeChart se integrarán en la Fase 8)");
        tabbedPane.addTab("Top de Puntajes", panelTop);

        // Pestaña 2: Historial
        JPanel panelHistorial = crearPanelPestana("📜 HISTORIAL COMPLETO DE PARTIDAS",
                "(El registro detallado de partidas jugadas y exportación a HTML/PDF se integrará en las Fases 8 y 9)");
        tabbedPane.addTab("Historial de Partidas", panelHistorial);

        add(tabbedPane, BorderLayout.CENTER);

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

    private JPanel crearPanelPestana(String subtitulo, String descripcion) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(TemaEspacial.FONDO_ESPACIAL);

        panel.add(Box.createVerticalGlue());

        JLabel lblSub = new JLabel(subtitulo, SwingConstants.CENTER);
        lblSub.setAlignmentX(CENTER_ALIGNMENT);
        lblSub.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblSub.setForeground(TemaEspacial.AMARILLO_ORO);
        panel.add(lblSub);

        panel.add(Box.createVerticalStrut(15));

        JLabel lblDesc = new JLabel(descripcion, SwingConstants.CENTER);
        lblDesc.setAlignmentX(CENTER_ALIGNMENT);
        lblDesc.setFont(TemaEspacial.FUENTE_TEXTO);
        lblDesc.setForeground(TemaEspacial.TEXTO_SECUNDARIO);
        panel.add(lblDesc);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    /**
     * Permite activar directamente la pestaña deseada al entrar desde el menú principal.
     *
     * @param index 0 para Top de Puntajes, 1 para Historial
     */
    public void seleccionarPestana(int index) {
        if (index >= 0 && index < tabbedPane.getTabCount()) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }
}
