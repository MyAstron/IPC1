package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.modelo.Usuario;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

// Servicio para la lectura y escritura de arreglos estaticos en archivos de texto plano (.txt)
public class PersistenciaServicio {

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_ANIMALES = "animales.txt";
    private static final String ARCHIVO_ADOPTANTES = "adoptantes.txt";
    private static final String ARCHIVO_SOLICITUDES = "solicitudes.txt";
    private static final String ARCHIVO_RESCATES = "rescates.txt";
    private static final String ARCHIVO_UBICACIONES = "ubicaciones.txt";

    // Modulo para guardar todos los datos del sistema en sus respectivos archivos
    public static String guardarTodo(String usuarioActivo) {
        try {
            guardarUsuarios();
            guardarAnimales();
            guardarAdoptantes();
            guardarSolicitudes();
            guardarRescates();
            guardarUbicaciones();
            BitacoraServicio.registrarAccion(usuarioActivo, "Persistencia", "Guardado exitoso de todos los datos en archivos .txt");
            return "SUCCESS";
        } catch (Exception e) {
            BitacoraServicio.registrarError(usuarioActivo, "Persistencia", "Error al guardar archivos de texto: " + e.getMessage());
            return "Error al guardar datos: " + e.getMessage();
        }
    }

    // Modulo para cargar todos los datos desde los archivos al iniciar la aplicacion
    public static void cargarTodo() {
        cargarUsuarios();
        cargarAnimales();
        cargarAdoptantes();
        cargarSolicitudes();
        cargarRescates();
        cargarUbicaciones();
    }

