package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cris.sic.proyecto2.estructuras.ListaDobleResidentes;
import cris.sic.proyecto2.estructuras.PilaEventos;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.hilos.SimuladorParqueo;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Vehiculo;
import cris.sic.proyecto2.persistencia.GestorArchivos;

/**
 * Ventana Principal de la aplicación ResiPark (JFrame).
 * Orquesta la navegación entre paneles mediante CardLayout, la persistencia automática
 * al cierre y el monitoreo global del parqueo y las garitas.
 * 
 * @author cris_sic
 */
public class VentanaPrincipal extends JFrame implements GaritaListener {

    private final ListaDobleResidentes listaResidentes;
    private final ControladorParqueo controladorParqueo;
    private final PilaEventos pilaEventos;
    private final SimuladorParqueo simulador;

    // Paneles de vistas
    private JPanel panelContenedorCards;
    private CardLayout cardLayout;

    private PanelResidentes panelResidentes;
    private PanelParqueo panelParqueo;
    private PanelEntrada panelEntrada;
    private PanelSalida panelSalida;
    private PanelEventos panelEventos;

    // Indicadores globales de ocupación
    private JLabel lblOcupacionSocios;
    private JLabel lblOcupacionGeneral;
    private JLabel lblOcupacionTotal;

    // Botones de navegación
    private JButton btnNavResidentes;
    private JButton btnNavParqueo;
    private JButton btnNavEntrada;
    private JButton btnNavSalida;
    private JButton btnNavEventos;

    public VentanaPrincipal() {
        // Carga automática de datos desde disco con java.io.*
        this.listaResidentes = GestorArchivos.cargarTodo();
        this.controladorParqueo = new ControladorParqueo();
        this.pilaEventos = new PilaEventos();
        this.simulador = new SimuladorParqueo(this.controladorParqueo, this.pilaEventos);

        initComponents();

        // Conectar listener global a la ventana para actualizar ocupación
        this.simulador.setListenerGlobal(this);

        // Iniciar los hilos de las garitas en segundo plano
        this.simulador.iniciarSimulacion();
    }

