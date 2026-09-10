package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.UbicacionServicio;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Panel interactivo para visualizar y gestionar la matriz de ubicaciones (5x5) del refugio con diseno ARAMS
public class UbicacionPanel extends JPanel {

    private JTextField txtFila;
    private JTextField txtColumna;
    private JTextField txtCodigoAnimal;
    private JTextField txtEstadoCelda;

    private JButton btnAsignar;
    private JButton btnLiberar;
    private JButton btnActualizar;

    private JButton[][] gridButtons = new JButton[BaseDatosMemoria.FILAS_REFUGIO][BaseDatosMemoria.COLUMNAS_REFUGIO];
    private int celdaSeleccionadaFila = -1;
    private int celdaSeleccionadaCol = -1;

    public UbicacionPanel() {
        setLayout(null);
        setBackground(ThemeARAMS.BACKGROUND);

        // 1. Panel de Control de Asignaciones (Izquierda)
        JLabel lblForm = new JLabel("GESTIONAR CELDA");
        lblForm.setFont(ThemeARAMS.FONT_HEADLINE);
        lblForm.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblForm.setBounds(20, 10, 260, 20);
        add(lblForm);

        JLabel lblFila = new JLabel("Fila:");
        lblFila.setFont(ThemeARAMS.FONT_BODY);
        lblFila.setBounds(20, 45, 110, 26);
        add(lblFila);

        txtFila = new JTextField();
        txtFila.setBounds(135, 45, 145, 26);
        txtFila.setEditable(false);
        ThemeARAMS.aplicarEstiloCampo(txtFila);
        add(txtFila);

        JLabel lblColumna = new JLabel("Columna:");
        lblColumna.setFont(ThemeARAMS.FONT_BODY);
        lblColumna.setBounds(20, 80, 110, 26);
        add(lblColumna);

        txtColumna = new JTextField();
        txtColumna.setBounds(135, 80, 145, 26);
        txtColumna.setEditable(false);
        ThemeARAMS.aplicarEstiloCampo(txtColumna);
        add(txtColumna);

        // Codigo Animal con Prefijo Estatico A-
        JLabel lblAnimal = new JLabel("Cód. Animal:");
        lblAnimal.setFont(ThemeARAMS.FONT_BODY);
        lblAnimal.setBounds(20, 115, 110, 26);
        add(lblAnimal);

        JLabel lblPrefijoA = ThemeARAMS.crearBadgePrefijo("A-");
        lblPrefijoA.setBounds(135, 115, 28, 26);
        add(lblPrefijoA);

        txtCodigoAnimal = new JTextField();
        txtCodigoAnimal.setBounds(167, 115, 113, 26);
        ThemeARAMS.aplicarEstiloCampo(txtCodigoAnimal);
        UIUtils.aplicarRestriccionNumerica(txtCodigoAnimal, 3);
        add(txtCodigoAnimal);

        JLabel lblEstado = new JLabel("Estado Celda:");
        lblEstado.setFont(ThemeARAMS.FONT_BODY);
        lblEstado.setBounds(20, 150, 110, 26);
        add(lblEstado);

        txtEstadoCelda = new JTextField();
        txtEstadoCelda.setBounds(135, 150, 145, 26);
        txtEstadoCelda.setEditable(false);
        ThemeARAMS.aplicarEstiloCampo(txtEstadoCelda);
        add(txtEstadoCelda);

        btnAsignar = new JButton("Asignar Animal a Celda");
        btnAsignar.setBounds(20, 195, 260, 32);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnAsignar);
        btnAsignar.setEnabled(false); // Inicialmente deshabilitado
        add(btnAsignar);

        btnLiberar = new JButton("Liberar Celda");
        btnLiberar.setBounds(20, 235, 260, 32);
        ThemeARAMS.aplicarEstiloBotonPeligro(btnLiberar);
        btnLiberar.setEnabled(false); // Inicialmente deshabilitado
        add(btnLiberar);

        btnActualizar = new JButton("Actualizar Vista");
        btnActualizar.setBounds(20, 275, 260, 32);
        ThemeARAMS.aplicarEstiloBotonSecundario(btnActualizar);
        add(btnActualizar);

