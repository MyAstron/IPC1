package cris.sic.refugio.vista;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

// Panel para mostrar la informacion institucional y del estudiante que desarrollo el proyecto con diseno ARAMS
public class EstudiantePanel extends JPanel {

    public EstudiantePanel() {
        setLayout(null);
        setBackground(ThemeARAMS.BACKGROUND);

        // Tarjeta Principal
        JPanel card = new JPanel();
        card.setBounds(30, 20, 715, 390);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(ThemeARAMS.OUTLINE, 1));
        card.setLayout(null);

        // Encabezado de la Tarjeta
        JPanel cardHeader = new JPanel();
        cardHeader.setBounds(0, 0, 715, 60);
        cardHeader.setBackground(ThemeARAMS.PRIMARY_DARK);
        cardHeader.setLayout(null);

        JLabel lblTitle = new JLabel("🎓 INFORMACIÓN ACADÉMICA DEL PROYECTO");
        lblTitle.setFont(ThemeARAMS.FONT_HEADLINE);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(20, 10, 600, 22);
        cardHeader.add(lblTitle);

        JLabel lblSub = new JLabel("Facultad de Ingeniería — Universidad de San Carlos de Guatemala");
        lblSub.setFont(ThemeARAMS.FONT_SMALL);
        lblSub.setForeground(ThemeARAMS.PRIMARY_CONTAINER);
        lblSub.setBounds(20, 32, 600, 18);
        cardHeader.add(lblSub);

        card.add(cardHeader);

        // Filas de Información
        int startY = 80;
        int rowHeight = 45;

        agregarFilaInfo(card, "🏛️ Institución:", "Universidad de San Carlos de Guatemala (USAC)", startY);
        agregarFilaInfo(card, "🏫 Unidad Académica:", "Facultad de Ingeniería — Escuela de Ciencias y Sistemas", startY + rowHeight);
        agregarFilaInfo(card, "📚 Curso:", "Introducción a la Programación y Computación 1 (IPC1)", startY + rowHeight * 2);
        agregarFilaInfo(card, "📅 Semestre:", "Segundo Semestre 2026", startY + rowHeight * 3);
        agregarFilaInfo(card, "🐾 Proyecto:", "ARAMS — Animal Rescue and Adoption Management System (Proyecto 1)", startY + rowHeight * 4);
        agregarFilaInfo(card, "👤 Desarrollador:", "Cristopher Sic", startY + rowHeight * 5);

        add(card);
    }

    private void agregarFilaInfo(JPanel container, String etiqueta, String valor, int y) {
        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(ThemeARAMS.FONT_BODY_BOLD);
        lblEtiqueta.setForeground(ThemeARAMS.PRIMARY_DARK);
        lblEtiqueta.setBounds(25, y, 170, 25);
        container.add(lblEtiqueta);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(ThemeARAMS.FONT_BODY);
        lblValor.setForeground(ThemeARAMS.TEXT_MAIN);
        lblValor.setBounds(200, y, 490, 25);
        container.add(lblValor);
    }
}
