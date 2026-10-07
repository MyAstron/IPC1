package cris.sic.proyecto2.util;

/**
 * Utilidades de validación para entradas de texto y reglas de negocio transversales.
 * Garantiza integridad en los datos antes de la serialización en disco (.txt con delimitador '|').
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
     * Valida si una placa vehicular cumple con el formato general:
     * - No nula ni vacía
     * - No contiene el carácter '|'
     * - Longitud mínima de 3 caracteres y máxima de 10 caracteres
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
