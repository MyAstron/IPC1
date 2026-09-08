package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Rescate;
import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para gestionar los rescates urgentes del refugio
public class RescateServicio {

    // Modulo para validar el formato del codigo de rescate (R-xxx)
    public static boolean validarCodigo(String codigo) {
        if (codigo == null || codigo.length() != 5) {
            return false;
        }
        if (!codigo.startsWith("R-")) {
            return false;
        }
        for (int i = 2; i < 5; i++) {
            if (!Character.isDigit(codigo.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Modulo para verificar si un codigo de rescate ya existe
    public static boolean existeCodigo(String codigo) {
        for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
            Rescate r = BaseDatosMemoria.rescates[i];
            if (r != null && r.getCodigo().equals(codigo)) {
                return true;
            }
        }
        return false;
    }

    // Modulo para generar el siguiente codigo correlativo de rescate disponible (R-xxx)
    public static String generarSiguienteCodigoRescate() {
        for (int i = 1; i <= 999; i++) {
            String codigo = String.format("R-%03d", i);
            if (!existeCodigo(codigo)) {
                return codigo;
            }
        }
        return null;
    }

    // Modulo para registrar un rescate unificado (inserta el rescate y el animal en una sola transaccion)
    public static String registrarRescateUnificado(String codigoRescate, String direccion, String prioridad, String fecha, 
                                                   String codigoAnimal, String nombreAnimal, String especieAnimal, String usuarioActivo) {
        // 1. Generar o validar codigo de rescate
        if (codigoRescate == null || codigoRescate.trim().isEmpty()) {
            codigoRescate = generarSiguienteCodigoRescate();
            if (codigoRescate == null) {
                BitacoraServicio.registrarError(usuarioActivo, "Rescates", "No hay codigos disponibles para nuevo rescate.");
                return "No hay codigos disponibles para registrar nuevos rescates.";
            }
        }

        if (!validarCodigo(codigoRescate)) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Codigo de rescate invalido: " + codigoRescate);
            return "Codigo de rescate invalido. Debe usar el formato R-xxx.";
        }

        if (existeCodigo(codigoRescate)) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Codigo de rescate duplicado: " + codigoRescate);
            return "El codigo de rescate ya existe.";
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            return "La direccion/descripcion es obligatoria.";
        }

        if (prioridad == null || (!prioridad.equals("ALTA") && !prioridad.equals("MEDIA") && !prioridad.equals("BAJA"))) {
            return "Prioridad invalida. Debe ser ALTA, MEDIA o BAJA.";
        }

        if (fecha == null || fecha.trim().isEmpty()) {
            return "La fecha de registro es obligatoria.";
        }

        if (BaseDatosMemoria.contadorRescates >= BaseDatosMemoria.MAX_RESCATES) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Capacidad maxima de rescates alcanzada.");
            return "No hay suficiente espacio para registrar mas rescates.";
        }

        // 2. Procesar Animal Vinculado
        String codigoAnimalFinal = codigoAnimal;
        if (codigoAnimalFinal != null && !codigoAnimalFinal.trim().isEmpty()) {
            Animal existente = null;
            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null && a.getCodigo().equals(codigoAnimalFinal)) {
                    existente = a;
                    break;
                }
            }