    // Modulo para guardar usuarios
    private static void guardarUsuarios() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (int i = 0; i < BaseDatosMemoria.contadorUsuarios; i++) {
                Usuario u = BaseDatosMemoria.usuarios[i];
                if (u != null) {
                    pw.println(u.getUsuario() + "|" + u.getContrasena() + "|" + u.getRol());
                }
            }
        }
    }

    // Modulo para cargar usuarios
    private static void cargarUsuarios() {
        File file = new File(ARCHIVO_USUARIOS);
        if (!file.exists()) {
            AutenticacionServicio.inicializarUsuariosPorDefecto();
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            BaseDatosMemoria.contadorUsuarios = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 3 && BaseDatosMemoria.contadorUsuarios < BaseDatosMemoria.MAX_USUARIOS) {
                    BaseDatosMemoria.usuarios[BaseDatosMemoria.contadorUsuarios] = new Usuario(p[0], p[1], p[2]);
                    BaseDatosMemoria.contadorUsuarios++;
                }
            }
            if (BaseDatosMemoria.contadorUsuarios == 0) {
                AutenticacionServicio.inicializarUsuariosPorDefecto();
            }
        } catch (Exception e) {
            AutenticacionServicio.inicializarUsuariosPorDefecto();
        }
    }

    // Modulo para guardar animales
    private static void guardarAnimales() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ANIMALES))) {
            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null) {
                    pw.println(a.getCodigo() + "|" + a.getNombre() + "|" + a.getEspecie() + "|" + a.getEdad() + "|" + a.getEstadoClinico() + "|" + a.getEstadoAdopcion());
                }
            }
        }
    }

    // Modulo para cargar animales
    private static void cargarAnimales() {
        File file = new File(ARCHIVO_ANIMALES);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            BaseDatosMemoria.contadorAnimales = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 6 && BaseDatosMemoria.contadorAnimales < BaseDatosMemoria.MAX_ANIMALES) {
                    int edad = 0;
                    try { edad = Integer.parseInt(p[3]); } catch (Exception ignored) {}
                    BaseDatosMemoria.animales[BaseDatosMemoria.contadorAnimales] = new Animal(p[0], p[1], p[2], edad, p[4], p[5]);
                    BaseDatosMemoria.contadorAnimales++;
                }
            }
        } catch (Exception ignored) {}
    }

    // Modulo para guardar adoptantes
    private static void guardarAdoptantes() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_ADOPTANTES))) {
            for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
                Adoptante ad = BaseDatosMemoria.adoptantes[i];
                if (ad != null) {
                    pw.println(ad.getCodigo() + "|" + ad.getNombre() + "|" + ad.getDpi() + "|" + ad.getTelefono());
                }
            }
        }
    }

    // Modulo para cargar adoptantes
    private static void cargarAdoptantes() {
        File file = new File(ARCHIVO_ADOPTANTES);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            BaseDatosMemoria.contadorAdoptantes = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 4 && BaseDatosMemoria.contadorAdoptantes < BaseDatosMemoria.MAX_ADOPTANTES) {
                    BaseDatosMemoria.adoptantes[BaseDatosMemoria.contadorAdoptantes] = new Adoptante(p[0], p[1], p[2], p[3]);
                    BaseDatosMemoria.contadorAdoptantes++;
                }
            }
        } catch (Exception ignored) {}
    }

    // Modulo para guardar solicitudes
    private static void guardarSolicitudes() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_SOLICITUDES))) {
            for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
                Solicitud s = BaseDatosMemoria.solicitudes[i];
                if (s != null) {
                    pw.println(s.getCodigo() + "|" + s.getCodigoAnimal() + "|" + s.getCodigoAdoptante() + "|" + s.getFecha() + "|" + s.getEstado());
                }
            }
        }
    }

    // Modulo para cargar solicitudes
    private static void cargarSolicitudes() {
        File file = new File(ARCHIVO_SOLICITUDES);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            BaseDatosMemoria.contadorSolicitudes = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 5 && BaseDatosMemoria.contadorSolicitudes < BaseDatosMemoria.MAX_SOLICITUDES) {
                    BaseDatosMemoria.solicitudes[BaseDatosMemoria.contadorSolicitudes] = new Solicitud(p[0], p[1], p[2], p[3], p[4]);
                    BaseDatosMemoria.contadorSolicitudes++;
                }
            }
        } catch (Exception ignored) {}
    }

    // Modulo para guardar rescates
    private static void guardarRescates() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESCATES))) {
            for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
                Rescate r = BaseDatosMemoria.rescates[i];
                if (r != null) {
                    pw.println(r.getCodigo() + "|" + r.getDireccionDescripcion() + "|" + r.getPrioridad() + "|" + r.getEstado() + "|" + r.getFecha() + "|" + r.getCodigoAnimalVinculado());
                }
            }
        }
    }

    // Modulo para cargar rescates
    private static void cargarRescates() {
        File file = new File(ARCHIVO_RESCATES);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            BaseDatosMemoria.contadorRescates = 0;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 6 && BaseDatosMemoria.contadorRescates < BaseDatosMemoria.MAX_RESCATES) {
                    BaseDatosMemoria.rescates[BaseDatosMemoria.contadorRescates] = new Rescate(p[0], p[1], p[2], p[3], p[4], p[5]);
                    BaseDatosMemoria.contadorRescates++;
                }
            }
        } catch (Exception ignored) {}
    }

    // Modulo para guardar la matriz de ubicaciones
    private static void guardarUbicaciones() throws Exception {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_UBICACIONES))) {
            for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
                for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                    String animal = BaseDatosMemoria.ubicacionesRefugio[f][c];
                    if (animal != null && !animal.trim().isEmpty()) {
                        pw.println(f + "|" + c + "|" + animal);
                    }
                }
            }
        }
    }

    // Modulo para cargar la matriz de ubicaciones
    private static void cargarUbicaciones() {
        // Limpiar la matriz primero
        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                BaseDatosMemoria.ubicacionesRefugio[f][c] = null;
            }
        }

        File file = new File(ARCHIVO_UBICACIONES);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] p = linea.split("\\|", -1);
                if (p.length >= 3) {
                    try {
                        int f = Integer.parseInt(p[0]);
                        int c = Integer.parseInt(p[1]);
                        if (f >= 0 && f < BaseDatosMemoria.FILAS_REFUGIO && c >= 0 && c < BaseDatosMemoria.COLUMNAS_REFUGIO) {
                            BaseDatosMemoria.ubicacionesRefugio[f][c] = p[2];
                        }
                    } catch (Exception ignored) {}
                }
            }
        } catch (Exception ignored) {}
    }
}
