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
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Panel para la gestion manual de los animales rescatados (CRUD) con Swing manual
public class AnimalPanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    
    // Grupo de Especie (RadioButtons y campo dinamico para "Otro")
    private JRadioButton rbPerro;
    private JRadioButton rbGato;
    private JRadioButton rbOtro;
    private ButtonGroup bgEspecie;
    private JTextField txtEspecieOtra;

    private JTextField txtEdad;
    private JComboBox<String> cbEstadoClinico;
    private JComboBox<String> cbEstadoAdopcion;

    private JButton btnRegistrar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

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

        // Codigo con Prefijo Estatico A-
        JLabel lblCodigo = new JLabel("Código Animal:");
        lblCodigo.setBounds(20, 40, 100, 25);
        add(lblCodigo);

        JLabel lblPrefijo = new JLabel("A-");
        lblPrefijo.setBounds(130, 40, 25, 25);
        add(lblPrefijo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(155, 40, 125, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigo, 3);
        add(txtCodigo);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setBounds(20, 75, 100, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(130, 75, 150, 25);
        add(txtNombre);

        // Especie con RadioButtons (Perro, Gato, Otro) y campo dinamico
        JLabel lblEspecie = new JLabel("Especie:");
        lblEspecie.setBounds(20, 110, 60, 20);
        add(lblEspecie);

        rbPerro = new JRadioButton("Perro");
        rbPerro.setBounds(85, 110, 65, 20);
        add(rbPerro);

        rbGato = new JRadioButton("Gato");
        rbGato.setBounds(155, 110, 60, 20);
        add(rbGato);

        rbOtro = new JRadioButton("Otro:");
        rbOtro.setBounds(85, 135, 60, 22);
        add(rbOtro);

        txtEspecieOtra = new JTextField();
        txtEspecieOtra.setBounds(145, 135, 135, 22);
        txtEspecieOtra.setEnabled(false);
        add(txtEspecieOtra);

        bgEspecie = new ButtonGroup();
        bgEspecie.add(rbPerro);
        bgEspecie.add(rbGato);
        bgEspecie.add(rbOtro);

        // Eventos para activar/desactivar txtEspecieOtra
        rbPerro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEspecieOtra.setEnabled(false);
                txtEspecieOtra.setText("");
            }
        });

        rbGato.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEspecieOtra.setEnabled(false);
                txtEspecieOtra.setText("");
            }
        });

        rbOtro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEspecieOtra.setEnabled(true);
                txtEspecieOtra.requestFocus();
            }
        });

        // Edad (solo numeros, max 2 digitos)
        JLabel lblEdad = new JLabel("Edad:");
        lblEdad.setBounds(20, 165, 100, 25);
        add(lblEdad);

        txtEdad = new JTextField();
        txtEdad.setBounds(130, 165, 150, 25);
        UIUtils.aplicarRestriccionNumerica(txtEdad, 2);
        add(txtEdad);

        // Estado Clinico
        JLabel lblClinico = new JLabel("E. Clínico:");
        lblClinico.setBounds(20, 200, 100, 25);
        add(lblClinico);

        String[] estadosClinicos = {"SANO", "EN_TRATAMIENTO", "RECUPERADO"};
        cbEstadoClinico = new JComboBox<>(estadosClinicos);
        cbEstadoClinico.setBounds(130, 200, 150, 25);
        add(cbEstadoClinico);

        // Estado Adopcion
        JLabel lblAdopcion = new JLabel("E. Adopción:");
        lblAdopcion.setBounds(20, 235, 100, 25);
        add(lblAdopcion);

        String[] estadosAdopcion = {"DISPONIBLE", "ADOPTADO"};
        cbEstadoAdopcion = new JComboBox<>(estadosAdopcion);
        cbEstadoAdopcion.setBounds(130, 235, 150, 25);
        add(cbEstadoAdopcion);

        // Botones del Formulario
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(20, 280, 120, 28);
        add(btnRegistrar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(160, 280, 120, 28);
        btnActualizar.setEnabled(false); // Deshabilitado inicialmente
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(20, 318, 120, 28);
        btnEliminar.setEnabled(false); // Deshabilitado inicialmente
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(160, 318, 120, 28);
        add(btnLimpiar);

        // 2. Panel de Filtros
        JLabel lblFiltros = new JLabel("FILTROS DE BÚSQUEDA");
        lblFiltros.setBounds(320, 10, 200, 20);
        add(lblFiltros);

        JLabel lblFCodigo = new JLabel("Código:");
        lblFCodigo.setBounds(320, 40, 50, 25);
        add(lblFCodigo);

        JLabel lblFPrefijo = new JLabel("A-");
        lblFPrefijo.setBounds(370, 40, 20, 25);
        add(lblFPrefijo);

        txtFiltroCodigo = new JTextField();
        txtFiltroCodigo.setBounds(390, 40, 75, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroCodigo, 3);
        add(txtFiltroCodigo);

        JLabel lblFNombre = new JLabel("Nombre:");
        lblFNombre.setBounds(475, 40, 55, 25);
        add(lblFNombre);

        txtFiltroNombre = new JTextField();
        txtFiltroNombre.setBounds(535, 40, 100, 25);
        add(txtFiltroNombre);

        JLabel lblFEspecie = new JLabel("Especie:");
        lblFEspecie.setBounds(320, 80, 55, 25);
        add(lblFEspecie);

        txtFiltroEspecie = new JTextField();
        txtFiltroEspecie.setBounds(375, 80, 90, 25);
        add(txtFiltroEspecie);

        JLabel lblFEstado = new JLabel("Estado:");
        lblFEstado.setBounds(475, 80, 55, 25);
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

        tblAnimales.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    seleccionarFila();
                }
            }
        });
    }

    // Modulo para obtener el valor de la especie seleccionada
    private String obtenerEspecieSeleccionada() {
        if (rbPerro.isSelected()) {
            return "Perro";
        } else if (rbGato.isSelected()) {
            return "Gato";
        } else if (rbOtro.isSelected()) {
            String otra = txtEspecieOtra.getText().trim();
            if (otra.isEmpty()) {
                return null; // Selecciono 'Otro' pero esta vacio
            }
            return otra;
        }
        return ""; // Ninguna opcion seleccionada
    }

    // Modulo para establecer la especie en los radio buttons del formulario
    private void establecerEspecieEnFormulario(String especie) {
        if (especie == null || especie.trim().isEmpty()) {
            bgEspecie.clearSelection();
            txtEspecieOtra.setText("");
            txtEspecieOtra.setEnabled(false);
            return;
        }
        String esp = especie.trim();
        if (esp.equalsIgnoreCase("Perro")) {
            rbPerro.setSelected(true);
            txtEspecieOtra.setText("");
            txtEspecieOtra.setEnabled(false);
        } else if (esp.equalsIgnoreCase("Gato")) {
            rbGato.setSelected(true);
            txtEspecieOtra.setText("");
            txtEspecieOtra.setEnabled(false);
        } else {
            rbOtro.setSelected(true);
            txtEspecieOtra.setEnabled(true);
            txtEspecieOtra.setText(esp);
        }
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
        String codigo = UIUtils.formatearCodigo("A-", txtCodigo.getText().trim());
        String nombre = txtNombre.getText().trim();
        
        String especie = obtenerEspecieSeleccionada();
        if (especie == null) {
            JOptionPane.showMessageDialog(this, "Debe especificar la especie en la caja de texto al seleccionar 'Otro'.", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (especie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una especie (Perro, Gato u Otro).", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int edad = -1;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero válido (0-25).", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, res, "Error al Registrar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para actualizar los datos del animal seleccionado
    private void actualizar() {
        String codigo = UIUtils.formatearCodigo("A-", txtCodigo.getText().trim());
        String nombre = txtNombre.getText().trim();
        
        String especie = obtenerEspecieSeleccionada();
        if (especie == null) {
            JOptionPane.showMessageDialog(this, "Debe especificar la especie en la caja de texto al seleccionar 'Otro'.", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (especie.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una especie (Perro, Gato u Otro).", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int edad = -1;
        try {
            edad = Integer.parseInt(txtEdad.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número entero válido (0-25).", "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, res, "Error al Actualizar", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para dar de baja logica al animal seleccionado
    private void eliminar() {
        String codigo = UIUtils.formatearCodigo("A-", txtCodigo.getText().trim());
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un animal para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int op = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar lógicamente al animal: " + codigo + "?", "Confirmación de Baja", JOptionPane.YES_NO_OPTION);
        if (op == JOptionPane.YES_OPTION) {
            Usuario u = AutenticacionServicio.getUsuarioLogueado();
            String userStr = (u != null) ? u.getUsuario() : "DESCONOCIDO";

            String res = AnimalServicio.eliminarAnimal(codigo, userStr);
            if (res.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Animal eliminado con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
                limpiarCampos();
                buscar();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error al Eliminar", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Modulo para ejecutar la busqueda con filtros y actualizar la tabla
    public void buscar() {
        String fc = UIUtils.formatearCodigo("A-", txtFiltroCodigo.getText().trim());
        String fn = txtFiltroNombre.getText().trim();
        String fe = txtFiltroEspecie.getText().trim();
        String fa = (String) cbFiltroEstado.getSelectedItem();

        Animal[] filtrados = AnimalServicio.filtrarAnimales(fc, fn, fe, fa);
        cargarTabla(filtrados);
    }

    // Modulo para limpiar los campos del formulario y restaurar estados
    private void limpiarCampos() {
        tblAnimales.clearSelection();
        txtCodigo.setText("");
        txtNombre.setText("");
        bgEspecie.clearSelection();
        txtEspecieOtra.setText("");
        txtEspecieOtra.setEnabled(false);
        txtEdad.setText("");
        cbEstadoClinico.setSelectedIndex(0);
        cbEstadoAdopcion.setSelectedIndex(0);
        txtCodigo.setEditable(true);
        btnActualizar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnRegistrar.setEnabled(true);
    }

    // Modulo para auto-rellenar los campos al seleccionar una fila de la tabla
    private void seleccionarFila() {
        int row = tblAnimales.getSelectedRow();
        if (row >= 0) {
            String codCompleto = (String) tblAnimales.getValueAt(row, 0);
            txtCodigo.setText(UIUtils.extraerNumeroCodigo(codCompleto, "A-"));
            txtNombre.setText((String) tblAnimales.getValueAt(row, 1));
            establecerEspecieEnFormulario((String) tblAnimales.getValueAt(row, 2));
            txtEdad.setText(String.valueOf(tblAnimales.getValueAt(row, 3)));
            cbEstadoClinico.setSelectedItem(tblAnimales.getValueAt(row, 4));
            cbEstadoAdopcion.setSelectedItem(tblAnimales.getValueAt(row, 5));
            txtCodigo.setEditable(false);
            btnActualizar.setEnabled(true);
            btnEliminar.setEnabled(true);
            btnRegistrar.setEnabled(false);
        }
    }
}