        // Tarjeta de Leyenda
        JPanel pnlLeyenda = new JPanel();
        pnlLeyenda.setLayout(null);
        pnlLeyenda.setBackground(Color.WHITE);
        pnlLeyenda.setBorder(BorderFactory.createLineBorder(ThemeARAMS.OUTLINE, 1));
        pnlLeyenda.setBounds(20, 325, 260, 100);

        JLabel lblLeyendaTitulo = new JLabel("MAPA DE ESTADOS");
        lblLeyendaTitulo.setFont(ThemeARAMS.FONT_SMALL);
        lblLeyendaTitulo.setForeground(ThemeARAMS.TEXT_MUTED);
        lblLeyendaTitulo.setBounds(10, 8, 200, 16);
        pnlLeyenda.add(lblLeyendaTitulo);

        JLabel pillLibre = new JLabel("Libre", SwingConstants.CENTER);
        pillLibre.setFont(ThemeARAMS.FONT_BODY_BOLD);
        pillLibre.setOpaque(true);
        pillLibre.setBackground(new Color(0xD8, 0xF3, 0xDC));
        pillLibre.setForeground(ThemeARAMS.PRIMARY_DARK);
        pillLibre.setBorder(BorderFactory.createLineBorder(new Color(0x95, 0xD5, 0xB2), 1));
        pillLibre.setBounds(10, 32, 60, 22);
        pnlLeyenda.add(pillLibre);

        JLabel lblDescLibre = new JLabel("Espacio disponible para asignar");
        lblDescLibre.setFont(ThemeARAMS.FONT_SMALL);
        lblDescLibre.setForeground(ThemeARAMS.TEXT_MAIN);
        lblDescLibre.setBounds(78, 32, 175, 22);
        pnlLeyenda.add(lblDescLibre);

        JLabel pillOcupado = new JLabel("A-xxx", SwingConstants.CENTER);
        pillOcupado.setFont(ThemeARAMS.FONT_CODE);
        pillOcupado.setOpaque(true);
        pillOcupado.setBackground(ThemeARAMS.ERROR_CONTAINER);
        pillOcupado.setForeground(ThemeARAMS.ERROR);
        pillOcupado.setBorder(BorderFactory.createLineBorder(new Color(0xF4, 0xA2, 0x9D), 1));
        pillOcupado.setBounds(10, 62, 60, 22);
        pnlLeyenda.add(pillOcupado);

        JLabel lblDescOcupado = new JLabel("Animal alojado en el refugio");
        lblDescOcupado.setFont(ThemeARAMS.FONT_SMALL);
        lblDescOcupado.setForeground(ThemeARAMS.TEXT_MAIN);
        lblDescOcupado.setBounds(78, 62, 175, 22);
        pnlLeyenda.add(lblDescOcupado);

        add(pnlLeyenda);

        // 2. Cuadrícula de Ubicaciones (Derecha)
        JLabel lblCuadricula = new JLabel("DISTRIBUCIÓN ESPACIAL DEL REFUGIO (5x5)");
        lblCuadricula.setFont(ThemeARAMS.FONT_HEADLINE);
        lblCuadricula.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblCuadricula.setBounds(310, 10, 400, 20);
        add(lblCuadricula);

        int startX = 310;
        int startY = 40;
        int btnWidth = 84;
        int btnHeight = 72;
        int gap = 8;

        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                final int fila = f;
                final int col = c;

