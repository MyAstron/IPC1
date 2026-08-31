package cris.sic.refugio.vista;

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;

// Panel para mostrar la informacion institucional y del estudiante que desarrollo el proyecto
public class EstudiantePanel extends JPanel {

    public EstudiantePanel() {
        setLayout(null);

        JLabel lblTitulo = new JLabel("INFORMACIÓN DEL ESTUDIANTE Y PROYECTO");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblTitulo.setBounds(30, 20, 500, 25);
        add(lblTitulo);

        JLabel lblUni = new JLabel("Universidad de San Carlos de Guatemala (USAC)");
        lblUni.setBounds(50, 70, 450, 25);
        add(lblUni);

        JLabel lblFac = new JLabel("Facultad de Ingeniería - Escuela de Ciencias y Sistemas");
        lblFac.setBounds(50, 105, 450, 25);
        add(lblFac);

        JLabel lblCurso = new JLabel("Curso: Introducción a la Programación y Computación 1 (IPC1)");
        lblCurso.setBounds(50, 140, 450, 25);
        add(lblCurso);

        JLabel lblSemestre = new JLabel("Semestre: Segundo Semestre 2026");
        lblSemestre.setBounds(50, 175, 450, 25);
        add(lblSemestre);

        JLabel lblProyecto = new JLabel("Proyecto: Sistema de Gestión de Refugio de Animales (Proyecto 1)");
        lblProyecto.setBounds(50, 210, 450, 25);
        add(lblProyecto);

        JLabel lblDesarrollador = new JLabel("Desarrollador: Cristopher Sic");
        lblDesarrollador.setBounds(50, 245, 450, 25);
        add(lblDesarrollador);
    }
}
