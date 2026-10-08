package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import cris.sic.proyecto2.estructuras.NodoPila;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Panel para la visualización en tiempo real de la bitácora de eventos del sistema ResiPark.
 * Implementa un recorrido no destructivo desde el tope hacia la base de la Pila LIFO.
 * 
 * @author cris_sic
 */
public class PanelEventos extends JPanel implements GaritaListener {

    private final PilaEventos pilaEventos;

    private JTable tablaEventos;
    private DefaultTableModel modeloEventos;
    private JLabel lblContador;

    public PanelEventos(PilaEventos pilaEventos) {
        this.pilaEventos = pilaEventos;
        initComponents();
        refrescarTablaEventos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(TemaUI.FONDO_PANEL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel tarjetaPrincipal = TemaUI.crearTarjeta();
        tarjetaPrincipal.setLayout(new BorderLayout(10, 10));

        // Cabecera con título y acciones
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(TemaUI.FONDO_TARJETA);

        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(TemaUI.FONDO_TARJETA);
        JLabel lblTitulo = TemaUI.crearEtiquetaTitulo("📋 Bitácora Histórica de Eventos (Pila LIFO)");
        lblContador = new JLabel("[0 eventos registrados]");
        lblContador.setFont(TemaUI.FUENTE_BOLD);
        lblContador.setForeground(TemaUI.ADVERTENCIA);
        panelTitulo.add(lblTitulo);
        panelTitulo.add(lblContador);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotones.setBackground(TemaUI.FONDO_TARJETA);

        JButton btnRefrescar = TemaUI.crearBoton("🔄 Refrescar", TemaUI.PRIMARIO, Color.WHITE);
        JButton btnVaciar = TemaUI.crearBoton("🗑️ Vaciar Bitácora", TemaUI.PELIGRO, Color.WHITE);

        btnRefrescar.addActionListener(e -> refrescarTablaEventos());
        btnVaciar.addActionListener(e -> accionVaciarBitacora());

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnVaciar);

        cabecera.add(panelTitulo, BorderLayout.WEST);
        cabecera.add(panelBotones, BorderLayout.EAST);

        tarjetaPrincipal.add(cabecera, BorderLayout.NORTH);

        // Tabla de eventos
        String[] columnas = {"# (Reciente=1)", "Fecha y Hora", "Garita / Origen", "Tipo de Evento", "Descripción del Suceso"};
        modeloEventos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tablaEventos = new JTable(modeloEventos);
        tablaEventos.setFont(TemaUI.FUENTE_REGULAR);
        tablaEventos.setRowHeight(26);
        tablaEventos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEventos.setBackground(TemaUI.FONDO_TARJETA);
        tablaEventos.setForeground(TemaUI.TEXTO_PRINCIPAL);
        tablaEventos.setGridColor(TemaUI.BORDE_SUAVE);

        // Anchos de columna
        tablaEventos.getColumnModel().getColumn(0).setPreferredWidth(90);
        tablaEventos.getColumnModel().getColumn(1).setPreferredWidth(140);
        tablaEventos.getColumnModel().getColumn(2).setPreferredWidth(150);
        tablaEventos.getColumnModel().getColumn(3).setPreferredWidth(130);
        tablaEventos.getColumnModel().getColumn(4).setPreferredWidth(450);

        JScrollPane scroll = new JScrollPane(tablaEventos);
        scroll.getViewport().setBackground(TemaUI.FONDO_TARJETA);
        scroll.setBorder(BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE));

        tarjetaPrincipal.add(scroll, BorderLayout.CENTER);
        add(tarjetaPrincipal, BorderLayout.CENTER);
    }

    private void accionVaciarBitacora() {
        if (pilaEventos.estaVacia()) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de vaciar el historial de eventos en memoria?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            pilaEventos.vaciar();
            cris.sic.proyecto2.persistencia.GestorArchivos.guardarBitacora(pilaEventos);
            refrescarTablaEventos();
        }
    }

    /**
     * Recorrido NO destructivo de la Pila LIFO desde el tope hacia la base.
     */
    public void refrescarTablaEventos() {
        SwingUtilities.invokeLater(() -> {
            modeloEventos.setRowCount(0);
            synchronized (pilaEventos) {
                NodoPila actual = pilaEventos.getPrimerNodo();
                int pos = 1;
                while (actual != null) {
                    Evento ev = actual.getDato();
                    if (ev != null) {
                        modeloEventos.addRow(new Object[]{
                            "#" + pos++,
                            ev.getFechaHora(),
                            ev.getGaritaInvolucrada(),
                            ev.getTipoEvento(),
                            ev.getDescripcion()
                        });
                    }
                    actual = actual.getSiguiente();
                }
                lblContador.setText("[" + pilaEventos.getTamaño() + " eventos]");
            }
        });
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        refrescarTablaEventos();
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        refrescarTablaEventos();
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        refrescarTablaEventos();
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        // No afecta la tabla
    }
}
