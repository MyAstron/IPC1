package cris.sic.proyecto2.persistencia;

/**
 * Utilitario de cifrado simétrico reversible mediante el algoritmo XOR.
 * 
 * Reglas y funcionamiento:
 * - Aplica una máscara XOR carácter por carácter utilizando una clave secreta.
 * - Al ser una operación involutiva (simétrica), aplicar procesar(procesar(texto))
 *   devuelve exactamente el texto original.
 * - Protege los archivos en disco frente a lecturas casuales sin romper la portabilidad java.io.*.
 * 
 * @author cris_sic
 */
public class Encriptador {

    private static final String CLAVE_POR_DEFECTO = "ResiPark@IPC1_2026#SecKey";

    /**
     * Procesa una cadena de texto aplicando cifrado/descifrado simétrico XOR con la clave por defecto.
     *
     * @param texto Texto a procesar
     * @return Texto cifrado o descifrado
     */
    public static String procesar(String texto) {
        return procesar(texto, CLAVE_POR_DEFECTO);
    }

    /**
     * Procesa una cadena de texto aplicando cifrado/descifrado simétrico XOR con una clave personalizada.
     *
     * @param texto Texto de entrada
     * @param clave Clave secreta
     * @return Texto resultante
     */
    public static String procesar(String texto, String clave) {
        if (texto == null) {
            return null;
        }
        if (texto.isEmpty()) {
            return "";
        }
        if (clave == null || clave.isEmpty()) {
            clave = CLAVE_POR_DEFECTO;
        }

        char[] entrada = texto.toCharArray();
        char[] claveChars = clave.toCharArray();
        char[] resultado = new char[entrada.length];

        for (int i = 0; i < entrada.length; i++) {
            resultado[i] = (char) (entrada[i] ^ claveChars[i % claveChars.length]);
        }

        return new String(resultado);
    }

    /**
     * Verifica si una cadena cifrada y descifrada recupera fielmente el texto de origen.
     *
     * @param original Texto a verificar
     * @return true si la simetría es perfecta
     */
    public static boolean verificarSimetria(String original) {
        if (original == null) {
            return true;
        }
        String cifrado = procesar(original);
        String descifrado = procesar(cifrado);
        return original.equals(descifrado);
    }
}
