package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.NodoCola;
import cris.sic.proyecto2.estructuras.NodoDoble;
import cris.sic.proyecto2.estructuras.NodoSimple;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.hilos.SimuladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.modelo.Visitante;
import cris.sic.proyecto2.util.ValidadorTexto;

/**
 * Panel para la gestión de ingresos, control de Garita 1 y Garita 2,
 * y visualización en tiempo real de la Cola de Entrada (FIFO).
 * 
 * @author cris_sic
 */
public class PanelEntrada extends JPanel implements GaritaListener {

    private final ListaDobleResidentes listaResidentes;
    private final SimuladorParqueo simulador;

    // Componentes ingreso residente
    private JComboBox<String> cmbResidentes;
    private JComboBox<String> cmbVehiculosResidente;

    // Componentes ingreso visitante
    private JTextField txtNombreVisitante;
    private JTextField txtPlacaVisitante;
    private JComboBox<String> cmbResidenteVisita;
    private JTextField txtMarcaVis;
    private JTextField txtModeloVis;
    private JTextField txtColorVis;
    private JComboBox<String> cmbTipoVis;

    // Visores de Garitas
    private JLabel lblEstadoGarita1;
    private JLabel lblVehiculoGarita1;
    private JLabel lblEstadoGarita2;
    private JLabel lblVehiculoGarita2;

    // Tabla Cola de Entrada
    private JTable tablaColaEntrada;
    private DefaultTableModel modeloColaEntrada;

    public PanelEntrada(ListaDobleResidentes listaResidentes, SimuladorParqueo simulador) {
        this.listaResidentes = listaResidentes;
        this.simulador = simulador;
        initComponents();
        actualizarCombosResidentes();
        simulador.setListenerGlobal(this);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(TemaUI.FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel Izquierdo: Formularios de Envío a Cola
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 10));
        panelIzquierdo.setBackground(TemaUI.FONDO_PANEL);
        panelIzquierdo.setPreferredSize(new Dimension(420, 600));

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.setFont(TemaUI.FUENTE_BOLD);
        pestañas.setBackground(TemaUI.FONDO_TARJETA);
        pestañas.setForeground(TemaUI.TEXTO_PRINCIPAL);

        pestañas.addTab("🚗 Vehículo Residente", crearPestañaResidente());
        pestañas.addTab("👥 Vehículo Visitante", crearPestañaVisitante());

        panelIzquierdo.add(pestañas, BorderLayout.NORTH);
        panelIzquierdo.add(crearTarjetaEstadoGaritas(), BorderLayout.CENTER);

