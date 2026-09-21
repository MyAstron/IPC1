package cris.sic.practica2.vista;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;

/**
 * Ventana Principal de la aplicación Swing.
 * Gestiona el marco principal (1000x600 px) y el CardLayout para la navegación
 * entre los paneles de Menú Principal, Crear Piloto, Juego y Reportes.
 */
public class VentanaPrincipal extends JFrame {

    public static final String PANEL_MENU = "MENU";
    public static final String PANEL_CREAR_PILOTO = "CREAR_PILOTO";
    public static final String PANEL_JUEGO = "JUEGO";
    public static final String PANEL_REPORTES = "REPORTES";

    private final CardLayout cardLayout;
    private final JPanel panelContenedor;

    private final PanelMenuPrincipal panelMenu;
    private final PanelCrearPiloto panelCrearPiloto;
    private final PanelJuego panelJuego;
    private final PanelReportes panelReportes;

    private String panelActual;

    public VentanaPrincipal() {
        super("Simulador de Vuelo Espacial - USAC IPC1 Práctica 2");

        // Configuración de la ventana Swing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(1000, 600));
        setPreferredSize(new Dimension(1000, 600));
        setResizable(false);
        setLocationRelativeTo(null);

        // Inicializar CardLayout y contenedor
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // Instanciar los paneles del sistema
        panelMenu = new PanelMenuPrincipal(this);
        panelCrearPiloto = new PanelCrearPiloto(this);
        panelJuego = new PanelJuego(this);
        panelReportes = new PanelReportes(this);

        // Registrar los paneles en el CardLayout
        panelContenedor.add(panelMenu, PANEL_MENU);
        panelContenedor.add(panelCrearPiloto, PANEL_CREAR_PILOTO);
        panelContenedor.add(panelJuego, PANEL_JUEGO);
        panelContenedor.add(panelReportes, PANEL_REPORTES);

        add(panelContenedor);

        // Iniciar en el menú principal
        mostrarPanel(PANEL_MENU);
    }

    /**
     * Alterna la vista activa en el contenedor utilizando el CardLayout.
     *
     * @param nombrePanel Identificador de la vista (PANEL_MENU, PANEL_CREAR_PILOTO, etc.)
     */
    public void mostrarPanel(String nombrePanel) {
        this.panelActual = nombrePanel;
        cardLayout.show(panelContenedor, nombrePanel);
    }

    /**
     * Muestra la pantalla de reportes seleccionando una pestaña específica.
     *
     * @param pestana 0 para Top de Puntajes, 1 para Historial
     */
    public void mostrarReportes(int pestana) {
        panelReportes.seleccionarPestana(pestana);
        mostrarPanel(PANEL_REPORTES);
    }

    public String getPanelActual() {
        return panelActual;
    }

    public PanelMenuPrincipal getPanelMenu() {
        return panelMenu;
    }

    public PanelCrearPiloto getPanelCrearPiloto() {
        return panelCrearPiloto;
    }

    public PanelJuego getPanelJuego() {
        return panelJuego;
    }

    public PanelReportes getPanelReportes() {
        return panelReportes;
    }
}
