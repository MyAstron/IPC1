package cris.sic.actividad5.vista;

import cris.sic.actividad5.controlador.GestorAcademico;
import cris.sic.actividad5.modelo.Curso;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

/**
 * Ventana principal de la aplicacion desarrollada exclusivamente con Java Swing nativo (sin GUI Builder).
 * Estructura la interfaz de usuario en modulos visuales independientes para cursos, tareas y reporte.
 */
public class VentanaPrincipal extends JFrame {

    // Instancia del controlador de logica academica
    private GestorAcademico gestor;

    // ==========================================================
    // COMPONENTES DEL MODULO DE CURSOS (PASO 2.2)
    // ==========================================================
    private JPanel panelCursos;
    private JTextField txtCodigoCurso;
    private JTextField txtNombreCurso;
    private JTextField txtTutorCurso;
    private JButton btnRegistrarCurso;

    // ==========================================================
    // COMPONENTES DEL MODULO DE TAREAS (PASO 2.3)
    // ==========================================================
    private JPanel panelTareas;
    private JComboBox<Curso> cbCursosAsociados;
    private JTextField txtTituloTarea;
    private JTextField txtDescripcionTarea;
    private JTextField txtFechaEntrega;
    private JButton btnAgregarTarea;

    // ==========================================================
    // COMPONENTES DEL AREA DE VISUALIZACION (PASO 2.4)
    // ==========================================================
    private JPanel panelVisualizacion;
    private JTextArea txtAreaConsola;
    private JScrollPane scrollConsola;
    private JButton btnMostrarInformacion;
    private JButton btnLimpiarConsola;

    // ==========================================================
    // ESPACIO RESERVADO PARA MODULO DE TEMPERATURA (FASE 3)
    // ==========================================================
    private JPanel panelTemperatura;

    // Tipografias institucionales reutilizables
    private final Font FONT_TITULO = new Font("SansSerif", Font.BOLD, 15);
    private final Font FONT_ETIQUETA = new Font("SansSerif", Font.PLAIN, 12);
    private final Font FONT_BOTON = new Font("SansSerif", Font.BOLD, 12);
    private final Font FONT_MONO = new Font("Monospaced", Font.PLAIN, 12);

    /**
     * Constructor que inicializa la ventana y sus componentes visuales.
     * 
     * @param gestor Referencia al gestor academico en memoria
     */
    public VentanaPrincipal(GestorAcademico gestor) {
        this.gestor = (gestor != null) ? gestor : new GestorAcademico();

        // Paso 2.1: Configuracion basica del JFrame
        configurarVentana();

        // Paso 2.2: Construccion del Panel de Cursos
        construirPanelCursos();

        // Paso 2.3: Construccion del Panel de Tareas
        construirPanelTareas();

        // Paso 2.4: Construccion del Area de Visualizacion y Reportes
        construirAreaVisualizacion();

        // Preparacion del contenedor para Fase 3 (Conversion de Temperatura)
        prepararContenedorTemperatura();
    }

    /**
     * Constructor por defecto que crea un nuevo gestor academico.
     */
    public VentanaPrincipal() {
        this(new GestorAcademico());
    }

