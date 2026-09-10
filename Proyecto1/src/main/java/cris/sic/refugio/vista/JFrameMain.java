package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.BitacoraServicio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Ventana principal del sistema, configurada manualmente con diseno ARAMS (Kindred Shelter Systems)
public class JFrameMain extends JFrame {

    public JFrameMain() {
        // Configuracion basica del JFrame principal
        setTitle("ARAMS — Sistema de Gestión de Refugio de Animales");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(ThemeARAMS.BACKGROUND);

        // Obtener el usuario autenticado
        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        final String nombreUser = (u != null) ? u.getUsuario() : "Invitado";
        final String rolUser = (u != null) ? u.getRol() : "Ninguno";

        // 1. TopAppBar Global ARAMS
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBounds(0, 0, 800, 65);
        pnlHeader.setBackground(ThemeARAMS.PRIMARY_DARK);
        pnlHeader.setLayout(null);

        JLabel lblBrand = new JLabel("🐾 ARAMS");
        lblBrand.setFont(ThemeARAMS.FONT_DISPLAY);
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setBounds(16, 12, 120, 24);
        pnlHeader.add(lblBrand);

        JLabel lblSubtitle = new JLabel("Kindred Shelter Systems — Refugio de Animales");
        lblSubtitle.setFont(ThemeARAMS.FONT_SMALL);
        lblSubtitle.setForeground(ThemeARAMS.PRIMARY_CONTAINER);
        lblSubtitle.setBounds(16, 38, 300, 16);
        pnlHeader.add(lblSubtitle);

        // Chip/Badge de Usuario Activo
        JLabel lblUserBadge = new JLabel("👤 " + nombreUser + " (" + rolUser + ")", SwingConstants.CENTER);
        lblUserBadge.setFont(ThemeARAMS.FONT_BODY_BOLD);
        lblUserBadge.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblUserBadge.setBackground(ThemeARAMS.PRIMARY_CONTAINER);
        lblUserBadge.setOpaque(true);
        lblUserBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x70, 0xA8, 0x88), 1),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        lblUserBadge.setBounds(480, 18, 175, 28);
        pnlHeader.add(lblUserBadge);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(665, 18, 115, 28);
        ThemeARAMS.aplicarEstiloBotonPeligro(btnCerrarSesion);
        pnlHeader.add(btnCerrarSesion);

        add(pnlHeader);

        // Instanciar los paneles correspondientes a los modulos
        final AnimalPanel panelAnimales = new AnimalPanel();
        final AdoptantePanel panelAdoptantes = new AdoptantePanel();
        final SolicitudPanel panelSolicitudes = new SolicitudPanel();
        final RescatePanel panelRescates = new RescatePanel();
        final UbicacionPanel panelUbicaciones = new UbicacionPanel();
        final ReportePanel panelReportes = new ReportePanel();
        final EstudiantePanel panelEstudiante = new EstudiantePanel();

        // Panel de pestañas para albergar los modulos core
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setBounds(10, 75, 777, 475);
        pestanas.setFont(ThemeARAMS.FONT_BODY_BOLD);
        pestanas.setBackground(Color.WHITE);
        pestanas.setForeground(ThemeARAMS.PRIMARY_DARK);
        pestanas.addTab("🐾 Animales", panelAnimales);
        pestanas.addTab("👥 Adoptantes", panelAdoptantes);
        pestanas.addTab("📄 Solicitudes", panelSolicitudes);
        pestanas.addTab("🚑 Rescates", panelRescates);
        pestanas.addTab("🗺️ Ubicaciones", panelUbicaciones);
        pestanas.addTab("📊 Reportes", panelReportes);
        pestanas.addTab("🎓 Estudiante", panelEstudiante);
        add(pestanas);

        // Evento para refrescar la informacion de las tablas y cuadriculas al cambiar de pestaña
        pestanas.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {
                panelAnimales.buscar();
                panelAdoptantes.buscar();
                panelSolicitudes.buscar();
                panelRescates.buscar();
                panelUbicaciones.actualizarCuadricula();
            }
        });

        // Evento para cerrar sesion
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AutenticacionServicio.cerrarSesion();
                LoginFrame lf = new LoginFrame();
                lf.setVisible(true);
                dispose();
                BitacoraServicio.registrarAccion(nombreUser, "Autenticacion", "Cierre de sesion exitoso. Rol: " + rolUser);
            }
        });
    }
}
