package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.PersistenciaServicio;
import cris.sic.refugio.servicio.ReporteHtmlServicio;
import cris.sic.refugio.servicio.ReporteTextoServicio;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// Panel para la generacion de reportes HTML/TXT y persistencia manual en archivos de texto con diseno ARAMS
public class ReportePanel extends JPanel {

    public ReportePanel() {
        setLayout(null);
        setBackground(ThemeARAMS.BACKGROUND);

        JLabel lblTitulo = new JLabel("MÓDULO DE REPORTES Y PERSISTENCIA");
        lblTitulo.setFont(ThemeARAMS.FONT_HEADLINE);
        lblTitulo.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblTitulo.setBounds(30, 15, 400, 25);
        add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Genere informes en formatos HTML y TXT o consolide la persistencia de las estructuras de datos.");
        lblSubtitulo.setFont(ThemeARAMS.FONT_SMALL);
        lblSubtitulo.setForeground(ThemeARAMS.TEXT_MUTED);
        lblSubtitulo.setBounds(30, 40, 700, 20);
        add(lblSubtitulo);

        JButton btnRepAnimales = new JButton("1. Reporte de Animales Rescatados (HTML / TXT)");
        btnRepAnimales.setBounds(40, 75, 335, 42);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnRepAnimales);
        add(btnRepAnimales);

        JButton btnRepAdopciones = new JButton("2. Reporte de Adopciones y Solicitudes (HTML / TXT)");
        btnRepAdopciones.setBounds(40, 130, 335, 42);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnRepAdopciones);
        add(btnRepAdopciones);

        JButton btnRepOcupacion = new JButton("3. Reporte de Ocupación del Refugio (HTML / TXT)");
        btnRepOcupacion.setBounds(40, 185, 335, 42);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnRepOcupacion);
        add(btnRepOcupacion);

        JButton btnRepAcciones = new JButton("4. Reporte Bitácora de Acciones (HTML / TXT)");
        btnRepAcciones.setBounds(400, 75, 335, 42);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnRepAcciones);
        add(btnRepAcciones);

        JButton btnRepErrores = new JButton("5. Reporte Bitácora de Errores (HTML / TXT)");
        btnRepErrores.setBounds(400, 130, 335, 42);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnRepErrores);
        add(btnRepErrores);

        JButton btnRepGeneralTxt = new JButton("6. Reporte General de Vectores y Matriz (.txt)");
        btnRepGeneralTxt.setBounds(400, 185, 335, 42);
        ThemeARAMS.aplicarEstiloBotonAprobacion(btnRepGeneralTxt);
        add(btnRepGeneralTxt);

        // Tarjeta informativa inferior
        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(null);
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.setBorder(javax.swing.BorderFactory.createLineBorder(ThemeARAMS.OUTLINE, 1));
        pnlInfo.setBounds(40, 250, 695, 120);

        JLabel lblInfoTitle = new JLabel("ℹ️ ESPECIFICACIONES DE EXPORTACIÓN Y FORMATOS");
        lblInfoTitle.setFont(ThemeARAMS.FONT_HEADLINE);
        lblInfoTitle.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblInfoTitle.setBounds(15, 12, 600, 20);
        pnlInfo.add(lblInfoTitle);

        JLabel lblInfo1 = new JLabel("• Los reportes en formato HTML se generan con diseño responsivo y se abren automáticamente en el navegador.");
        lblInfo1.setFont(ThemeARAMS.FONT_BODY);
        lblInfo1.setForeground(ThemeARAMS.TEXT_MAIN);
        lblInfo1.setBounds(15, 38, 660, 20);
        pnlInfo.add(lblInfo1);

        JLabel lblInfo2 = new JLabel("• De forma complementaria, cada reporte HTML produce una réplica exacta en archivo de texto plano (.txt).");
        lblInfo2.setFont(ThemeARAMS.FONT_BODY);
        lblInfo2.setForeground(ThemeARAMS.TEXT_MAIN);
        lblInfo2.setBounds(15, 62, 660, 20);
        pnlInfo.add(lblInfo2);

        JLabel lblInfo3 = new JLabel("• La opción '6. Reporte General' sincroniza y consolida todos los vectores y matrices del sistema a disco.");
        lblInfo3.setFont(ThemeARAMS.FONT_BODY);
        lblInfo3.setForeground(ThemeARAMS.TEXT_MAIN);
        lblInfo3.setBounds(15, 86, 660, 20);
        pnlInfo.add(lblInfo3);

        add(pnlInfo);

        // Eventos
        btnRepAnimales.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = ReporteHtmlServicio.generarReporteAnimales(user);
                procesarResultadoReporte(res);
            }
        });

        btnRepAdopciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = ReporteHtmlServicio.generarReporteAdopciones(user);
                procesarResultadoReporte(res);
            }
        });

        btnRepOcupacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = ReporteHtmlServicio.generarReporteOcupacion(user);
                procesarResultadoReporte(res);
            }
        });

        btnRepAcciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = ReporteHtmlServicio.generarReporteBitacoraAcciones(user);
                procesarResultadoReporte(res);
            }
        });

        btnRepErrores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = ReporteHtmlServicio.generarReporteBitacoraErrores(user);
                procesarResultadoReporte(res);
            }
        });

        btnRepGeneralTxt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Usuario u = AutenticacionServicio.getUsuarioLogueado();
                String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";
                String res = PersistenciaServicio.guardarTodo(user);
                if (res.equals("SUCCESS")) {
                    String archivo = "reporte_general_vectores_matriz.txt";
                    int opt = JOptionPane.showConfirmDialog(ReportePanel.this, 
                        "Todos los datos han sido guardados exitosamente en sus archivos de texto (.txt)\n" +
                        "y se ha generado el reporte consolidado: " + archivo + "\n\n" +
                        "¿Desea abrir el reporte en su editor de texto predeterminado?", 
                        "Reporte Consolidado y Persistencia Generada", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (opt == JOptionPane.YES_OPTION) {
                        try {
                            File txtFile = new File(archivo);
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                                Desktop.getDesktop().open(txtFile);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(ReportePanel.this, "No se pudo abrir automáticamente el archivo: " + ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(ReportePanel.this, res, "Error al Guardar", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // Modulo para procesar el resultado de la generacion de un reporte HTML y TXT
    private void procesarResultadoReporte(String resultado) {
        if (resultado.startsWith("SUCCESS|")) {
            String archivo = resultado.substring(8);
            String archivoTxt = archivo.replace(".html", ".txt");
            int opt = JOptionPane.showConfirmDialog(this, 
                "Reportes generados con éxito:\n- " + archivo + " (HTML)\n- " + archivoTxt + " (TXT)\n\n¿Desea abrir el reporte HTML en su navegador predeterminado?", 
                "Reportes Generados", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                try {
                    File htmlFile = new File(archivo);
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(htmlFile);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo abrir automáticamente el archivo en el navegador: " + ex.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
