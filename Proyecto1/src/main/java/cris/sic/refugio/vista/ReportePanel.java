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
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// Panel para la generacion de reportes HTML/TXT y persistencia manual en archivos de texto
public class ReportePanel extends JPanel {

    public ReportePanel() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("MÓDULO DE REPORTES Y PERSISTENCIA");
        lblTitulo.setBounds(30, 20, 400, 25);
        add(lblTitulo);

        JButton btnRepAnimales = new JButton("1. Reporte de Animales Rescatados (HTML / TXT)");
        btnRepAnimales.setBounds(50, 70, 320, 40);
        add(btnRepAnimales);

        JButton btnRepAdopciones = new JButton("2. Reporte de Adopciones y Solicitudes (HTML / TXT)");
        btnRepAdopciones.setBounds(50, 130, 320, 40);
        add(btnRepAdopciones);

        JButton btnRepOcupacion = new JButton("3. Reporte de Ocupación del Refugio (HTML / TXT)");
        btnRepOcupacion.setBounds(50, 190, 320, 40);
        add(btnRepOcupacion);

        JButton btnRepAcciones = new JButton("4. Reporte Bitácora de Acciones (HTML / TXT)");
        btnRepAcciones.setBounds(400, 70, 320, 40);
        add(btnRepAcciones);

        JButton btnRepErrores = new JButton("5. Reporte Bitácora de Errores (HTML / TXT)");
        btnRepErrores.setBounds(400, 130, 320, 40);
        add(btnRepErrores);

        JButton btnRepGeneralTxt = new JButton("6. Reporte General de Vectores y Matriz (.txt)");
        btnRepGeneralTxt.setBounds(400, 190, 320, 40);
        add(btnRepGeneralTxt);

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
