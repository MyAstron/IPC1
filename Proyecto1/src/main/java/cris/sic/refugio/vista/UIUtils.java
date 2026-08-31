package cris.sic.refugio.vista;

import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

// Utilidades para la interfaz grafica Swing (restricciones de entrada, manejo de fechas y codigos)
public class UIUtils {

    // Modulo para restringir un JTextField a solo digitos numericos y una longitud maxima
    public static void aplicarRestriccionNumerica(JTextField textField, final int maxLongitud) {
        if (textField == null) return;
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (esNumerico(string) && (fb.getDocument().getLength() + string.length() <= maxLongitud)) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (esNumerico(text) && (fb.getDocument().getLength() - length + text.length() <= maxLongitud)) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }

            private boolean esNumerico(String text) {
                for (int i = 0; i < text.length(); i++) {
                    if (!Character.isDigit(text.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        });
    }

    // Modulo para obtener la fecha actual del sistema en formato dd/MM/yyyy
    public static String obtenerFechaHoy() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(new Date());
    }

    // Modulo para formatear un codigo con su prefijo estatico y numero a 3 digitos (ej. A-001)
    public static String formatearCodigo(String prefijo, String numeroStr) {
        if (numeroStr == null || numeroStr.trim().isEmpty()) {
            return "";
        }
        try {
            int num = Integer.parseInt(numeroStr.trim());
            return prefijo + String.format("%03d", num);
        } catch (Exception e) {
            return prefijo + numeroStr.trim();
        }
    }

    // Modulo para extraer la parte numerica de un codigo completo quitando su prefijo
    public static String extraerNumeroCodigo(String codigoCompleto, String prefijo) {
        if (codigoCompleto == null) return "";
        String s = codigoCompleto.trim();
        if (s.startsWith(prefijo)) {
            return s.substring(prefijo.length());
        }
        return s;
    }
}
