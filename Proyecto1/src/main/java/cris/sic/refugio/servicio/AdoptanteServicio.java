package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para la gestion de los adoptantes y sus validaciones correspondientes
public class AdoptanteServicio {

    // Modulo para validar el formato del codigo (AD-xxx donde xxx son digitos)
    public static boolean validarCodigo(String codigo) {
        if (codigo == null || codigo.length() != 6) {
            return false;
        }
        if (!codigo.startsWith("AD-")) {
            return false;
        }
        for (int i = 3; i < 6; i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para validar que el DPI contenga exactamente 13 digitos numericos
    public static boolean validarDpi(String dpi) {
        if (dpi == null || dpi.length() != 13) {
            return false;
        }
        for (int i = 0; i < 13; i++) {
            if (!Character.isDigit(dpi.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para validar que el telefono contenga exactamente 8 digitos numericos
    public static boolean validarTelefono(String telefono) {
        if (telefono == null || telefono.length() != 8) {
            return false;
        }
        for (int i = 0; i < 8; i++) {
            if (!Character.isDigit(telefono.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para verificar si un DPI ya esta registrado en la base de datos
    public static boolean existeDpi(String dpi) {
        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null && ad.getDpi().equals(dpi)) {
                return true;
            }
        }
        return false;
    }

    // Modulo para verificar si un codigo de adoptante ya esta registrado
    public static boolean existeCodigo(String codigo) {
        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null && ad.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    // Modulo para generar el siguiente codigo correlativo disponible (AD-xxx)
    public static String generarSiguienteCodigoAdoptante() {
        for (int i = 1; i <= 999; i++) {
            String codigo = String.format("AD-%03d", i);
            if (!existeCodigo(codigo)) {
                return codigo;
            }
        }
        return null;
    }

    // Modulo para registrar un nuevo adoptante con sus validaciones correspondientes
    public static String registrarAdoptante(String codigo, String nombre, String dpi, String telefono, String usuarioActivo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = generarSiguienteCodigoAdoptante();
            if (codigo == null) {
                return "No hay codigos disponibles para registrar nuevos adoptantes.";
            }
        }
        if (!validarCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Codigo de adoptante invalido: " + codigo);
            return "Codigo invalido. Debe usar el patron AD-xxx.";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Nombre de adoptante vacio.");
            return "El nombre del adoptante es obligatorio.";
        }
        if (!validarDpi(dpi)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "DPI invalido: " + dpi);
            return "DPI invalido. Debe tener exactamente 13 digitos.";
        }
        if (!validarTelefono(telefono)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Telefono invalido: " + telefono);
            return "Telefono invalido. Debe tener exactamente 8 digitos.";
        }
        if (existeCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Codigo de adoptante duplicado: " + codigo);
            return "El codigo de adoptante ya esta registrado.";
        }
        if (existeDpi(dpi)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Intento de registro con DPI duplicado: " + dpi);
            return "Ya existe un adoptante registrado con ese DPI.";
        }
        if (BaseDatosMemoria.contadorAdoptantes >= BaseDatosMemoria.MAX_ADOPTANTES) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Capacidad maxima de adoptantes alcanzada.");
            return "No hay espacio en el sistema para registrar mas adoptantes.";
        }

        Adoptante nuevo = new Adoptante(codigo, nombre, dpi, telefono);
        BaseDatosMemoria.adoptantes[BaseDatosMemoria.contadorAdoptantes] = nuevo;
        BaseDatosMemoria.contadorAdoptantes++;

        BitacoraServicio.registrarAccion(usuarioActivo, "Adoptantes", "Adoptante registrado exitosamente: " + codigo);
        return "SUCCESS";
    }

    // Modulo para editar la informacion de un adoptante existente
    public static String editarAdoptante(String codigo, String nombre, String dpi, String telefono, String usuarioActivo) {
        if (nombre == null || nombre.trim().isEmpty() || !validarDpi(dpi) || !validarTelefono(telefono)) {
            BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Validacion fallida al editar adoptante: " + codigo);
            return "Campos invalidos. Verifique el DPI (13 digitos) y el Telefono (8 digitos).";
        }

        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null && ad.getCodigo().equals(codigo)) {
                if (!ad.getDpi().equals(dpi) && existeDpi(dpi)) {
                    BitacoraServicio.registrarError(usuarioActivo, "Adoptantes", "Intento de duplicar DPI al editar: " + dpi);
                    return "El DPI ingresado ya pertenece a otro adoptante.";
                }
                ad.setNombre(nombre);
                ad.setDpi(dpi);
                ad.setTelefono(telefono);

                BitacoraServicio.registrarAccion(usuarioActivo, "Adoptantes", "Adoptante editado exitosamente: " + codigo);
                return "SUCCESS";
            }
        }
        return "Adoptante no encontrado.";
    }

    // Modulo para filtrar adoptantes activos por filtros
    public static Adoptante[] filtrarAdoptantes(String codigoFiltro, String nombreFiltro, String dpiFiltro) {
        int coincidentes = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !ad.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (nombreFiltro != null && !nombreFiltro.trim().isEmpty() && !ad.getNombre().toLowerCase().contains(nombreFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (dpiFiltro != null && !dpiFiltro.trim().isEmpty() && !ad.getDpi().toLowerCase().contains(dpiFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (cumple) coincidentes++;
            }
        }

        Adoptante[] resultado = new Adoptante[coincidentes];
        int idx = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !ad.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (nombreFiltro != null && !nombreFiltro.trim().isEmpty() && !ad.getNombre().toLowerCase().contains(nombreFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (dpiFiltro != null && !dpiFiltro.trim().isEmpty() && !ad.getDpi().toLowerCase().contains(dpiFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (cumple) {
                    resultado[idx] = ad;
                    idx++;
                }
            }
        }
        return resultado;
    }
}
