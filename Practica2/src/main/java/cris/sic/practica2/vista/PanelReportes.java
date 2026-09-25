package cris.sic.practica2.vista;

import cris.sic.practica2.datos.GestorArchivos;
import cris.sic.practica2.datos.GestorDatos;
import cris.sic.practica2.modelo.Partida;
import cris.sic.practica2.modelo.Piloto;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * Panel para la visualización de reportes, estadísticas, historial y gráficas JFreeChart.
 * Fases 8 y 9:
 * - Subfase 8.2: Tabla interactiva JTable con el historial de partidas del vector en GestorDatos.
 * - Subfase 8.3: Consulta y ordenamiento del Top de mejores puntajes.
 * - Subfase 8.4: Gráfica de barras estilizada con JFreeChart para el Top 10.
 * - Subfase 9.1: Botón de Exportación de Reportes a HTML / PDF.
 * - Subfase 9.2: Exportación de la gráfica JFreeChart a imagen PNG en disco local.
 * - Subfase 9.3: Generación del archivo reporte_partidas.html con diseño espacial en CSS y tablas completas.
 * - Subfase 9.4: Apertura automática en el navegador web e instrucciones para guardar como PDF (Ctrl + P).
 */
public class PanelReportes extends JPanel {

    private final VentanaPrincipal ventanaPrincipal;
    private final JTabbedPane tabbedPane;
    private final JButton btnVolver;
    private final JButton btnExportar;

    // Contenedores dinámicos para actualización en vivo
    private JPanel panelTopContenedor;
    private JPanel panelHistorialContenedor;
    private JFreeChart graficoTopActual;
    private JFreeChart graficoHistorialActual;

    public PanelReportes(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout());
        setBackground(TemaEspacial.FONDO_ESPACIAL);

