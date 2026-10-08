package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.NodoDoble;
import cris.sic.proyecto2.estructuras.NodoSimple;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Panel de gestión y administración de Residentes y sus Vehículos asociados.
 * Implementa operaciones de inserción, actualización, eliminación y asignación con validaciones visuales.
 * 
 * @author cris_sic
 */
public class PanelResidentes extends JPanel {

    private final ListaDobleResidentes listaResidentes;

    // Campos formulario Residente
    private JTextField txtIdResidente;
    private JTextField txtNombreResidente;
    private JTextField txtCasaLote;
    private JCheckBox chkEsSocio;

    // Campos formulario Vehículo
    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtColor;
    private JComboBox<String> cmbTipoVehiculo;

    // Tablas
    private JTable tablaResidentes;
    private DefaultTableModel modeloTablaResidentes;
    private JTable tablaVehiculos;
    private DefaultTableModel modeloTablaVehiculos;

    // Residente seleccionado actualmente
    private Residente residenteSeleccionado;

    public PanelResidentes(ListaDobleResidentes listaResidentes) {
        this.listaResidentes = (listaResidentes != null) ? listaResidentes : new ListaDobleResidentes();
        this.residenteSeleccionado = null;
        initComponents();
        refrescarTablaResidentes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(TemaUI.FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel Izquierdo: Formularios
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setBackground(TemaUI.FONDO_PANEL);
        panelIzquierdo.setPreferredSize(new Dimension(380, 600));

        panelIzquierdo.add(crearTarjetaResidente(), BorderLayout.NORTH);
        panelIzquierdo.add(crearTarjetaVehiculo(), BorderLayout.CENTER);

        // Panel Derecho: Tablas de visualización
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 15));
        panelDerecho.setBackground(TemaUI.FONDO_PANEL);
        panelDerecho.add(crearContenedorTablaResidentes());
        panelDerecho.add(crearContenedorTablaVehiculos());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(390);
        splitPane.setDividerSize(6);
        splitPane.setBackground(TemaUI.FONDO_PANEL);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaResidente() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = TemaUI.crearEtiquetaTitulo("👤 Datos del Residente");
        tarjeta.add(lblTitulo, gbc);

        // Campo ID
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        tarjeta.add(TemaUI.crearEtiquetaCampo("ID Único:"), gbc);
        gbc.gridx = 1;
        txtIdResidente = TemaUI.crearCampoTexto(12);
        tarjeta.add(txtIdResidente, gbc);

        // Campo Nombre
        gbc.gridx = 0;
        gbc.gridy = 2;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Nombre:"), gbc);
        gbc.gridx = 1;
        txtNombreResidente = TemaUI.crearCampoTexto(12);
        tarjeta.add(txtNombreResidente, gbc);

        // Campo Casa/Lote
        gbc.gridx = 0;
        gbc.gridy = 3;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Casa / Lote:"), gbc);
        gbc.gridx = 1;
        txtCasaLote = TemaUI.crearCampoTexto(12);
        tarjeta.add(txtCasaLote, gbc);

        // Checkbox Socio
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        chkEsSocio = new JCheckBox("Membresía Socio del Club");
        chkEsSocio.setFont(TemaUI.FUENTE_BOLD);
        chkEsSocio.setForeground(TemaUI.PURPURA);
        chkEsSocio.setBackground(TemaUI.FONDO_TARJETA);
        chkEsSocio.setFocusPainted(false);
        tarjeta.add(chkEsSocio, gbc);

        // Botonera de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        panelBotones.setBackground(TemaUI.FONDO_TARJETA);

