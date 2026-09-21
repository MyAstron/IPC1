package cris.sic.practica2.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Paleta de colores, fuentes y utilidades de diseño visual para el Simulador Espacial.
 */
public class TemaEspacial {

    // Paleta de colores temática espacial
    public static final Color FONDO_ESPACIAL = new Color(0x0B, 0x0E, 0x17);       // Negro azulado profundo
    public static final Color FONDO_PANEL = new Color(0x16, 0x1B, 0x26);          // Superficie de tarjetas/paneles
    public static final Color BORDE_CIAN = new Color(0x00, 0xE5, 0xFF);           // Acento Cian Neón
    public static final Color AZUL_PRIMARIO = new Color(0x1E, 0x88, 0xE5);        // Azul brillante
    public static final Color AZUL_OSCURO = new Color(0x0D, 0x47, 0xA1);          // Azul marino oscuro
    public static final Color AMARILLO_ORO = new Color(0xFF, 0xD7, 0x00);         // Oro/Estrellas
    public static final Color ROJO_PELIGRO = new Color(0xE5, 0x39, 0x35);         // Alerta / Salir
    public static final Color TEXTO_BLANCO = Color.WHITE;
    public static final Color TEXTO_SECUNDARIO = new Color(0xB0, 0xBE, 0xC5);     // Gris azulado claro

    // Tipografías
    public static final Font FUENTE_TITULO_GRANDE = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FUENTE_SUBTITULO = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FUENTE_BOTON = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_TEXTO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_TEXTO_BOLD = new Font("SansSerif", Font.BOLD, 13);

    /**
     * Aplica el estilo estelar estándar a un botón de acción.
     *
     * @param btn Botón a estilizar
     * @param fondo Color de fondo
     * @param texto Color del texto
     */
    public static void aplicarEstiloBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(FUENTE_BOTON);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_CIAN, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
    }

    /**
     * Aplica el estilo a botones de navegación de retorno.
     *
     * @param btn Botón de volver
     */
    public static void aplicarEstiloBotonVolver(JButton btn) {
        aplicarEstiloBoton(btn, AZUL_OSCURO, TEXTO_BLANCO);
        btn.setPreferredSize(new Dimension(220, 42));
    }
}
