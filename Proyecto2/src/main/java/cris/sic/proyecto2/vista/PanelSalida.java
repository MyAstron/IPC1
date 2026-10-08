package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
 * y monitoreo de vehículos estacionados y en cola de salida (FIFO) con limpieza y guía interactiva.
 * 
 * @author cris_sic
 */
public class PanelSalida extends JPanel implements GaritaListener {

    private final ControladorParqueo controladorParqueo;
    private final SimuladorParqueo simulador;

    // Campos de búsqueda / acción
    private JTextField txtPlacaSalida;
    private JButton btnSalida;
    private JButton btnLimpiarSalida;

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
        actualizarEstadoBotonSalida();
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
        txtPlacaSalida.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                actualizarEstadoBotonSalida();
            }
        });
        tarjeta.add(txtPlacaSalida, gbc);

        // Botonera de Salida
        JPanel panelBotonesSalida = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBotonesSalida.setBackground(TemaUI.FONDO_TARJETA);

        btnSalida = TemaUI.crearBoton("🚙 Enviar a Salida", TemaUI.PELIGRO, Color.WHITE);
        btnLimpiarSalida = TemaUI.crearBoton("🧹 Limpiar", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnSalida.addActionListener(e -> accionSolicitarSalida());
        btnLimpiarSalida.addActionListener(e -> limpiarFormularioSalida());

        panelBotonesSalida.add(btnSalida);
        panelBotonesSalida.add(btnLimpiarSalida);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        tarjeta.add(panelBotonesSalida, gbc);

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

        JLabel lbl = TemaUI.crearEtiquetaTitulo("🅿️ Vehículos Actualmente Estacionados (Clic para seleccionar)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Espacio", "Área", "Placa", "Conductor / Dueño", "Vehículo", "Tipo"};
        modeloEstacionados = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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

        tablaEstacionados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaEstacionados.getSelectedRow();
                if (fila >= 0) {
                    String placa = (String) modeloEstacionados.getValueAt(fila, 2);
                    txtPlacaSalida.setText(placa);
                    actualizarEstadoBotonSalida();
                }
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

        JLabel lbl = TemaUI.crearEtiquetaTitulo("🚦 Cola de Espera para Salida (FIFO)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Pos.", "Placa", "Vehículo", "Propietario / Visitante", "Estado"};
        modeloColaSalida = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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
    // ACCIONES
    // =========================================================================

    private void accionSolicitarSalida() {
        String placa = txtPlacaSalida.getText().trim().toUpperCase();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese la placa o seleccione un vehículo estacionado.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        EspacioParqueo espacio = controladorParqueo.buscarPorPlaca(placa);
        if (espacio == null || espacio.getVehiculoEstacionado() == null) {
            JOptionPane.showMessageDialog(this, "El vehículo con placa '" + placa + "' no se encuentra estacionado en el residencial.", "No Encontrado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Vehiculo v = espacio.getVehiculoEstacionado();
        if (v.getEstado() == EstadoVehiculo.ESTACIONADO) {
            simulador.encolarVehiculoSalida(v);
            limpiarFormularioSalida();
            refrescarTablas();
        } else {
            JOptionPane.showMessageDialog(this, "El vehículo se encuentra en estado: " + v.getEstado(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void limpiarFormularioSalida() {
        txtPlacaSalida.setText("");
        if (tablaEstacionados != null) {
            tablaEstacionados.clearSelection();
        }
        actualizarEstadoBotonSalida();
    }

    private void actualizarEstadoBotonSalida() {
        boolean hayTexto = (txtPlacaSalida != null && !txtPlacaSalida.getText().trim().isEmpty());
        if (btnSalida != null) {
            btnSalida.setEnabled(hayTexto);
            if (!hayTexto) {
                btnSalida.setToolTipText("Seleccione un vehículo de la tabla o escriba la placa.");
            } else {
                btnSalida.setToolTipText("Enviar vehículo a la cola de la Garita de Salida");
            }
        }
        if (btnLimpiarSalida != null) {
            btnLimpiarSalida.setEnabled(hayTexto);
        }
    }

    public void refrescarTablas() {
        refrescarTablaEstacionados();
        refrescarTablaColaSalida();
        actualizarEstadoBotonSalida();
    }

    private void refrescarTablaEstacionados() {
        SwingUtilities.invokeLater(() -> {
            modeloEstacionados.setRowCount(0);

            // Área Socios
            recorrerAreaEstacionados(controladorParqueo.getAreaSocios());

            // Área General
            recorrerAreaEstacionados(controladorParqueo.getAreaGeneral());
        });
    }

    private void recorrerAreaEstacionados(ListaCircularParqueo area) {
        NodoCircular actual = area.getCabeza();
        int cont = 0;
        while (cont < area.getCapacidad() && actual != null) {
            EspacioParqueo esp = actual.getDato();
            if (esp != null && esp.estaOcupado() && esp.getVehiculoEstacionado() != null) {
                Vehiculo v = esp.getVehiculoEstacionado();
                String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante Temporal";
                String auto = v.getMarca() + " " + v.getModelo() + " (" + v.getColor() + ")";

                modeloEstacionados.addRow(new Object[]{
                    esp.getIdEspacio(),
                    esp.getTipoEspacio(),
                    v.getPlaca(),
                    prop,
                    auto,
                    v.getTipo()
                });
            }
            actual = actual.getSiguiente();
            cont++;
        }
    }

    private void refrescarTablaColaSalida() {
        SwingUtilities.invokeLater(() -> {
            modeloColaSalida.setRowCount(0);
            ColaFIFO cola = simulador.getColaSalida();
            NodoCola actual = cola.getPrimerNodo();
            int pos = 1;
            while (actual != null) {
                Vehiculo v = actual.getDato();
                if (v != null) {
                    String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante";
                    String auto = v.getMarca() + " " + v.getModelo();

                    modeloColaSalida.addRow(new Object[]{
                        pos++,
                        v.getPlaca(),
                        auto,
                        prop,
                        v.getEstado().getDescripcion()
                    });
                }
                actual = actual.getSiguiente();
            }
        });
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        refrescarTablas();
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        // No afecta estacionados
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        refrescarTablas();
        SwingUtilities.invokeLater(() -> {
            lblVehiculoSaliente.setText("Último egreso: " + vehiculo.getPlaca() + " (Espacio " + espacioLiberado.getIdEspacio() + ")");
        });
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        if ("GARITA-3".equalsIgnoreCase(idGarita)) {
            SwingUtilities.invokeLater(() -> {
                lblEstadoGaritaSalida.setText("Estado: " + estado);
            });
        }
    }
}
