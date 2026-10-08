package cris.sic.proyecto2.vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import cris.sic.proyecto2.estructuras.NodoCircular;
import cris.sic.proyecto2.hilos.GaritaListener;
import cris.sic.proyecto2.hilos.SimuladorParqueo;
import cris.sic.proyecto2.modelo.ControladorParqueo;
import cris.sic.proyecto2.modelo.EspacioParqueo;
import cris.sic.proyecto2.modelo.Evento;
import cris.sic.proyecto2.modelo.Residente;
import cris.sic.proyecto2.modelo.TipoEspacio;
import cris.sic.proyecto2.modelo.Vehiculo;

/**
 * Panel de Renderizado Dinámico del Parqueo (Fase 8).
 * Muestra la cuadrícula interactiva de 150 espacios (75 Socios en Filas A-C, 75 General en Filas E-I),
 * con diferenciación cromática en tiempo real y visualización de detalles al seleccionar cada celda.
 * 
 * @author cris_sic
 */
public class PanelParqueo extends JPanel implements GaritaListener {

    private final ControladorParqueo controladorParqueo;
    private final SimuladorParqueo simulador;

    // Métricas en cabecera
    private JLabel lblTotalSocios;
    private JLabel lblTotalGeneral;
    private JLabel lblTotalGlobal;

    // Paneles contenedores de cuadrículas
    private JPanel panelCuadriculaSocios;
    private JPanel panelCuadriculaGeneral;

    // Referencias a botones de celdas para actualización directa
    private JButton[] botonesSocios;
    private EspacioParqueo[] espaciosSocios;

    private JButton[] botonesGeneral;
    private EspacioParqueo[] espaciosGeneral;

    // Colores semánticos para celdas
    private static final Color COLOR_LIBRE = new Color(34, 197, 94);          // Verde
    private static final Color COLOR_SOCIO_OCUPADO = new Color(168, 85, 247); // Púrpura
    private static final Color COLOR_GENERAL_OCUPADO = new Color(59, 130, 246);// Azul
    private static final Color COLOR_VISITANTE_OCUPADO = new Color(245, 158, 11);// Ámbar
    private static final Color COLOR_DESBORDE = new Color(236, 72, 153);       // Rosa/Fucsia (Socio en General)

