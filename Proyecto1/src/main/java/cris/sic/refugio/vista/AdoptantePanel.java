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
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Panel para la gestion manual de los adoptantes con Swing manual
public class AdoptantePanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDpi;
    private JTextField txtTelefono;

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

        JLabel lblCodigo = new JLabel("Código (AD-xxx):");
        lblCodigo.setBounds(20, 40, 100, 25);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(130, 40, 150, 25);
        add(txtCodigo);

        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 80, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(130, 80, 150, 25);
        add(txtNombre);

        JLabel lblDpi = new JLabel("DPI (13 dígitos):");
        lblDpi.setBounds(20, 120, 100, 25);
        add(lblDpi);

        txtDpi = new JTextField();
        txtDpi.setBounds(130, 120, 150, 25);
        add(txtDpi);

        JLabel lblTelefono = new JLabel("Teléfono (8 dígitos):");
        lblTelefono.setBounds(20, 160, 120, 25);
        add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(130, 160, 150, 25);
        add(txtTelefono);

        // Botones del Formulario
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 210, 120, 25);
        add(btnRegistrar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 210, 120, 25);
        add(btnActualizar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(20, 250, 120, 25);
        add(btnLimpiar);

        // 2. Panel de Filtros
        JLabel lblFiltros = new JLabel("FILTROS DE BÚSQUEDA");
        lblFiltros.setBounds(320, 10, 200, 20);
        add(lblFiltros);

        JLabel lblFCodigo = new JLabel("Código:");
        lblFCodigo.setBounds(320, 40, 60, 25);
        add(lblFCodigo);

        txtFiltroCodigo = new JTextField();
        txtFiltroCodigo.setBounds(380, 40, 90, 25);
        add(txtFiltroCodigo);

        JLabel lblFNombre = new JLabel("Nombre:");
        lblFNombre.setBounds(480, 40, 60, 25);
        add(lblFNombre);

        txtFiltroNombre = new JTextField();
        txtFiltroNombre.setBounds(540, 40, 90, 25);
        add(txtFiltroNombre);

        JLabel lblFDpi = new JLabel("DPI:");
        lblFDpi.setBounds(320, 80, 60, 25);
        add(lblFDpi);

        txtFiltroDpi = new JTextField();
        txtFiltroDpi.setBounds(380, 80, 90, 25);
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

        tblAdoptantes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
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
        String codigo = txtCodigo.getText().trim();
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
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para actualizar los datos del adoptante seleccionado
    private void actualizar() {
        String codigo = txtCodigo.getText().trim();
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
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para ejecutar la busqueda con filtros y actualizar la tabla
    private void buscar() {
        String fc = txtFiltroCodigo.getText().trim();
        String fn = txtFiltroNombre.getText().trim();
        String fd = txtFiltroDpi.getText().trim();

        Adoptante[] filtrados = AdoptanteServicio.filtrarAdoptantes(fc, fn, fd);
        cargarTabla(filtrados);
    }

    // Modulo para limpiar los campos del formulario
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDpi.setText("");
        txtTelefono.setText("");
        txtCodigo.setEditable(true);
    }

    // Modulo para auto-rellenar los campos al seleccionar una fila de la tabla
    private void seleccionarFila() {
        int row = tblAdoptantes.getSelectedRow();
        if (row >= 0) {
            txtCodigo.setText((String) tblAdoptantes.getValueAt(row, 0));
            txtNombre.setText((String) tblAdoptantes.getValueAt(row, 1));
            txtDpi.setText((String) tblAdoptantes.getValueAt(row, 2));
            txtTelefono.setText((String) tblAdoptantes.getValueAt(row, 3));
            txtCodigo.setEditable(false);
        }
    }
}