    private void initComponents() {
        setTitle("ResiPark - Sistema de Control y Parqueo Residencial");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Manejo de cierre seguro con persistencia automática
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                accionCerrarAplicacion();
            }
        });

        // Contenedor principal
        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(TemaUI.FONDO_APP);

        // Barra Superior de Estado
        panelRaiz.add(crearBarraSuperior(), BorderLayout.NORTH);

        // Barra Lateral de Navegación
        panelRaiz.add(crearBarraLateral(), BorderLayout.WEST);

        // Contenedor Central con CardLayout
        cardLayout = new CardLayout();
        panelContenedorCards = new JPanel(cardLayout);
        panelContenedorCards.setBackground(TemaUI.FONDO_PANEL);

        // Instanciación de paneles
        panelResidentes = new PanelResidentes(listaResidentes);
        panelParqueo = new PanelParqueo(controladorParqueo, simulador);
        panelEntrada = new PanelEntrada(listaResidentes, simulador);
        panelSalida = new PanelSalida(controladorParqueo, simulador);
        panelEventos = new PanelEventos(pilaEventos);

        // Registro de tarjetas
        panelContenedorCards.add(panelResidentes, "RESIDENTES");
        panelContenedorCards.add(panelParqueo, "PARQUEO");
        panelContenedorCards.add(panelEntrada, "ENTRADA");
        panelContenedorCards.add(panelSalida, "SALIDA");
        panelContenedorCards.add(panelEventos, "EVENTOS");

        panelRaiz.add(panelContenedorCards, BorderLayout.CENTER);
        setContentPane(panelRaiz);

        actualizarMetricasOcupacion();
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(15, 0));
        barra.setBackground(TemaUI.FONDO_BARRA_LATERAL);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, TemaUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Logo y Título
        JPanel panelLogo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelLogo.setBackground(TemaUI.FONDO_BARRA_LATERAL);
        JLabel lblLogo = new JLabel("🅿️ RESIPARK");
        lblLogo.setFont(TemaUI.FUENTE_TITULO_GRANDE);
        lblLogo.setForeground(TemaUI.PRIMARIO);

        JLabel lblSub = new JLabel("Control y Concurrencia");
        lblSub.setFont(TemaUI.FUENTE_SMALL);
        lblSub.setForeground(TemaUI.TEXTO_MUTED);

        panelLogo.add(lblLogo);
        panelLogo.add(lblSub);

        // Métricas de Ocupación en Vivo
        JPanel panelMetricas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelMetricas.setBackground(TemaUI.FONDO_BARRA_LATERAL);

        lblOcupacionSocios = new JLabel("Socios: 0/75");
        lblOcupacionSocios.setFont(TemaUI.FUENTE_BOLD);
        lblOcupacionSocios.setForeground(TemaUI.PURPURA);

        lblOcupacionGeneral = new JLabel("General: 0/75");
        lblOcupacionGeneral.setFont(TemaUI.FUENTE_BOLD);
        lblOcupacionGeneral.setForeground(TemaUI.PRIMARIO);

        lblOcupacionTotal = new JLabel("Total Ocupación: 0/150");
        lblOcupacionTotal.setFont(TemaUI.FUENTE_BOLD);
        lblOcupacionTotal.setForeground(TemaUI.EXITO);

        panelMetricas.add(lblOcupacionSocios);
        panelMetricas.add(lblOcupacionGeneral);
        panelMetricas.add(lblOcupacionTotal);

        barra.add(panelLogo, BorderLayout.WEST);
        barra.add(panelMetricas, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearBarraLateral() {
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(TemaUI.FONDO_BARRA_LATERAL);
        barra.setPreferredSize(new Dimension(210, 600));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, TemaUI.BORDE_SUAVE),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));

        btnNavResidentes = TemaUI.crearBoton("👥 Residentes", TemaUI.PRIMARIO, Color.WHITE);
        btnNavParqueo = TemaUI.crearBoton("🅿️ Mapa Parqueo", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);
        btnNavEntrada = TemaUI.crearBoton("🚗 Garitas Entrada", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);
        btnNavSalida = TemaUI.crearBoton("🚙 Garita Salida", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);
        btnNavEventos = TemaUI.crearBoton("📋 Bitácora", TemaUI.FONDO_INPUT, TemaUI.TEXTO_PRINCIPAL);

        btnNavResidentes.setMaximumSize(new Dimension(190, 42));
        btnNavParqueo.setMaximumSize(new Dimension(190, 42));
        btnNavEntrada.setMaximumSize(new Dimension(190, 42));
        btnNavSalida.setMaximumSize(new Dimension(190, 42));
        btnNavEventos.setMaximumSize(new Dimension(190, 42));

        btnNavResidentes.addActionListener(e -> mostrarPanel("RESIDENTES", btnNavResidentes));
        btnNavParqueo.addActionListener(e -> {
            panelParqueo.actualizarMatrizVisual();
            mostrarPanel("PARQUEO", btnNavParqueo);
        });
        btnNavEntrada.addActionListener(e -> {
            panelEntrada.actualizarCombosResidentes();
            mostrarPanel("ENTRADA", btnNavEntrada);
        });
        btnNavSalida.addActionListener(e -> {
            panelSalida.refrescarTablas();
            mostrarPanel("SALIDA", btnNavSalida);
        });
        btnNavEventos.addActionListener(e -> {
            panelEventos.refrescarTablaEventos();
            mostrarPanel("EVENTOS", btnNavEventos);
        });

        barra.add(btnNavResidentes);
        barra.add(Box.createRigidArea(new Dimension(0, 10)));
        barra.add(btnNavParqueo);
        barra.add(Box.createRigidArea(new Dimension(0, 10)));
        barra.add(btnNavEntrada);
        barra.add(Box.createRigidArea(new Dimension(0, 10)));
        barra.add(btnNavSalida);
        barra.add(Box.createRigidArea(new Dimension(0, 10)));
        barra.add(btnNavEventos);
        barra.add(Box.createVerticalGlue());

        // Botón de persistencia manual y guardado rápido
        JButton btnGuardarDisco = TemaUI.crearBoton("💾 Guardar Disco", TemaUI.FONDO_TARJETA, TemaUI.TEXTO_PRINCIPAL);
        btnGuardarDisco.setMaximumSize(new Dimension(190, 36));
        btnGuardarDisco.addActionListener(e -> {
            boolean ok = GestorArchivos.guardarTodo(listaResidentes);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Datos guardados en disco exitosamente.", "Persistencia", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en disco.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        barra.add(btnGuardarDisco);

        return barra;
    }

    private void mostrarPanel(String nombreCard, JButton botonActivo) {
        cardLayout.show(panelContenedorCards, nombreCard);

        // Estilo activo para botones
        JButton[] botones = {btnNavResidentes, btnNavParqueo, btnNavEntrada, btnNavSalida, btnNavEventos};
        for (JButton b : botones) {
            if (b == botonActivo) {
                b.setBackground(TemaUI.PRIMARIO);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(TemaUI.FONDO_INPUT);
                b.setForeground(TemaUI.TEXTO_PRINCIPAL);
            }
        }
    }

    public void actualizarMetricasOcupacion() {
        SwingUtilities.invokeLater(() -> {
            int soc = controladorParqueo.getAreaSocios().getOcupados();
            int gen = controladorParqueo.getAreaGeneral().getOcupados();
            int tot = controladorParqueo.getTotalOcupados();

            lblOcupacionSocios.setText("Socios: " + soc + "/75");
            lblOcupacionGeneral.setText("General: " + gen + "/75");
            lblOcupacionTotal.setText("Total Ocupación: " + tot + "/150 (" + (tot * 100 / 150) + "%)");
        });
    }

    private void accionCerrarAplicacion() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea guardar los datos y salir de ResiPark?",
                "Confirmar Cierre", JOptionPane.YES_NO_CANCEL_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Detener hilos con seguridad
            simulador.detenerSimulacion();

            // Guardar automáticamente en disco con java.io.*
            GestorArchivos.guardarTodo(listaResidentes);

            dispose();
            System.exit(0);
        } else if (confirm == JOptionPane.NO_OPTION) {
            simulador.detenerSimulacion();
            dispose();
            System.exit(0);
        }
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        actualizarMetricasOcupacion();
        if (panelParqueo != null) {
            panelParqueo.onVehiculoIngresado(idGarita, vehiculo, espacioAsignado, evento);
        }
        if (panelEntrada != null) {
            panelEntrada.onVehiculoIngresado(idGarita, vehiculo, espacioAsignado, evento);
        }
        if (panelSalida != null) {
            panelSalida.onVehiculoIngresado(idGarita, vehiculo, espacioAsignado, evento);
        }
        if (panelEventos != null) {
            panelEventos.onVehiculoIngresado(idGarita, vehiculo, espacioAsignado, evento);
        }
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        actualizarMetricasOcupacion();
        if (panelParqueo != null) {
            panelParqueo.onVehiculoRechazado(idGarita, vehiculo, evento);
        }
        if (panelEntrada != null) {
            panelEntrada.onVehiculoRechazado(idGarita, vehiculo, evento);
        }
        if (panelEventos != null) {
            panelEventos.onVehiculoRechazado(idGarita, vehiculo, evento);
        }
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        actualizarMetricasOcupacion();
        if (panelParqueo != null) {
            panelParqueo.onVehiculoSalida(idGarita, vehiculo, espacioLiberado, evento);
        }
        if (panelEntrada != null) {
            panelEntrada.onVehiculoSalida(idGarita, vehiculo, espacioLiberado, evento);
        }
        if (panelSalida != null) {
            panelSalida.onVehiculoSalida(idGarita, vehiculo, espacioLiberado, evento);
        }
        if (panelEventos != null) {
            panelEventos.onVehiculoSalida(idGarita, vehiculo, espacioLiberado, evento);
        }
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        if (panelEntrada != null) {
            panelEntrada.onEstadoCambiado(idGarita, estado);
        }
        if (panelSalida != null) {
            panelSalida.onEstadoCambiado(idGarita, estado);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal v = new VentanaPrincipal();
            v.setVisible(true);
        });
    }
}
