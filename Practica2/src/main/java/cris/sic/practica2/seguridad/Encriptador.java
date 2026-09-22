package cris.sic.practica2.seguridad;

/**
 * Servicio de Seguridad para el cifrado y descifrado simétrico de datos en disco (.txt).
 * Implementa el algoritmo reversible XOR con una clave secreta fija ("QuetzalKey2026").
 * 
 * Propiedad matemática de XOR:
 * (TextoPlano ^ Clave) = TextoCifrado
 * (TextoCifrado ^ Clave) = TextoPlano
 * Por lo tanto, el mismo método `procesar` sirve tanto para cifrar como para descifrar.
 */
public class Encriptador {

    // Clave secreta interna fija para la operación XOR
    private static final String CLAVE_SECRETA = "QuetzalKey2026";

    /**
     * Procesa una cadena de texto aplicando Cifrado XOR simétrico e involutivo.
     * Sirve tanto para cifrar al guardar como para descifrar al cargar.
     *
     * @param texto Cadena de texto a procesar (plana o cifrada)
     * @return Cadena de texto procesada (cifrada o descifrada)
     */
    public static String procesar(String texto) {
        if (texto == null) {
            return null;
        }
        if (texto.isEmpty()) {
            return "";
        }

        StringBuilder resultado = new StringBuilder(texto.length());
        int longitudClave = CLAVE_SECRETA.length();

        for (int i = 0; i < texto.length(); i++) {
            char caracterOriginal = texto.charAt(i);
            char caracterClave = CLAVE_SECRETA.charAt(i % longitudClave);
            // Operación XOR a nivel de caracter
            char caracterProcesado = (char) (caracterOriginal ^ caracterClave);
            resultado.append(caracterProcesado);
        }

        return resultado.toString();
    }
}
