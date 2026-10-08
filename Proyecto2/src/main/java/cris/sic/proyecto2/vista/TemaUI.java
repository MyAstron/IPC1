package cris.sic.proyecto2.vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

/**
 * Paleta cromática, tipografías y componentes estilizados con identidad visual moderna MyAstron.
 * 
 * @author cris_sic
 */
public class TemaUI {

    // Paleta de colores Dark Dashboard
    public static final Color FONDO_APP = new Color(18, 22, 31);
    public static final Color FONDO_BARRA_LATERAL = new Color(24, 30, 42);
    public static final Color FONDO_PANEL = new Color(28, 35, 48);
    public static final Color FONDO_TARJETA = new Color(36, 45, 62);
    public static final Color FONDO_INPUT = new Color(44, 55, 76);
    public static final Color BORDE_SUAVE = new Color(55, 68, 92);

    public static final Color PRIMARIO = new Color(59, 130, 246);       // Azul vibrante
    public static final Color PRIMARIO_HOVER = new Color(37, 99, 235);
    public static final Color EXITO = new Color(34, 197, 94);           // Verde esmeralda
    public static final Color EXITO_HOVER = new Color(22, 163, 74);
    public static final Color ADVERTENCIA = new Color(245, 158, 11);    // Ámbar
    public static final Color PELIGRO = new Color(239, 68, 68);         // Rojo carmesí
    public static final Color PELIGRO_HOVER = new Color(220, 38, 38);
    public static final Color PURPURA = new Color(168, 85, 247);        // Púrpura Socios

    public static final Color TEXTO_PRINCIPAL = new Color(248, 250, 252);
    public static final Color TEXTO_SECUNDARIO = new Color(148, 163, 184);
    public static final Color TEXTO_MUTED = new Color(100, 116, 139);

    // Tipografías
    public static final Font FUENTE_TITULO_GRANDE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FUENTE_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_MONO = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * Crea un botón estilizado moderno con esquinas redondeadas y colores reactivos.
     */
    public static JButton crearBoton(String texto, Color colorFondo, Color colorTexto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getBackground();
                if (bg == null) {
                    bg = colorFondo;
                }
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        boton.setBackground(colorFondo);
        boton.setFont(FUENTE_BOLD);
        boton.setForeground(colorTexto);
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(boton.getPreferredSize().width + 20, 36));
        return boton;
    }

    /**
     * Crea un campo de texto estilizado para formularios.
     */
    public static JTextField crearCampoTexto(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE_REGULAR);
        campo.setBackground(FONDO_INPUT);
        campo.setForeground(TEXTO_PRINCIPAL);
        campo.setCaretColor(TEXTO_PRINCIPAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return campo;
    }

    /**
     * Crea una tarjeta con borde y fondo oscuro.
     */
    public static JPanel crearTarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE_SUAVE, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        return panel;
    }

    /**
     * Crea una etiqueta para encabezados de sección.
     */
    public static JLabel crearEtiquetaTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_TITULO);
        label.setForeground(TEXTO_PRINCIPAL);
        return label;
    }

    /**
     * Crea una etiqueta para campos de formulario.
     */
    public static JLabel crearEtiquetaCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(FUENTE_BOLD);
        label.setForeground(TEXTO_SECUNDARIO);
        return label;
    }
}