        // Panel Derecho: Cola de Entrada FIFO
        JPanel panelDerecho = crearContenedorColaEntrada();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        split.setDividerLocation(430);
        split.setDividerSize(6);
        split.setBackground(TemaUI.FONDO_PANEL);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);
    }

    private JPanel crearPestañaResidente() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Seleccionar Residente:"), gbc);
        gbc.gridx = 1;
        cmbResidentes = new JComboBox<>();
        cmbResidentes.setFont(TemaUI.FUENTE_REGULAR);
        cmbResidentes.setBackground(TemaUI.FONDO_INPUT);
        cmbResidentes.setForeground(TemaUI.TEXTO_PRINCIPAL);
        cmbResidentes.addActionListener(e -> actualizarComboVehiculosResidente());
        tarjeta.add(cmbResidentes, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Vehículo (Estado FUERA):"), gbc);
        gbc.gridx = 1;
        cmbVehiculosResidente = new JComboBox<>();
        cmbVehiculosResidente.setFont(TemaUI.FUENTE_REGULAR);
        cmbVehiculosResidente.setBackground(TemaUI.FONDO_INPUT);
        cmbVehiculosResidente.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tarjeta.add(cmbVehiculosResidente, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnEnviarCola = TemaUI.crearBoton("🚗 Enviar a Cola de Entrada", TemaUI.EXITO, Color.WHITE);
        btnEnviarCola.addActionListener(e -> accionEnviarVehiculoResidente());
        tarjeta.add(btnEnviarCola, gbc);

        return tarjeta;
    }

    private JPanel crearPestañaVisitante() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre Visitante
        gbc.gridx = 0;
        gbc.gridy = 0;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Nombre Visitante:"), gbc);
        gbc.gridx = 1;
        txtNombreVisitante = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtNombreVisitante, gbc);

        // Placa
        gbc.gridx = 0;
        gbc.gridy = 1;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Placa:"), gbc);
        gbc.gridx = 1;
        txtPlacaVisitante = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtPlacaVisitante, gbc);

        // Residente a visitar
        gbc.gridx = 0;
        gbc.gridy = 2;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Residente a Visitar:"), gbc);
        gbc.gridx = 1;
        cmbResidenteVisita = new JComboBox<>();
        cmbResidenteVisita.setFont(TemaUI.FUENTE_REGULAR);
        cmbResidenteVisita.setBackground(TemaUI.FONDO_INPUT);
        cmbResidenteVisita.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tarjeta.add(cmbResidenteVisita, gbc);

        // Marca / Modelo
        gbc.gridx = 0;
        gbc.gridy = 3;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Marca:"), gbc);
        gbc.gridx = 1;
        txtMarcaVis = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtMarcaVis, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Modelo:"), gbc);
        gbc.gridx = 1;
        txtModeloVis = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtModeloVis, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Color:"), gbc);
        gbc.gridx = 1;
        txtColorVis = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtColorVis, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Tipo:"), gbc);
        gbc.gridx = 1;
        cmbTipoVis = new JComboBox<>(new String[]{"Automóvil", "Motocicleta", "Pickup"});
        cmbTipoVis.setFont(TemaUI.FUENTE_REGULAR);
        cmbTipoVis.setBackground(TemaUI.FONDO_INPUT);
        cmbTipoVis.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tarjeta.add(cmbTipoVis, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        JButton btnEnviarVis = TemaUI.crearBoton("👥 Enviar Visitante a Cola", TemaUI.PRIMARIO, Color.WHITE);
        btnEnviarVis.addActionListener(e -> accionEnviarVehiculoVisitante());
        tarjeta.add(btnEnviarVis, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaEstadoGaritas() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridLayout(2, 1, 0, 10));

        // Garita 1
        JPanel pnlG1 = new JPanel(new GridLayout(2, 1));
        pnlG1.setBackground(TemaUI.FONDO_INPUT);
        pnlG1.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JLabel lblG1Titulo = new JLabel("🏢 Garita de Entrada 1");
        lblG1Titulo.setFont(TemaUI.FUENTE_BOLD);
        lblG1Titulo.setForeground(TemaUI.PRIMARIO);
        lblEstadoGarita1 = new JLabel("Estado: En espera");
        lblEstadoGarita1.setFont(TemaUI.FUENTE_SMALL);
        lblEstadoGarita1.setForeground(TemaUI.TEXTO_SECUNDARIO);
        pnlG1.add(lblG1Titulo);
        pnlG1.add(lblEstadoGarita1);

        // Garita 2
        JPanel pnlG2 = new JPanel(new GridLayout(2, 1));
        pnlG2.setBackground(TemaUI.FONDO_INPUT);
        pnlG2.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JLabel lblG2Titulo = new JLabel("🏢 Garita de Entrada 2");
        lblG2Titulo.setFont(TemaUI.FUENTE_BOLD);
        lblG2Titulo.setForeground(TemaUI.PURPURA);
        lblEstadoGarita2 = new JLabel("Estado: En espera");
        lblEstadoGarita2.setFont(TemaUI.FUENTE_SMALL);
        lblEstadoGarita2.setForeground(TemaUI.TEXTO_SECUNDARIO);
        pnlG2.add(lblG2Titulo);
        pnlG2.add(lblEstadoGarita2);

        tarjeta.add(pnlG1);
        tarjeta.add(pnlG2);

        return tarjeta;
    }

    private JPanel crearContenedorColaEntrada() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(8, 8));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(TemaUI.FONDO_TARJETA);
        JLabel lbl = TemaUI.crearEtiquetaTitulo("⏳ Cola de Entrada en Vivo (Estructura FIFO)");
        header.add(lbl, BorderLayout.WEST);

        contenedor.add(header, BorderLayout.NORTH);

        String[] columnas = {"Posición", "Placa", "Propietario / Tipo", "Marca / Modelo", "Color", "Condición"};
        modeloColaEntrada = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaColaEntrada = new JTable(modeloColaEntrada);
        tablaColaEntrada.setFont(TemaUI.FUENTE_REGULAR);
        tablaColaEntrada.setRowHeight(26);
        tablaColaEntrada.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaColaEntrada.setBackground(TemaUI.FONDO_TARJETA);
        tablaColaEntrada.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaColaEntrada.setGridColor(TemaUI.BORDE_SUAVE);

        JScrollPane scroll = new JScrollPane(tablaColaEntrada);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    // =========================================================================
    // ACCIONES DE NEGOCIO Y SINCRONIZACIÓN
    // =========================================================================

    private void accionEnviarVehiculoResidente() {
        if (cmbResidentes.getSelectedIndex() < 0 || cmbVehiculosResidente.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente y un vehículo disponible.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idRes = ((String) cmbResidentes.getSelectedItem()).split(" - ")[0];
        Residente r = listaResidentes.buscarPorId(idRes);
        if (r == null) {
            return;
        }

        String itemVeh = (String) cmbVehiculosResidente.getSelectedItem();
        String placa = itemVeh.split(" ")[0];
        Vehiculo v = r.getListaVehiculos().buscarPorPlaca(placa);

        if (v == null) {
            return;
        }

        if (v.getEstado() != EstadoVehiculo.FUERA) {
            JOptionPane.showMessageDialog(this, "El vehículo no está en estado FUERA. Estado actual: " + v.getEstado(), "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean encolado = simulador.encolarVehiculoEntrada(v);
        if (encolado) {
            actualizarComboVehiculosResidente();
            refrescarTablaColaEntrada();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo encolar el vehículo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionEnviarVehiculoVisitante() {
        String nombre = txtNombreVisitante.getText().trim();
        String placa = txtPlacaVisitante.getText().trim();
        String marca = txtMarcaVis.getText().trim();
        String modelo = txtModeloVis.getText().trim();
        String color = txtColorVis.getText().trim();
        String tipo = (String) cmbTipoVis.getSelectedItem();

        if (nombre.isEmpty() || !ValidadorTexto.esPlacaValida(placa) || marca.isEmpty() || modelo.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos del visitante con formato válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cmbResidenteVisita.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el residente al que visita.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idResVis = ((String) cmbResidenteVisita.getSelectedItem()).split(" - ")[0];

        // Validar que la placa no esté en la cola ni en el parqueo actualmente
        if (simulador.getColaEntrada().contienePlaca(placa)
                || simulador.getControladorParqueo().buscarPorPlaca(placa) != null) {
            JOptionPane.showMessageDialog(this, "Esta placa ya se encuentra dentro o en cola de atención.", "Placa en Uso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Visitante vis = new Visitante(nombre, placa, idResVis, marca, modelo, color, tipo);
        Vehiculo autoVis = vis.getVehiculo();

        boolean encolado = simulador.encolarVehiculoEntrada(autoVis);
        if (encolado) {
            txtNombreVisitante.setText("");
            txtPlacaVisitante.setText("");
            txtMarcaVis.setText("");
            txtModeloVis.setText("");
            txtColorVis.setText("");
            refrescarTablaColaEntrada();
        }
    }

    public void actualizarCombosResidentes() {
        cmbResidentes.removeAllItems();
        cmbResidenteVisita.removeAllItems();

        NodoDoble actual = listaResidentes.getCabeza();
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null) {
                String item = r.getId() + " - " + r.getNombre() + (r.isEsSocio() ? " (Socio)" : "");
                cmbResidentes.addItem(item);
                cmbResidenteVisita.addItem(item);
            }
            actual = actual.getSiguiente();
        }

        actualizarComboVehiculosResidente();
    }

    private void actualizarComboVehiculosResidente() {
        cmbVehiculosResidente.removeAllItems();
        if (cmbResidentes.getSelectedIndex() >= 0) {
            String idRes = ((String) cmbResidentes.getSelectedItem()).split(" - ")[0];
            Residente r = listaResidentes.buscarPorId(idRes);
            if (r != null && r.getListaVehiculos() != null) {
                NodoSimple actual = r.getListaVehiculos().getCabeza();
                while (actual != null) {
                    Vehiculo v = actual.getDato();
                    if (v != null && v.getEstado() == EstadoVehiculo.FUERA) {
                        cmbVehiculosResidente.addItem(v.getPlaca() + " (" + v.getMarca() + " " + v.getModelo() + ")");
                    }
                    actual = actual.getSiguiente();
                }
            }
        }
    }

    public void refrescarTablaColaEntrada() {
        SwingUtilities.invokeLater(() -> {
            modeloColaEntrada.setRowCount(0);
            ColaFIFO cola = simulador.getColaEntrada();
            synchronized (cola) {
                NodoCola actual = cola.getPrimerNodo();
                int pos = 1;
                while (actual != null) {
                    Vehiculo v = actual.getDato();
                    if (v != null) {
                        String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante Temporal";
                        String condicion = (v.getPropietario() != null && v.getPropietario().isEsSocio()) ? "Socio VIP" : "General";
                        modeloColaEntrada.addRow(new Object[]{
                            "#" + pos++,
                            v.getPlaca(),
                            prop,
                            v.getMarca() + " " + v.getModelo(),
                            v.getColor(),
                            condicion
                        });
                    }
                    actual = actual.getSiguiente();
                }
            }
        });
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER (SwingUtilities.invokeLater)
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        SwingUtilities.invokeLater(() -> {
            refrescarTablaColaEntrada();
            actualizarComboVehiculosResidente();
            if (idGarita.contains("1")) {
                lblEstadoGarita1.setText("Atendido: " + vehiculo.getPlaca() + " -> Espacio " + espacioAsignado.getIdEspacio());
            } else {
                lblEstadoGarita2.setText("Atendido: " + vehiculo.getPlaca() + " -> Espacio " + espacioAsignado.getIdEspacio());
            }
        });
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        SwingUtilities.invokeLater(() -> {
            refrescarTablaColaEntrada();
            actualizarComboVehiculosResidente();
            if (idGarita.contains("1")) {
                lblEstadoGarita1.setText("RECHAZADO: " + vehiculo.getPlaca() + " (Sin espacio)");
            } else {
                lblEstadoGarita2.setText("RECHAZADO: " + vehiculo.getPlaca() + " (Sin espacio)");
            }
        });
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        SwingUtilities.invokeLater(this::actualizarComboVehiculosResidente);
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        SwingUtilities.invokeLater(() -> {
            if (idGarita.contains("1")) {
                lblEstadoGarita1.setText(estado);
            } else if (idGarita.contains("2")) {
                lblEstadoGarita2.setText(estado);
            }
        });
    }
}