        JButton btnGuardar = TemaUI.crearBoton("Registrar", TemaUI.EXITO, Color.WHITE);
        JButton btnActualizar = TemaUI.crearBoton("Actualizar", TemaUI.PRIMARIO, Color.WHITE);
        JButton btnEliminar = TemaUI.crearBoton("Eliminar", TemaUI.PELIGRO, Color.WHITE);
        JButton btnLimpiar = TemaUI.crearBoton("Limpiar", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnGuardar.addActionListener(e -> accionRegistrarResidente());
        btnActualizar.addActionListener(e -> accionActualizarResidente());
        btnEliminar.addActionListener(e -> accionEliminarResidente());
        btnLimpiar.addActionListener(e -> limpiarFormularioResidente());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        gbc.gridy = 5;
        tarjeta.add(panelBotones, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaVehiculo() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblTitulo = TemaUI.crearEtiquetaTitulo("🚗 Asignar Vehículo (Máx. 3)");
        tarjeta.add(lblTitulo, gbc);

        // Placa
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Placa:"), gbc);
        gbc.gridx = 1;
        txtPlaca = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtPlaca, gbc);

        // Marca
        gbc.gridx = 0;
        gbc.gridy = 2;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Marca:"), gbc);
        gbc.gridx = 1;
        txtMarca = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtMarca, gbc);

