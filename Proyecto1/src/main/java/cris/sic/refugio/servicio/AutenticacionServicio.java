package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para gestionar el inicio de sesion y la autenticacion de usuarios
public class AutenticacionServicio {

    // Variable estatica para guardar el usuario actualmente logueado
    private static Usuario usuarioLogueado = null;

    // Modulo para inicializar los usuarios predeterminados si el arreglo esta vacio
    public static void inicializarUsuariosPorDefecto() {
        if (BaseDatosMemoria.contadorUsuarios == 0) {
            // Agregar Administrador
            BaseDatosMemoria.usuarios[0] = new Usuario("admin1", "Refugio2026", "ADMIN");
            BaseDatosMemoria.contadorUsuarios++;

            // Agregar Auxiliar
            BaseDatosMemoria.usuarios[1] = new Usuario("auxiliar1", "Refugio2026", "AUXILIAR");
            BaseDatosMemoria.contadorUsuarios++;
        }
    }

    // Modulo para validar credenciales de un usuario
    public static Usuario autenticar(String nombreUsuario, String contrasena) {
        for (int i = 0; i < BaseDatosMemoria.contadorUsuarios; i++) {
            Usuario u = BaseDatosMemoria.usuarios[i];
            if (u != null && u.getUsuario().equals(nombreUsuario) && u.getContrasena().equals(contrasena)) {
                usuarioLogueado = u;
                return u;
            }
        }
        return null;
    }

    // Modulo para obtener el usuario que inicio sesion
    public static Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    // Modulo para cerrar la sesion actual
    public static void cerrarSesion() {
        usuarioLogueado = null;
    }
}
