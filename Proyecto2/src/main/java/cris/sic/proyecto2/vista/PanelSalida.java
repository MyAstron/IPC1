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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import cris.sic.proyecto2.estructuras.ColaFIFO;
import cris.sic.proyecto2.estructuras.ListaCircularParqueo;
import cris.sic.proyecto2.estructuras.NodoCircular;
import cris.sic.proyecto2.estructuras.NodoCola;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.hilos.SimuladorParqueo;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.EstadoVehiculo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Panel para la gestión de solicitudes de salida, control de la Garita de Salida
 * y monitoreo de vehículos estacionados y en cola de salida (FIFO).
 * 
 * @author cris_sic
 */
public class PanelSalida extends JPanel implements GaritaListener {

    private final ControladorParqueo controladorParqueo;
    private final SimuladorParqueo simulador;

    // Campos de búsqueda / acción
    private JTextField txtPlacaSalida;
    private JLabel lblEstadoGaritaSalida;
    private JLabel lblVehiculoSaliente;

    // Tablas
    private JTable tablaEstacionados;
    private DefaultTableModel modeloEstacionados;
    private JTable tablaColaSalida;
    private DefaultTableModel modeloColaSalida;

    public PanelSalida(ControladorParqueo controladorParqueo, SimuladorParqueo simulador) {
        this.controladorParqueo = controladorParqueo;
        this.simulador = simulador;
        initComponents();
        refrescarTablas();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(TemaUI.FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel Izquierdo: Control y Garita de Salida
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 15));
        panelIzquierdo.setBackground(TemaUI.FONDO_PANEL);
        panelIzquierdo.setPreferredSize(new Dimension(380, 600));

        panelIzquierdo.add(crearTarjetaSolicitudSalida(), BorderLayout.NORTH);
        panelIzquierdo.add(crearTarjetaVisorGaritaSalida(), BorderLayout.CENTER);

        // Panel Derecho: Tablas Estacionados y Cola de Salida
        JPanel panelDerecho = new JPanel(new GridLayout(2, 1, 0, 15));
        panelDerecho.setBackground(TemaUI.FONDO_PANEL);
        panelDerecho.add(crearContenedorEstacionados());
        panelDerecho.add(crearContenedorColaSalida());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        split.setDividerLocation(390);
        split.setDividerSize(6);
        split.setBackground(TemaUI.FONDO_PANEL);
        split.setBorder(null);

