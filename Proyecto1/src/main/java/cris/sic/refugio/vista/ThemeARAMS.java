package cris.sic.refugio.vista;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

// Clase de soporte para la identidad visual y diseno del sistema ARAMS (Kindred Shelter Systems)
public class ThemeARAMS {

    // Paleta de Colores de la Marca Kindred Shelter Systems (ARAMS)
    public static final Color PRIMARY = new Color(0x2D, 0x6A, 0x4F);            // #2d6a4f (Verde Bosque)
    public static final Color PRIMARY_DARK = new Color(0x0F, 0x52, 0x38);       // #0f5238
    public static final Color PRIMARY_CONTAINER = new Color(0xB1, 0xF0, 0xCE);  // #b1f0ce (Verde Menta Suave)
    public static final Color SECONDARY = new Color(0x40, 0x91, 0x6C);          // #40916c
    public static final Color SECONDARY_CONTAINER = new Color(0xBE, 0xEA, 0xD1);// #beead1
    public static final Color BACKGROUND = new Color(0xF8, 0xF9, 0xFA);         // #f8f9fa (Fondo limpio)
    public static final Color SURFACE_CONTAINER = Color.WHITE;                  // #ffffff (Tarjetas/Contenedores)
    public static final Color TEXT_MAIN = new Color(0x19, 0x1C, 0x1D);          // #191c1d (Texto Principal)
    public static final Color TEXT_MUTED = new Color(0x40, 0x49, 0x43);         // #404943
    public static final Color OUTLINE = new Color(0xBF, 0xC9, 0xC1);            // #bfc9c1 (Bordes sutiles)
    public static final Color ERROR = new Color(0xBA, 0x1A, 0x1A);              // #ba1a1a (Rojo de Alerta/Peligro)
    public static final Color ERROR_CONTAINER = new Color(0xFF, 0xDA, 0xD6);    // #ffdad6 (Fondo Ocupado/Urgente)
    public static final Color ZEBRA_EVEN = Color.WHITE;
    public static final Color ZEBRA_ODD = new Color(0xF2, 0xF8, 0xF4);          // Verde pastel muy suave

    // Tipografia Estandar
    public static final Font FONT_DISPLAY = new Font("SansSerif", Font.BOLD, 18);
    public static final Font FONT_HEADLINE = new Font("SansSerif", Font.BOLD, 13);
    public static final Font FONT_BODY = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_BODY_BOLD = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONT_CODE = new Font("Monospaced", Font.BOLD, 12);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 11);

    // Modulo para aplicar estilo al boton de accion principal (Registrar, Filtrar, Guardar)
    public static void aplicarEstiloBotonPrincipal(JButton btn) {
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Modulo para aplicar estilo al boton de accion secundaria (Limpiar, Cancelar)
    public static void aplicarEstiloBotonSecundario(JButton btn) {
        btn.setBackground(new Color(0xF0, 0xF2, 0xF0));
        btn.setForeground(TEXT_MAIN);
        btn.setFont(FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OUTLINE, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Modulo para aplicar estilo a botones de advertencia o peligro (Eliminar, Rechazar)
    public static void aplicarEstiloBotonPeligro(JButton btn) {
        btn.setBackground(ERROR);
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0x8B, 0x00, 0x00), 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Modulo para aplicar estilo a botones de aprobacion
    public static void aplicarEstiloBotonAprobacion(JButton btn) {
        btn.setBackground(new Color(0x1B, 0x7A, 0x4D));
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_BODY_BOLD);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
    }

    // Modulo para crear un JLabel con formato visual de prefijo estatico tipo badge/pill
    public static JLabel crearBadgePrefijo(String prefijo) {
        JLabel lbl = new JLabel(prefijo, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PRIMARY_CONTAINER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(OUTLINE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setFont(FONT_CODE);
        lbl.setForeground(PRIMARY_DARK);
        lbl.setOpaque(false);
        return lbl;
    }

    // Modulo para formatear un JTextField con borde suave y fuente limpia
    public static void aplicarEstiloCampo(JTextField tf) {
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(OUTLINE, 1),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
    }

    // Modulo para estilizar tablas JTable con cabecera ARAMS y zebra striping
    public static void aplicarEstiloTabla(final JTable tabla) {
        tabla.setFont(FONT_BODY);
        tabla.setRowHeight(26);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(0xE5, 0xE7, 0xEB));
        tabla.setSelectionBackground(PRIMARY_CONTAINER);
        tabla.setSelectionForeground(new Color(0x00, 0x21, 0x14));

        // Estilizar Cabecera
        JTableHeader header = tabla.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(PRIMARY);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 30));
        header.setReorderingAllowed(false);

        // Renderizador con Zebra Striping
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(ZEBRA_EVEN);
                    } else {
                        c.setBackground(ZEBRA_ODD);
                    }
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setBackground(PRIMARY_CONTAINER);
                    c.setForeground(new Color(0x00, 0x21, 0x14));
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return c;
            }
        });
    }
}
