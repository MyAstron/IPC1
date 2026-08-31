package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.UbicacionServicio;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Panel interactivo para visualizar y gestionar la matriz de ubicaciones (5x5) del refugio
public class UbicacionPanel extends JPanel {

    private JTextField txtFila;
    private JTextField txtColumna;
    private JTextField txtCodigoAnimal;
    private JTextField txtEstadoCelda;

    private JButton btnAsignar;
    private JButton btnLiberar;
    private JButton btnActualizar;

    private JButton[][] gridButtons = new JButton[BaseDatosMemoria.FILAS_REFUGIO][BaseDatosMemoria.COLUMNAS_REFUGIO];

    public UbicacionPanel() {
        setLayout(null);

        // 1. Panel de Control de Asignaciones (Izquierda)
        JLabel lblForm = new JLabel("GESTIONAR CELDA");
        lblForm.setBounds(20, 10, 200, 20);
        add(lblForm);

        JLabel lblFila = new JLabel("Fila Seleccionada:");
        lblFila.setBounds(20, 50, 130, 25);
        add(lblFila);

        txtFila = new JTextField();
        txtFila.setBounds(160, 50, 120, 25);
        txtFila.setEditable(false);
        add(txtFila);

        JLabel lblColumna = new JLabel("Columna Seleccionada:");
        lblColumna.setBounds(20, 90, 150, 25);
        add(lblColumna);

        txtColumna = new JTextField();
        txtColumna.setBounds(160, 90, 120, 25);
        txtColumna.setEditable(false);
        add(txtColumna);

        // Codigo Animal con Prefijo Estatico A-
        JLabel lblAnimal = new JLabel("Cód. Animal:");
        lblAnimal.setBounds(20, 130, 130, 25);
        add(lblAnimal);

        JLabel lblPrefijoA = new JLabel("A-");
        lblPrefijoA.setBounds(160, 130, 25, 25);
        add(lblPrefijoA);

        txtCodigoAnimal = new JTextField();
        txtCodigoAnimal.setBounds(185, 130, 95, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigoAnimal, 3);
        add(txtCodigoAnimal);

        JLabel lblEstado = new JLabel("Estado de Celda:");
        lblEstado.setBounds(20, 170, 130, 25);
        add(lblEstado);

        txtEstadoCelda = new JTextField();
        txtEstadoCelda.setBounds(160, 170, 120, 25);
        txtEstadoCelda.setEditable(false);
        add(txtEstadoCelda);

        btnAsignar = new JButton("Asignar Animal a Celda");
        btnAsignar.setBounds(20, 220, 260, 30);
        btnAsignar.setEnabled(false); // Inicialmente deshabilitado
        add(btnAsignar);

        btnLiberar = new JButton("Liberar Celda");
        btnLiberar.setBounds(20, 260, 260, 30);
        btnLiberar.setEnabled(false); // Inicialmente deshabilitado
        add(btnLiberar);

        btnActualizar = new JButton("Actualizar Vista");
        btnActualizar.setBounds(20, 300, 260, 30);
        add(btnActualizar);

        // 2. Cuadrícula de Ubicaciones (Derecha)
        JLabel lblCuadricula = new JLabel("DISTRIBUCIÓN DEL REFUGIO (5x5)");
        lblCuadricula.setBounds(340, 10, 300, 20);
        add(lblCuadricula);

        int startX = 340;
        int startY = 40;
        int btnWidth = 75;
        int btnHeight = 75;
        int gap = 8;

        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                final int fila = f;
                final int col = c;

                JButton btn = new JButton();
                btn.setBounds(startX + c * (btnWidth + gap), startY + f * (btnHeight + gap), btnWidth, btnHeight);
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
            }
        });

        // Inicializar la cuadrícula visual
        actualizarCuadricula();
    }

    // Modulo para actualizar el texto y color de los botones de la cuadricula segun el estado en memoria
    public void actualizarCuadricula() {
        String[][] ubi = UbicacionServicio.getUbicaciones();
        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                String animal = ubi[f][c];
                JButton btn = gridButtons[f][c];
                if (animal == null || animal.trim().isEmpty()) {
                    btn.setText("Libre");
                    btn.setBackground(new Color(220, 245, 220)); // Verde suave
                    btn.setForeground(new Color(0, 100, 0));
                } else {
                    btn.setText(animal);
                    btn.setBackground(new Color(255, 220, 220)); // Rojo suave
                    btn.setForeground(new Color(150, 0, 0));
                }
            }
        }
    }

    // Modulo para seleccionar una celda y mostrar sus datos en el formulario
    private void seleccionarCelda(int fila, int col) {
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
            JOptionPane.showMessageDialog(this, "Animal asignado a la celda con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            actualizarCuadricula();
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
            JOptionPane.showMessageDialog(this, "Celda liberada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            actualizarCuadricula();
            seleccionarCelda(fila, col);
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Liberar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
