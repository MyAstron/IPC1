package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.AnimalServicio;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Panel para la gestion manual de los animales rescatados (CRUD) con Swing manual
public class AnimalPanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtEspecie;
    private JTextField txtEdad;
    private JComboBox<String> cbEstadoClinico;
    private JComboBox<String> cbEstadoAdopcion;

    private JTextField txtFiltroCodigo;
    private JTextField txtFiltroNombre;
    private JTextField txtFiltroEspecie;
    private JComboBox<String> cbFiltroEstado;

    private JTable tblAnimales;
    private DefaultTableModel tableModel;

    public AnimalPanel() {
        setLayout(null);

        // 1. Panel de Formulario
        JLabel lblForm = new JLabel("DATOS DEL ANIMAL");
        lblForm.setBounds(20, 10, 200, 20);
        add(lblForm);

        JLabel lblCodigo = new JLabel("Código (A-xxx):");
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

        JLabel lblEspecie = new JLabel("Especie:");
        lblEspecie.setBounds(20, 120, 100, 25);
        add(lblEspecie);

        txtEspecie = new JTextField();
        txtEspecie.setBounds(130, 120, 150, 25);
        add(txtEspecie);

        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setBounds(20, 160, 100, 25);
        add(lblEdad);

        txtEdad = new JTextField();
        txtEdad.setBounds(130, 160, 150, 25);
        add(txtEdad);

        JLabel lblClinico = new JLabel("E. Clínico:");
        lblClinico.setBounds(20, 200, 100, 25);
        add(lblClinico);

        String[] estadosClinicos = {"SANO", "EN_TRATAMIENTO", "RECUPERADO"};
        cbEstadoClinico = new JComboBox<>(estadosClinicos);
        cbEstadoClinico.setBounds(130, 200, 150, 25);
        add(cbEstadoClinico);

        JLabel lblAdopcion = new JLabel("E. Adopción:");
        lblAdopcion.setBounds(20, 240, 100, 25);
        add(lblAdopcion);

        String[] estadosAdopcion = {"DISPONIBLE", "ADOPTADO"};
        cbEstadoAdopcion = new JComboBox<>(estadosAdopcion);
        cbEstadoAdopcion.setBounds(130, 240, 150, 25);
        add(cbEstadoAdopcion);

        // Botones del Formulario
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 290, 120, 25);
        add(btnRegistrar);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 290, 120, 25);
        add(btnActualizar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(20, 330, 120, 25);
        add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(160, 330, 120, 25);
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

        JLabel lblFEspecie = new JLabel("Especie:");
        lblFEspecie.setBounds(320, 80, 60, 25);
        add(lblFEspecie);

        txtFiltroEspecie = new JTextField();
        txtFiltroEspecie.setBounds(380, 80, 90, 25);
        add(txtFiltroEspecie);

        JLabel lblFEstado = new JLabel("Estado:");
        lblFEstado.setBounds(480, 80, 60, 25);
        add(lblFEstado);

        String[] opcionesFiltroEstado = {"TODOS", "DISPONIBLE", "ADOPTADO"};
        cbFiltroEstado = new JComboBox<>(opcionesFiltroEstado);
        cbFiltroEstado.setBounds(540, 80, 90, 25);
        add(cbFiltroEstado);

        JButton btnFiltrar = new JButton("Filtrar / Buscar");
        btnFiltrar.setBounds(650, 40, 120, 65);
        add(btnFiltrar);

        // 3. Tabla de Resultados
        String[] columnas = {"Código", "Nombre", "Especie", "Edad", "E. Clínico", "E. Adopción"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblAnimales = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tblAnimales);
        scrollTable.setBounds(320, 120, 450, 320);
        add(scrollTable);

        // Carga inicial
        cargarTabla(AnimalServicio.filtrarAnimales("", "", "", "TODOS"));

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

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminar();
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

        tblAnimales.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFila();
            }
        });
    }

    // Modulo para cargar el listado de animales en el modelo de la tabla
    private void cargarTabla(Animal[] lista) {
        tableModel.setRowCount(0);
        for (int i = 0; i < lista.length; i++) {
            Animal a = lista[i];
            if (a != null) {
                Object[] fila = {
                    a.getCodigo(),
                    a.getNombre(),
                    a.getEspecie(),
                    a.getEdad(),
                    a.getEstadoClinico(),
                    a.getEstadoAdopcion()
                };
                tableModel.addRow(fila);
            }
        }
    }

    // Modulo para registrar un nuevo animal desde los campos de la vista
    private void registrar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String especie = txtEspecie.getText().trim();
        int edad = -1;

        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clinico = (String) cbEstadoClinico.getSelectedItem();
        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = AnimalServicio.registrarAnimal(codigo, nombre, especie, edad, clinico, userStr);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Animal registrado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para actualizar los datos del animal seleccionado
    private void actualizar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String especie = txtEspecie.getText().trim();
        int edad = -1;

        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clinico = (String) cbEstadoClinico.getSelectedItem();
        String adopcion = (String) cbEstadoAdopcion.getSelectedItem();
        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = AnimalServicio.actualizarAnimal(codigo, nombre, especie, edad, clinico, adopcion, userStr);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Animal actualizado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para dar de baja logica al animal seleccionado
    private void eliminar() {
        String codigo = txtCodigo.getText().trim();
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un animal para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar lógicamente al animal: " + codigo + "?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            Usuario u = AutenticacionServicio.getUsuarioLogueado();
            String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

            String res = AnimalServicio.eliminarAnimal(codigo, userStr);
            if (res.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Animal eliminado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                buscar();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Modulo para ejecutar la busqueda con filtros y actualizar la tabla
    private void buscar() {
        String fc = txtFiltroCodigo.getText().trim();
        String fn = txtFiltroNombre.getText().trim();
        String fe = txtFiltroEspecie.getText().trim();
        String fa = (String) cbFiltroEstado.getSelectedItem();

        Animal[] filtrados = AnimalServicio.filtrarAnimales(fc, fn, fe, fa);
        cargarTabla(filtrados);
    }

    // Modulo para limpiar los campos del formulario
    private void limpiarCampos() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtEspecie.setText("");
        txtEdad.setText("");
        cbEstadoClinico.setSelectedIndex(0);
        cbEstadoAdopcion.setSelectedIndex(0);
        txtCodigo.setEditable(true);
    }

    // Modulo para auto-rellenar los campos al seleccionar una fila de la tabla
    private void seleccionarFila() {
        int row = tblAnimales.getSelectedRow();
        if (row >= 0) {
            txtCodigo.setText((String) tblAnimales.getValueAt(row, 0));
            txtNombre.setText((String) tblAnimales.getValueAt(row, 1));
            txtEspecie.setText((String) tblAnimales.getValueAt(row, 2));
            txtEdad.setText(String.valueOf(tblAnimales.getValueAt(row, 3)));
            cbEstadoClinico.setSelectedItem(tblAnimales.getValueAt(row, 4));
            cbEstadoAdopcion.setSelectedItem(tblAnimales.getValueAt(row, 5));
            txtCodigo.setEditable(false);
        }
    }
}
