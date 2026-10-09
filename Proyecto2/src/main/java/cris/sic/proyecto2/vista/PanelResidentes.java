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
 * Implementa operaciones de inserción, actualización, eliminación y asignación con
 * control inteligente de botones habilitados/deshabilitados (enable/disable), limpieza guiada
 * y validación estricta del formato oficial de placas.
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

    // Botones Residente
    private JButton btnRegistrarResidente;
    private JButton btnActualizarResidente;
    private JButton btnEliminarResidente;
    private JButton btnLimpiarResidente;

    // Campos formulario Vehículo
    private JTextField txtPlaca;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtColor;
    private JComboBox<String> cmbTipoVehiculo;

    // Botones Vehículo
    private JButton btnAgregarVehiculo;
    private JButton btnEliminarVehiculo;
    private JButton btnLimpiarVehiculo;

    // Tablas
    private JTable tablaResidentes;
    private DefaultTableModel modeloTablaResidentes;
    private JTable tablaVehiculos;
    private DefaultTableModel modeloTablaVehiculos;

    // Entidades seleccionadas
    private Residente residenteSeleccionado;
    private Vehiculo vehiculoSeleccionado;

    public PanelResidentes(ListaDobleResidentes listaResidentes) {
        this.listaResidentes = (listaResidentes != null) ? listaResidentes : new ListaDobleResidentes();
        this.residenteSeleccionado = null;
        this.vehiculoSeleccionado = null;
        initComponents();
        refrescarTablaResidentes();
        actualizarEstadosGuiados();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(TemaUI.FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel Izquierdo: Formularios
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setBackground(TemaUI.FONDO_PANEL);
        panelIzquierdo.setPreferredSize(new Dimension(390, 600));

        panelIzquierdo.add(crearTarjetaResidente(), BorderLayout.NORTH);
        panelIzquierdo.add(crearTarjetaVehiculo(), BorderLayout.CENTER);

        // Panel Derecho: Tablas de visualización
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 15));
        panelDerecho.setBackground(TemaUI.FONDO_PANEL);
        panelDerecho.add(crearContenedorTablaResidentes());
        panelDerecho.add(crearContenedorTablaVehiculos());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setDividerLocation(400);
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
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 6));
        panelBotones.setBackground(TemaUI.FONDO_TARJETA);

        btnRegistrarResidente = TemaUI.crearBoton("Registrar", TemaUI.EXITO, Color.WHITE);
        btnActualizarResidente = TemaUI.crearBoton("Actualizar", TemaUI.PRIMARIO, Color.WHITE);
        btnEliminarResidente = TemaUI.crearBoton("Eliminar", TemaUI.PELIGRO, Color.WHITE);
        btnLimpiarResidente = TemaUI.crearBoton("🧹 Limpiar Residente", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnRegistrarResidente.addActionListener(e -> accionRegistrarResidente());
        btnActualizarResidente.addActionListener(e -> accionActualizarResidente());
        btnEliminarResidente.addActionListener(e -> accionEliminarResidente());
        btnLimpiarResidente.addActionListener(e -> limpiarFormularioResidente());

        panelBotones.add(btnRegistrarResidente);
        panelBotones.add(btnActualizarResidente);
        panelBotones.add(btnEliminarResidente);
        panelBotones.add(btnLimpiarResidente);

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
        tarjeta.add(TemaUI.crearEtiquetaCampo("Placa (7 chars):"), gbc);
        gbc.gridx = 1;
        txtPlaca = TemaUI.crearCampoTexto(10);
        txtPlaca.setEditable(true);
        txtPlaca.setEnabled(true);
        txtPlaca.setToolTipText("Ej: P123ABC (Auto/Pickup) o M123ABC (Moto)");
        tarjeta.add(txtPlaca, gbc);

        // Marca
        gbc.gridx = 0;
        gbc.gridy = 2;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Marca:"), gbc);
        gbc.gridx = 1;
        txtMarca = TemaUI.crearCampoTexto(10);
        txtMarca.setEditable(true);
        txtMarca.setEnabled(true);
        tarjeta.add(txtMarca, gbc);

        // Modelo
        gbc.gridx = 0;
        gbc.gridy = 3;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Modelo:"), gbc);
        gbc.gridx = 1;
        txtModelo = TemaUI.crearCampoTexto(10);
        txtModelo.setEditable(true);
        txtModelo.setEnabled(true);
        tarjeta.add(txtModelo, gbc);

        // Color
        gbc.gridx = 0;
        gbc.gridy = 4;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Color:"), gbc);
        gbc.gridx = 1;
        txtColor = TemaUI.crearCampoTexto(10);
        txtColor.setEditable(true);
        txtColor.setEnabled(true);
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
        JPanel panelBotonesVeh = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 6));
        panelBotonesVeh.setBackground(TemaUI.FONDO_TARJETA);

        btnAgregarVehiculo = TemaUI.crearBoton("+ Agregar Auto", TemaUI.EXITO, Color.WHITE);
        btnEliminarVehiculo = TemaUI.crearBoton("Eliminar Auto", TemaUI.PELIGRO, Color.WHITE);
        btnLimpiarVehiculo = TemaUI.crearBoton("🧹 Limpiar Campos", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnAgregarVehiculo.addActionListener(e -> accionAgregarVehiculo());
        btnEliminarVehiculo.addActionListener(e -> accionEliminarVehiculo());
        btnLimpiarVehiculo.addActionListener(e -> limpiarFormularioVehiculo());

        panelBotonesVeh.add(btnAgregarVehiculo);
        panelBotonesVeh.add(btnEliminarVehiculo);
        panelBotonesVeh.add(btnLimpiarVehiculo);

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

        tablaVehiculos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaVehiculos.getSelectedRow();
                if (fila >= 0 && residenteSeleccionado != null && residenteSeleccionado.getListaVehiculos() != null) {
                    String placa = (String) modeloTablaVehiculos.getValueAt(fila, 0);
                    seleccionarVehiculo(placa);
                }
            }
        });

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
            JOptionPane.showMessageDialog(this, "Todos los campos del residente son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ValidadorTexto.contienePipe(id) || ValidadorTexto.contienePipe(nombre) || ValidadorTexto.contienePipe(casa)) {
            JOptionPane.showMessageDialog(this, "No se permite el carácter delimitador '|'.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (listaResidentes.buscarPorId(id) != null) {
            JOptionPane.showMessageDialog(this, "El ID de residente ya existe en el sistema.", "ID Duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Residente nuevo = new Residente(id, nombre, casa, esSocio);
        if (listaResidentes.insertar(nuevo)) {
            JOptionPane.showMessageDialog(this, "Residente registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioResidente();
            refrescarTablaResidentes();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo registrar el residente.", "Error", JOptionPane.ERROR_MESSAGE);
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

        if (ValidadorTexto.contienePipe(nombre) || ValidadorTexto.contienePipe(casa)) {
            JOptionPane.showMessageDialog(this, "No se permite el carácter delimitador '|'.", "Validación", JOptionPane.WARNING_MESSAGE);
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
        actualizarEstadosGuiados();
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
                "¿Está seguro de eliminar al residente " + residenteSeleccionado.getNombre() + " y todos sus vehículos asociados?",
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

        String placaIngresada = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color = txtColor.getText().trim();
        String tipo = (String) cmbTipoVehiculo.getSelectedItem();

        // 1. Normalizar placa a MAYÚSCULAS
        String placa = placaIngresada.toUpperCase();

        // 2. Validación Estricta del Formato de Placa
        String errorPlaca = ValidadorTexto.obtenerErrorPlaca(placa, tipo);
        if (errorPlaca != null) {
            JOptionPane.showMessageDialog(this, errorPlaca, "Error de Formato", JOptionPane.WARNING_MESSAGE);
            txtPlaca.requestFocus();
            return;
        }

        // 3. Validación de campos obligatorios
        if (marca.isEmpty() || modelo.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del vehículo (Marca, Modelo y Color).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ValidadorTexto.contienePipe(marca) || ValidadorTexto.contienePipe(modelo) || ValidadorTexto.contienePipe(color)) {
            JOptionPane.showMessageDialog(this, "No se permite el carácter delimitador '|'.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Validar unicidad de placa en el sistema
        if (existePlacaEnSistema(placa)) {
            JOptionPane.showMessageDialog(this, "La placa '" + placa + "' ya pertenece a otro vehículo en el sistema.", "Placa Duplicada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 5. Inserción del vehículo normalizado
        Vehiculo v = new Vehiculo(placa, marca, modelo, color, tipo, residenteSeleccionado);
        if (residenteSeleccionado.agregarVehiculo(v)) {
            JOptionPane.showMessageDialog(this, "Vehículo [" + placa + "] agregado exitosamente al residente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            limpiarFormularioVehiculo();
            refrescarTablaResidentes();
            refrescarTablaVehiculos();
            actualizarEstadosGuiados();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar el vehículo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEliminarVehiculo() {
        if (residenteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (vehiculoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placa = vehiculoSeleccionado.getPlaca();
        if (vehiculoSeleccionado.getEstado() != EstadoVehiculo.FUERA) {
            JOptionPane.showMessageDialog(this,
                    "REGLA DE ORO: No se puede eliminar el vehículo porque su estado actual es '" + vehiculoSeleccionado.getEstado() + "'. Debe estar FUERA.",
                    "Eliminación Bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de desvincular y eliminar el vehículo con placa " + placa + "?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (residenteSeleccionado.getListaVehiculos().eliminar(placa)) {
                JOptionPane.showMessageDialog(this, "Vehículo eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormularioVehiculo();
                refrescarTablaResidentes();
                refrescarTablaVehiculos();
                actualizarEstadosGuiados();
            }
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
            limpiarFormularioVehiculo();
            refrescarTablaVehiculos();
            actualizarEstadosGuiados();
        }
    }

    private void seleccionarVehiculo(String placa) {
        if (residenteSeleccionado != null && residenteSeleccionado.getListaVehiculos() != null) {
            vehiculoSeleccionado = residenteSeleccionado.getListaVehiculos().buscarPorPlaca(placa);
            if (vehiculoSeleccionado != null) {
                txtPlaca.setText(vehiculoSeleccionado.getPlaca());
                txtMarca.setText(vehiculoSeleccionado.getMarca());
                txtModelo.setText(vehiculoSeleccionado.getModelo());
                txtColor.setText(vehiculoSeleccionado.getColor());
                cmbTipoVehiculo.setSelectedItem(vehiculoSeleccionado.getTipo());

                // Mantener los campos siempre editables y habilitados para fluidez total
                txtPlaca.setEditable(true);
                txtPlaca.setEnabled(true);
                txtMarca.setEditable(true);
                txtMarca.setEnabled(true);
                txtModelo.setEditable(true);
                txtModelo.setEnabled(true);
                txtColor.setEditable(true);
                txtColor.setEnabled(true);

                actualizarEstadosGuiados();
            }
        }
    }

    /**
     * Limpia todos los campos del formulario de residente, restablece el campo ID como editable,
     * desmarca la tabla de residentes y actualiza la botonera guiada.
     */
    public void limpiarFormularioResidente() {
        residenteSeleccionado = null;
        vehiculoSeleccionado = null;
        txtIdResidente.setText("");
        txtIdResidente.setEditable(true);
        txtIdResidente.setEnabled(true);
        txtNombreResidente.setText("");
        txtCasaLote.setText("");
        chkEsSocio.setSelected(false);
        if (tablaResidentes != null) {
            tablaResidentes.clearSelection();
        }
        limpiarFormularioVehiculo();
        refrescarTablaVehiculos();
        actualizarEstadosGuiados();
        txtIdResidente.requestFocus();
    }

    /**
     * Limpia los campos del formulario de vehículos, restablece el selector de tipo
     * y desmarca la selección en la tabla de vehículos.
     */
    public void limpiarFormularioVehiculo() {
        vehiculoSeleccionado = null;
        txtPlaca.setText("");
        txtPlaca.setEditable(true);
        txtPlaca.setEnabled(true);
        txtMarca.setText("");
        txtMarca.setEditable(true);
        txtMarca.setEnabled(true);
        txtModelo.setText("");
        txtModelo.setEditable(true);
        txtModelo.setEnabled(true);
        txtColor.setText("");
        txtColor.setEditable(true);
        txtColor.setEnabled(true);
        if (cmbTipoVehiculo != null && cmbTipoVehiculo.getItemCount() > 0) {
            cmbTipoVehiculo.setSelectedIndex(0);
        }
        if (tablaVehiculos != null) {
            tablaVehiculos.clearSelection();
        }
        actualizarEstadosGuiados();
    }

    /**
     * Sincroniza el estado de habilitación/deshabilitación (enable/disable) de cada botón
     * para guiar al usuario de forma intuitiva según el contexto actual.
     */
    private void actualizarEstadosGuiados() {
        boolean hayResidenteSeleccionado = (residenteSeleccionado != null);
        boolean hayVehiculoSeleccionado = (vehiculoSeleccionado != null);

        // Control botones Residente
        btnRegistrarResidente.setEnabled(!hayResidenteSeleccionado);
        btnActualizarResidente.setEnabled(hayResidenteSeleccionado);
        btnEliminarResidente.setEnabled(hayResidenteSeleccionado);
        btnLimpiarResidente.setEnabled(true);

        // Control botones Vehículo
        if (hayResidenteSeleccionado) {
            int cantidadAutos = residenteSeleccionado.getCantidadVehiculos();
            boolean puedeAgregar = (cantidadAutos < 3);
            btnAgregarVehiculo.setEnabled(puedeAgregar);
            btnEliminarVehiculo.setEnabled(hayVehiculoSeleccionado);
            btnLimpiarVehiculo.setEnabled(true);

            if (cantidadAutos >= 3) {
                btnAgregarVehiculo.setToolTipText("Límite alcanzado: Este residente ya tiene 3 vehículos.");
            } else {
                btnAgregarVehiculo.setToolTipText("Agregar un nuevo vehículo a " + residenteSeleccionado.getNombre());
            }
        } else {
            btnAgregarVehiculo.setEnabled(false);
            btnEliminarVehiculo.setEnabled(false);
            btnLimpiarVehiculo.setEnabled(true);
            btnAgregarVehiculo.setToolTipText("Seleccione primero un residente de la tabla.");
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
        modeloTablaResidentes.fireTableDataChanged();
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
        modeloTablaVehiculos.fireTableDataChanged();
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
