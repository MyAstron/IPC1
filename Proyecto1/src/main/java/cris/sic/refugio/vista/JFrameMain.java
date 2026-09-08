package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.BitacoraServicio;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Ventana principal del sistema, configurada manualmente utilizando Java Swing
public class JFrameMain extends JFrame {

    public JFrameMain() {
        // Configuracion basica del JFrame principal
        setTitle("Sistema de Gestion de Refugio de Animales");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // Obtener el usuario autenticado
        Usuario u = AutenticacionServicio.getUsuarioLogueado();
        String nombreUser = (u != null) ? u.getUsuario() : "Invitado";
        String rolUser = (u != null) ? u.getRol() : "Ninguno";

        JLabel lblBienvenida = new JLabel("Bienvenido: " + nombreUser + " (" + rolUser + ")");
        lblBienvenida.setBounds(50, 30, 300, 25);
        add(lblBienvenida);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(630, 30, 120, 30);
        add(btnCerrarSesion);

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
        pestanas.setBounds(10, 80, 777, 470);
        pestanas.addTab("Animales", panelAnimales);
        pestanas.addTab("Adoptantes", panelAdoptantes);
        pestanas.addTab("Solicitudes", panelSolicitudes);
        pestanas.addTab("Rescates", panelRescates);
        pestanas.addTab("Ubicaciones", panelUbicaciones);
        pestanas.addTab("Reportes", panelReportes);
        pestanas.addTab("Estudiante", panelEstudiante);
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
                BitacoraServicio.registrarAccion(nombreUser, "Autenticacion", "Inicio de sesion exitoso. Rol: " + rolUser);
            }
        });
    }
}
