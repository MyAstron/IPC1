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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import java.util.Arrays;
import java.util.List;
import cris.sic.practica2.datos.GestorDatos;
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
JPanel panelTop = crearPanelTop();
tabbedPane.addTab("Top de Puntajes", panelTop);

// Pestaña 2: Historial
JPanel panelHistorial = crearPanelHistorial();
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

    // ----- Helper: Panel Top -----
    private JPanel crearPanelTop() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaEspacial.FONDO_ESPACIAL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("🏆 TOP DE MEJORES PUNTAJES", SwingConstants.CENTER);
        lblTitle.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblTitle.setForeground(TemaEspacial.AMARILLO_ORO);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Obtener partidas y ordenar por puntaje descendente (top 10)
        List<cris.sic.practica2.modelo.Partida> topPartidas = Arrays.stream(
                GestorDatos.getInstancia().obtenerPartidas())
                .filter(p -> p != null)
                .sorted((a, b) -> Integer.compare(b.getPuntajeObtenido(), a.getPuntajeObtenido()))
                .limit(10)
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (cris.sic.practica2.modelo.Partida p : topPartidas) {
            dataset.addValue(p.getPuntajeObtenido(), "Puntaje", p.getNombrePiloto());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 10 Puntajes",
                "Piloto",
                "Puntaje",
                dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 400));
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    // ----- Helper: Panel Historial -----
    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TemaEspacial.FONDO_ESPACIAL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel("📜 HISTORIAL COMPLETO DE PARTIDAS", SwingConstants.CENTER);
        lblTitle.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblTitle.setForeground(TemaEspacial.AMARILLO_ORO);
        panel.add(lblTitle, BorderLayout.NORTH);

        // Tabla de partidas
        cris.sic.practica2.modelo.Partida[] partidas = GestorDatos.getInstancia().obtenerPartidas();
        List<cris.sic.practica2.modelo.Partida> list = Arrays.stream(partidas)
                .filter(p -> p != null)
                .toList();
        String[] columnNames = {"Piloto", "Puntaje", "Enemigos destruidos"};
        Object[][] data = new Object[list.size()][3];
        for (int i = 0; i < list.size(); i++) {
            cris.sic.practica2.modelo.Partida p = list.get(i);
            data[i][0] = p.getNombrePiloto();
            data[i][1] = p.getPuntajeObtenido();
            data[i][2] = p.getEnemigosDestruidos();
        }
        javax.swing.JTable table = new javax.swing.JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setFont(TemaEspacial.FUENTE_TEXTO);
        table.setRowHeight(22);
        table.setBackground(TemaEspacial.FONDO_ESPACIAL);
        table.setForeground(TemaEspacial.TEXTO_BLANCO);
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(table);
        scrollPane.setPreferredSize(new java.awt.Dimension(600, 180));
        panel.add(scrollPane, BorderLayout.SOUTH);

        // Gráfico de línea: evolución de puntaje
        XYSeries series = new XYSeries("Puntaje");
        for (int i = 0; i < list.size(); i++) {
            series.add(i + 1, list.get(i).getPuntajeObtenido());
        }
        XYSeriesCollection dataset = new XYSeriesCollection(series);
        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Evolución de Puntaje",
                "Partida #",
                "Puntaje",
                dataset);
        ChartPanel lineChartPanel = new ChartPanel(lineChart);
        lineChartPanel.setPreferredSize(new java.awt.Dimension(600, 300));
        panel.add(lineChartPanel, BorderLayout.CENTER);

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