        // Modelo
        gbc.gridx = 0;
        gbc.gridy = 3;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Modelo:"), gbc);
        gbc.gridx = 1;
        txtModelo = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtModelo, gbc);

        // Color
        gbc.gridx = 0;
        gbc.gridy = 4;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Color:"), gbc);
        gbc.gridx = 1;
        txtColor = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtColor, gbc);

        // Tipo
        gbc.gridx = 0;
        gbc.gridy = 5;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Tipo:"), gbc);
        gbc.gridx = 1;
        String[] tipos = {"Automóvil", "Motocicleta", "Pickup"};
        cmbTipoVehiculo = new JComboBox<>(tipos);
        cmbTipoVehiculo.setFont(TemaUI.FUENTE_REGULAR);
        cmbTipoVehiculo.setBackground(TemaUI.FONDO_INPUT);
        cmbTipoVehiculo.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tarjeta.add(cmbTipoVehiculo, gbc);

        // Botonera vehículo
        JPanel panelBotonesVeh = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 6));
        panelBotonesVeh.setBackground(TemaUI.FONDO_TARJETA);

        JButton btnAgregarVeh = TemaUI.crearBoton("Agregar Auto", TemaUI.EXITO, Color.WHITE);
        JButton btnEliminarVeh = TemaUI.crearBoton("Eliminar Auto", TemaUI.PELIGRO, Color.WHITE);

        btnAgregarVeh.addActionListener(e -> accionAgregarVehiculo());
        btnEliminarVeh.addActionListener(e -> accionEliminarVehiculo());

        panelBotonesVeh.add(btnAgregarVeh);
        panelBotonesVeh.add(btnEliminarVeh);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        tarjeta.add(panelBotonesVeh, gbc);

        return tarjeta;
    }

    private JPanel crearContenedorTablaResidentes() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(5, 5));

        JLabel lbl = TemaUI.crearEtiquetaTitulo("📋 Listado General de Residentes (Lista Doble)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre Completo", "Casa / Lote", "Es Socio", "Cant. Vehículos"};
        modeloTablaResidentes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaResidentes = new JTable(modeloTablaResidentes);
        tablaResidentes.setFont(TemaUI.FUENTE_REGULAR);
        tablaResidentes.setRowHeight(24);
        tablaResidentes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaResidentes.setBackground(TemaUI.FONDO_TARJETA);
        tablaResidentes.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaResidentes.setGridColor(TemaUI.BORDE_SUAVE);

        tablaResidentes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaResidentes.getSelectedRow();
                if (fila >= 0) {
                    String id = (String) modeloTablaResidentes.getValueAt(fila, 0);
                    seleccionarResidente(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaResidentes);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearContenedorTablaVehiculos() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(5, 5));

        JLabel lbl = TemaUI.crearEtiquetaTitulo("🚘 Vehículos del Residente Seleccionado (Lista Simple)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Placa", "Marca", "Modelo", "Color", "Tipo", "Estado de Flujo"};
        modeloTablaVehiculos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVehiculos = new JTable(modeloTablaVehiculos);
        tablaVehiculos.setFont(TemaUI.FUENTE_REGULAR);
        tablaVehiculos.setRowHeight(24);
        tablaVehiculos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaVehiculos.setBackground(TemaUI.FONDO_TARJETA);
        tablaVehiculos.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaVehiculos.setGridColor(TemaUI.BORDE_SUAVE);

        JScrollPane scroll = new JScrollPane(tablaVehiculos);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    // =========================================================================
    // ACCIONES DE LÓGICA Y NEGOCIO
    // =========================================================================

    private void accionRegistrarResidente() {
        String id = txtIdResidente.getText().trim();
        String nombre = txtNombreResidente.getText().trim();
        String casa = txtCasaLote.getText().trim();
        boolean esSocio = chkEsSocio.isSelected();

        if (id.isEmpty() || nombre.isEmpty() || casa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de residente son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ValidadorTexto.contienePipe(id) || ValidadorTexto.contienePipe(nombre) || ValidadorTexto.contienePipe(casa)) {
            JOptionPane.showMessageDialog(this, "No se permite el carácter delimitador '|'.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listaResidentes.buscarPorId(id) != null) {
            JOptionPane.showMessageDialog(this, "El ID de residente ya existe en el sistema.", "Duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Residente nuevo = new Residente(id, nombre, casa, esSocio);
        if (listaResidentes.insertar(nuevo)) {
            JOptionPane.showMessageDialog(this, "Residente registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioResidente();
            refrescarTablaResidentes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo insertar el residente.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionActualizarResidente() {
        if (residenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente de la tabla para actualizar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = txtNombreResidente.getText().trim();
        String casa = txtCasaLote.getText().trim();
        boolean nuevoEstadoSocio = chkEsSocio.isSelected();

        if (nombre.isEmpty() || casa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y Casa/Lote no pueden estar vacíos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Regla de Oro: Cambio de socio solo permitido si todos los autos están FUERA
        if (nuevoEstadoSocio != residenteSeleccionado.isEsSocio()) {
            boolean cambioSocioExitoso = residenteSeleccionado.cambiarEstadoSocio(nuevoEstadoSocio);
            if (!cambioSocioExitoso) {
                JOptionPane.showMessageDialog(this,
                        "REGLA DE ORO: No se puede cambiar la condición de socio si el residente tiene vehículos activos en el parqueo.",
                        "Acción Bloqueada", JOptionPane.WARNING_MESSAGE);
                chkEsSocio.setSelected(residenteSeleccionado.isEsSocio());
                return;
            }
        }

        residenteSeleccionado.setNombre(nombre);
        residenteSeleccionado.setCasaLote(casa);

        JOptionPane.showMessageDialog(this, "Datos del residente actualizados con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        refrescarTablaResidentes();
    }

    private void accionEliminarResidente() {
        if (residenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Regla de Oro: Bloqueo de eliminación si tiene autos dentro
        if (residenteSeleccionado.tieneVehiculosDentro()) {
            JOptionPane.showMessageDialog(this,
                    "REGLA DE ORO: No se puede eliminar al residente porque tiene vehículos activos en el parqueo.",
                    "Eliminación Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar al residente " + residenteSeleccionado.getNombre() + " y todos sus vehículos?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String id = residenteSeleccionado.getId();
            if (listaResidentes.eliminar(id)) {
                JOptionPane.showMessageDialog(this, "Residente eliminado satisfactoriamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormularioResidente();
                refrescarTablaResidentes();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el residente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionAgregarVehiculo() {
        if (residenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente de la tabla antes de agregar un vehículo.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (residenteSeleccionado.getCantidadVehiculos() >= 3) {
            JOptionPane.showMessageDialog(this, "LÍMITE ALCANZADO: El residente ya tiene el máximo permitido de 3 vehículos.", "Capacidad Máxima", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color = txtColor.getText().trim();
        String tipo = (String) cmbTipoVehiculo.getSelectedItem();

        if (!ValidadorTexto.esPlacaValida(placa)) {
            JOptionPane.showMessageDialog(this, "Placa inválida (3-10 caracteres, sin '|').", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (marca.isEmpty() || modelo.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del vehículo.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar unicidad de placa en el sistema
        if (existePlacaEnSistema(placa)) {
            JOptionPane.showMessageDialog(this, "La placa ingresada ya pertenece a otro vehículo en el sistema.", "Placa Duplicada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehiculo v = new Vehiculo(placa, marca, modelo, color, tipo, residenteSeleccionado);
        if (residenteSeleccionado.agregarVehiculo(v)) {
            JOptionPane.showMessageDialog(this, "Vehículo agregado exitosamente al residente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioVehiculo();
            refrescarTablaResidentes();
            refrescarTablaVehiculos();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar el vehículo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarVehiculo() {
        if (residenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int fila = tablaVehiculos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placa = (String) modeloTablaVehiculos.getValueAt(fila, 0);
        Vehiculo v = residenteSeleccionado.getListaVehiculos().buscarPorPlaca(placa);

        if (v != null && v.getEstado() != EstadoVehiculo.FUERA) {
            JOptionPane.showMessageDialog(this,
                    "REGLA DE ORO: No se puede eliminar el vehículo porque su estado actual es '" + v.getEstado() + "'. Debe estar FUERA.",
                    "Eliminación Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (residenteSeleccionado.getListaVehiculos().eliminar(placa)) {
            JOptionPane.showMessageDialog(this, "Vehículo eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            refrescarTablaResidentes();
            refrescarTablaVehiculos();
        }
    }

    private void seleccionarResidente(String id) {
        residenteSeleccionado = listaResidentes.buscarPorId(id);
        if (residenteSeleccionado != null) {
            txtIdResidente.setText(residenteSeleccionado.getId());
            txtIdResidente.setEditable(false);
            txtNombreResidente.setText(residenteSeleccionado.getNombre());
            txtCasaLote.setText(residenteSeleccionado.getCasaLote());
            chkEsSocio.setSelected(residenteSeleccionado.isEsSocio());
            refrescarTablaVehiculos();
        }
    }

    private void limpiarFormularioResidente() {
        residenteSeleccionado = null;
        txtIdResidente.setText("");
        txtIdResidente.setEditable(true);
        txtNombreResidente.setText("");
        txtCasaLote.setText("");
        chkEsSocio.setSelected(false);
        limpiarFormularioVehiculo();
        refrescarTablaVehiculos();
    }

    private void limpiarFormularioVehiculo() {
        txtPlaca.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        txtColor.setText("");
        if (cmbTipoVehiculo != null && cmbTipoVehiculo.getItemCount() > 0) {
            cmbTipoVehiculo.setSelectedIndex(0);
        }
    }

    public void refrescarTablaResidentes() {
        modeloTablaResidentes.setRowCount(0);
        NodoDoble actual = listaResidentes.getCabeza();
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null) {
                modeloTablaResidentes.addRow(new Object[]{
                    r.getId(),
                    r.getNombre(),
                    r.getCasaLote(),
                    r.isEsSocio() ? "SÍ" : "NO",
                    r.getCantidadVehiculos() + "/3"
                });
            }
            actual = actual.getSiguiente();
        }
    }

    public void refrescarTablaVehiculos() {
        modeloTablaVehiculos.setRowCount(0);
        if (residenteSeleccionado != null && residenteSeleccionado.getListaVehiculos() != null) {
            NodoSimple actual = residenteSeleccionado.getListaVehiculos().getCabeza();
            while (actual != null) {
                Vehiculo v = actual.getDato();
                if (v != null) {
                    modeloTablaVehiculos.addRow(new Object[]{
                        v.getPlaca(),
                        v.getMarca(),
                        v.getModelo(),
                        v.getColor(),
                        v.getTipo(),
                        v.getEstado().getDescripcion()
                    });
                }
                actual = actual.getSiguiente();
            }
        }
    }

    private boolean existePlacaEnSistema(String placa) {
        NodoDoble actual = listaResidentes.getCabeza();
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null && r.getListaVehiculos() != null) {
                if (r.getListaVehiculos().buscarPorPlaca(placa) != null) {
                    return true;
                }
            }
            actual = actual.getSiguiente();
        }
        return false;
    }
}