        add(split, BorderLayout.CENTER);
    }

    private JPanel crearTarjetaSolicitudSalida() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        tarjeta.add(TemaUI.crearEtiquetaTitulo("🚙 Solicitar Salida de Vehículo"), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        tarjeta.add(TemaUI.crearEtiquetaCampo("Placa del Vehículo:"), gbc);
        gbc.gridx = 1;
        txtPlacaSalida = TemaUI.crearCampoTexto(10);
        tarjeta.add(txtPlacaSalida, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnSalida = TemaUI.crearBoton("🚙 Enviar a Cola de Salida", TemaUI.PELIGRO, Color.WHITE);
        btnSalida.addActionListener(e -> accionSolicitarSalida());
        tarjeta.add(btnSalida, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaVisorGaritaSalida() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridLayout(3, 1, 0, 10));

        JLabel lblTitulo = new JLabel("🏢 Garita de Salida");
        lblTitulo.setFont(TemaUI.FUENTE_TITULO);
        lblTitulo.setForeground(TemaUI.PELIGRO);

        lblEstadoGaritaSalida = new JLabel("Estado: En servicio");
        lblEstadoGaritaSalida.setFont(TemaUI.FUENTE_BOLD);
        lblEstadoGaritaSalida.setForeground(TemaUI.TEXTO_PRINCIPAL);

        lblVehiculoSaliente = new JLabel("Último movimiento: Ninguno");
        lblVehiculoSaliente.setFont(TemaUI.FUENTE_SMALL);
        lblVehiculoSaliente.setForeground(TemaUI.TEXTO_SECUNDARIO);

        tarjeta.add(lblTitulo);
        tarjeta.add(lblEstadoGaritaSalida);
        tarjeta.add(lblVehiculoSaliente);

        return tarjeta;
    }

    private JPanel crearContenedorEstacionados() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(5, 5));

        JLabel lbl = TemaUI.crearEtiquetaTitulo("🅿️ Vehículos Actualmente Estacionados");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Espacio", "Área", "Placa", "Marca / Modelo", "Color", "Propietario"};
        modeloEstacionados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaEstacionados = new JTable(modeloEstacionados);
        tablaEstacionados.setFont(TemaUI.FUENTE_REGULAR);
        tablaEstacionados.setRowHeight(24);
        tablaEstacionados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEstacionados.setBackground(TemaUI.FONDO_TARJETA);
        tablaEstacionados.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaEstacionados.setGridColor(TemaUI.BORDE_SUAVE);

        tablaEstacionados.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaEstacionados.getSelectedRow();
            if (fila >= 0) {
                txtPlacaSalida.setText((String) modeloEstacionados.getValueAt(fila, 2));
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEstacionados);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearContenedorColaSalida() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(5, 5));

        JLabel lbl = TemaUI.crearEtiquetaTitulo("⏳ Cola de Salida en Vivo (Estructura FIFO)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Posición", "Placa", "Marca / Modelo", "Color", "Estado"};
        modeloColaSalida = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaColaSalida = new JTable(modeloColaSalida);
        tablaColaSalida.setFont(TemaUI.FUENTE_REGULAR);
        tablaColaSalida.setRowHeight(24);
        tablaColaSalida.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaColaSalida.setBackground(TemaUI.FONDO_TARJETA);
        tablaColaSalida.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaColaSalida.setGridColor(TemaUI.BORDE_SUAVE);

        JScrollPane scroll = new JScrollPane(tablaColaSalida);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));
        contenedor.add(scroll, BorderLayout.CENTER);

        return contenedor;
    }

    // =========================================================================
    // ACCIONES DE NEGOCIO
    // =========================================================================

    private void accionSolicitarSalida() {
        String placa = txtPlacaSalida.getText().trim().toUpperCase();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese o seleccione la placa de un vehículo estacionado.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EspacioParqueo espacio = controladorParqueo.buscarPorPlaca(placa);
        if (espacio == null || espacio.getVehiculoEstacionado() == null) {
            JOptionPane.showMessageDialog(this, "El vehículo con placa '" + placa + "' no se encuentra estacionado en el parqueo.", "No Encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehiculo vehiculo = espacio.getVehiculoEstacionado();
        if (vehiculo.getEstado() != EstadoVehiculo.ESTACIONADO) {
            JOptionPane.showMessageDialog(this, "El vehículo no se encuentra en estado ESTACIONADO.", "Estado Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean encolado = simulador.encolarVehiculoSalida(vehiculo);
        if (encolado) {
            txtPlacaSalida.setText("");
            refrescarTablas();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo encolar el vehículo para salida (¿ya está en cola?).", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refrescarTablas() {
        SwingUtilities.invokeLater(() -> {
            refrescarTablaEstacionados();
            refrescarTablaColaSalida();
        });
    }

    private void refrescarTablaEstacionados() {
        modeloEstacionados.setRowCount(0);

        // Recorrer área de socios
        ListaCircularParqueo socios = controladorParqueo.getAreaSocios();
        synchronized (socios) {
            NodoCircular actual = socios.getCabeza();
            int pasos = 0;
            while (pasos < socios.getCapacidad() && actual != null) {
                EspacioParqueo esp = actual.getDato();
                if (esp != null && esp.estaOcupado() && esp.getVehiculoEstacionado() != null) {
                    Vehiculo v = esp.getVehiculoEstacionado();
                    String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante";
                    modeloEstacionados.addRow(new Object[]{
                        esp.getIdEspacio(),
                        "Socios",
                        v.getPlaca(),
                        v.getMarca() + " " + v.getModelo(),
                        v.getColor(),
                        prop
                    });
                }
                actual = actual.getSiguiente();
                pasos++;
            }
        }

        // Recorrer área general
        ListaCircularParqueo general = controladorParqueo.getAreaGeneral();
        synchronized (general) {
            NodoCircular actual = general.getCabeza();
            int pasos = 0;
            while (pasos < general.getCapacidad() && actual != null) {
                EspacioParqueo esp = actual.getDato();
                if (esp != null && esp.estaOcupado() && esp.getVehiculoEstacionado() != null) {
                    Vehiculo v = esp.getVehiculoEstacionado();
                    String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante";
                    modeloEstacionados.addRow(new Object[]{
                        esp.getIdEspacio(),
                        "General",
                        v.getPlaca(),
                        v.getMarca() + " " + v.getModelo(),
                        v.getColor(),
                        prop
                    });
                }
                actual = actual.getSiguiente();
                pasos++;
            }
        }
    }

    private void refrescarTablaColaSalida() {
        modeloColaSalida.setRowCount(0);
        ColaFIFO cola = simulador.getColaSalida();
        synchronized (cola) {
            NodoCola actual = cola.getPrimerNodo();
            int pos = 1;
            while (actual != null) {
                Vehiculo v = actual.getDato();
                if (v != null) {
                    modeloColaSalida.addRow(new Object[]{
                        "#" + pos++,
                        v.getPlaca(),
                        v.getMarca() + " " + v.getModelo(),
                        v.getColor(),
                        v.getEstado().getDescripcion()
                    });
                }
                actual = actual.getSiguiente();
            }
        }
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        SwingUtilities.invokeLater(this::refrescarTablas);
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        // No afecta parqueo
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        SwingUtilities.invokeLater(() -> {
            refrescarTablas();
            lblVehiculoSaliente.setText("Último atendido: " + vehiculo.getPlaca() + " (Liberó " + (espacioLiberado != null ? espacioLiberado.getIdEspacio() : "espacio") + ")");
        });
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        if (idGarita.contains("Salida")) {
            SwingUtilities.invokeLater(() -> lblEstadoGaritaSalida.setText("Estado: " + estado));
        }
    }
}
