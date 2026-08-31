package cris.sic.refugio.vista;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.servicio.AutenticacionServicio;
import cris.sic.refugio.servicio.BitacoraServicio;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Formulario de inicio de sesion con validacion de credenciales y bloqueo de intentos fallidos
public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private int intentosFallidos = 0;

    public LoginFrame() {
        // Configuracion basica del JFrame de Login
        setTitle("Inicio de Sesion");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Componentes de la interfaz de usuario
        JLabel lblTitulo = new JLabel("ACCESO AL SISTEMA");
        lblTitulo.setBounds(130, 20, 150, 25);
        add(lblTitulo);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(50, 70, 80, 25);
        add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(150, 70, 180, 25);
        add(txtUsuario);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 110, 80, 25);
        add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 110, 180, 25);
        add(txtContrasena);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(150, 160, 100, 30);
        add(btnIngresar);

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
