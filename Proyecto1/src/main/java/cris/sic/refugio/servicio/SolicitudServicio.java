package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Solicitud;
import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.modelo.Adoptante;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para gestionar las solicitudes de adopcion de animales
public class SolicitudServicio {

    // Modulo para validar el formato del codigo de la solicitud (S-xxx)
    public static boolean validarCodigo(String codigo) {
        if (codigo == null || codigo.length() != 5) {
            return false;
        }
        if (!codigo.startsWith("S-")) {
            return false;
        }
        for (int i = 2; i < 5; i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para buscar si un codigo de solicitud ya esta registrado
    public static boolean existeCodigo(String codigo) {
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null && s.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    // Modulo para generar el siguiente codigo correlativo disponible (S-xxx)
    public static String generarSiguienteCodigoSolicitud() {
        for (int i = 1; i <= 999; i++) {
            String codigo = String.format("S-%03d", i);
            if (!existeCodigo(codigo)) {
                return codigo;
            }
        }
        return null;
    }

    // Modulo para registrar una nueva solicitud en el sistema
    public static String registrarSolicitud(String codigo, String codigoAnimal, String codigoAdoptante, String fecha, String usuarioActivo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = generarSiguienteCodigoSolicitud();
            if (codigo == null) {
                return "No hay codigos disponibles para registrar nuevas solicitudes.";
            }
        }
        if (!validarCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Codigo de solicitud invalido: " + codigo);
            return "Codigo invalido. Debe usar el formato S-xxx.";
        }

        if (existeCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Codigo de solicitud duplicado: " + codigo);
            return "El codigo de solicitud ya existe.";
        }

        // Validar que el animal exista y este DISPONIBLE
        Animal animal = null;
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(codigoAnimal)) {
                animal = a;
                break;
            }
        }

        if (animal == null || animal.getEstadoAdopcion().equals("ELIMINADO")) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Intento de solicitud para animal inexistente o eliminado: " + codigoAnimal);
            return "El animal no existe o no esta registrado.";
        }

