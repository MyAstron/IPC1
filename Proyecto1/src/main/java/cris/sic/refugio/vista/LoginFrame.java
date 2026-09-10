package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.BitacoraServicio;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Formulario de inicio de sesion con identidad ARAMS, validacion de credenciales y bloqueo de intentos fallidos
public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private int intentosFallidos = 0;

    public LoginFrame() {
        // Configuracion basica del JFrame de Login
        setTitle("ARAMS — Iniciar Sesión");
        setSize(420, 340);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(ThemeARAMS.BACKGROUND);

        // Header Superior con Marca ARAMS
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBounds(0, 0, 420, 70);
        pnlHeader.setBackground(ThemeARAMS.PRIMARY_DARK);
        pnlHeader.setLayout(null);

        JLabel lblLogo = new JLabel("🐾 ARAMS", SwingConstants.CENTER);
        lblLogo.setFont(ThemeARAMS.FONT_DISPLAY);
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setBounds(0, 12, 420, 26);
        pnlHeader.add(lblLogo);

        JLabel lblSub = new JLabel("Kindred Shelter Systems — Acceso al Sistema", SwingConstants.CENTER);
        lblSub.setFont(ThemeARAMS.FONT_SMALL);
        lblSub.setForeground(ThemeARAMS.PRIMARY_CONTAINER);
        lblSub.setBounds(0, 40, 420, 16);
        pnlHeader.add(lblSub);

        add(pnlHeader);

        // Campos de Acceso
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(ThemeARAMS.FONT_BODY_BOLD);
        lblUsuario.setForeground(ThemeARAMS.TEXT_MAIN);
        lblUsuario.setBounds(50, 95, 100, 22);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 120, 320, 30);
        ThemeARAMS.aplicarEstiloCampo(txtUsuario);
        add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(ThemeARAMS.FONT_BODY_BOLD);
        lblContrasena.setForeground(ThemeARAMS.TEXT_MAIN);
        lblContrasena.setBounds(50, 160, 100, 22);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(50, 185, 320, 30);
        ThemeARAMS.aplicarEstiloCampo(txtContrasena);
        add(txtContrasena);

        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setBounds(50, 235, 320, 36);
        ThemeARAMS.aplicarEstiloBotonPrincipal(btnIngresar);
        add(btnIngresar);

        getRootPane().setDefaultButton(btnIngresar);

        // Evento para procesar la autenticacion al hacer clic en el boton
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                procesarLogin();
            }
        });
    }

    // Modulo para procesar las credenciales ingresadas por el usuario
    private void procesarLogin() {
        String usuarioStr = txtUsuario.getText().trim();
        String contrasenaStr = new String(txtContrasena.getPassword());

        if (usuarioStr.isEmpty() || contrasenaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioAutenticado = AutenticacionServicio.autenticar(usuarioStr, contrasenaStr);

        if (usuarioAutenticado != null) {
            // Registro de evento exitoso en la bitacora
            BitacoraServicio.registrarAccion(usuarioStr, "Autenticacion", "Inicio de sesion exitoso. Rol: " + usuarioAutenticado.getRol());

            // Abrir la ventana principal y cerrar el login
            JFrameMain mainFrame = new JFrameMain();
            mainFrame.setVisible(true);
            this.dispose();
        } else {
            intentosFallidos++;
            // Registro de error en la bitacora
            BitacoraServicio.registrarError(usuarioStr.isEmpty() ? "DESCONOCIDO" : usuarioStr, "Autenticacion", "Intento de inicio de sesion fallido (Intentos fallidos: " + intentosFallidos + ")");

            if (intentosFallidos >= 3) {
                btnIngresar.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Ha superado el limite de 3 intentos fallidos. Acceso bloqueado.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas. Intentos restantes: " + (3 - intentosFallidos), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
