package cris.sic.actividad5.vista;

import cris.sic.actividad5.controlador.GestorAcademico;
import cris.sic.actividad5.modelo.Curso;
import cris.sic.actividad5.modelo.TareaAcademica;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    // COMPONENTES DEL MODULO DE CONVERSION DE TEMPERATURA (FASE 3)
    // ==========================================================
    private JPanel panelTemperatura;
    private JTextField txtTemperaturaC;
    private JButton btnConvertirFahrenheit;
    private JButton btnConvertirKelvin;
    private JLabel lblResultadoConversion;

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

        // Paso 3.1 - 3.3: Construccion del Modulo de Conversion de Temperatura (Fase 3)
        construirPanelTemperatura();

        // Cargar cursos iniciales existentes al selector de cursos
        actualizarComboCursos();

        // Paso 4: Configuracion de eventos y validaciones (Fase 4)
        configurarEventos();
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
     * Paso 3.1, 3.2 y 3.3: Construye el modulo de conversion de temperatura.
     * Incluye campo para entrada en Celsius, botones para conversion a Fahrenheit y Kelvin,
     * y etiqueta de salida estilizada para presentar el resultado.
     */
    private void construirPanelTemperatura() {
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

        // Paso 3.1: Etiqueta y Campo de Texto para Temperatura en Celsius
        JLabel lblTemp = new JLabel("Temperatura (°C):");
        lblTemp.setFont(FONT_ETIQUETA);
        lblTemp.setBounds(20, 30, 120, 25);
        panelTemperatura.add(lblTemp);

        txtTemperaturaC = new JTextField();
        txtTemperaturaC.setBounds(150, 30, 250, 25);
        txtTemperaturaC.setToolTipText("Ingrese un valor numerico en grados Celsius (ej. 25 o 36.5)");
        panelTemperatura.add(txtTemperaturaC);

        // Paso 3.2: Botones de Accion para Conversion a Fahrenheit y Kelvin
        btnConvertirFahrenheit = new JButton("Convertir a °F");
        btnConvertirFahrenheit.setFont(FONT_BOTON);
        btnConvertirFahrenheit.setBounds(20, 70, 185, 32);
        btnConvertirFahrenheit.setBackground(new Color(210, 105, 45)); // Tono calido/ambar
        btnConvertirFahrenheit.setForeground(Color.WHITE);
        btnConvertirFahrenheit.setFocusPainted(false);
        btnConvertirFahrenheit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConvertirFahrenheit.setToolTipText("Formula: (°C * 9/5) + 32");
        panelTemperatura.add(btnConvertirFahrenheit);

        btnConvertirKelvin = new JButton("Convertir a K");
        btnConvertirKelvin.setFont(FONT_BOTON);
        btnConvertirKelvin.setBounds(215, 70, 185, 32);
        btnConvertirKelvin.setBackground(new Color(45, 125, 175)); // Tono azul termico
        btnConvertirKelvin.setForeground(Color.WHITE);
        btnConvertirKelvin.setFocusPainted(false);
        btnConvertirKelvin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConvertirKelvin.setToolTipText("Formula: °C + 273.15");
        panelTemperatura.add(btnConvertirKelvin);

        // Paso 3.3: Area de Salida y Etiqueta Estilizada de Resultado
        lblResultadoConversion = new JLabel("Resultado: Ingrese un valor y presione una escala", SwingConstants.CENTER);
        lblResultadoConversion.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblResultadoConversion.setForeground(new Color(25, 45, 75));
        lblResultadoConversion.setOpaque(true);
        lblResultadoConversion.setBackground(new Color(240, 245, 252));
        lblResultadoConversion.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(195, 215, 235), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        lblResultadoConversion.setBounds(20, 115, 380, 36);
        panelTemperatura.add(lblResultadoConversion);

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

    public JTextField getTxtTemperaturaC() {
        return txtTemperaturaC;
    }

    public JButton getBtnConvertirFahrenheit() {
        return btnConvertirFahrenheit;
    }

    public JButton getBtnConvertirKelvin() {
        return btnConvertirKelvin;
    }

    public JLabel getLblResultadoConversion() {
        return lblResultadoConversion;
    }

    // ==========================================================
    // FASE 4: CONFIGURACION DE EVENTOS, VALIDACIONES Y LOGICA
    // ==========================================================

    /**
     * Paso 4.1 a 4.4: Vincula los escuchadores de eventos (ActionListener)
     * a cada uno de los botones interactivos de la interfaz grafica.
     */
    private void configurarEventos() {
        // Evento Paso 4.1: Registrar Curso
        btnRegistrarCurso.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarCurso();
            }
        });

        // Evento Paso 4.2: Agregar Tarea Academica
        btnAgregarTarea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarTarea();
            }
        });

        // Evento Paso 4.3: Mostrar Informacion Consolidada
        btnMostrarInformacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarInformacionConsolidada();
            }
        });

        // Evento Auxiliar: Limpiar Consola de Reporte
        btnLimpiarConsola.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarConsola();
            }
        });

        // Evento Paso 4.4: Conversion a Fahrenheit
        btnConvertirFahrenheit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertirAFahrenheit();
            }
        });

        // Evento Paso 4.4: Conversion a Kelvin
        btnConvertirKelvin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertirAKelvin();
            }
        });
    }

    /**
     * Paso 4.1: Logica para registrar un nuevo Curso.
     * Valida campos vacios con JOptionPane, verifica que el codigo sea unico,
     * instancia el Curso, lo agrega al gestor y actualiza el selector JComboBox.
     */
    private void registrarCurso() {
        String codigo = txtCodigoCurso.getText().trim();
        String nombre = txtNombreCurso.getText().trim();
        String tutor = txtTutorCurso.getText().trim();

        // Validacion de campos vacios con JOptionPane
        if (codigo.isEmpty() || nombre.isEmpty() || tutor.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Todos los campos del curso son obligatorios:\n- Código del Curso\n- Nombre del Curso\n- Tutor / Catedrático",
                "Validación - Campos Vacíos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Validacion de unicidad de codigo
        if (gestor.buscarCursoPorCodigo(codigo) != null) {
            JOptionPane.showMessageDialog(
                this,
                "Ya existe un curso registrado con el código '" + codigo + "'.\nPor favor ingrese un código único.",
                "Validación - Código Duplicado",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // Instanciar el objeto Curso y agregarlo a la coleccion
        Curso nuevoCurso = new Curso(codigo, nombre, tutor);
        boolean registrado = gestor.registrarCurso(nuevoCurso);

        if (registrado) {
            // Incorporar el nuevo curso al JComboBox y seleccionarlo
            cbCursosAsociados.addItem(nuevoCurso);
            cbCursosAsociados.setSelectedItem(nuevoCurso);

            // Limpiar los campos del formulario de cursos
            txtCodigoCurso.setText("");
            txtNombreCurso.setText("");
            txtTutorCurso.setText("");

            JOptionPane.showMessageDialog(
                this,
                "¡Curso registrado exitosamente!\n\n" + nuevoCurso.getInformacion(),
                "Registro Exitoso",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Refrescar automaticamente el reporte en la consola
            mostrarInformacionConsolidada();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Ocurrió un error inesperado al registrar el curso.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Paso 4.2: Logica para registrar una nueva Tarea Academica.
     * Valida que existan cursos disponibles, valida campos vacios,
     * instancia TareaAcademica vinculada al curso seleccionado y la almacena.
     */
    private void agregarTarea() {
        // Validar que exista al menos un curso registrado en el sistema
        if (gestor.getTotalCursos() == 0 || cbCursosAsociados.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(
                this,
                "No es posible agregar tareas porque no existe ningún curso registrado.\nPrimero registre al menos un curso en el Módulo 1.",
                "Validación - Sin Cursos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        Curso cursoSeleccionado = (Curso) cbCursosAsociados.getSelectedItem();
        String titulo = txtTituloTarea.getText().trim();
        String descripcion = txtDescripcionTarea.getText().trim();
        String fechaEntrega = txtFechaEntrega.getText().trim();

        // Validar campos obligatorios de la tarea con JOptionPane
        if (titulo.isEmpty() || descripcion.isEmpty() || fechaEntrega.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Todos los campos de la tarea son obligatorios:\n- Título de la Tarea\n- Descripción\n- Fecha de Entrega",
                "Validación - Campos Vacíos",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Instanciar y almacenar la tarea academica
        TareaAcademica nuevaTarea = new TareaAcademica(cursoSeleccionado, titulo, descripcion, fechaEntrega);
        boolean agregada = gestor.registrarTarea(nuevaTarea);

        if (agregada) {
            // Limpiar los campos del formulario de tareas
            txtTituloTarea.setText("");
            txtDescripcionTarea.setText("");
            txtFechaEntrega.setText("");

            JOptionPane.showMessageDialog(
                this,
                "¡Tarea académica agregada exitosamente!\n\n"
                + "Curso: " + cursoSeleccionado.getCodigo() + " - " + cursoSeleccionado.getNombre() + "\n"
                + "Tarea: " + nuevaTarea.getTitulo() + "\n"
                + "Entrega: " + nuevaTarea.getFechaEntrega(),
                "Tarea Registrada",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Refrescar automaticamente el reporte en pantalla
            mostrarInformacionConsolidada();
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Ocurrió un error al vincular la tarea con el curso.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Paso 4.3: Logica del evento 'Mostrar Informacion'.
     * Recorre los listados a traves del gestor academico y construye el reporte
     * de salida estructurado dentro del JTextArea.
     */
    public void mostrarInformacionConsolidada() {
        String reporte = gestor.generarReporteConsolidado();
        txtAreaConsola.setText(reporte);
        txtAreaConsola.setCaretPosition(0); // Desplazar hacia arriba
    }

    /**
     * Metodo auxiliar para restablecer el area de texto de reportes.
     */
    private void limpiarConsola() {
        txtAreaConsola.setText("=== CONSOLA DE VISUALIZACIÓN LIMPIA ===\n\n"
                            + "Presione 'Mostrar Información Consolidada' para recargar el listado.\n");
    }

    /**
     * Paso 4.4: Logica de conversion de temperatura de Celsius a Fahrenheit.
     * Valida que la entrada sea numerica utilizando un bloque try-catch (NumberFormatException).
     * Aplica la formula: °F = (°C * 9/5) + 32.
     */
    private void convertirAFahrenheit() {
        String texto = txtTemperaturaC.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Debe ingresar un valor numérico para la temperatura en °C.",
                "Validación - Entrada Vacía",
                JOptionPane.WARNING_MESSAGE
            );
            lblResultadoConversion.setText("Error: Ingrese un valor en °C");
            lblResultadoConversion.setForeground(new Color(180, 40, 40));
            return;
        }

        try {
            double celsius = Double.parseDouble(texto);
            if (Double.isNaN(celsius) || Double.isInfinite(celsius)) {
                throw new NumberFormatException("Valor no finito");
            }
            double fahrenheit = (celsius * 9.0 / 5.0) + 32.0;

            String salida = String.format("%.2f °C  =  %.2f °F", celsius, fahrenheit);
            lblResultadoConversion.setText("Resultado: " + salida);
            lblResultadoConversion.setForeground(new Color(190, 80, 20));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "El valor ingresado '" + texto + "' no es un número válido.\n"
                + "Asegúrese de ingresar solo números reales (ej. 25, -10, 36.5).",
                "Error - Formato Numérico Inválido",
                JOptionPane.ERROR_MESSAGE
            );
            lblResultadoConversion.setText("Error: Formato numérico incorrecto ('" + texto + "')");
            lblResultadoConversion.setForeground(new Color(180, 40, 40));
        }
    }

    /**
     * Paso 4.4: Logica de conversion de temperatura de Celsius a Kelvin.
     * Valida que la entrada sea numerica utilizando un bloque try-catch (NumberFormatException).
     * Aplica la formula: K = °C + 273.15.
     */
    private void convertirAKelvin() {
        String texto = txtTemperaturaC.getText().trim();

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Debe ingresar un valor numérico para la temperatura en °C.",
                "Validación - Entrada Vacía",
                JOptionPane.WARNING_MESSAGE
            );
            lblResultadoConversion.setText("Error: Ingrese un valor en °C");
            lblResultadoConversion.setForeground(new Color(180, 40, 40));
            return;
        }

        try {
            double celsius = Double.parseDouble(texto);
            if (Double.isNaN(celsius) || Double.isInfinite(celsius)) {
                throw new NumberFormatException("Valor no finito");
            }
            double kelvin = celsius + 273.15;

            String salida = String.format("%.2f °C  =  %.2f K", celsius, kelvin);
            lblResultadoConversion.setText("Resultado: " + salida);
            lblResultadoConversion.setForeground(new Color(30, 100, 160));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "El valor ingresado '" + texto + "' no es un número válido.\n"
                + "Asegúrese de ingresar solo números reales (ej. 25, -10, 36.5).",
                "Error - Formato Numérico Inválido",
                JOptionPane.ERROR_MESSAGE
            );
            lblResultadoConversion.setText("Error: Formato numérico incorrecto ('" + texto + "')");
            lblResultadoConversion.setForeground(new Color(180, 40, 40));
        }
    }

    /**
     * Actualiza los elementos del JComboBox de cursos a partir de la lista en memoria.
     */
    public void actualizarComboCursos() {
        cbCursosAsociados.removeAllItems();
        for (Curso c : gestor.getCursos()) {
            cbCursosAsociados.addItem(c);
        }
    }
}