        if (!animal.getEstadoAdopcion().equals("DISPONIBLE")) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Intento de solicitud para animal no disponible: " + codigoAnimal);
            return "El animal no se encuentra disponible para adopcion (actualmente " + animal.getEstadoAdopcion() + ").";
        }

        // Validar que el adoptante exista
        boolean adoptanteExiste = false;
        for (int i = 0; i < BaseDatosMemoria.contadorAdoptantes; i++) {
            Adoptante ad = BaseDatosMemoria.adoptantes[i];
            if (ad != null && ad.getCodigo().equals(codigoAdoptante)) {
                adoptanteExiste = true;
                break;
            }
        }

        if (!adoptanteExiste) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Adoptante no encontrado en el sistema: " + codigoAdoptante);
            return "El adoptante especificado no esta registrado.";
        }

        if (fecha == null || fecha.trim().isEmpty()) {
            return "La fecha de solicitud es obligatoria.";
        }

        if (BaseDatosMemoria.contadorSolicitudes >= BaseDatosMemoria.MAX_SOLICITUDES) {
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Capacidad maxima de solicitudes alcanzada.");
            return "No hay suficiente espacio para registrar mas solicitudes.";
        }

        Solicitud nueva = new Solicitud(codigo, codigoAnimal, codigoAdoptante, fecha, "PENDIENTE");
        BaseDatosMemoria.solicitudes[BaseDatosMemoria.contadorSolicitudes] = nueva;
        BaseDatosMemoria.contadorSolicitudes++;

        BitacoraServicio.registrarAccion(usuarioActivo, "Solicitudes", "Solicitud registrada con exito: " + codigo);
        return "SUCCESS";
    }

    // Modulo para aprobar una solicitud de adopcion
    public static String aprobarSolicitud(String codigo, String usuarioActivo) {
        Solicitud solicitud = null;
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null && s.getCodigo().equals(codigo)) {
                solicitud = s;
                break;
            }
        }

        if (solicitud == null) {
            return "La solicitud no existe.";
        }

        if (!solicitud.getEstado().equals("PENDIENTE")) {
            return "La solicitud ya ha sido procesada (Estado actual: " + solicitud.getEstado() + ").";
        }

        // Validar el animal
        Animal animal = null;
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(solicitud.getCodigoAnimal())) {
                animal = a;
                break;
            }
        }

        if (animal == null || animal.getEstadoAdopcion().equals("ELIMINADO")) {
            solicitud.setEstado("RECHAZADA");
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Solicitud rechazada automaticamente por animal inexistente o eliminado: " + solicitud.getCodigo());
            return "El animal vinculado ya no se encuentra en el refugio.";
        }

        if (!animal.getEstadoAdopcion().equals("DISPONIBLE")) {
            solicitud.setEstado("RECHAZADA");
            BitacoraServicio.registrarError(usuarioActivo, "Solicitudes", "Solicitud rechazada automaticamente por animal no disponible: " + solicitud.getCodigo());
            return "El animal ya no esta disponible para adopcion.";
        }

        // Marcar solicitud como aprobada
        solicitud.setEstado("APROBADA");

        // Cambiar el estado del animal a ADOPTADO
        animal.setEstadoAdopcion("ADOPTADO");

        // Liberar la celda en la matriz
        UbicacionServicio.liberarAnimal(animal.getCodigo(), usuarioActivo);

        // Rechazar de manera automatica cualquier otra solicitud PENDIENTE para ese mismo animal
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null && s.getCodigoAnimal().equals(animal.getCodigo()) && s.getEstado().equals("PENDIENTE")) {
                s.setEstado("RECHAZADA");
                BitacoraServicio.registrarAccion(usuarioActivo, "Solicitudes", 
                    "Rechazo automatico de la solicitud " + s.getCodigo() + " al aprobarse otra para el mismo animal " + animal.getCodigo());
            }
        }

        BitacoraServicio.registrarAccion(usuarioActivo, "Solicitudes", "Solicitud " + codigo + " aprobada con exito. Animal " + animal.getCodigo() + " adoptado.");
        return "SUCCESS";
    }

    // Modulo para rechazar una solicitud
    public static String rechazarSolicitud(String codigo, String usuarioActivo) {
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null && s.getCodigo().equals(codigo)) {
                if (!s.getEstado().equals("PENDIENTE")) {
                    return "La solicitud ya no esta pendiente (Estado: " + s.getEstado() + ").";
                }
                s.setEstado("RECHAZADA");
                BitacoraServicio.registrarAccion(usuarioActivo, "Solicitudes", "Solicitud " + codigo + " rechazada manualmente.");
                return "SUCCESS";
            }
        }
        return "La solicitud no existe.";
    }

    // Modulo para filtrar solicitudes por filtros de busqueda
    public static Solicitud[] filtrarSolicitudes(String codigoFiltro, String codigoAnimalFiltro, String codigoAdoptanteFiltro, String estadoFiltro) {
        int coincidentes = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !s.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (codigoAnimalFiltro != null && !codigoAnimalFiltro.trim().isEmpty() && !s.getCodigoAnimal().toLowerCase().contains(codigoAnimalFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (codigoAdoptanteFiltro != null && !codigoAdoptanteFiltro.trim().isEmpty() && !s.getCodigoAdoptante().toLowerCase().contains(codigoAdoptanteFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !s.getEstado().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) coincidentes++;
            }
        }

        Solicitud[] resultado = new Solicitud[coincidentes];
        int idx = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorSolicitudes; i++) {
            Solicitud s = BaseDatosMemoria.solicitudes[i];
            if (s != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !s.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (codigoAnimalFiltro != null && !codigoAnimalFiltro.trim().isEmpty() && !s.getCodigoAnimal().toLowerCase().contains(codigoAnimalFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (codigoAdoptanteFiltro != null && !codigoAdoptanteFiltro.trim().isEmpty() && !s.getCodigoAdoptante().toLowerCase().contains(codigoAdoptanteFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !s.getEstado().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) {
                    resultado[idx] = s;
                    idx++;
                }
            }
        }
        return resultado;
    }
}
