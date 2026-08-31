package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.persistencia.BaseDatosMemoria;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.RescateServicio;

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

// Panel para la gestion unificada de rescates urgentes y animales asociados
public class RescatePanel extends JPanel {

    private JTextField txtCodigo;
    private JTextField txtDireccion;
    private JComboBox<String> cbPrioridad;
    private JTextField txtFecha;

    private JTextField txtCodigoAnimal;
    private JTextField txtNombreAnimal;

    // Grupo de Especie de Animal Rescatado (RadioButtons y campo dinamico para "Otro")
    private JRadioButton rbPerro;
    private JRadioButton rbGato;
    private JRadioButton rbOtro;
    private ButtonGroup bgEspecie;
    private JTextField txtEspecieOtra;

    private JTextField txtFiltroCodigo;
    private JComboBox<String> cbFiltroPrioridad;
    private JComboBox<String> cbFiltroEstado;

    private JButton btnRegistrar;
    private JButton btnLimpiar;

    private JTable tblRescates;
    private DefaultTableModel tableModel;

    public RescatePanel() {
        setLayout(null);

        // 1. Panel de Formulario Unificado - Registro de Rescate
        JLabel lblForm = new JLabel("REGISTRO UNIFICADO DE RESCATE");
        lblForm.setBounds(20, 10, 260, 20);
        add(lblForm);

        // Codigo de Rescate con Prefijo Estatico R-
        JLabel lblCodigo = new JLabel("Cód. Rescate:");
        lblCodigo.setBounds(20, 35, 100, 25);
        add(lblCodigo);

        JLabel lblPrefijoRescate = new JLabel("R-");
        lblPrefijoRescate.setBounds(130, 35, 25, 25);
        add(lblPrefijoRescate);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(155, 35, 125, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigo, 3);
        add(txtCodigo);

        // Direccion / Descripcion
        JLabel lblDireccion = new JLabel("Dirección / Desc:");
        lblDireccion.setBounds(20, 68, 110, 25);
        add(lblDireccion);

        txtDireccion = new JTextField();
        txtDireccion.setBounds(130, 68, 150, 25);
        add(txtDireccion);

        // Prioridad
        JLabel lblPrioridad = new JLabel("Prioridad:");
        lblPrioridad.setBounds(20, 101, 100, 25);
        add(lblPrioridad);

        String[] prioridades = {"ALTA", "MEDIA", "BAJA"};
        cbPrioridad = new JComboBox<>(prioridades);
        cbPrioridad.setBounds(130, 101, 150, 25);
        add(cbPrioridad);

        // Fecha de Solo Lectura (Fecha del sistema por defecto)
        JLabel lblFecha = new JLabel("Fecha Rescate:");
        lblFecha.setBounds(20, 134, 110, 25);
        add(lblFecha);

        txtFecha = new JTextField(UIUtils.obtenerFechaHoy());
        txtFecha.setBounds(130, 134, 150, 25);
        txtFecha.setEditable(false);
        add(txtFecha);

        // Subseccion del Animal Rescatado dentro de la misma vista
        JLabel lblAnimalSec = new JLabel("DATOS DEL ANIMAL RESCATADO");
        lblAnimalSec.setBounds(20, 168, 260, 20);
        add(lblAnimalSec);

        // Codigo de Animal con Prefijo Estatico A-
        JLabel lblCodAnimal = new JLabel("Cód. Animal:");
        lblCodAnimal.setBounds(20, 193, 100, 25);
        add(lblCodAnimal);

        JLabel lblPrefijoAnimal = new JLabel("A-");
        lblPrefijoAnimal.setBounds(130, 193, 25, 25);
        add(lblPrefijoAnimal);

        txtCodigoAnimal = new JTextField();
        txtCodigoAnimal.setBounds(155, 193, 125, 25);
        UIUtils.aplicarRestriccionNumerica(txtCodigoAnimal, 3);
        add(txtCodigoAnimal);

        // Nombre del Animal
        JLabel lblNombreAuto = new JLabel("Nombre Animal:");
        lblNombreAuto.setBounds(20, 226, 100, 25);
        add(lblNombreAuto);

        txtNombreAnimal = new JTextField();
        txtNombreAnimal.setBounds(130, 226, 150, 25);
        add(txtNombreAnimal);

        // Especie con RadioButtons (Perro, Gato, Otro) y campo dinamico
        JLabel lblEspecie = new JLabel("Especie:");
        lblEspecie.setBounds(20, 258, 60, 20);
        add(lblEspecie);

        rbPerro = new JRadioButton("Perro");
        rbPerro.setBounds(85, 258, 65, 20);
        add(rbPerro);

        rbGato = new JRadioButton("Gato");
        rbGato.setBounds(155, 258, 60, 20);
        add(rbGato);

        rbOtro = new JRadioButton("Otro:");
        rbOtro.setBounds(85, 281, 60, 22);
        add(rbOtro);

        txtEspecieOtra = new JTextField();
        txtEspecieOtra.setBounds(145, 281, 135, 22);
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

        // Botones de Accion del Formulario
        btnRegistrar = new JButton("Registrar Rescate");
        btnRegistrar.setBounds(20, 316, 260, 30);
        add(btnRegistrar);

        btnLimpiar = new JButton("Limpiar Formulario");
        btnLimpiar.setBounds(20, 353, 260, 28);
        add(btnLimpiar);

        // 2. Panel de Filtros de Busqueda (Derecha)
        JLabel lblFiltros = new JLabel("FILTROS DE BÚSQUEDA");
        lblFiltros.setBounds(320, 10, 200, 20);
        add(lblFiltros);

        JLabel lblFCodigo = new JLabel("Código:");
        lblFCodigo.setBounds(320, 40, 50, 25);
        add(lblFCodigo);

        JLabel lblFPrefijo = new JLabel("R-");
        lblFPrefijo.setBounds(370, 40, 20, 25);
        add(lblFPrefijo);

        txtFiltroCodigo = new JTextField();
        txtFiltroCodigo.setBounds(390, 40, 75, 25);
        UIUtils.aplicarRestriccionNumerica(txtFiltroCodigo, 3);
        add(txtFiltroCodigo);

        JLabel lblFPrioridad = new JLabel("Prioridad:");
        lblFPrioridad.setBounds(480, 40, 70, 25);
        add(lblFPrioridad);

        String[] filtroPrioridades = {"TODOS", "ALTA", "MEDIA", "BAJA"};
        cbFiltroPrioridad = new JComboBox<>(filtroPrioridades);
        cbFiltroPrioridad.setBounds(550, 40, 85, 25);
        add(cbFiltroPrioridad);

        JLabel lblFEstado = new JLabel("Estado:");
        lblFEstado.setBounds(320, 80, 60, 25);
        add(lblFEstado);

        String[] filtroEstados = {"TODOS", "PENDIENTE", "ATENDIDO"};
        cbFiltroEstado = new JComboBox<>(filtroEstados);
        cbFiltroEstado.setBounds(380, 80, 90, 25);
        add(cbFiltroEstado);

        JButton btnFiltrar = new JButton("Filtrar / Buscar");
        btnFiltrar.setBounds(650, 40, 120, 65);
        add(btnFiltrar);

        // 3. Tabla de Resultados
        String[] columnas = {"Código", "Descripción", "Prioridad", "Estado", "Fecha", "Animal Vinc."};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblRescates = new JTable(tableModel);
        JScrollPane scrollTable = new JScrollPane(tblRescates);
        scrollTable.setBounds(320, 120, 450, 315);
        add(scrollTable);

        // Eventos
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
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

        tblRescates.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
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

    // Modulo para refrescar el contenido de la tabla segun los filtros
    public void buscar() {
        String fc = UIUtils.formatearCodigo("R-", txtFiltroCodigo.getText().trim());
        String fp = (String) cbFiltroPrioridad.getSelectedItem();
        String fe = (String) cbFiltroEstado.getSelectedItem();

        Rescate[] filtrados = RescateServicio.filtrarRescates(fc, fp, fe);
        cargarTabla(filtrados);
    }

    // Modulo para volcar la informacion en la tabla
    private void cargarTabla(Rescate[] lista) {
        tableModel.setRowCount(0);
        for (int i = 0; i < lista.length; i++) {
            Rescate r = lista[i];
            if (r != null) {
                Object[] fila = {
                    r.getCodigo(),
                    r.getDireccionDescripcion(),
                    r.getPrioridad(),
                    r.getEstado(),
                    r.getFecha(),
                    r.getCodigoAnimalVinculado().isEmpty() ? "Ninguno" : r.getCodigoAnimalVinculado()
                };
                tableModel.addRow(fila);
            }
        }
    }

    // Modulo para registrar un rescate unificado (Rescate + Animal en transaccion unica)
    private void registrar() {
        String codRescate = UIUtils.formatearCodigo("R-", txtCodigo.getText().trim());
        String direccion = txtDireccion.getText().trim();
        String prioridad = (String) cbPrioridad.getSelectedItem();
        String fecha = txtFecha.getText().trim();

        String codAnimal = UIUtils.formatearCodigo("A-", txtCodigoAnimal.getText().trim());
        String nombreAnimal = txtNombreAnimal.getText().trim();
        
        String especieAnimal = obtenerEspecieSeleccionada();
        if (especieAnimal == null) {
            JOptionPane.showMessageDialog(this, "Debe especificar la especie en la caja de texto al seleccionar 'Otro'.", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (especieAnimal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una especie para el animal rescatado (Perro, Gato u Otro).", "Validación Requerida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String user = (u != null) ? u.getUsuario() : "DESCONOCIDO";

        String res = RescateServicio.registrarRescateUnificado(codRescate, direccion, prioridad, fecha, 
                                                               codAnimal, nombreAnimal, especieAnimal, user);
        if (res.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, "Rescate y animal asociado registrados con éxito.", "Registro Unificado Exitoso", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            buscar();
        } else {
            JOptionPane.showMessageDialog(this, res, "Error al Registrar Rescate", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Modulo para limpiar todos los campos del formulario y restaurar estados (restaura fecha del sistema)
    private void limpiarCampos() {
        tblRescates.clearSelection();
        txtCodigo.setText("");
        txtDireccion.setText("");
        cbPrioridad.setSelectedIndex(0);
        txtFecha.setText(UIUtils.obtenerFechaHoy());
        txtCodigoAnimal.setText("");
        txtNombreAnimal.setText("");
        bgEspecie.clearSelection();
        txtEspecieOtra.setText("");
        txtEspecieOtra.setEnabled(false);
        txtCodigo.setEditable(true);
        txtCodigoAnimal.setEditable(true);
        btnRegistrar.setEnabled(true);
    }

    // Modulo para auto-rellenar los campos al hacer clic en una fila de la tabla (mantiene la fecha del registro)
    private void seleccionarFila() {
        int row = tblRescates.getSelectedRow();
        if (row >= 0) {
            String codRescateCompleto = (String) tblRescates.getValueAt(row, 0);
            String codAnimalCompleto = (String) tblRescates.getValueAt(row, 5);

            txtCodigo.setText(UIUtils.extraerNumeroCodigo(codRescateCompleto, "R-"));
            txtDireccion.setText((String) tblRescates.getValueAt(row, 1));
            cbPrioridad.setSelectedItem(tblRescates.getValueAt(row, 2));
            txtFecha.setText((String) tblRescates.getValueAt(row, 4));

            if (codAnimalCompleto != null && !codAnimalCompleto.equals("Ninguno") && !codAnimalCompleto.isEmpty()) {
                txtCodigoAnimal.setText(UIUtils.extraerNumeroCodigo(codAnimalCompleto, "A-"));
                // Buscar datos adicionales del animal en memoria
                Animal animalEncontrado = null;
                for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                    Animal a = BaseDatosMemoria.animales[i];
                    if (a != null && a.getCodigo().equals(codAnimalCompleto)) {
                        animalEncontrado = a;
                        break;
                    }
                }
                if (animalEncontrado != null) {
                    txtNombreAnimal.setText(animalEncontrado.getNombre());
                    establecerEspecieEnFormulario(animalEncontrado.getEspecie());
                } else {
                    txtNombreAnimal.setText("");
                    establecerEspecieEnFormulario("");
                }
            } else {
                txtCodigoAnimal.setText("");
                txtNombreAnimal.setText("");
                establecerEspecieEnFormulario("");
            }

            txtCodigo.setEditable(false);
            txtCodigoAnimal.setEditable(false);
            btnRegistrar.setEnabled(false);
        }
    }
}