    /**
     * Paso 2.1: Configura las propiedades generales de la ventana principal.
     */
    private void configurarVentana() {
        setTitle("Sistema de Gestión Académica y Tareas - Actividad 5");
        setSize(960, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrado en pantalla
        setResizable(false);
        setLayout(null); // Posicionamiento manual exacto para garantizar armonia visual
        getContentPane().setBackground(new Color(245, 247, 250)); // Fondo claro y limpio
    }

    /**
     * Paso 2.2: Construye el panel visual para la captura y registro de cursos.
     */
    private void construirPanelCursos() {
        panelCursos = new JPanel();
        panelCursos.setLayout(null);
        panelCursos.setBounds(20, 15, 430, 185);
        panelCursos.setBackground(Color.WHITE);

        // Borde con titulo descriptivo
        TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 210), 1),
            "1. MÓDULO DE CURSOS",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            FONT_TITULO,
            new Color(25, 45, 75)
        );
        panelCursos.setBorder(borde);

        // Etiqueta y Campo: Codigo
        JLabel lblCodigo = new JLabel("Código del Curso:");
        lblCodigo.setFont(FONT_ETIQUETA);
        lblCodigo.setBounds(20, 30, 120, 25);
        panelCursos.add(lblCodigo);

        txtCodigoCurso = new JTextField();
        txtCodigoCurso.setBounds(150, 30, 250, 25);
        txtCodigoCurso.setToolTipText("Ej. 0770, IPC1");
        panelCursos.add(txtCodigoCurso);

        // Etiqueta y Campo: Nombre
        JLabel lblNombre = new JLabel("Nombre del Curso:");
        lblNombre.setFont(FONT_ETIQUETA);
        lblNombre.setBounds(20, 65, 120, 25);
        panelCursos.add(lblNombre);

        txtNombreCurso = new JTextField();
        txtNombreCurso.setBounds(150, 65, 250, 25);
        txtNombreCurso.setToolTipText("Ej. Introduccion a la Programacion");
        panelCursos.add(txtNombreCurso);

        // Etiqueta y Campo: Tutor
        JLabel lblTutor = new JLabel("Tutor / Catedrático:");
        lblTutor.setFont(FONT_ETIQUETA);
        lblTutor.setBounds(20, 100, 120, 25);
        panelCursos.add(lblTutor);

        txtTutorCurso = new JTextField();
        txtTutorCurso.setBounds(150, 100, 250, 25);
        txtTutorCurso.setToolTipText("Ej. Ing. Catedratico");
        panelCursos.add(txtTutorCurso);

        // Boton de accion: Registrar Curso
        btnRegistrarCurso = new JButton("Registrar Curso");
        btnRegistrarCurso.setFont(FONT_BOTON);
        btnRegistrarCurso.setBounds(150, 138, 250, 32);
        btnRegistrarCurso.setBackground(new Color(40, 110, 180));
        btnRegistrarCurso.setForeground(Color.WHITE);
        btnRegistrarCurso.setFocusPainted(false);
        btnRegistrarCurso.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelCursos.add(btnRegistrarCurso);

        add(panelCursos);
    }

    /**
     * Paso 2.3: Construye el panel visual para la creacion y asignacion de tareas academicas.
     */
    private void construirPanelTareas() {
        panelTareas = new JPanel();
        panelTareas.setLayout(null);
        panelTareas.setBounds(20, 210, 430, 230);
        panelTareas.setBackground(Color.WHITE);

        TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 210), 1),
            "2. MÓDULO DE TAREAS ACADÉMICAS",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            FONT_TITULO,
            new Color(25, 45, 75)
        );
        panelTareas.setBorder(borde);

        // Etiqueta y Selector: Curso Asociado
        JLabel lblCurso = new JLabel("Curso Asociado:");
        lblCurso.setFont(FONT_ETIQUETA);
        lblCurso.setBounds(20, 30, 120, 25);
        panelTareas.add(lblCurso);

        cbCursosAsociados = new JComboBox<>();
        cbCursosAsociados.setFont(FONT_ETIQUETA);
        cbCursosAsociados.setBounds(150, 30, 250, 25);
        panelTareas.add(cbCursosAsociados);

        // Etiqueta y Campo: Titulo
        JLabel lblTitulo = new JLabel("Título de la Tarea:");
        lblTitulo.setFont(FONT_ETIQUETA);
        lblTitulo.setBounds(20, 65, 120, 25);
        panelTareas.add(lblTitulo);

        txtTituloTarea = new JTextField();
        txtTituloTarea.setBounds(150, 65, 250, 25);
        txtTituloTarea.setToolTipText("Ej. Practica 1, Hoja de Trabajo 1");
        panelTareas.add(txtTituloTarea);

        // Etiqueta y Campo: Descripcion
        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setFont(FONT_ETIQUETA);
        lblDesc.setBounds(20, 100, 120, 25);
        panelTareas.add(lblDesc);

        txtDescripcionTarea = new JTextField();
        txtDescripcionTarea.setBounds(150, 100, 250, 25);
        txtDescripcionTarea.setToolTipText("Instrucciones breves o enunciado");
        panelTareas.add(txtDescripcionTarea);

        // Etiqueta y Campo: Fecha de Entrega
        JLabel lblFecha = new JLabel("Fecha de Entrega:");
        lblFecha.setFont(FONT_ETIQUETA);
        lblFecha.setBounds(20, 135, 120, 25);
        panelTareas.add(lblFecha);

        txtFechaEntrega = new JTextField();
        txtFechaEntrega.setBounds(150, 135, 250, 25);
        txtFechaEntrega.setToolTipText("Formato libre: ej. 15/09/2026");
        panelTareas.add(txtFechaEntrega);

        // Boton de accion: Agregar Tarea
        btnAgregarTarea = new JButton("Agregar Tarea");
        btnAgregarTarea.setFont(FONT_BOTON);
        btnAgregarTarea.setBounds(150, 175, 250, 32);
        btnAgregarTarea.setBackground(new Color(45, 130, 90));
        btnAgregarTarea.setForeground(Color.WHITE);
        btnAgregarTarea.setFocusPainted(false);
        btnAgregarTarea.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelTareas.add(btnAgregarTarea);

        add(panelTareas);
    }

    /**
     * Paso 2.4: Construye el area de visualizacion con JTextArea dentro de JScrollPane y botones de reporte.
     */
    private void construirAreaVisualizacion() {
        panelVisualizacion = new JPanel();
        panelVisualizacion.setLayout(null);
        panelVisualizacion.setBounds(470, 15, 460, 610);
        panelVisualizacion.setBackground(Color.WHITE);

        TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 210), 1),
            "ÁREA DE VISUALIZACIÓN DE INFORMACIÓN",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            FONT_TITULO,
            new Color(25, 45, 75)
        );
        panelVisualizacion.setBorder(borde);

        // Boton: Mostrar Informacion
        btnMostrarInformacion = new JButton("Mostrar Información Consolidada");
        btnMostrarInformacion.setFont(FONT_BOTON);
        btnMostrarInformacion.setBounds(20, 30, 280, 34);
        btnMostrarInformacion.setBackground(new Color(70, 80, 95));
        btnMostrarInformacion.setForeground(Color.WHITE);
        btnMostrarInformacion.setFocusPainted(false);
        btnMostrarInformacion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelVisualizacion.add(btnMostrarInformacion);

        // Boton auxiliar: Limpiar consola
        btnLimpiarConsola = new JButton("Limpiar");
        btnLimpiarConsola.setFont(FONT_BOTON);
        btnLimpiarConsola.setBounds(310, 30, 130, 34);
        btnLimpiarConsola.setBackground(new Color(220, 225, 230));
        btnLimpiarConsola.setForeground(new Color(40, 50, 60));
        btnLimpiarConsola.setFocusPainted(false);
        btnLimpiarConsola.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panelVisualizacion.add(btnLimpiarConsola);

        // Area de Texto para Reportes (No editable)
        txtAreaConsola = new JTextArea();
        txtAreaConsola.setEditable(false);
        txtAreaConsola.setFont(FONT_MONO);
        txtAreaConsola.setBackground(new Color(250, 252, 255));
        txtAreaConsola.setLineWrap(false);
        txtAreaConsola.setText("=== BIENVENIDO AL SISTEMA DE GESTIÓN ACADÉMICA ===\n\n"
                            + "Presione 'Mostrar Información Consolidada' para listar los\n"
                            + "cursos y tareas actualmente registrados en memoria.\n");

        // ScrollPane para permitir desplazamiento vertical y horizontal
        scrollConsola = new JScrollPane(txtAreaConsola);
        scrollConsola.setBounds(20, 75, 420, 515);
        scrollConsola.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 230), 1));
        panelVisualizacion.add(scrollConsola);

        add(panelVisualizacion);
    }

    /**
     * Prepara el contenedor inferior izquierdo para albergar la Fase 3 (Conversion de Temperatura).
     * Muestra un panel de estado mientras se implementa la Fase 3.
     */
    private void prepararContenedorTemperatura() {
        panelTemperatura = new JPanel();
        panelTemperatura.setLayout(null);
        panelTemperatura.setBounds(20, 450, 430, 175);
        panelTemperatura.setBackground(Color.WHITE);

        TitledBorder borde = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 195, 210), 1),
            "3. MÓDULO DE CONVERSIÓN DE TEMPERATURA",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            FONT_TITULO,
            new Color(25, 45, 75)
        );
        panelTemperatura.setBorder(borde);

        JLabel lblInfoFase3 = new JLabel("Espacio reservado para la Fase 3 del planificador.");
        lblInfoFase3.setFont(FONT_ETIQUETA);
        lblInfoFase3.setForeground(Color.GRAY);
        lblInfoFase3.setBounds(30, 60, 360, 25);
        panelTemperatura.add(lblInfoFase3);

        add(panelTemperatura);
    }

    // ==========================================================
    // METODOS DE ACCESO PARA LOS COMPONENTES (GETTERS)
    // ==========================================================

    public GestorAcademico getGestor() {
        return gestor;
    }

    public JTextField getTxtCodigoCurso() {
        return txtCodigoCurso;
    }

    public JTextField getTxtNombreCurso() {
        return txtNombreCurso;
    }

    public JTextField getTxtTutorCurso() {
        return txtTutorCurso;
    }

    public JButton getBtnRegistrarCurso() {
        return btnRegistrarCurso;
    }

    public JComboBox<Curso> getCbCursosAsociados() {
        return cbCursosAsociados;
    }

    public JTextField getTxtTituloTarea() {
        return txtTituloTarea;
    }

    public JTextField getTxtDescripcionTarea() {
        return txtDescripcionTarea;
    }

    public JTextField getTxtFechaEntrega() {
        return txtFechaEntrega;
    }

    public JButton getBtnAgregarTarea() {
        return btnAgregarTarea;
    }

    public JTextArea getTxtAreaConsola() {
        return txtAreaConsola;
    }

    public JButton getBtnMostrarInformacion() {
        return btnMostrarInformacion;
    }

    public JButton getBtnLimpiarConsola() {
        return btnLimpiarConsola;
    }

    public JPanel getPanelTemperatura() {
        return panelTemperatura;
    }
}