                JButton btn = new JButton();
                btn.setBounds(startX + c * (btnWidth + gap), startY + f * (btnHeight + gap), btnWidth, btnHeight);
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        seleccionarCelda(fila, col);
                    }
                });

                gridButtons[f][c] = btn;
                add(btn);
            }
        }

        // Eventos de Botones
        btnAsignar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asignar();
            }
        });

        btnLiberar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                liberar();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarCuadricula();
                limpiarSeleccion();
            }
        });

        // Inicializar la cuadrícula visual
        actualizarCuadricula();
    }

    // Modulo para actualizar el texto y color de los botones de la cuadricula segun el estado en memoria
    public void actualizarCuadricula() {
        UbicacionServicio.cargarDesdeBitacora();
        String[][] ubi = UbicacionServicio.getUbicaciones();
        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                String animal = ubi[f][c];
                JButton btn = gridButtons[f][c];
                boolean isSelected = (f == celdaSeleccionadaFila && c == celdaSeleccionadaCol);

                if (animal == null || animal.trim().isEmpty()) {
                    btn.setText("Libre");
                    btn.setFont(ThemeARAMS.FONT_BODY_BOLD);
                    btn.setBackground(new Color(0xD8, 0xF3, 0xDC)); // Menta suave
                    btn.setForeground(ThemeARAMS.PRIMARY_DARK);
                    if (isSelected) {
                        btn.setBorder(BorderFactory.createLineBorder(ThemeARAMS.PRIMARY, 3));
                    } else {
                        btn.setBorder(BorderFactory.createLineBorder(new Color(0x95, 0xD5, 0xB2), 1));
                    }
                } else {
                    btn.setText(animal);
                    btn.setFont(ThemeARAMS.FONT_CODE);
                    btn.setBackground(ThemeARAMS.ERROR_CONTAINER);   // #ffdad6
                    btn.setForeground(ThemeARAMS.ERROR);             // #ba1a1a
                    if (isSelected) {
                        btn.setBorder(BorderFactory.createLineBorder(ThemeARAMS.ERROR, 3));
                    } else {
                        btn.setBorder(BorderFactory.createLineBorder(new Color(0xF4, 0xA2, 0x9D), 1));
                    }
                }
            }
        }
    }

    // Modulo para seleccionar una celda y mostrar sus datos en el formulario
    private void seleccionarCelda(int fila, int col) {
        celdaSeleccionadaFila = fila;
        celdaSeleccionadaCol = col;

        txtFila.setText(String.valueOf(fila));
        txtColumna.setText(String.valueOf(col));

        String animal = BaseDatosMemoria.ubicacionesRefugio[fila][col];
        if (animal == null || animal.trim().isEmpty()) {
            txtEstadoCelda.setText("Libre");
            txtCodigoAnimal.setText("");
            txtCodigoAnimal.setEditable(true);
            btnAsignar.setEnabled(true);
            btnLiberar.setEnabled(false);
        } else {
            txtEstadoCelda.setText("Ocupada");
            txtCodigoAnimal.setText(UIUtils.extraerNumeroCodigo(animal, "A-"));
            txtCodigoAnimal.setEditable(false);
            btnAsignar.setEnabled(false);
            btnLiberar.setEnabled(true);
        }

        actualizarCuadricula();
    }

    // Modulo para deseleccionar y resetear los controles
    private void limpiarSeleccion() {
        celdaSeleccionadaFila = -1;
        celdaSeleccionadaCol = -1;
        txtFila.setText("");
        txtColumna.setText("");
        txtEstadoCelda.setText("");
        txtCodigoAnimal.setText("");
        txtCodigoAnimal.setEditable(true);
        btnAsignar.setEnabled(false);
        btnLiberar.setEnabled(false);
        actualizarCuadricula();
    }

    // Modulo para asignar el animal ingresado a la celda seleccionada
    private void asignar() {
        if (txtFila.getText().isEmpty() || txtColumna.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una celda de la cuadrícula haciendo clic sobre ella.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fila = Integer.parseInt(txtFila.getText());
        int col = Integer.parseInt(txtColumna.getText());
        String codigoAnimal = UIUtils.formatearCodigo("A-", txtCodigoAnimal.getText().trim());

        if (codigoAnimal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del animal a asignar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = UbicacionServicio.asignarUbicacion(fila, col, codigoAnimal, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Animal " + codigoAnimal + " asignado a la celda [" + fila + "][" + col + "] con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            seleccionarCelda(fila, col);
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Asignar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para liberar la celda seleccionada
    private void liberar() {
        if (txtFila.getText().isEmpty() || txtColumna.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una celda de la cuadrícula haciendo clic sobre ella.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fila = Integer.parseInt(txtFila.getText());
        int col = Integer.parseInt(txtColumna.getText());

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = UbicacionServicio.liberarUbicacion(fila, col, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Celda [" + fila + "][" + col + "] liberada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            seleccionarCelda(fila, col);
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Liberar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
