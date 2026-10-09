package cris.sic.proyecto2.util;

/**
 * Utilidades de validación para entradas de texto y reglas de negocio transversales.
 * Garantiza integridad en los datos antes de la serialización en disco (.txt con delimitador '|').
 * 
 * Reglas de validación estricta de placas:
 * - Longitud exacta de 7 caracteres.
 * - Formato: ^[PMpm]\d{3}[A-Za-z]{3}$
 * - Inicial 'M' obligatoria para Motocicletas.
 * - Inicial 'P' obligatoria para Automóviles y Pickups.
 * - 3 dígitos centrales numéricos ('0'-'9', ASCII 48-57).
 * - 3 letras finales alfabéticas ('A'-'Z' o 'a'-'z', ASCII 65-90 y 97-122).
 * 
 * @author cris_sic
 */
public class ValidadorTexto {

    private ValidadorTexto() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Verifica si una cadena de texto es no nula y no contiene solo espacios en blanco.
     *
     * @param texto Cadena a evaluar
     * @return true si el texto es válido; false si es nulo o vacío
     */
    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    /**
     * Verifica si el texto contiene el carácter reservado delimitador '|'
     * utilizado en la persistencia de datos.
     *
     * @param texto Cadena a inspeccionar
     * @return true si contiene '|', false en caso contrario
     */
    public static boolean contienePipe(String texto) {
        if (texto == null) {
            return false;
        }
        return texto.indexOf('|') >= 0;
    }

    /**
     * Valida de forma estricta el formato de una placa vehicular según el tipo de vehículo.
     * Retorna una descripción detallada del error si es inválida, o null si es correcta.
     *
     * @param placa        Placa a validar (ej: "P123ABC", "M456XYZ")
     * @param tipoVehiculo Tipo de vehículo ("Automóvil", "Motocicleta", "Pickup")
     * @return Mensaje descriptivo de error, o null si la placa es válida
     */
    public static String validarPlaca(String placa, String tipoVehiculo) {
        if (placa == null || placa.trim().isEmpty()) {
            return "Error de formato: La placa no puede estar vacía.";
        }
        if (contienePipe(placa)) {
            return "Error de formato: La placa no puede contener el carácter reservado '|'.";
        }

        String p = placa.trim().toUpperCase();
        if (p.length() != 7) {
            return "Error de formato: La placa debe tener una inicial P o M, seguida de 3 números y 3 letras (ej. P101ABC).";
        }

        char inicial = p.charAt(0);
        if (inicial != 'P' && inicial != 'M') {
            return "Error de formato: La placa debe tener una inicial P o M, seguida de 3 números y 3 letras (ej. P101ABC).";
        }

        if (tipoVehiculo != null && !tipoVehiculo.trim().isEmpty()) {
            String tipoNorm = tipoVehiculo.trim().toUpperCase();
            if (tipoNorm.contains("MOTO")) {
                if (inicial != 'M') {
                    return "Error de formato: Una motocicleta debe usar la inicial M en la placa (ej. M101ABC).";
                }
            } else if (tipoNorm.contains("AUTO") || tipoNorm.contains("PICK")) {
                if (inicial != 'P') {
                    return "Error de formato: Un automóvil o pickup debe usar la inicial P en la placa (ej. P101ABC).";
                }
            }
        }

        // Caracteres 2, 3 y 4: Dígitos numéricos ('0' - '9', ASCII 48-57)
        for (int i = 1; i <= 3; i++) {
            char c = p.charAt(i);
            if (c < '0' || c > '9') {
                return "Error de formato: La placa debe tener una inicial P o M, seguida de 3 números y 3 letras (ej. P101ABC).";
            }
        }

        // Caracteres 5, 6 y 7: Letras ('A'-'Z', ASCII 65-90)
        for (int i = 4; i <= 6; i++) {
            char c = p.charAt(i);
            if (c < 'A' || c > 'Z') {
                return "Error de formato: La placa debe tener una inicial P o M, seguida de 3 números y 3 letras (ej. P101ABC).";
            }
        }

        return null; // Placa válida
    }

    /**
     * Alias para validarPlaca.
     */
    public static String obtenerErrorPlaca(String placa, String tipoVehiculo) {
        return validarPlaca(placa, tipoVehiculo);
    }

    /**
     * Valida si una placa cumple con el formato estricto según su tipo de vehículo.
     *
     * @param placa        Placa a validar
     * @param tipoVehiculo Tipo de vehículo ("Automóvil", "Motocicleta", "Pickup")
     * @return true si cumple con todas las reglas
     */
    public static boolean esPlacaValida(String placa, String tipoVehiculo) {
        return validarPlaca(placa, tipoVehiculo) == null;
    }

    /**
     * Valida si una placa vehicular cumple con el formato general permisivo (3 a 10 caracteres, sin '|').
     *
     * @param placa Placa a validar
     * @return true si la placa es válida
     */
    public static boolean esPlacaValida(String placa) {
        if (!esTextoValido(placa) || contienePipe(placa)) {
            return false;
        }
        String placaLimpia = placa.trim();
        return placaLimpia.length() >= 3 && placaLimpia.length() <= 10;
    }

    /**
     * Valida que el tipo de vehículo corresponda a uno de los tres tipos admitidos:
     * Automóvil, Motocicleta o Pickup.
     *
     * @param tipo Tipo a evaluar
     * @return true si corresponde a uno de los tipos permitidos
     */
    public static boolean esTipoVehiculoValido(String tipo) {
        if (!esTextoValido(tipo)) {
            return false;
        }
        String tipoNorm = tipo.trim().toUpperCase();
        return tipoNorm.equals("AUTOMÓVIL") || tipoNorm.equals("AUTOMOVIL")
                || tipoNorm.equals("MOTOCICLETA") || tipoNorm.equals("MOTO")
                || tipoNorm.equals("PICKUP") || tipoNorm.equals("PICK-UP");
    }
}
