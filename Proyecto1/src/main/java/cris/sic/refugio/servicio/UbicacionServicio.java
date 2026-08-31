package cris.sic.refugio.servicio;

import cris.sic.refugio.modelo.Animal;
import cris.sic.refugio.persistencia.BaseDatosMemoria;

// Servicio para gestionar la matriz de ubicaciones del refugio (5x5)
public class UbicacionServicio {

    // Modulo para obtener el estado de la matriz de ubicaciones
    public static String[][] getUbicaciones() {
        return BaseDatosMemoria.ubicacionesRefugio;
    }

    // Modulo para liberar la celda donde se encuentra un animal especifico
    public static void liberarAnimal(String codigoAnimal, String usuarioActivo) {
        for (int f = 0; f < BaseDatosMemoria.FILAS_REFUGIO; f++) {
            for (int c = 0; c < BaseDatosMemoria.COLUMNAS_REFUGIO; c++) {
                if (BaseDatosMemoria.ubicacionesRefugio[f][c] != null && BaseDatosMemoria.ubicacionesRefugio[f][c].equals(codigoAnimal)) {
                    BaseDatosMemoria.ubicacionesRefugio[f][c] = null;
                    BitacoraServicio.registrarAccion(usuarioActivo, "Ubicaciones", 
                        "Celda [" + f + "][" + c + "] liberada automaticamente al cambiar de estado el animal: " + codigoAnimal);
                }
            }
        }
    }

    // Modulo para asignar un animal a una celda especifica
    public static String asignarUbicacion(int fila, int columna, String codigoAnimal, String usuarioActivo) {
        if (fila < 0 || fila >= BaseDatosMemoria.FILAS_REFUGIO || columna < 0 || columna >= BaseDatosMemoria.COLUMNAS_REFUGIO) {
            BitacoraServicio.registrarError(usuarioActivo, "Ubicaciones", "Fila o columna fuera de rango al intentar asignar animal.");
            return "Coordenadas fuera de rango.";
        }

        if (BaseDatosMemoria.ubicacionesRefugio[fila][columna] != null) {
            BitacoraServicio.registrarError(usuarioActivo, "Ubicaciones", "Intento de asignar celda ocupada: [" + fila + "][" + columna + "]");
            return "La celda seleccionada ya está ocupada.";
        }

        // Validar que el animal exista y este en estado valido
        Animal animal = null;
        for (int i = 0; i < BaseDatosMemoria.contadorAnimales; i++) {
            Animal a = BaseDatosMemoria.animales[i];
            if (a != null && a.getCodigo().equals(codigoAnimal)) {
                animal = a;
                break;
            }
        }

        if (animal == null || animal.getEstadoAdopcion().equals("ELIMINADO")) {
            BitacoraServicio.registrarError(usuarioActivo, "Ubicaciones", "Intento de asignar animal inexistente o eliminado: " + codigoAnimal);
            return "El código de animal no existe o ha sido eliminado.";
        }

        if (animal.getEstadoAdopcion().equals("ADOPTADO")) {
            BitacoraServicio.registrarError(usuarioActivo, "Ubicaciones", "Intento de asignar animal adoptado a una celda: " + codigoAnimal);
            return "El animal ya se encuentra adoptado y no puede ingresar a una celda.";
        }

        // Liberar al animal de cualquier otra celda donde este asignado previamente
        liberarAnimal(codigoAnimal, usuarioActivo);

        // Asignar en la nueva celda
        BaseDatosMemoria.ubicacionesRefugio[fila][columna] = codigoAnimal;
        BitacoraServicio.registrarAccion(usuarioActivo, "Ubicaciones", 
            "Asignación exitosa del animal " + codigoAnimal + " a la celda [" + fila + "][" + columna + "]");
        return "SUCCESS";
    }

    // Modulo para liberar una celda especifica por coordenadas
    public static String liberarUbicacion(int fila, int columna, String usuarioActivo) {
        if (fila < 0 || fila >= BaseDatosMemoria.FILAS_REFUGIO || columna < 0 || columna >= BaseDatosMemoria.COLUMNAS_REFUGIO) {
            BitacoraServicio.registrarError(usuarioActivo, "Ubicaciones", "Intento de liberar celda fuera de rango.");
            return "Coordenadas fuera de rango.";
        }

        String animal = BaseDatosMemoria.ubicacionesRefugio[fila][columna];
        if (animal == null) {
            return "La celda ya está libre.";
        }

        BaseDatosMemoria.ubicacionesRefugio[fila][columna] = null;
        BitacoraServicio.registrarAccion(usuarioActivo, "Ubicaciones", 
            "Celda [" + fila + "][" + columna + "] liberada manualmente. Se retiro al animal: " + animal);
        return "SUCCESS";
    }
}
