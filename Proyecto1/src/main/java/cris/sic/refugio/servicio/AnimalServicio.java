package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para gestionar el CRUD y las validaciones de los animales rescatados
public class AnimalServicio {

    // Modulo para validar el formato del codigo (A-xxx donde xxx son digitos)
    public static boolean validarCodigo(String codigo) {
        if (codigo == null || codigo.length() != 5) {
            return false;
        }
        if (!codigo.startsWith("A-")) {
            return false;
        }
        for (int i = 2; i < 5; i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para buscar si un codigo de animal ya existe en la base de datos (incluyendo eliminados)
    public static boolean existeCodigo(String codigo) {
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    // Modulo para generar el siguiente codigo correlativo disponible (A-xxx)
    public static String generarSiguienteCodigoAnimal() {
        for (int i = 1; i <= 999; i++) {
            String codigo = String.format("A-%03d", i);
            if (!existeCodigo(codigo)) {
                return codigo;
            }
        }
        return null;
    }

    // Modulo para registrar un nuevo animal con sus validaciones
    public static String registrarAnimal(String codigo, String nombre, String especie, int edad, String estadoClinico, String usuarioActivo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = generarSiguienteCodigoAnimal();
            if (codigo == null) {
                return "No hay codigos disponibles para registrar nuevos animales.";
            }
        }
        if (!validarFormatoCompleto(codigo, nombre, especie, edad, estadoClinico)) {
            BitacoraServicio.registrarError(usuarioActivo, "Animales", "Error de validacion al intentar registrar animal: " + codigo);
            return "Campos invalidos. Verifique formatos y rango de edad (0-25).";
        }
        if (existeCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Animales", "Intento de registro con codigo duplicado: " + codigo);
            return "El codigo del animal ya existe (puede estar registrado o haber sido eliminado).";
        }
        if (BaseDatosMemoria.contadorAnimales >= BaseDatosMemoria.MAX_ANIMALES) {
            BitacoraServicio.registrarError(usuarioActivo, "Animales", "Capacidad maxima de animales alcanzada.");
            return "No hay espacio suficiente en el sistema para mas registros.";
        }

        Animal nuevo = new Animal(codigo, nombre, especie, edad, estadoClinico, "DISPONIBLE");
        BaseDatosMemoria.animales[BaseDatosMemoria.contadorAnimales] = nuevo;
        BaseDatosMemoria.contadorAnimales++;

        BitacoraServicio.registrarAccion(usuarioActivo, "Animales", "Registro exitoso de animal: " + codigo + " (" + nombre + ")");
        BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "REGISTRO", nuevo);
        return "SUCCESS";
    }

    // Modulo para actualizar los datos de un animal existente
    public static String actualizarAnimal(String codigo, String nombre, String especie, int edad, String estadoClinico, String estadoAdopcion, String usuarioActivo) {
        if (nombre.trim().isEmpty() || especie.trim().isEmpty() || edad < 0 || edad > 25) {
            BitacoraServicio.registrarError(usuarioActivo, "Animales", "Error de validacion al actualizar animal: " + codigo);
            return "Campos invalidos. Edad debe estar entre 0 y 25.";
        }

        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(codigo)) {
                if (a.getEstadoAdopcion().equals("ELIMINADO")) {
                    return "El animal seleccionado ha sido eliminado logicamente.";
                }
                a.setNombre(nombre);
                a.setEspecie(especie);
                a.setEdad(edad);
                a.setEstadoClinico(estadoClinico);
                a.setEstadoAdopcion(estadoAdopcion);

                BitacoraServicio.registrarAccion(usuarioActivo, "Animales", "Actualizacion exitosa de animal: " + codigo);
                BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "ACTUALIZACION", a);
                return "SUCCESS";
            }
        }
        return "Animal no encontrado.";
    }

    // Modulo para realizar la baja logica del animal (estadoAdopcion = "ELIMINADO")
    public static String eliminarAnimal(String codigo, String usuarioActivo) {
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(codigo)) {
                if (a.getEstadoAdopcion().equals("ELIMINADO")) {
                    return "El animal ya se encuentra eliminado.";
                }
                a.setEstadoAdopcion("ELIMINADO");
                // Liberar la celda correspondiente en la matriz de ubicaciones
                UbicacionServicio.liberarAnimal(codigo, usuarioActivo);
                // Eliminar los datos del rescate asociado si pertenece a uno
                RescateServicio.eliminarRescatePorAnimal(codigo, usuarioActivo);

                BitacoraServicio.registrarAccion(usuarioActivo, "Animales", "Baja logica aplicada al animal: " + codigo);
                BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "BAJA_LOGICA", a);
                return "SUCCESS";
            }
        }
        return "Animal no encontrado.";
    }

    // Modulo para validar de forma integral los datos de un animal
    private static boolean validarFormatoCompleto(String codigo, String nombre, String especie, int edad, String estadoClinico) {
        if (!validarCodigo(codigo)) return false;
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (especie == null || especie.trim().isEmpty()) return false;
        if (edad < 0 || edad > 25) return false;
        if (estadoClinico == null || estadoClinico.trim().isEmpty()) return false;
        return true;
    }

    // Modulo para buscar animales activos por filtros
    public static Animal[] filtrarAnimales(String codigoFiltro, String nombreFiltro, String especieFiltro, String estadoFiltro) {
        int coincidentes = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && !a.getEstadoAdopcion().equals("ELIMINADO")) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !a.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (nombreFiltro != null && !nombreFiltro.trim().isEmpty() && !a.getNombre().toLowerCase().contains(nombreFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (especieFiltro != null && !especieFiltro.trim().isEmpty() && !a.getEspecie().toLowerCase().contains(especieFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !a.getEstadoAdopcion().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) coincidentes++;
            }
        }

        Animal[] resultado = new Animal[coincidentes];
        int idx = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && !a.getEstadoAdopcion().equals("ELIMINADO")) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !a.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (nombreFiltro != null && !nombreFiltro.trim().isEmpty() && !a.getNombre().toLowerCase().contains(nombreFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (especieFiltro != null && !especieFiltro.trim().isEmpty() && !a.getEspecie().toLowerCase().contains(especieFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !a.getEstadoAdopcion().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) {
                    resultado[idx] = a;
                    idx++;
                }
            }
        }
        return resultado;
    }

    // Modulo para recargar los animales directamente desde el archivo de bitacora
    public static void cargarDesdeBitacora() {
        PersistenciaServicio.cargarAnimalesDesdeBitacora();
    }
}
