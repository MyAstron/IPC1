package cris.sic.practica2.vista;

import cris.sic.practica2.datos.GestorDatos;
import cris.sic.practica2.modelo.Piloto;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/**
 * Panel para el registro y selección de pilotos con dificultad.
 * Fase 3:
 * - Subfase 3.1: Formulario con JTextField para nombre y JComboBox para tipo de nave.
 * - Subfase 3.2: Validaciones con JOptionPane (evita duplicados y nombres vacíos).
 * - Subfase 3.3: Integración directa y persistente con los vectores de GestorDatos y tabla en vivo.
 * - Subfase 3.4: Eliminación de pilotos con confirmación modal y sincronización en disco.
 */
public class PanelCrearPiloto extends JPanel {

    private final VentanaPrincipal ventanaPrincipal;
    private final JTextField txtNombre;
    private final JComboBox<String> cmbTipoNave;
    private final JLabel lblDetalleNave;
    private final JButton btnGuardar;
    private final JButton btnLimpiar;
    private final JButton btnEliminar;
    private final JButton btnVolver;

    private final DefaultTableModel modeloTabla;
    private final JTable tablaPilotos;

    public PanelCrearPiloto(VentanaPrincipal ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setLayout(new BorderLayout(15, 15));
        setBackground(TemaEspacial.FONDO_ESPACIAL);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // ==========================================
        // ENCABEZADO SUPERIOR
        // ==========================================
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelHeader.setBackground(TemaEspacial.FONDO_PANEL);
        panelHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, TemaEspacial.BORDE_CIAN),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)
        ));

        JLabel lblTitulo = new JLabel("👨‍🚀 REGISTRO Y SELECCIÓN DE PILOTOS ESPACIALES");
        lblTitulo.setFont(TemaEspacial.FUENTE_TITULO_GRANDE);
        lblTitulo.setForeground(TemaEspacial.BORDE_CIAN);
        panelHeader.add(lblTitulo);
        add(panelHeader, BorderLayout.NORTH);

        // ==========================================
        // ÁREA CENTRAL DIVIDIDA (FORMULARIO Y TABLA)
        // ==========================================
        JPanel panelCentro = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentro.setOpaque(false);

        // --- TARJETA IZQUIERDA: FORMULARIO ---
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(TemaEspacial.FONDO_PANEL);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEspacial.AZUL_PRIMARIO, 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblSubForm = new JLabel("📝 Formulario de Nuevo Piloto");
        lblSubForm.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblSubForm.setForeground(TemaEspacial.AMARILLO_ORO);
        lblSubForm.setAlignmentX(LEFT_ALIGNMENT);
        panelFormulario.add(lblSubForm);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Campo 1: Nombre del Piloto
        JLabel lblNombre = new JLabel("Nombre del Piloto:");
        lblNombre.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);
        lblNombre.setForeground(TemaEspacial.TEXTO_BLANCO);
        lblNombre.setAlignmentX(LEFT_ALIGNMENT);
        panelFormulario.add(lblNombre);
        panelFormulario.add(Box.createVerticalStrut(5));

        txtNombre = new JTextField();
        txtNombre.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);
        txtNombre.setBackground(new Color(0x0E, 0x14, 0x22));
        txtNombre.setForeground(Color.WHITE);
        txtNombre.setCaretColor(TemaEspacial.BORDE_CIAN);
        txtNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        txtNombre.setPreferredSize(new Dimension(380, 35));
        txtNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEspacial.BORDE_CIAN, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        txtNombre.setAlignmentX(LEFT_ALIGNMENT);
        panelFormulario.add(txtNombre);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Campo 2: Tipo de Nave / Dificultad (JComboBox)
        JLabel lblNave = new JLabel("Tipo de Nave (Dificultad):");
        lblNave.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);
        lblNave.setForeground(TemaEspacial.TEXTO_BLANCO);
        lblNave.setAlignmentX(LEFT_ALIGNMENT);
        panelFormulario.add(lblNave);
        panelFormulario.add(Box.createVerticalStrut(5));

        String[] opcionesNaves = {
                "Explorador (Fácil)",
                "Caza Estelar (Normal)",
                "Acorazado (Difícil)"
        };
        cmbTipoNave = new JComboBox<>(opcionesNaves);
        cmbTipoNave.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);
        cmbTipoNave.setBackground(TemaEspacial.FONDO_PANEL);
        cmbTipoNave.setForeground(TemaEspacial.TEXTO_BLANCO);
        cmbTipoNave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cmbTipoNave.setPreferredSize(new Dimension(380, 35));
        cmbTipoNave.setAlignmentX(LEFT_ALIGNMENT);
        panelFormulario.add(cmbTipoNave);
        panelFormulario.add(Box.createVerticalStrut(15));

        // Tarjeta de especificaciones técnicas de la nave seleccionada
        JPanel panelInfoNave = new JPanel(new BorderLayout());
        panelInfoNave.setBackground(new Color(0x0E, 0x14, 0x22));
        panelInfoNave.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEspacial.AZUL_OSCURO, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panelInfoNave.setAlignmentX(LEFT_ALIGNMENT);
        panelInfoNave.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        lblDetalleNave = new JLabel();
        lblDetalleNave.setFont(TemaEspacial.FUENTE_TEXTO);
        lblDetalleNave.setForeground(TemaEspacial.TEXTO_SECUNDARIO);
        panelInfoNave.add(lblDetalleNave, BorderLayout.CENTER);
        panelFormulario.add(panelInfoNave);

        // Actualizar especificaciones al cambiar la selección
        actualizarEspecificacionesNave();
        cmbTipoNave.addActionListener(e -> actualizarEspecificacionesNave());

        panelFormulario.add(Box.createVerticalStrut(20));

        // Botones de acción del formulario
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotonesForm.setOpaque(false);
        panelBotonesForm.setAlignmentX(LEFT_ALIGNMENT);

        btnGuardar = new JButton("💾 Guardar Piloto");
        TemaEspacial.aplicarEstiloBoton(btnGuardar, TemaEspacial.AZUL_PRIMARIO, TemaEspacial.TEXTO_BLANCO);
        btnGuardar.setPreferredSize(new Dimension(170, 40));
        btnGuardar.addActionListener(e -> ejecutarGuardarPiloto());

        btnLimpiar = new JButton("🔄 Limpiar");
        TemaEspacial.aplicarEstiloBoton(btnLimpiar, TemaEspacial.AZUL_OSCURO, TemaEspacial.TEXTO_BLANCO);
        btnLimpiar.setPreferredSize(new Dimension(130, 40));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnLimpiar);
        panelFormulario.add(panelBotonesForm);

        panelFormulario.add(Box.createVerticalGlue());
        panelCentro.add(panelFormulario);

        // --- TARJETA DERECHA: TABLA DE PILOTOS REGISTRADOS ---
        JPanel panelTablaContenedor = new JPanel(new BorderLayout(0, 10));
        panelTablaContenedor.setBackground(TemaEspacial.FONDO_PANEL);
        panelTablaContenedor.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TemaEspacial.AZUL_PRIMARIO, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblSubTabla = new JLabel("📋 Pilotos Registrados en Memoria");
        lblSubTabla.setFont(TemaEspacial.FUENTE_SUBTITULO);
        lblSubTabla.setForeground(TemaEspacial.BORDE_CIAN);
        panelTablaContenedor.add(lblSubTabla, BorderLayout.NORTH);

        String[] columnas = {"#", "Nombre", "Nave", "Dificultad", "Recarga", "Récord"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPilotos = new JTable(modeloTabla);
        tablaPilotos.setBackground(TemaEspacial.FONDO_ESPACIAL);
        tablaPilotos.setForeground(Color.WHITE);
        tablaPilotos.setFont(TemaEspacial.FUENTE_TEXTO);
        tablaPilotos.setRowHeight(26);
        tablaPilotos.setGridColor(TemaEspacial.AZUL_OSCURO);

        JTableHeader header = tablaPilotos.getTableHeader();
        header.setBackground(TemaEspacial.AZUL_OSCURO);
        header.setForeground(TemaEspacial.BORDE_CIAN);
        header.setFont(TemaEspacial.FUENTE_TEXTO_BOLD);

        // Centrado de columnas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < tablaPilotos.getColumnCount(); i++) {
            if (i != 1) { // No centrar el nombre
                tablaPilotos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        JScrollPane scrollTabla = new JScrollPane(tablaPilotos);
        scrollTabla.getViewport().setBackground(TemaEspacial.FONDO_ESPACIAL);
        scrollTabla.setBorder(BorderFactory.createLineBorder(TemaEspacial.AZUL_OSCURO, 1));
        panelTablaContenedor.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de la tabla con botón de eliminar piloto (Subfase 3.4)
        JPanel panelBotonTabla = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        panelBotonTabla.setOpaque(false);

        btnEliminar = new JButton("🗑️ Eliminar Piloto");
        TemaEspacial.aplicarEstiloBoton(btnEliminar, TemaEspacial.ROJO_PELIGRO, TemaEspacial.TEXTO_BLANCO);
        btnEliminar.setPreferredSize(new Dimension(170, 36));
        btnEliminar.addActionListener(e -> ejecutarEliminarPiloto());
        panelBotonTabla.add(btnEliminar);
        panelTablaContenedor.add(panelBotonTabla, BorderLayout.SOUTH);

        panelCentro.add(panelTablaContenedor);
        add(panelCentro, BorderLayout.CENTER);

        // ==========================================
        // BARRA INFERIOR CON RETORNO
        // ==========================================
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelInferior.setBackground(TemaEspacial.FONDO_PANEL);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, TemaEspacial.AZUL_OSCURO));

        btnVolver = new JButton("⬅️ Volver al Menú Principal");
        TemaEspacial.aplicarEstiloBotonVolver(btnVolver);
        btnVolver.addActionListener(e -> ventanaPrincipal.mostrarPanel(VentanaPrincipal.PANEL_MENU));
        panelInferior.add(btnVolver);

        add(panelInferior, BorderLayout.SOUTH);

        // Cargar pilotos existentes si los hay
        actualizarTablaPilotos();
    }

    /**
     * Actualiza la descripción técnica de la nave elegida en el combo box.
     */
    private void actualizarEspecificacionesNave() {
        String seleccion = (String) cmbTipoNave.getSelectedItem();
        if (seleccion == null) return;

        if (seleccion.contains("Explorador")) {
            lblDetalleNave.setText("<html><b style='color:#00E5FF;'>Explorador (Fácil):</b><br>" +
                    "• <b>Velocidad:</b> Rápida (10 px)<br>" +
                    "• <b>Recarga de disparo:</b> 2.0 segundos (2000 ms)<br>" +
                    "• <i>Recomendado para pilotos novatos.</i></html>");
        } else if (seleccion.contains("Caza")) {
            lblDetalleNave.setText("<html><b style='color:#FFD700;'>Caza Estelar (Normal):</b><br>" +
                    "• <b>Velocidad:</b> Media (7 px)<br>" +
                    "• <b>Recarga de disparo:</b> 1.0 segundo (1000 ms)<br>" +
                    "• <i>Equilibrio óptimo entre agilidad y poder de fuego.</i></html>");
        } else if (seleccion.contains("Acorazado")) {
            lblDetalleNave.setText("<html><b style='color:#E53935;'>Acorazado (Difícil):</b><br>" +
                    "• <b>Velocidad:</b> Lenta (4 px)<br>" +
                    "• <b>Recarga de disparo:</b> 0.3 segundos (300 ms - Ráfaga)<br>" +
                    "• <i>Poder destructor masivo con movilidad reducida.</i></html>");
        }
    }

    /**
     * Valida los campos ingresados y guarda el piloto en el vector de persistencia en memoria.
     * Cumple con la Subfase 3.2 y 3.3.
     */
    public boolean ejecutarGuardarPiloto() {
        String nombre = txtNombre.getText().trim();
        String naveSeleccionada = (String) cmbTipoNave.getSelectedItem();

        // Validación 1: Campo de nombre vacío
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El nombre del piloto no puede estar vacío. Ingrese un identificador válido.",
                    "Validación Requerida",
                    JOptionPane.WARNING_MESSAGE
            );
            txtNombre.requestFocus();
            return false;
        }

        // Obtener el tipo de nave limpio
        String tipoNave = Piloto.NAVE_EXPLORADOR;
        if (naveSeleccionada != null) {
            if (naveSeleccionada.contains("Acorazado")) {
                tipoNave = Piloto.NAVE_ACORAZADO;
            } else if (naveSeleccionada.contains("Caza")) {
                tipoNave = Piloto.NAVE_CAZA_ESTELAR;
            }
        }

        GestorDatos gestor = GestorDatos.getInstancia();

        // Validación 2: Nombre duplicado
        if (gestor.existePiloto(nombre)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Ya existe un piloto registrado con el nombre '" + nombre + "'.\nPor favor elija un nombre diferente.",
                    "Piloto Duplicado",
                    JOptionPane.ERROR_MESSAGE
            );
            txtNombre.selectAll();
            txtNombre.requestFocus();
            return false;
        }

        // Subfase 3.3: Inserción en el vector de GestorDatos
        Piloto nuevoPiloto = new Piloto(nombre, tipoNave);
        boolean insertado = gestor.insertarPiloto(nuevoPiloto);

        if (insertado) {
            // Confirmación por consola requerida por la fase
            System.out.println("\n[SISTEMA - PILOTO REGISTRADO]");
            System.out.println(" -> " + nuevoPiloto);
            gestor.listarPilotos();

            JOptionPane.showMessageDialog(
                    this,
                    "¡Piloto '" + nombre + "' registrado con éxito!\nNave: " + nuevoPiloto.getTipoNave() +
                            " | Dificultad: " + nuevoPiloto.getNivelDificultad(),
                    "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );

            limpiarFormulario();
            actualizarTablaPilotos();
            return true;
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Ocurrió un error inesperado al registrar el piloto.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    public void limpiarFormulario() {
        txtNombre.setText("");
        cmbTipoNave.setSelectedIndex(0);
        actualizarEspecificacionesNave();
        txtNombre.requestFocus();
    }

    /**
     * Refresca el contenido visual de la tabla a partir de los datos en el vector de memoria.
     */
    public void actualizarTablaPilotos() {
        modeloTabla.setRowCount(0);
        Piloto[] pilotos = GestorDatos.getInstancia().obtenerPilotos();
        for (int i = 0; i < pilotos.length; i++) {
            Piloto p = pilotos[i];
            modeloTabla.addRow(new Object[]{
                    (i + 1),
                    p.getNombre(),
                    p.getTipoNave(),
                    p.getNivelDificultad(),
                    (p.getTiempoRecargaMs() / 1000.0) + "s",
                    p.getPunteoMaximo() + " pts"
            });
        }
    }

    /**
     * Elimina el piloto seleccionado en la tabla previa confirmación modal con JOptionPane.
     * Sincroniza la remoción tanto en memoria como en disco cifrado (Subfase 3.4).
     *
     * @return true si se eliminó con éxito, false si se canceló o falló
     */
    public boolean ejecutarEliminarPiloto() {
        int filaSeleccionada = tablaPilotos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor seleccione un piloto de la tabla para eliminar.",
                    "Ningún Piloto Seleccionado",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        String nombrePiloto = (String) modeloTabla.getValueAt(filaSeleccionada, 1);

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea eliminar al piloto '" + nombrePiloto + "'?\nEsta acción lo borrará permanentemente de la memoria y del archivo en disco.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean eliminado = GestorDatos.getInstancia().eliminarPiloto(nombrePiloto);
            if (eliminado) {
                System.out.println("\n[SISTEMA - PILOTO ELIMINADO]");
                System.out.println(" -> Piloto eliminado: " + nombrePiloto);
                GestorDatos.getInstancia().listarPilotos();

                JOptionPane.showMessageDialog(
                        this,
                        "El piloto '" + nombrePiloto + "' ha sido eliminado con éxito.",
                        "Piloto Eliminado",
                        JOptionPane.INFORMATION_MESSAGE
                );
                actualizarTablaPilotos();
                return true;
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar al piloto '" + nombrePiloto + "'.",
                        "Error al Eliminar",
                        JOptionPane.ERROR_MESSAGE
                );
                return false;
            }
        }
        return false;
    }

    // Getters para pruebas y acceso programático
    public JTextField getTxtNombre() {
        return txtNombre;
    }

    public JComboBox<String> getCmbTipoNave() {
        return cmbTipoNave;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnLimpiar() {
        return btnLimpiar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JButton getBtnVolver() {
        return btnVolver;
    }

    public JTable getTablaPilotos() {
        return tablaPilotos;
    }
}
