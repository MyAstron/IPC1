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
 * y visualización en tiempo real de la Cola de Entrada (FIFO) con limpieza y guía visual de botones.
 * 
 * @author cris_sic
 */
public class PanelEntrada extends JPanel implements GaritaListener {

    private final ListaDobleResidentes listaResidentes;
    private final SimuladorParqueo simulador;

    // Componentes ingreso residente
    private JComboBox<String> cmbResidentes;
    private JComboBox<String> cmbVehiculosResidente;
    private JButton btnEnviarColaResidente;
    private JButton btnLimpiarResidenteEntrada;

    // Componentes ingreso visitante
    private JTextField txtNombreVisitante;
    private JTextField txtPlacaVisitante;
    private JComboBox<String> cmbResidenteVisita;
    private JTextField txtMarcaVis;
    private JTextField txtModeloVis;
    private JTextField txtColorVis;
    private JComboBox<String> cmbTipoVis;
    private JButton btnEnviarVis;
    private JButton btnLimpiarVisitante;

    // Visores de Garitas
    private JLabel lblEstadoGarita1;
    private JLabel lblEstadoGarita2;

    // Tabla Cola de Entrada
    private JTable tablaColaEntrada;
    private DefaultTableModel modeloColaEntrada;

    public PanelEntrada(ListaDobleResidentes listaResidentes, SimuladorParqueo simulador) {
        this.listaResidentes = listaResidentes;
        this.simulador = simulador;
        initComponents();
        actualizarCombosResidentes();
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
        cmbVehiculosResidente.addActionListener(e -> {
            boolean hayVehiculo = (cmbVehiculosResidente.getItemCount() > 0 && cmbVehiculosResidente.getSelectedItem() != null);
            if (btnEnviarColaResidente != null) {
                btnEnviarColaResidente.setEnabled(hayVehiculo);
            }
        });
        tarjeta.add(cmbVehiculosResidente, gbc);

        // Botonera Residente
        JPanel panelBotonesRes = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBotonesRes.setBackground(TemaUI.FONDO_TARJETA);

        btnEnviarColaResidente = TemaUI.crearBoton("🚗 Enviar a Cola", TemaUI.EXITO, Color.WHITE);
        btnLimpiarResidenteEntrada = TemaUI.crearBoton("🧹 Limpiar", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnEnviarColaResidente.addActionListener(e -> accionEnviarVehiculoResidente());
        btnLimpiarResidenteEntrada.addActionListener(e -> limpiarFormularioEntradaResidente());

        panelBotonesRes.add(btnEnviarColaResidente);
        panelBotonesRes.add(btnLimpiarResidenteEntrada);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        tarjeta.add(panelBotonesRes, gbc);

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

        // Botonera Visitante
        JPanel panelBotonesVis = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        panelBotonesVis.setBackground(TemaUI.FONDO_TARJETA);

        btnEnviarVis = TemaUI.crearBoton("👥 Enviar Visitante a Cola", TemaUI.PRIMARIO, Color.WHITE);
        btnLimpiarVisitante = TemaUI.crearBoton("🧹 Limpiar", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnEnviarVis.addActionListener(e -> accionEnviarVehiculoVisitante());
        btnLimpiarVisitante.addActionListener(e -> limpiarFormularioVisitante());

        panelBotonesVis.add(btnEnviarVis);
        panelBotonesVis.add(btnLimpiarVisitante);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        tarjeta.add(panelBotonesVis, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaEstadoGaritas() {
        JPanel tarjeta = TemaUI.crearTarjeta();
        tarjeta.setLayout(new GridLayout(2, 1, 0, 10));

        // Garita 1
        JPanel pnlG1 = new JPanel(new GridLayout(2, 1));
        pnlG1.setBackground(TemaUI.FONDO_INPUT);
        pnlG1.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel lblG1Titulo = new JLabel("🏢 Garita de Entrada 1");
        lblG1Titulo.setFont(TemaUI.FUENTE_BOLD);
        lblG1Titulo.setForeground(TemaUI.PRIMARIO);
        lblEstadoGarita1 = new JLabel("Estado: En espera (Cola vacía)");
        lblEstadoGarita1.setFont(TemaUI.FUENTE_REGULAR);
        lblEstadoGarita1.setForeground(TemaUI.TEXTO_PRINCIPAL);
        pnlG1.add(lblG1Titulo);
        pnlG1.add(lblEstadoGarita1);

        // Garita 2
        JPanel pnlG2 = new JPanel(new GridLayout(2, 1));
        pnlG2.setBackground(TemaUI.FONDO_INPUT);
        pnlG2.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JLabel lblG2Titulo = new JLabel("🏢 Garita de Entrada 2");
        lblG2Titulo.setFont(TemaUI.FUENTE_BOLD);
        lblG2Titulo.setForeground(TemaUI.PRIMARIO);
        lblEstadoGarita2 = new JLabel("Estado: En espera (Cola vacía)");
        lblEstadoGarita2.setFont(TemaUI.FUENTE_REGULAR);
        lblEstadoGarita2.setForeground(TemaUI.TEXTO_PRINCIPAL);
        pnlG2.add(lblG2Titulo);
        pnlG2.add(lblEstadoGarita2);

        tarjeta.add(pnlG1);
        tarjeta.add(pnlG2);

        return tarjeta;
    }

    private JPanel crearContenedorColaEntrada() {
        JPanel contenedor = TemaUI.crearTarjeta();
        contenedor.setLayout(new BorderLayout(5, 5));

        JLabel lbl = TemaUI.crearEtiquetaTitulo("🚦 Cola de Espera en Garitas de Entrada (FIFO)");
        contenedor.add(lbl, BorderLayout.NORTH);

        String[] columnas = {"Pos.", "Placa", "Tipo Usuario", "Vehículo / Color", "Destino / Residente"};
        modeloColaEntrada = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
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
    // ACCIONES
    // =========================================================================

    public void actualizarCombosResidentes() {
        cmbResidentes.removeAllItems();
        cmbResidenteVisita.removeAllItems();

        NodoDoble actual = listaResidentes.getCabeza();
        while (actual != null) {
            Residente r = actual.getDato();
            if (r != null) {
                String item = r.getId() + " - " + r.getNombre() + " (" + (r.isEsSocio() ? "Socio" : "General") + ")";
                cmbResidentes.addItem(item);
                cmbResidenteVisita.addItem(item);
            }
            actual = actual.getSiguiente();
        }

        actualizarComboVehiculosResidente();
    }

    public void actualizarComboVehiculosResidente() {
        cmbVehiculosResidente.removeAllItems();
        String itemSeleccionado = (String) cmbResidentes.getSelectedItem();
        if (itemSeleccionado == null) {
            if (btnEnviarColaResidente != null) {
                btnEnviarColaResidente.setEnabled(false);
            }
            return;
        }

        String idResidente = itemSeleccionado.split(" - ")[0].trim();
        Residente r = listaResidentes.buscarPorId(idResidente);
        if (r != null && r.getListaVehiculos() != null) {
            NodoSimple actual = r.getListaVehiculos().getCabeza();
            while (actual != null) {
                Vehiculo v = actual.getDato();
                // Solo mostrar vehículos en estado FUERA
                if (v != null && v.getEstado() == EstadoVehiculo.FUERA) {
                    cmbVehiculosResidente.addItem(v.getPlaca() + " (" + v.getMarca() + " " + v.getModelo() + " - " + v.getColor() + ")");
                }
                actual = actual.getSiguiente();
            }
        }

        boolean tieneVehiculosDisponibles = (cmbVehiculosResidente.getItemCount() > 0);
        if (btnEnviarColaResidente != null) {
            btnEnviarColaResidente.setEnabled(tieneVehiculosDisponibles);
            if (!tieneVehiculosDisponibles) {
                btnEnviarColaResidente.setToolTipText("El residente no tiene vehículos fuera del residencial.");
            } else {
                btnEnviarColaResidente.setToolTipText("Enviar vehículo seleccionado a la cola de entrada");
            }
        }
    }

    private void accionEnviarVehiculoResidente() {
        String itemVehiculo = (String) cmbVehiculosResidente.getSelectedItem();
        if (itemVehiculo == null) {
            JOptionPane.showMessageDialog(this, "No hay vehículos en estado FUERA disponibles para este residente.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String placa = itemVehiculo.split(" ")[0].trim();
        String itemRes = (String) cmbResidentes.getSelectedItem();
        String idRes = itemRes.split(" - ")[0].trim();
        Residente r = listaResidentes.buscarPorId(idRes);

        if (r != null && r.getListaVehiculos() != null) {
            Vehiculo v = r.getListaVehiculos().buscarPorPlaca(placa);
            if (v != null) {
                simulador.encolarVehiculoEntrada(v);
                actualizarComboVehiculosResidente();
                refrescarTablaColaEntrada();
            }
        }
    }

    private void accionEnviarVehiculoVisitante() {
        String nombre = txtNombreVisitante.getText().trim();
        String placa = txtPlacaVisitante.getText().trim().toUpperCase();
        String marca = txtMarcaVis.getText().trim();
        String modelo = txtModeloVis.getText().trim();
        String color = txtColorVis.getText().trim();
        String tipo = (String) cmbTipoVis.getSelectedItem();

        if (nombre.isEmpty() || placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || color.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los datos del visitante.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!ValidadorTexto.esPlacaValida(placa)) {
            JOptionPane.showMessageDialog(this, "Placa inválida (3-10 caracteres, sin '|').", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String itemResVisita = (String) cmbResidenteVisita.getSelectedItem();
        if (itemResVisita == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un residente a visitar.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idResVisita = itemResVisita.split(" - ")[0].trim();

        Visitante visitante = new Visitante(nombre, placa, idResVisita, marca, modelo, color, tipo);
        simulador.encolarVehiculoEntrada(visitante.getVehiculo());

        limpiarFormularioVisitante();
        refrescarTablaColaEntrada();
    }

    public void limpiarFormularioEntradaResidente() {
        if (cmbResidentes != null && cmbResidentes.getItemCount() > 0) {
            cmbResidentes.setSelectedIndex(0);
        }
        actualizarComboVehiculosResidente();
    }

    public void limpiarFormularioVisitante() {
        txtNombreVisitante.setText("");
        txtPlacaVisitante.setText("");
        txtMarcaVis.setText("");
        txtModeloVis.setText("");
        txtColorVis.setText("");
        if (cmbResidenteVisita != null && cmbResidenteVisita.getItemCount() > 0) {
            cmbResidenteVisita.setSelectedIndex(0);
        }
        if (cmbTipoVis != null && cmbTipoVis.getItemCount() > 0) {
            cmbTipoVis.setSelectedIndex(0);
        }
        txtNombreVisitante.requestFocus();
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
                        String tipoUsuario = (v.getPropietario() != null)
                                ? (v.getPropietario().isEsSocio() ? "⭐ Residente Socio" : "🚗 Residente General")
                                : "👥 Visitante";
                        String infoVehiculo = v.getMarca() + " " + v.getModelo() + " (" + v.getColor() + ")";
                        String destino = (v.getPropietario() != null)
                                ? v.getPropietario().getNombre() + " (" + v.getPropietario().getCasaLote() + ")"
                                : "Visita a Residente";

                        modeloColaEntrada.addRow(new Object[]{
                            pos++,
                            v.getPlaca(),
                            tipoUsuario,
                            infoVehiculo,
                            destino
                        });
                    }
                    actual = actual.getSiguiente();
                }
            }
            modeloColaEntrada.fireTableDataChanged();
        });
    }

    private boolean esGarita1(String idGarita) {
        if (idGarita == null) return false;
        String id = idGarita.toUpperCase();
        return id.contains("1") || id.contains("GARITA-1") || id.contains("ENTRADA 1");
    }

    private boolean esGarita2(String idGarita) {
        if (idGarita == null) return false;
        String id = idGarita.toUpperCase();
        return id.contains("2") || id.contains("GARITA-2") || id.contains("ENTRADA 2");
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        refrescarTablaColaEntrada();
        SwingUtilities.invokeLater(() -> {
            String texto = "✅ Ingresó: " + vehiculo.getPlaca() + " ➔ " + espacioAsignado.getIdEspacio();
            if (esGarita1(idGarita) && lblEstadoGarita1 != null) {
                lblEstadoGarita1.setText(texto);
                lblEstadoGarita1.setForeground(TemaUI.EXITO);
            } else if (esGarita2(idGarita) && lblEstadoGarita2 != null) {
                lblEstadoGarita2.setText(texto);
                lblEstadoGarita2.setForeground(TemaUI.EXITO);
            }
            actualizarComboVehiculosResidente();
        });
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        refrescarTablaColaEntrada();
        SwingUtilities.invokeLater(() -> {
            String texto = "❌ Rechazado: " + vehiculo.getPlaca() + " (Lleno)";
            if (esGarita1(idGarita) && lblEstadoGarita1 != null) {
                lblEstadoGarita1.setText(texto);
                lblEstadoGarita1.setForeground(TemaUI.PELIGRO);
            } else if (esGarita2(idGarita) && lblEstadoGarita2 != null) {
                lblEstadoGarita2.setText(texto);
                lblEstadoGarita2.setForeground(TemaUI.PELIGRO);
            }
            actualizarComboVehiculosResidente();
        });
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        // Al salir un vehículo, puede quedar en estado FUERA y habilitarse en el combo
        SwingUtilities.invokeLater(this::actualizarComboVehiculosResidente);
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        refrescarTablaColaEntrada();
        SwingUtilities.invokeLater(() -> {
            boolean atendiendo = estado != null && estado.contains("Atendiendo");
            Color colorTexto = atendiendo ? TemaUI.ADVERTENCIA : TemaUI.TEXTO_PRINCIPAL;

            if (esGarita1(idGarita) && lblEstadoGarita1 != null) {
                lblEstadoGarita1.setText(estado);
                lblEstadoGarita1.setForeground(colorTexto);
            } else if (esGarita2(idGarita) && lblEstadoGarita2 != null) {
                lblEstadoGarita2.setText(estado);
                lblEstadoGarita2.setForeground(colorTexto);
            }
        });
    }
}