        // ==========================================
        // ENCABEZADO SUPERIOR
        // ==========================================
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelHeader.setBackground(TemaEspacial.FONDO_PANEL);
        panelHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, TemaEspacial.BORDE_CIAN));

        JLabel lblTitulo = new JLabel("📊 REPORTES Y SALÓN DE LA FAMA");
        lblTitulo.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        lblTitulo.setForeground(TemaEspacial.BORDE_CIAN);
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // ==========================================
        // PESTAÑAS (Top de Puntajes e Historial)
        // ==========================================
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(TemaEspacial.FONDO_PANEL);
        tabbedPane.setForeground(TemaEspacial.TEXTO_BLANCO);
        tabbedPane.setFont(TemaEspacial.FUENTE_BOTON);

        panelTopContenedor = new JPanel(new BorderLayout());
        panelTopContenedor.setBackground(TemaEspacial.FONDO_ESPACIAL);
        tabbedPane.addTab("🏆 Top de Puntajes", panelTopContenedor);

        panelHistorialContenedor = new JPanel(new BorderLayout());
        panelHistorialContenedor.setBackground(TemaEspacial.FONDO_ESPACIAL);
        tabbedPane.addTab("📜 Historial de Partidas", panelHistorialContenedor);

        add(tabbedPane, BorderLayout.CENTER);

        // ==========================================
        // BARRA INFERIOR DE ACCIONES (Fase 9)
        // ==========================================
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 15));
        panelInferior.setBackground(TemaEspacial.FONDO_PANEL);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, TemaEspacial.AZUL_OSCURO));

        btnExportar = new JButton("📄 Exportar Reporte HTML / PDF");
        TemaEspacial.aplicarEstiloBoton(btnExportar, TemaEspacial.AZUL_PRIMARIO, TemaEspacial.TEXTO_BLANCO);
        btnExportar.setPreferredSize(new Dimension(270, 42));
        btnExportar.setFont(TemaEspacial.FUENTE_BOTON);
        btnExportar.addActionListener(e -> exportarReporteHTML());
        panelInferior.add(btnExportar);

        btnVolver = new JButton("⬅️ Volver al Menú Principal");
        TemaEspacial.aplicarEstiloBotonVolver(btnVolver);
        btnVolver.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_MENU));
        panelInferior.add(btnVolver);

        add(panelInferior, BorderLayout.SOUTH);

        // Inicializar datos en vivo
        actualizarReportes();
    }

    /**
     * Reconstruye las vistas del Top 10 y del Historial de partidas con los datos más recientes en memoria.
     */
    public void actualizarReportes() {
        actualizarPanelTop();
        actualizarPanelHistorial();
        revalidate();
        repaint();
    }

    // ==========================================
    // SUBFASE 8.3 & 8.4: PESTAÑA TOP DE PUNTAJES
    // ==========================================
    private void actualizarPanelTop() {
        panelTopContenedor.removeAll();
        panelTopContenedor.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("🏆 TOP 10 DE MEJORES PUNTAJES ESPACIALES", SwingConstants.CENTER);
        lblTitle.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblTitle.setForeground(TemaEspacial.AMARILLO_ORO);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelTopContenedor.add(lblTitle, BorderLayout.NORTH);

        Partida[] partidas = GestorDatos.getInstancia().obtenerPartidas();
        List<Partida> topPartidas = Arrays.stream(partidas)
                .filter(p -> p != null)
                .sorted((a, b) -> Integer.compare(b.getPuntajeObtenido(), a.getPuntajeObtenido()))
                .limit(10)
                .toList();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (topPartidas.isEmpty()) {
            // Si no hay partidas registradas todavía, mostrar barras de demostración o mensaje vacío
            dataset.addValue(0, "Puntaje", "Sin partidas");
        } else {
            for (Partida p : topPartidas) {
                dataset.addValue(p.getPuntajeObtenido(), "Puntaje", p.getNombrePiloto());
            }
        }

        graficoTopActual = ChartFactory.createBarChart(
                "Top 10 Mejores Puntajes",
                "Piloto",
                "Puntaje Obtenido",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Personalizar estética espacial en JFreeChart
        estilizarGraficoBarras(graficoTopActual);

        ChartPanel chartPanel = new ChartPanel(graficoTopActual);
        chartPanel.setBackground(TemaEspacial.FONDO_ESPACIAL);
        chartPanel.setBorder(BorderFactory.createLineBorder(TemaEspacial.AZUL_OSCURO, 1));
        panelTopContenedor.add(chartPanel, BorderLayout.CENTER);
    }

    // ==========================================
    // SUBFASE 8.2: PESTAÑA HISTORIAL DE PARTIDAS
    // ==========================================
    private void actualizarPanelHistorial() {
        panelHistorialContenedor.removeAll();
        panelHistorialContenedor.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("📜 HISTORIAL Y EVOLUCIÓN DE PARTIDAS", SwingConstants.CENTER);
        lblTitle.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblTitle.setForeground(TemaEspacial.AMARILLO_ORO);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panelHistorialContenedor.add(lblTitle, BorderLayout.NORTH);

        Partida[] partidas = GestorDatos.getInstancia().obtenerPartidas();
        List<Partida> list = Arrays.stream(partidas)
                .filter(p -> p != null)
                .toList();

        // 1. Tabla de partidas guardadas
        String[] columnNames = {"#", "Piloto", "Puntaje Obtenido", "Enemigos Destruidos", "Fecha / Hora"};
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            Partida p = list.get(i);
            data[i][0] = (i + 1);
            data[i][1] = p.getNombrePiloto();
            data[i][2] = p.getPuntajeObtenido() + " pts";
            data[i][3] = p.getEnemigosDestruidos();
            data[i][4] = p.getFecha();
        }

        DefaultTableModel modelo = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(modelo);
        table.setFillsViewportHeight(true);
        table.setFont(TemaEspacial.FUENTE_TEXTO);
        table.setRowHeight(24);
        table.setBackground(TemaEspacial.FONDO_PANEL);
        table.setForeground(TemaEspacial.TEXTO_BLANCO);
        table.setGridColor(TemaEspacial.AZUL_OSCURO);

        JTableHeader header = table.getTableHeader();
        header.setBackground(TemaEspacial.AZUL_OSCURO);
        header.setForeground(TemaEspacial.BORDE_CIAN);
        header.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 1) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 160));
        scrollPane.getViewport().setBackground(TemaEspacial.FONDO_PANEL);
        scrollPane.setBorder(BorderFactory.createLineBorder(TemaEspacial.AZUL_OSCURO, 1));
        panelHistorialContenedor.add(scrollPane, BorderLayout.SOUTH);

        // 2. Gráfico de evolución de puntajes
        XYSeries series = new XYSeries("Evolución de Puntaje");
        if (list.isEmpty()) {
            series.add(1, 0);
        } else {
            for (int i = 0; i < list.size(); i++) {
                series.add(i + 1, list.get(i).getPuntajeObtenido());
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection(series);
        graficoHistorialActual = ChartFactory.createXYLineChart(
                "Evolución de Puntajes por Partida",
                "Partida N°",
                "Puntaje Obtenido",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        estilizarGraficoLineas(graficoHistorialActual);

        ChartPanel lineChartPanel = new ChartPanel(graficoHistorialActual);
        lineChartPanel.setBackground(TemaEspacial.FONDO_ESPACIAL);
        lineChartPanel.setBorder(BorderFactory.createLineBorder(TemaEspacial.AZUL_OSCURO, 1));
        panelHistorialContenedor.add(lineChartPanel, BorderLayout.CENTER);
    }

    private void estilizarGraficoBarras(JFreeChart chart) {
        chart.setBackgroundPaint(TemaEspacial.FONDO_ESPACIAL);
        chart.getTitle().setPaint(TemaEspacial.BORDE_CIAN);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(TemaEspacial.FONDO_PANEL);
        plot.setDomainGridlinePaint(TemaEspacial.AZUL_OSCURO);
        plot.setRangeGridlinePaint(TemaEspacial.AZUL_OSCURO);
        plot.getDomainAxis().setLabelPaint(TemaEspacial.AMARILLO_ORO);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(TemaEspacial.AMARILLO_ORO);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, TemaEspacial.AZUL_PRIMARIO);
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, TemaEspacial.BORDE_CIAN);
    }

    private void estilizarGraficoLineas(JFreeChart chart) {
        chart.setBackgroundPaint(TemaEspacial.FONDO_ESPACIAL);
        chart.getTitle().setPaint(TemaEspacial.BORDE_CIAN);
        chart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 16));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(TemaEspacial.FONDO_PANEL);
        plot.setDomainGridlinePaint(TemaEspacial.AZUL_OSCURO);
        plot.setRangeGridlinePaint(TemaEspacial.AZUL_OSCURO);
        plot.getDomainAxis().setLabelPaint(TemaEspacial.AMARILLO_ORO);
        plot.getDomainAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(TemaEspacial.AMARILLO_ORO);
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, TemaEspacial.AMARILLO_ORO);
        plot.setRenderer(renderer);
    }

    // ==========================================
    // FASE 9: EXPORTACIÓN DE REPORTES A ARCHIVOS (HTML / PDF)
    // ==========================================

    /**
     * Ejecuta el flujo completo de la Fase 9:
     * 1. Exporta la gráfica de JFreeChart a un archivo .png local con ImageIO.
     * 2. Genera el archivo reporte_partidas.html usando java.io.* (PrintWriter / FileWriter).
     * 3. Abre el archivo en el navegador predeterminado para su visualización o guardado como PDF (Ctrl+P).
     */
    public void exportarReporteHTML() {
        Partida[] partidas = GestorDatos.getInstancia().obtenerPartidas();
        Piloto[] pilotos = GestorDatos.getInstancia().obtenerPilotos();

        File archivoImagen = GestorArchivos.obtenerArchivo("reporte_grafica.png");
        File archivoHtml = GestorArchivos.obtenerArchivo("reporte_partidas.html");
        File archivoHtmlSimple = GestorArchivos.obtenerArchivo("reporte.html");

        try {
            // Subfase 9.2: Exportar la gráfica de JFreeChart como PNG
            JFreeChart chartAExportar = (graficoTopActual != null) ? graficoTopActual : graficoHistorialActual;
            if (chartAExportar != null) {
                BufferedImage imagen = chartAExportar.createBufferedImage(800, 420);
                ImageIO.write(imagen, "PNG", archivoImagen);
                System.out.println("[REPORTE] Gráfica exportada exitosamente a: " + archivoImagen.getAbsolutePath());
            }

            // Subfase 9.3: Generar archivo HTML con PrintWriter / FileWriter
            try (PrintWriter writer = new PrintWriter(new FileWriter(archivoHtml))) {
                writer.println("<!DOCTYPE html>");
                writer.println("<html lang='es'>");
                writer.println("<head>");
                writer.println("  <meta charset='UTF-8'>");
                writer.println("  <title>Simulador Espacial - Reporte General</title>");
                writer.println("  <style>");
                writer.println("    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #0B0E17; color: #FFFFFF; margin: 0; padding: 25px; }");
                writer.println("    .container { max-width: 1000px; margin: 0 auto; background-color: #161B26; padding: 30px; border-radius: 12px; border: 1px solid #00E5FF; box-shadow: 0 0 25px rgba(0,229,255,0.2); }");
                writer.println("    h1 { color: #00E5FF; text-align: center; margin-top: 0; border-bottom: 2px solid #00E5FF; padding-bottom: 12px; }");
                writer.println("    h2 { color: #FFD700; margin-top: 30px; border-bottom: 1px solid #0D47A1; padding-bottom: 6px; }");
                writer.println("    .meta { text-align: center; color: #B0BEC5; font-size: 14px; margin-bottom: 25px; }");
                writer.println("    table { width: 100%; border-collapse: collapse; margin-top: 15px; margin-bottom: 25px; }");
                writer.println("    th { background-color: #0D47A1; color: #00E5FF; padding: 12px; text-align: left; font-size: 14px; border: 1px solid #1E88E5; }");
                writer.println("    td { padding: 10px 12px; border: 1px solid #232D3F; font-size: 13px; }");
                writer.println("    tr:nth-child(even) { background-color: #0E1422; }");
                writer.println("    tr:hover { background-color: #1C2638; }");
                writer.println("    .chart-box { text-align: center; margin: 30px 0; background: #0B0E17; padding: 20px; border-radius: 8px; border: 1px solid #1E88E5; }");
                writer.println("    .chart-box img { max-width: 100%; height: auto; border-radius: 6px; }");
                writer.println("    .footer { text-align: center; color: #B0BEC5; font-size: 12px; margin-top: 40px; padding-top: 15px; border-top: 1px solid #232D3F; }");
                writer.println("    .pdf-tip { background: #0D47A1; color: #FFD700; padding: 12px; border-radius: 8px; text-align: center; margin-top: 20px; font-weight: bold; }");
                writer.println("  </style>");
                writer.println("</head>");
                writer.println("<body>");
                writer.println("  <div class='container'>");
                writer.println("    <h1>🚀 SIMULADOR DE VUELO ESPACIAL — REPORTE GENERAL</h1>");
                writer.println("    <div class='meta'>Universidad de San Carlos de Guatemala — IPC1 | Práctica 2</div>");

                // Sección 1: Top Pilotos y Estadísticas
                writer.println("    <h2>🏆 Top de Pilotos y Récords Máximos</h2>");
                writer.println("    <table>");
                writer.println("      <thead><tr><th>#</th><th>Piloto</th><th>Tipo de Nave</th><th>Dificultad</th><th>Punteo Máximo</th></tr></thead>");
                writer.println("      <tbody>");
                if (pilotos == null || pilotos.length == 0) {
                    writer.println("        <tr><td colspan='5' style='text-align:center;'>No hay pilotos registrados.</td></tr>");
                } else {
                    List<Piloto> topPilotos = Arrays.stream(pilotos)
                            .filter(p -> p != null)
                            .sorted((a, b) -> Integer.compare(b.getPunteoMaximo(), a.getPunteoMaximo()))
                            .toList();
                    for (int i = 0; i < topPilotos.size(); i++) {
                        Piloto p = topPilotos.get(i);
                        writer.printf("        <tr><td>%d</td><td><b>%s</b></td><td>%s</td><td>%s</td><td><b style='color:#00E676;'>%d pts</b></td></tr>%n",
                                (i + 1), p.getNombre(), p.getTipoNave(), p.getNivelDificultad(), p.getPunteoMaximo());
                    }
                }
                writer.println("      </tbody>");
                writer.println("    </table>");

                // Sección 2: Gráfica incrustada
                if (archivoImagen.exists()) {
                    writer.println("    <h2>📈 Gráfica Estadística de Rendimiento</h2>");
                    writer.println("    <div class='chart-box'>");
                    writer.println("      <img src='" + archivoImagen.getName() + "' alt='Gráfica de Rendimiento Espacial'>");
                    writer.println("    </div>");
                }

                // Sección 3: Historial completo de partidas
                writer.println("    <h2>📜 Historial Completo de Misiones</h2>");
                writer.println("    <table>");
                writer.println("      <thead><tr><th>#</th><th>Piloto</th><th>Puntaje Obtenido</th><th>Bajas Enemigas</th><th>Fecha y Hora</th></tr></thead>");
                writer.println("      <tbody>");
                if (partidas == null || partidas.length == 0) {
                    writer.println("        <tr><td colspan='5' style='text-align:center;'>No se han registrado partidas completadas.</td></tr>");
                } else {
                    for (int i = 0; i < partidas.length; i++) {
                        Partida p = partidas[i];
                        if (p == null) continue;
                        writer.printf("        <tr><td>%d</td><td>%s</td><td><b>%d pts</b></td><td>%d</td><td>%s</td></tr>%n",
                                (i + 1), p.getNombrePiloto(), p.getPuntajeObtenido(), p.getEnemigosDestruidos(), p.getFecha());
                    }
                }
                writer.println("      </tbody>");
                writer.println("    </table>");

                // Instrucción de guardado PDF
                writer.println("    <div class='pdf-tip'>💡 Para guardar este reporte en PDF: Presione <u>Ctrl + P</u> (o Archivo &gt; Imprimir) en su navegador y seleccione la opción 'Guardar como PDF'.</div>");

                writer.println("    <div class='footer'>Reporte generado automáticamente por el Simulador Espacial — IPC1 2026.</div>");
                writer.println("  </div>");
                writer.println("</body>");
                writer.println("</html>");
            }

            try {
                java.nio.file.Files.copy(archivoHtml.toPath(), archivoHtmlSimple.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception ignored) {}

            System.out.println("[REPORTE] Reporte HTML generado exitosamente en: " + archivoHtml.getAbsolutePath());

            // Subfase 9.4: Apertura automática en el navegador web
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(archivoHtml.toURI());
            } else if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(archivoHtml);
            }

            // Notificación modal al usuario con JOptionPane
            JOptionPane.showMessageDialog(
                    this,
                    "¡Reporte exportado exitosamente!\n\n" +
                            "📁 Ubicación: " + archivoHtml.getName() + "\n" +
                            "🖼️ Gráfica: " + archivoImagen.getName() + "\n\n" +
                            "💡 Se ha abierto el reporte en su navegador.\n" +
                            "Para guardarlo como PDF, presione Ctrl + P y elija 'Guardar como PDF'.",
                    "Exportación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException ex) {
            System.err.println("[REPORTE] Error al exportar reporte: " + ex.getMessage());
            JOptionPane.showMessageDialog(
                    this,
                    "Error al exportar el reporte a HTML/PDF:\n" + ex.getMessage(),
                    "Error de Exportación",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Permite activar directamente la pestaña deseada al entrar desde el menú principal.
     *
     * @param index 0 para Top de Puntajes, 1 para Historial
     */
    public void seleccionarPestana(int index) {
        actualizarReportes();
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

    public JButton getBtnExportar() {
        return btnExportar;
    }
}
