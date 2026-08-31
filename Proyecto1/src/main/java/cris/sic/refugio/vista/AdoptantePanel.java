package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.AdoptanteServicio;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Panel para la gestion manual de los adoptantes con Swing manual
public class AdoptantePanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDpi;
    private JTextField txtTelefono;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnLimpiar;

    private JTextField txtFiltroCodigo;
    private JTextField txtFiltroNombre;
    private JTextField txtFiltroDpi;

    private JTable tblAdoptantes;
    private DefaultTableModel tableModel;

    public AdoptantePanel() {
        setLayout(null);

        // 1. Panel de Formulario
        JLabel lblForm = new JLabel("DATOS DEL ADOPTANTE");
        lblForm.setBounds(20, 10, 200, 20);
        add(lblForm);

        // Codigo con Prefijo Estatico AD-
        JLabel lblCodigo = new JLabel("Código Adoptante:");
        lblCodigo.setBounds(20, 40, 110, 25);
        add(lblCodigo);

        JLabel lblPrefijo = new JLabel("AD-");
        lblPrefijo.setBounds(140, 40, 30, 25);
        add(lblPrefijo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(170, 40, 110, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigo, 3);
        add(txtCodigo);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setBounds(20, 80, 120, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(140, 80, 140, 25);
        add(txtNombre);

        // DPI (solo numeros, 13 digitos max)
        JLabel lblDpi = new JLabel("DPI (13 dígitos):");
        lblDpi.setBounds(20, 120, 120, 25);
        add(lblDpi);

        txtDpi = new JTextField();
        txtDpi.setBounds(140, 120, 140, 25);
        UIUtils.aplicarRestriccionNumerica(txtDpi, 13);
        add(txtDpi);

        // Telefono (solo numeros, 8 digitos max)
        JLabel lblTelefono = new JLabel("Teléfono (8 dígitos):");
        lblTelefono.setBounds(20, 160, 120, 25);
        add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(140, 160, 140, 25);
        UIUtils.aplicarRestriccionNumerica(txtTelefono, 8);
        add(txtTelefono);

        // Botones del Formulario
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 210, 120, 25);
        add(btnRegistrar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 210, 120, 25);
        btnActualizar.setEnabled(false); // Deshabilitado inicialmente
        add(btnActualizar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(20, 250, 260, 25);
        add(btnLimpiar);

        // 2. Panel de Filtros
        JLabel lblFiltros = new JLabel("FILTROS DE BÚSQUEDA");
        lblFiltros.setBounds(320, 10, 200, 20);
        add(lblFiltros);

        JLabel lblFCodigo = new JLabel("Código:");
        lblFCodigo.setBounds(320, 40, 50, 25);
        add(lblFCodigo);

        JLabel lblFPrefijo = new JLabel("AD-");
        lblFPrefijo.setBounds(370, 40, 25, 25);
        add(lblFPrefijo);

        txtFiltroCodigo = new JTextField();
        txtFiltroCodigo.setBounds(395, 40, 70, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroCodigo, 3);
        add(txtFiltroCodigo);

        JLabel lblFNombre = new JLabel("Nombre:");
        lblFNombre.setBounds(475, 40, 55, 25);
        add(lblFNombre);

        txtFiltroNombre = new JTextField();
        txtFiltroNombre.setBounds(535, 40, 100, 25);
        add(txtFiltroNombre);

        JLabel lblFDpi = new JLabel("DPI:");
        lblFDpi.setBounds(320, 80, 50, 25);
        add(lblFDpi);

        txtFiltroDpi = new JTextField();
        txtFiltroDpi.setBounds(370, 80, 265, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroDpi, 13);
        add(txtFiltroDpi);

        JButton btnFiltrar = new JButton("Filtrar / Buscar");
        btnFiltrar.setBounds(650, 40, 120, 65);
        add(btnFiltrar);

        // 3. Tabla de Resultados
        String[] columnas = {"Código", "Nombre", "DPI", "Teléfono"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAdoptantes = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tblAdoptantes);
        scrollTable.setBounds(320, 120, 450, 320);
        add(scrollTable);

        // Carga inicial
        cargarTabla(AdoptanteServicio.filtrarAdoptantes("", "", ""));

        // Eventos
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        btnActualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizar();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        btnFiltrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscar();
            }
        });

        tblAdoptantes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    seleccionarFila();
                }
            }
        });
    }

    // Modulo para cargar el listado de adoptantes en el modelo de la tabla
    private void cargarTabla(Adoptante[] lista) {
        tableModel.setRowCount(0);
        for (int i = 0; i < lista.length; i++) {
            Adoptante ad = lista[i];
            if (ad != null) {
                Object[] fila = {
                    ad.getCodigo(),
                    ad.getNombre(),
                    ad.getDpi(),
                    ad.getTelefono()
                };
                tableModel.addRow(fila);
            }
        }
    }

    // Modulo para registrar un nuevo adoptante desde los campos de la vista
    private void registrar() {
        String codigo = UIUtils.formatearCodigo("AD-", txtCodigo.getText().trim());
        String nombre = txtNombre.getText().trim();
        String dpi = txtDpi.getText().trim();
        String telefono = txtTelefono.getText().trim();

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = AdoptanteServicio.registrarAdoptante(codigo, nombre, dpi, telefono, userStr);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Adoptante registrado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para actualizar los datos del adoptante seleccionado
    private void actualizar() {
        String codigo = UIUtils.formatearCodigo("AD-", txtCodigo.getText().trim());
        String nombre = txtNombre.getText().trim();
        String dpi = txtDpi.getText().trim();
        String telefono = txtTelefono.getText().trim();

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = AdoptanteServicio.editarAdoptante(codigo, nombre, dpi, telefono, userStr);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Adoptante actualizado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para ejecutar la busqueda con filtros y actualizar la tabla
    public void buscar() {
        String fc = UIUtils.formatearCodigo("AD-", txtFiltroCodigo.getText().trim());
        String fn = txtFiltroNombre.getText().trim();
        String fd = txtFiltroDpi.getText().trim();

        Adoptante[] filtrados = AdoptanteServicio.filtrarAdoptantes(fc, fn, fd);
        cargarTabla(filtrados);
    }

    // Modulo para limpiar los campos del formulario y restaurar estados
    private void limpiarCampos() {
        tblAdoptantes.clearSelection();
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDpi.setText("");
        txtTelefono.setText("");
        txtCodigo.setEditable(true);
        btnActualizar.setEnabled(false);
        btnRegistrar.setEnabled(true);
    }

    // Modulo para auto-rellenar los campos al seleccionar una fila de la tabla
    private void seleccionarFila() {
        int row = tblAdoptantes.getSelectedRow();
        if (row >= 0) {
            String codCompleto = (String) tblAdoptantes.getValueAt(row, 0);
            txtCodigo.setText(UIUtils.extraerNumeroCodigo(codCompleto, "AD-"));
            txtNombre.setText((String) tblAdoptantes.getValueAt(row, 1));
            txtDpi.setText((String) tblAdoptantes.getValueAt(row, 2));
            txtTelefono.setText((String) tblAdoptantes.getValueAt(row, 3));
            txtCodigo.setEditable(false);
            btnActualizar.setEnabled(true);
            btnRegistrar.setEnabled(false);
        }
    }
}