            if (existente != null) {
                if (existente.getEstadoAdopcion().equals("ELIMINADO")) {
                    return "El animal especificado ha sido eliminado.";
                }
                existente.setEstadoClinico("EN_TRATAMIENTO");
                if (nombreAnimal != null && !nombreAnimal.trim().isEmpty()) {
                    existente.setNombre(nombreAnimal.trim());
                }
                if (especieAnimal != null && !especieAnimal.trim().isEmpty()) {
                    existente.setEspecie(especieAnimal.trim());
                }
            } else {
                if (!AnimalServicio.validarCodigo(codigoAnimalFinal)) {
                    return "Codigo de animal invalido. Debe tener formato A-xxx.";
                }
                if (BaseDatosMemoria.contadorAnimales >= BaseDatosMemoria.MAX_ANIMALES) {
                    return "No hay espacio suficiente para registrar el nuevo animal.";
                }
                String nom = (nombreAnimal == null || nombreAnimal.trim().isEmpty()) ? "Desconocido" : nombreAnimal.trim();
                String esp = (especieAnimal == null || especieAnimal.trim().isEmpty()) ? "Desconocido" : especieAnimal.trim();
                Animal nuevoAnimal = new Animal(codigoAnimalFinal, nom, esp, 0, "EN_TRATAMIENTO", "DISPONIBLE");
                BaseDatosMemoria.animales[BaseDatosMemoria.contadorAnimales] = nuevoAnimal;
                BaseDatosMemoria.contadorAnimales++;
                BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "REGISTRO", nuevoAnimal);
            }
        } else {
            if (BaseDatosMemoria.contadorAnimales >= BaseDatosMemoria.MAX_ANIMALES) {
                return "No hay espacio en la base de datos para registrar el animal auto-generado.";
            }
            codigoAnimalFinal = AnimalServicio.generarSiguienteCodigoAnimal();
            if (codigoAnimalFinal == null) {
                return "No fue posible generar un codigo automatico libre para el animal.";
            }
            String nom = (nombreAnimal == null || nombreAnimal.trim().isEmpty()) ? "Desconocido" : nombreAnimal.trim();
            String esp = (especieAnimal == null || especieAnimal.trim().isEmpty()) ? "Desconocido" : especieAnimal.trim();
            Animal nuevoAnimal = new Animal(codigoAnimalFinal, nom, esp, 0, "EN_TRATAMIENTO", "DISPONIBLE");
            BaseDatosMemoria.animales[BaseDatosMemoria.contadorAnimales] = nuevoAnimal;
            BaseDatosMemoria.contadorAnimales++;
            BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "REGISTRO", nuevoAnimal);
        }

        // 3. Registrar el Rescate
        Rescate nuevoRescate = new Rescate(codigoRescate, direccion.trim(), prioridad, "ATENDIDO", fecha.trim(), codigoAnimalFinal);
        BaseDatosMemoria.rescates[BaseDatosMemoria.contadorRescates] = nuevoRescate;
        BaseDatosMemoria.contadorRescates++;

        BitacoraServicio.registrarBitacoraRescate(usuarioActivo, "REGISTRO", nuevoRescate);
        BitacoraServicio.registrarAccion(usuarioActivo, "Rescates", 
            "Registro unificado exitoso: Rescate " + codigoRescate + " vinculado al animal " + codigoAnimalFinal);
        return "SUCCESS";
    }

    // Modulo para registrar un nuevo rescate urgente
    public static String registrarRescate(String codigo, String direccion, String prioridad, String fecha, String usuarioActivo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            codigo = generarSiguienteCodigoRescate();
            if (codigo == null) {
                return "No hay codigos disponibles para registrar nuevos rescates.";
            }
        }
        if (!validarCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Codigo de rescate invalido: " + codigo);
            return "Codigo invalido. Debe usar el formato R-xxx.";
        }

        if (existeCodigo(codigo)) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Codigo de rescate duplicado: " + codigo);
            return "El codigo de rescate ya existe.";
        }

        if (direccion == null || direccion.trim().isEmpty()) {
            return "La direccion/descripcion es obligatoria.";
        }

        if (!prioridad.equals("ALTA") && !prioridad.equals("MEDIA") && !prioridad.equals("BAJA")) {
            return "Prioridad invalida. Debe ser ALTA, MEDIA o BAJA.";
        }

        if (fecha == null || fecha.trim().isEmpty()) {
            return "La fecha de registro es obligatoria.";
        }

        if (BaseDatosMemoria.contadorRescates >= BaseDatosMemoria.MAX_RESCATES) {
            BitacoraServicio.registrarError(usuarioActivo, "Rescates", "Capacidad maxima de rescates alcanzada.");
            return "No hay suficiente espacio para registrar mas rescates.";
        }

        Rescate nuevo = new Rescate(codigo, direccion, prioridad, "PENDIENTE", fecha, "");
        BaseDatosMemoria.rescates[BaseDatosMemoria.contadorRescates] = nuevo;
        BaseDatosMemoria.contadorRescates++;

        BitacoraServicio.registrarBitacoraRescate(usuarioActivo, "REGISTRO", nuevo);
        BitacoraServicio.registrarAccion(usuarioActivo, "Rescates", "Rescate " + codigo + " registrado con exito.");
        return "SUCCESS";
    }

    // Modulo para atender un rescate urgente (genera un animal nuevo o vincula uno existente)
    public static String atenderRescate(String codigoRescate, String codigoAnimalExistente, String nombreAnimalAuto, String especieAnimalAuto, String usuarioActivo) {
        Rescate rescate = null;
        for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
            Rescate r = BaseDatosMemoria.rescates[i];
            if (r != null && r.getCodigo().equals(codigoRescate)) {
                rescate = r;
                break;
            }
        }

        if (rescate == null) {
            return "El rescate especificado no existe.";
        }

        if (!rescate.getEstado().equals("PENDIENTE")) {
            return "El rescate ya se encuentra atendido.";
        }

        String codigoAnimalVinculado = "";

        // Caso 1: Vincular uno existente
        if (codigoAnimalExistente != null && !codigoAnimalExistente.trim().isEmpty()) {
            Animal animal = null;
            for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
                Animal a = BaseDatosMemoria.animales[i];
                if (a != null && a.getCodigo().equals(codigoAnimalExistente)) {
                    animal = a;
                    break;
                }
            }

            if (animal == null || animal.getEstadoAdopcion().equals("ELIMINADO")) {
                return "El animal especificado para vincular no existe en el sistema.";
            }

            animal.setEstadoClinico("EN_TRATAMIENTO");
            codigoAnimalVinculado = animal.getCodigo();
            BitacoraServicio.registrarAccion(usuarioActivo, "Rescates", 
                "Rescate " + codigoRescate + " atendido vinculando al animal existente " + codigoAnimalVinculado);

        } else {
            // Caso 2: Generar uno nuevo automaticamente
            if (BaseDatosMemoria.contadorAnimales >= BaseDatosMemoria.MAX_ANIMALES) {
                return "No hay espacio en la base de datos para registrar el animal auto-generado.";
            }

            String nuevoCodigo = generarSiguienteCodigoAnimal();
            if (nuevoCodigo == null) {
                return "No fue posible generar un codigo automatico libre para el animal.";
            }

            String nombre = (nombreAnimalAuto == null || nombreAnimalAuto.trim().isEmpty()) ? "Rescatado " + rescate.getCodigo() : nombreAnimalAuto;
            String especie = (especieAnimalAuto == null || especieAnimalAuto.trim().isEmpty()) ? "Desconocida" : especieAnimalAuto;

            Animal nuevoAnimal = new Animal(nuevoCodigo, nombre, especie, 0, "EN_TRATAMIENTO", "DISPONIBLE");
            BaseDatosMemoria.animales[BaseDatosMemoria.contadorAnimales] = nuevoAnimal;
            BaseDatosMemoria.contadorAnimales++;
            BitacoraServicio.registrarBitacoraAnimal(usuarioActivo, "REGISTRO", nuevoAnimal);

            codigoAnimalVinculado = nuevoCodigo;
            BitacoraServicio.registrarAccion(usuarioActivo, "Rescates", 
                "Rescate " + codigoRescate + " atendido autogenerando al animal " + codigoAnimalVinculado);
        }

        // Cambiar estado del rescate a ATENDIDO y guardar el codigo del animal vinculado
        rescate.setEstado("ATENDIDO");
        rescate.setCodigoAnimalVinculado(codigoAnimalVinculado);
        BitacoraServicio.registrarBitacoraRescate(usuarioActivo, "ATENCION", rescate);

        return "SUCCESS";
    }

    // Modulo para eliminar el registro de rescate asociado a un animal dado de baja
    public static void eliminarRescatePorAnimal(String codigoAnimal, String usuarioActivo) {
        if (codigoAnimal == null || codigoAnimal.trim().isEmpty()) return;

        for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
            Rescate r = BaseDatosMemoria.rescates[i];
            if (r != null && r.getCodigoAnimalVinculado().equals(codigoAnimal)) {
                String codRescate = r.getCodigo();
                BitacoraServicio.registrarBitacoraRescate(usuarioActivo, "ELIMINACION", r);
                // Desplazar elementos en el arreglo estatico para eliminar el rescate
                for (int j = i; j < BaseDatosMemoria.contadorRescates - 1; j++) {
                    BaseDatosMemoria.rescates[j] = BaseDatosMemoria.rescates[j + 1];
                }
                BaseDatosMemoria.rescates[BaseDatosMemoria.contadorRescates - 1] = null;
                BaseDatosMemoria.contadorRescates--;
                i--;

                // Registrar en bitacora de errores y de acciones como evento validado
                BitacoraServicio.registrarError(usuarioActivo, "Rescates", 
                    "Rescate " + codRescate + " eliminado automaticamente por baja del animal vinculado " + codigoAnimal);
                BitacoraServicio.registrarAccion(usuarioActivo, "Rescates", 
                    "Eliminacion de datos de rescate " + codRescate + " vinculado al animal eliminado " + codigoAnimal);
            }
        }
    }

    // Modulo para encontrar el siguiente codigo libre de tipo A-xxx
    private static String generarSiguienteCodigoAnimal() {
        for (int i = 1; i <= 999; i++) {
            String codigo = String.format("A-%03d", i);
            if (!AnimalServicio.existeCodigo(codigo)) {
                return codigo;
            }
        }
        return null;
    }

    // Modulo para filtrar rescates por filtros de busqueda
    public static Rescate[] filtrarRescates(String codigoFiltro, String prioridadFiltro, String estadoFiltro) {
        int coincidentes = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
            Rescate r = BaseDatosMemoria.rescates[i];
            if (r != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !r.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (prioridadFiltro != null && !prioridadFiltro.equals("TODOS") && !r.getPrioridad().equals(prioridadFiltro)) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !r.getEstado().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) coincidentes++;
            }
        }

        Rescate[] resultado = new Rescate[coincidentes];
        int idx = 0;
        for (int i = 0; i < BaseDatosMemoria.contadorRescates; i++) {
            Rescate r = BaseDatosMemoria.rescates[i];
            if (r != null) {
                boolean cumple = true;
                if (codigoFiltro != null && !codigoFiltro.trim().isEmpty() && !r.getCodigo().toLowerCase().contains(codigoFiltro.toLowerCase())) {
                    cumple = false;
                }
                if (prioridadFiltro != null && !prioridadFiltro.equals("TODOS") && !r.getPrioridad().equals(prioridadFiltro)) {
                    cumple = false;
                }
                if (estadoFiltro != null && !estadoFiltro.equals("TODOS") && !r.getEstado().equals(estadoFiltro)) {
                    cumple = false;
                }
                if (cumple) {
                    resultado[idx] = r;
                    idx++;
                }
            }
        }
        return resultado;
    }

    // Modulo para recargar los rescates directamente desde el archivo de bitacora
    public static void cargarDesdeBitacora() {
        PersistenciaServicio.cargarRescatesDesdeBitacora();
    }
}