    public PanelParqueo(ControladorParqueo controladorParqueo, SimuladorParqueo simulador) {
        this.controladorParqueo = controladorParqueo;
        this.simulador = simulador;
        this.botonesSocios = new JButton[75];
        this.espaciosSocios = new EspacioParqueo[75];
        this.botonesGeneral = new JButton[75];
        this.espaciosGeneral = new EspacioParqueo[75];

        setLayout(new BorderLayout(15, 15));
        setBackground(TemaUI.FONDO_APP);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));

        initComponents();
        actualizarMatrizVisual();
    }

    private void initComponents() {
        // Encabezado con métricas y leyenda
        add(crearCabeceraConMetricas(), BorderLayout.NORTH);

        // Pestañas de visualización (Todas las áreas, Área Socios, Área General)
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TemaUI.FUENTE_BOLD);
        tabbedPane.setBackground(TemaUI.FONDO_PANEL);
        tabbedPane.setForeground(TemaUI.TEXTO_PRINCIPAL);

        // Pestaña 1: Vista General (Ambas Áreas)
        JPanel panelVistaCompleta = new JPanel();
        panelVistaCompleta.setLayout(new BoxLayout(panelVistaCompleta, BoxLayout.Y_AXIS));
        panelVistaCompleta.setBackground(TemaUI.FONDO_APP);

        JPanel seccionSocios = crearSeccionArea("Área de Socios (Filas A, B, C - 75 Espacios)", crearCuadriculaSocios());
        JPanel seccionGeneral = crearSeccionArea("Área General (Filas E, F, G, H, I - 75 Espacios)", crearCuadriculaGeneral());

        panelVistaCompleta.add(seccionSocios);
        panelVistaCompleta.add(Box.createRigidArea(new Dimension(0, 15)));
        panelVistaCompleta.add(seccionGeneral);

        JScrollPane scrollCompleto = new JScrollPane(panelVistaCompleta);
        scrollCompleto.setBorder(BorderFactory.createEmptyBorder());
        scrollCompleto.getVerticalScrollBar().setUnitIncrement(16);
        scrollCompleto.getViewport().setBackground(TemaUI.FONDO_APP);

        tabbedPane.addTab("🗺️ Vista Completa", scrollCompleto);

        // Pestaña 2: Solo Socios
        JScrollPane scrollSocios = new JScrollPane(crearSeccionArea("Área de Socios (Filas A, B, C)", panelCuadriculaSocios));
        scrollSocios.setBorder(BorderFactory.createEmptyBorder());
        scrollSocios.getVerticalScrollBar().setUnitIncrement(16);
        scrollSocios.getViewport().setBackground(TemaUI.FONDO_APP);
        tabbedPane.addTab("⭐ Área Socios (75)", scrollSocios);

        // Pestaña 3: Solo General
        JScrollPane scrollGeneral = new JScrollPane(crearSeccionArea("Área General (Filas E, F, G, H, I)", panelCuadriculaGeneral));
        scrollGeneral.setBorder(BorderFactory.createEmptyBorder());
        scrollGeneral.getVerticalScrollBar().setUnitIncrement(16);
        scrollGeneral.getViewport().setBackground(TemaUI.FONDO_APP);
        tabbedPane.addTab("🚗 Área General (75)", scrollGeneral);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearCabeceraConMetricas() {
        JPanel cabecera = new JPanel(new BorderLayout(15, 10));
        cabecera.setBackground(TemaUI.FONDO_TARJETA);
        cabecera.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        // Título
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelTitulo.setBackground(TemaUI.FONDO_TARJETA);
        JLabel lblTitulo = new JLabel("🅿️ Mapa en Vivo del Residencial");
        lblTitulo.setFont(TemaUI.FUENTE_TITULO);
        lblTitulo.setForeground(TemaUI.TEXTO_PRINCIPAL);
        panelTitulo.add(lblTitulo);

        // Leyenda Cromática
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelLeyenda.setBackground(TemaUI.FONDO_TARJETA);

        panelLeyenda.add(crearItemLeyenda("Libre", COLOR_LIBRE));
        panelLeyenda.add(crearItemLeyenda("Socio", COLOR_SOCIO_OCUPADO));
        panelLeyenda.add(crearItemLeyenda("General", COLOR_GENERAL_OCUPADO));
        panelLeyenda.add(crearItemLeyenda("Visitante", COLOR_VISITANTE_OCUPADO));
        panelLeyenda.add(crearItemLeyenda("Desborde Socio", COLOR_DESBORDE));

        // Botón Refrescar
        JButton btnRefrescar = TemaUI.crearBoton("🔄 Refrescar", TemaUI.PRIMARIO, Color.WHITE);
        btnRefrescar.setPreferredSize(new Dimension(110, 30));
        btnRefrescar.addActionListener(e -> actualizarMatrizVisual());
        panelLeyenda.add(btnRefrescar);

        // Métricas inferiores
        JPanel panelMetricas = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelMetricas.setBackground(TemaUI.FONDO_TARJETA);

        lblTotalSocios = new JLabel("Socios: 0 / 75 ocupados");
        lblTotalSocios.setFont(TemaUI.FUENTE_BOLD);
        lblTotalSocios.setForeground(TemaUI.PURPURA);

        lblTotalGeneral = new JLabel("General: 0 / 75 ocupados");
        lblTotalGeneral.setFont(TemaUI.FUENTE_BOLD);
        lblTotalGeneral.setForeground(TemaUI.PRIMARIO);

        lblTotalGlobal = new JLabel("Ocupación Global: 0 / 150 (0%)");
        lblTotalGlobal.setFont(TemaUI.FUENTE_BOLD);
        lblTotalGlobal.setForeground(TemaUI.EXITO);

        panelMetricas.add(lblTotalSocios);
        panelMetricas.add(new JLabel("•"));
        panelMetricas.add(lblTotalGeneral);
        panelMetricas.add(new JLabel("•"));
        panelMetricas.add(lblTotalGlobal);

        cabecera.add(panelTitulo, BorderLayout.WEST);
        cabecera.add(panelLeyenda, BorderLayout.EAST);
        cabecera.add(panelMetricas, BorderLayout.SOUTH);

        return cabecera;
    }

    private JPanel crearItemLeyenda(String texto, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(TemaUI.FONDO_TARJETA);

        JPanel cuadroColor = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        cuadroColor.setPreferredSize(new Dimension(14, 14));
        cuadroColor.setOpaque(false);

        JLabel lbl = new JLabel(texto);
        lbl.setFont(TemaUI.FUENTE_SMALL);
        lbl.setForeground(TemaUI.TEXTO_SECUNDARIO);

        item.add(cuadroColor);
        item.add(lbl);
        return item;
    }

    private JPanel crearSeccionArea(String titulo, JPanel panelCuadricula) {
        JPanel seccion = new JPanel(new BorderLayout(0, 10));
        seccion.setBackground(TemaUI.FONDO_PANEL);
        seccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaUI.BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(12, 15, 15, 15)
        ));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TemaUI.FUENTE_SUBTITULO);
        lblTitulo.setForeground(TemaUI.TEXTO_PRINCIPAL);

        seccion.add(lblTitulo, BorderLayout.NORTH);
        seccion.add(panelCuadricula, BorderLayout.CENTER);
        return seccion;
    }

    /**
     * Construye la cuadrícula de 3 filas x 25 celdas para el Área de Socios.
     */
    private JPanel crearCuadriculaSocios() {
        panelCuadriculaSocios = new JPanel(new GridLayout(3, 25, 4, 4));
        panelCuadriculaSocios.setBackground(TemaUI.FONDO_PANEL);

        // Recorrer la lista circular de socios para instanciar las 75 celdas
        NodoCircular actual = controladorParqueo.getAreaSocios().getCabeza();
        int idx = 0;
        while (idx < 75 && actual != null) {
            EspacioParqueo espacio = actual.getDato();
            espaciosSocios[idx] = espacio;

            JButton btnCelda = crearBotonCelda(espacio);
            final int indexFinal = idx;
            btnCelda.addActionListener(e -> mostrarDetalleEspacio(espaciosSocios[indexFinal]));

            botonesSocios[idx] = btnCelda;
            panelCuadriculaSocios.add(btnCelda);

            actual = actual.getSiguiente();
            idx++;
        }

        return panelCuadriculaSocios;
    }

    /**
     * Construye la cuadrícula de 5 filas x 15 celdas para el Área General.
     */
    private JPanel crearCuadriculaGeneral() {
        panelCuadriculaGeneral = new JPanel(new GridLayout(5, 15, 4, 4));
        panelCuadriculaGeneral.setBackground(TemaUI.FONDO_PANEL);

        NodoCircular actual = controladorParqueo.getAreaGeneral().getCabeza();
        int idx = 0;
        while (idx < 75 && actual != null) {
            EspacioParqueo espacio = actual.getDato();
            espaciosGeneral[idx] = espacio;

            JButton btnCelda = crearBotonCelda(espacio);
            final int indexFinal = idx;
            btnCelda.addActionListener(e -> mostrarDetalleEspacio(espaciosGeneral[indexFinal]));

            botonesGeneral[idx] = btnCelda;
            panelCuadriculaGeneral.add(btnCelda);

            actual = actual.getSiguiente();
            idx++;
        }

        return panelCuadriculaGeneral;
    }

    private JButton crearBotonCelda(EspacioParqueo espacio) {
        JButton btn = new JButton(espacio.getIdEspacio()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(42, 38));
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Actualiza el color, texto y tooltip de cada una de las 150 celdas en tiempo real.
     */
    public void actualizarMatrizVisual() {
        SwingUtilities.invokeLater(() -> {
            // Actualizar Área Socios (75)
            for (int i = 0; i < 75; i++) {
                EspacioParqueo esp = espaciosSocios[i];
                JButton btn = botonesSocios[i];
                if (esp != null && btn != null) {
                    aplicarEstiloCelda(btn, esp);
                }
            }

            // Actualizar Área General (75)
            for (int i = 0; i < 75; i++) {
                EspacioParqueo esp = espaciosGeneral[i];
                JButton btn = botonesGeneral[i];
                if (esp != null && btn != null) {
                    aplicarEstiloCelda(btn, esp);
                }
            }

            // Actualizar métricas numéricas
            int soc = controladorParqueo.getAreaSocios().getOcupados();
            int gen = controladorParqueo.getAreaGeneral().getOcupados();
            int tot = controladorParqueo.getTotalOcupados();

            lblTotalSocios.setText("Socios: " + soc + " / 75 ocupados (" + (75 - soc) + " libres)");
            lblTotalGeneral.setText("General: " + gen + " / 75 ocupados (" + (75 - gen) + " libres)");
            lblTotalGlobal.setText("Ocupación Global: " + tot + " / 150 (" + (tot * 100 / 150) + "%)");

            revalidate();
            repaint();
        });
    }

    private void aplicarEstiloCelda(JButton btn, EspacioParqueo esp) {
        if (esp.estaLibre()) {
            btn.setBackground(COLOR_LIBRE);
            btn.setText(esp.getIdEspacio());
            btn.setToolTipText("<html><b>Espacio " + esp.getIdEspacio() + "</b><br>Estado: LIBRE<br>Área: " + esp.getTipoEspacio() + "</html>");
        } else {
            Vehiculo v = esp.getVehiculoEstacionado();
            String placa = (v != null) ? v.getPlaca() : "OCUPADO";
            btn.setText("<html><center>" + esp.getIdEspacio() + "<br><font size='1'>" + (placa.length() > 6 ? placa.substring(0, 6) : placa) + "</font></center></html>");

            // Determinar color cromático según el tipo de ocupante
            if (v != null) {
                if (v.getPropietario() == null) {
                    btn.setBackground(COLOR_VISITANTE_OCUPADO);
                } else if (v.getPropietario() != null) {
                    Residente res = v.getPropietario();
                    if (res.isEsSocio()) {
                        if (esp.getTipoEspacio() == TipoEspacio.GENERAL) {
                            btn.setBackground(COLOR_DESBORDE); // Socio desbordado a General
                        } else {
                            btn.setBackground(COLOR_SOCIO_OCUPADO);
                        }
                    } else {
                        btn.setBackground(COLOR_GENERAL_OCUPADO);
                    }
                } else {
                    btn.setBackground(COLOR_GENERAL_OCUPADO);
                }

                String prop = (v.getPropietario() != null) ? v.getPropietario().getNombre() : "Visitante Temporal";
                btn.setToolTipText("<html><b>Espacio " + esp.getIdEspacio() + "</b> [OCUPADO]<br>"
                        + "Placa: " + v.getPlaca() + "<br>"
                        + "Propietario: " + prop + "<br>"
                        + "Vehículo: " + v.getMarca() + " " + v.getModelo() + " (" + v.getColor() + ")<br>"
                        + "Tipo: " + v.getTipo() + "</html>");
            } else {
                btn.setBackground(TemaUI.PELIGRO);
                btn.setToolTipText("Espacio " + esp.getIdEspacio() + " (Ocupado)");
            }
        }
    }

    /**
     * Muestra una ventana de diálogo con la información detallada del espacio y vehículo.
     */
    private void mostrarDetalleEspacio(EspacioParqueo esp) {
        if (esp == null) return;

        if (esp.estaLibre()) {
            JOptionPane.showMessageDialog(this,
                    "Espacio: " + esp.getIdEspacio() + "\n"
                    + "Área: " + esp.getTipoEspacio() + "\n"
                    + "Fila: " + esp.getFila() + " | Número: " + esp.getNumero() + "\n"
                    + "Estado: LIBRE Y DISPONIBLE",
                    "Detalle de Espacio Libre", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Vehiculo v = esp.getVehiculoEstacionado();
            String infoVehiculo = (v != null) ? (
                    "Placa: " + v.getPlaca() + "\n"
                    + "Marca/Modelo: " + v.getMarca() + " " + v.getModelo() + "\n"
                    + "Color: " + v.getColor() + "\n"
                    + "Tipo: " + v.getTipo() + "\n"
                    + "Propietario: " + ((v.getPropietario() != null) ? v.getPropietario().getNombre() + " (" + (v.getPropietario().isEsSocio() ? "Socio" : "General") + ")" : "Visitante Temporal")
            ) : "Sin datos de vehículo";

            String[] opciones = {"Cerrar", "Enviar a Cola de Salida 🚗💨"};
            int seleccion = JOptionPane.showOptionDialog(this,
                    "=== INFORMACIÓN DE ESPACIO DE PARQUEO ===\n\n"
                    + "Identificador: " + esp.getIdEspacio() + "\n"
                    + "Área: " + esp.getTipoEspacio() + "\n"
                    + "Estado: OCUPADO\n\n"
                    + "=== DATOS DEL VEHÍCULO ESTACIONADO ===\n"
                    + infoVehiculo,
                    "Detalle de Espacio " + esp.getIdEspacio(),
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, opciones, opciones[0]);

            if (seleccion == 1 && v != null) {
                // Acción rápida: Encolar para salida
                if (v.getEstado() == cris.sic.proyecto2.modelo.EstadoVehiculo.ESTACIONADO) {
                    simulador.encolarVehiculoSalida(v);
                    actualizarMatrizVisual();
                    JOptionPane.showMessageDialog(this,
                            "Vehículo " + v.getPlaca() + " enviado a la Cola de Salida.",
                            "Solicitud de Salida", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "El vehículo se encuentra en estado: " + v.getEstado(),
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        }
    }

    // =========================================================================
    // CALLBACKS DE GARITALISTENER
    // =========================================================================

    @Override
    public void onVehiculoIngresado(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioAsignado, Evento evento) {
        actualizarMatrizVisual();
    }

    @Override
    public void onVehiculoRechazado(String idGarita, Vehiculo vehiculo, Evento evento) {
        actualizarMatrizVisual();
    }

    @Override
    public void onVehiculoSalida(String idGarita, Vehiculo vehiculo, EspacioParqueo espacioLiberado, Evento evento) {
        actualizarMatrizVisual();
    }

    @Override
    public void onEstadoCambiado(String idGarita, String estado) {
        // No requiere actualización visual de celdas
    }
}
