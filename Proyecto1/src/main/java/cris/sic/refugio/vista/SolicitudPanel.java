package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.SolicitudServicio;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Panel para la gestion manual de solicitudes de adopcion con Swing manual
public class SolicitudPanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtCodigoAnimal;
    private JTextField txtCodigoAdoptante;
    private JTextField txtFecha;

    private JButton btnRegistrar;
    private JButton btnAprobar;
    private JButton btnRechazar;
    private JButton btnLimpiar;

    private JTextField txtFiltroCodigo;
    private JTextField txtFiltroAnimal;
    private JTextField txtFiltroAdoptante;
    private JComboBox<String> cbFiltroEstado;

    private JTable tblSolicitudes;
    private DefaultTableModel tableModel;

    public SolicitudPanel() {
        setLayout(null);

        // 1. Panel de Formulario
        JLabel lblForm = new JLabel("DATOS DE LA SOLICITUD");
        lblForm.setBounds(20, 10, 200, 20);
        add(lblForm);

        // Codigo de Solicitud con Prefijo Estatico S-
        JLabel lblCodigo = new JLabel("Cód. Solicitud:");
        lblCodigo.setBounds(20, 40, 110, 25);
        add(lblCodigo);

        JLabel lblPrefijoS = new JLabel("S-");
        lblPrefijoS.setBounds(135, 40, 25, 25);
        add(lblPrefijoS);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(160, 40, 120, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigo, 3);
        add(txtCodigo);

        // Codigo de Animal con Prefijo Estatico A-
        JLabel lblAnimal = new JLabel("Cód. Animal:");
        lblAnimal.setBounds(20, 80, 110, 25);
        add(lblAnimal);

        JLabel lblPrefijoA = new JLabel("A-");
        lblPrefijoA.setBounds(135, 80, 25, 25);
        add(lblPrefijoA);

        txtCodigoAnimal = new JTextField();
        txtCodigoAnimal.setBounds(160, 80, 120, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigoAnimal, 3);
        add(txtCodigoAnimal);

        // Codigo de Adoptante con Prefijo Estatico AD-
        JLabel lblAdoptante = new JLabel("Cód. Adoptante:");
        lblAdoptante.setBounds(20, 120, 110, 25);
        add(lblAdoptante);

        JLabel lblPrefijoAD = new JLabel("AD-");
        lblPrefijoAD.setBounds(135, 120, 30, 25);
        add(lblPrefijoAD);

        txtCodigoAdoptante = new JTextField();
        txtCodigoAdoptante.setBounds(165, 120, 115, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigoAdoptante, 3);
        add(txtCodigoAdoptante);

        // Fecha de Solicitud (Solo Lectura con fecha actual del sistema)
        JLabel lblFecha = new JLabel("Fecha Solicitud:");
        lblFecha.setBounds(20, 160, 110, 25);
        add(lblFecha);

        txtFecha = new JTextField(UIUtils.obtenerFechaHoy());
        txtFecha.setBounds(135, 160, 145, 25);
        txtFecha.setEditable(false);
        add(txtFecha);

        // Botones de Accion
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 210, 120, 25);
        add(btnRegistrar);

        btnAprobar = new JButton("Aprobar");
        btnAprobar.setBounds(160, 210, 120, 25);
        btnAprobar.setEnabled(false); // Deshabilitado inicialmente
        add(btnAprobar);

        btnRechazar = new JButton("Rechazar");
        btnRechazar.setBounds(20, 250, 120, 25);
        btnRechazar.setEnabled(false); // Deshabilitado inicialmente
        add(btnRechazar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(160, 250, 120, 25);
        add(btnLimpiar);

        // 2. Panel de Filtros
        JLabel lblFiltros = new JLabel("FILTROS DE BÚSQUEDA");
        lblFiltros.setBounds(320, 10, 200, 20);
        add(lblFiltros);

        JLabel lblFCodigo = new JLabel("Código:");
        lblFCodigo.setBounds(320, 40, 50, 25);
        add(lblFCodigo);

        JLabel lblFPrefijoS = new JLabel("S-");
        lblFPrefijoS.setBounds(370, 40, 20, 25);
        add(lblFPrefijoS);

        txtFiltroCodigo = new JTextField();
        txtFiltroCodigo.setBounds(390, 40, 70, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroCodigo, 3);
        add(txtFiltroCodigo);

        JLabel lblFAnimal = new JLabel("Animal:");
        lblFAnimal.setBounds(475, 40, 50, 25);
        add(lblFAnimal);

        JLabel lblFPrefijoA = new JLabel("A-");
        lblFPrefijoA.setBounds(525, 40, 20, 25);
        add(lblFPrefijoA);

        txtFiltroAnimal = new JTextField();
        txtFiltroAnimal.setBounds(545, 40, 90, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroAnimal, 3);
        add(txtFiltroAnimal);

        JLabel lblFAdoptante = new JLabel("Adopt.:");
        lblFAdoptante.setBounds(320, 80, 50, 25);
        add(lblFAdoptante);

        JLabel lblFPrefijoAD = new JLabel("AD-");
        lblFPrefijoAD.setBounds(370, 80, 25, 25);
        add(lblFPrefijoAD);

        txtFiltroAdoptante = new JTextField();
        txtFiltroAdoptante.setBounds(395, 80, 65, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroAdoptante, 3);
        add(txtFiltroAdoptante);

        JLabel lblFEstado = new JLabel("Estado:");
        lblFEstado.setBounds(475, 80, 50, 25);
        add(lblFEstado);

        String[] estados = {"TODOS", "PENDIENTE", "APROBADA", "RECHAZADA"};
        cbFiltroEstado = new JComboBox<>(estados);
        cbFiltroEstado.setBounds(525, 80, 110, 25);
        add(cbFiltroEstado);

        JButton btnFiltrar = new JButton("Filtrar / Buscar");
        btnFiltrar.setBounds(650, 40, 120, 65);
        add(btnFiltrar);

        // 3. Tabla de Resultados
        String[] columnas = {"Código", "Animal", "Adoptante", "Fecha", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblSolicitudes = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tblSolicitudes);
        scrollTable.setBounds(320, 120, 450, 310);
        add(scrollTable);

        // Eventos
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        btnAprobar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aprobar();
            }
        });

        btnRechazar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rechazar();
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

        tblSolicitudes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    seleccionarFila();
                }
            }
        });

        // Carga inicial
        buscar();
    }

    // Modulo para refrescar el contenido de la tabla
    public void buscar() {
        String fc = UIUtils.formatearCodigo("S-", txtFiltroCodigo.getText().trim());
        String fa = UIUtils.formatearCodigo("A-", txtFiltroAnimal.getText().trim());
        String fad = UIUtils.formatearCodigo("AD-", txtFiltroAdoptante.getText().trim());
        String fe = (String) cbFiltroEstado.getSelectedItem();

        Solicitud[] filtradas = SolicitudServicio.filtrarSolicitudes(fc, fa, fad, fe);
        cargarTabla(filtradas);
    }

    // Modulo para cargar el listado en el DefaultTableModel
    private void cargarTabla(Solicitud[] lista) {
        tableModel.setRowCount(0);
        for (int i = 0; i < lista.length; i++) {
            Solicitud s = lista[i];
            if (s != null) {
                Object[] fila = {
                    s.getCodigo(),
                    s.getCodigoAnimal(),
                    s.getCodigoAdoptante(),
                    s.getFecha(),
                    s.getEstado()
                };
                tableModel.addRow(fila);
            }
        }
    }

    // Modulo para registrar una solicitud
    private void registrar() {
        String codigo = UIUtils.formatearCodigo("S-", txtCodigo.getText().trim());
        String codAnimal = UIUtils.formatearCodigo("A-", txtCodigoAnimal.getText().trim());
        String codAdopt = UIUtils.formatearCodigo("AD-", txtCodigoAdoptante.getText().trim());
        String fecha = txtFecha.getText().trim();

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = SolicitudServicio.registrarSolicitud(codigo, codAnimal, codAdopt, fecha, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Solicitud registrada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Registrar Solicitud", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para aprobar la solicitud seleccionada
    private void aprobar() {
        String codigo = UIUtils.formatearCodigo("S-", txtCodigo.getText().trim());
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = SolicitudServicio.aprobarSolicitud(codigo, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Solicitud aprobada con éxito. El animal ha sido adoptado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Aprobar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para rechazar la solicitud seleccionada
    private void rechazar() {
        String codigo = UIUtils.formatearCodigo("S-", txtCodigo.getText().trim());
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = SolicitudServicio.rechazarSolicitud(codigo, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Solicitud rechazada con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Rechazar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para limpiar campos del formulario y restaurar estados (restaura fecha del sistema)
    private void limpiarCampos() {
        tblSolicitudes.clearSelection();
        txtCodigo.setText("");
        txtCodigoAnimal.setText("");
        txtCodigoAdoptante.setText("");
        txtFecha.setText(UIUtils.obtenerFechaHoy());
        txtCodigo.setEditable(true);
        btnAprobar.setEnabled(false);
        btnRechazar.setEnabled(false);
        btnRegistrar.setEnabled(true);
    }

    // Modulo para auto-completar los campos al hacer clic en una fila (mantiene la fecha del registro)
    private void seleccionarFila() {
        int row = tblSolicitudes.getSelectedRow();
        if (row >= 0) {
            String codS = (String) tblSolicitudes.getValueAt(row, 0);
            String codA = (String) tblSolicitudes.getValueAt(row, 1);
            String codAD = (String) tblSolicitudes.getValueAt(row, 2);
            String fechaStr = (String) tblSolicitudes.getValueAt(row, 3);
            String estadoStr = (String) tblSolicitudes.getValueAt(row, 4);

            txtCodigo.setText(UIUtils.extraerNumeroCodigo(codS, "S-"));
            txtCodigoAnimal.setText(UIUtils.extraerNumeroCodigo(codA, "A-"));
            txtCodigoAdoptante.setText(UIUtils.extraerNumeroCodigo(codAD, "AD-"));
            txtFecha.setText(fechaStr);

            txtCodigo.setEditable(false);
            btnRegistrar.setEnabled(false);

            if ("PENDIENTE".equals(estadoStr)) {
                btnAprobar.setEnabled(true);
                btnRechazar.setEnabled(true);
            } else {
                btnAprobar.setEnabled(false);
                btnRechazar.setEnabled(false);
            }
        }
    }
}
