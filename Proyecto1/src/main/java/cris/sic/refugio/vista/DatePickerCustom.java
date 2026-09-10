package cris.sic.refugio.vista;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// Componente visual interactivo para seleccion de fechas con restriccion de fecha maxima actual (MaxDate = hoy)
public class DatePickerCustom extends JPanel {

    private JSpinner spinnerFecha;
    private SpinnerDateModel modelFecha;
    private JButton btnCalendario;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public DatePickerCustom() {
        setLayout(null);
        setOpaque(false);

        Date hoy = new Date();
        // Modelo de fecha restringido: fecha inicial hoy, sin minimo, maximo hoy
        modelFecha = new SpinnerDateModel(hoy, null, hoy, Calendar.DAY_OF_MONTH);
        spinnerFecha = new JSpinner(modelFecha);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinnerFecha, "dd/MM/yyyy");
        spinnerFecha.setEditor(editor);
        spinnerFecha.setFont(ThemeARAMS.FONT_BODY);

        btnCalendario = new JButton("📅");
        btnCalendario.setFont(ThemeARAMS.FONT_BODY);
        btnCalendario.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendario.setFocusPainted(false);
        btnCalendario.setToolTipText("Abrir selector interactivo de fecha");
        ThemeARAMS.aplicarEstiloBotonSecundario(btnCalendario);

        add(spinnerFecha);
        add(btnCalendario);

        btnCalendario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDialogoCalendario();
            }
        });
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        int btnWidth = 32;
        int spinnerWidth = Math.max(0, width - btnWidth - 4);
        spinnerFecha.setBounds(0, 0, spinnerWidth, height);
        btnCalendario.setBounds(spinnerWidth + 4, 0, btnWidth, height);
    }

    // Modulo para obtener la fecha seleccionada en formato dd/MM/yyyy
    public String getFechaFormateada() {
        Date d = (Date) spinnerFecha.getValue();
        if (d == null) d = new Date();
        return sdf.format(d);
    }

    // Modulo para establecer la fecha desde una cadena dd/MM/yyyy
    public void setFechaFormateada(String fechaStr) {
        if (fechaStr == null || fechaStr.trim().isEmpty()) {
            setFechaHoy();
            return;
        }
        try {
            Date d = sdf.parse(fechaStr.trim());
            Date hoy = new Date();
            if (d.after(hoy)) {
                d = hoy;
            }
            spinnerFecha.setValue(d);
        } catch (Exception e) {
            setFechaHoy();
        }
    }

    // Modulo para restablecer la fecha a hoy
    public void setFechaHoy() {
        spinnerFecha.setValue(new Date());
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        spinnerFecha.setEnabled(enabled);
        btnCalendario.setEnabled(enabled);
    }

    // Modulo que despliega una ventana modal con el calendario mensual interactivo
    private void mostrarDialogoCalendario() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        final JDialog dialog = new JDialog(parentWindow, "Selector de Fecha", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(280, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(5, 5));
        dialog.setResizable(false);

        final Calendar cal = Calendar.getInstance();
        Date fechaActual = (Date) spinnerFecha.getValue();
        if (fechaActual != null) {
            cal.setTime(fechaActual);
        }
        final Date hoy = new Date();

        final JLabel lblMesAno = new JLabel("", SwingConstants.CENTER);
        lblMesAno.setFont(ThemeARAMS.FONT_HEADLINE);
        lblMesAno.setForeground(ThemeARAMS.PRIMARY_DARK);

        final JPanel panelDias = new JPanel(new GridLayout(0, 7, 2, 2));
        panelDias.setBackground(ThemeARAMS.BACKGROUND);

        final Runnable actualizarVista = new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdfMes = new SimpleDateFormat("MMMM yyyy");
                String mesStr = sdfMes.format(cal.getTime());
                lblMesAno.setText(mesStr.substring(0, 1).toUpperCase() + mesStr.substring(1));

                panelDias.removeAll();
                String[] encabezados = {"D", "L", "M", "M", "J", "V", "S"};
                for (String diaH : encabezados) {
                    JLabel lblH = new JLabel(diaH, SwingConstants.CENTER);
                    lblH.setFont(ThemeARAMS.FONT_SMALL);
                    lblH.setForeground(ThemeARAMS.PRIMARY);
                    panelDias.add(lblH);
                }

                Calendar tempCal = (Calendar) cal.clone();
                tempCal.set(Calendar.DAY_OF_MONTH, 1);
                int primerDiaSemana = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Domingo
                int diasEnMes = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int i = 0; i < primerDiaSemana; i++) {
                    panelDias.add(new JLabel(""));
                }

                Calendar calHoy = Calendar.getInstance();
                calHoy.setTime(hoy);

                for (int d = 1; d <= diasEnMes; d++) {
                    final int diaFinal = d;
                    tempCal.set(Calendar.DAY_OF_MONTH, d);
                    boolean esFuturo = tempCal.after(calHoy);

                    JButton btnDia = new JButton(String.valueOf(d));
                    btnDia.setFont(ThemeARAMS.FONT_SMALL);
                    btnDia.setMargin(new java.awt.Insets(1, 1, 1, 1));
                    btnDia.setFocusPainted(false);

                    if (esFuturo) {
                        btnDia.setEnabled(false);
                        btnDia.setForeground(ThemeARAMS.OUTLINE);
                    } else {
                        btnDia.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        btnDia.setBackground(ThemeARAMS.SURFACE_CONTAINER);
                        btnDia.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                cal.set(Calendar.DAY_OF_MONTH, diaFinal);
                                spinnerFecha.setValue(cal.getTime());
                                dialog.dispose();
                            }
                        });
                    }
                    panelDias.add(btnDia);
                }
                panelDias.revalidate();
                panelDias.repaint();
            }
        };

        // Panel de navegacion del calendario
        JPanel panelNav = new JPanel(new BorderLayout());
        panelNav.setBackground(ThemeARAMS.PRIMARY_CONTAINER);
        JButton btnPrev = new JButton("◀");
        JButton btnNext = new JButton("▶");
        btnPrev.setFont(ThemeARAMS.FONT_SMALL);
        btnNext.setFont(ThemeARAMS.FONT_SMALL);

        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cal.add(Calendar.MONTH, -1);
                actualizarVista.run();
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar checkCal = (Calendar) cal.clone();
                checkCal.add(Calendar.MONTH, 1);
                checkCal.set(Calendar.DAY_OF_MONTH, 1);
                // Si el primer dia del mes siguiente ya es futuro respecto a hoy
                Calendar calHoy = Calendar.getInstance();
                calHoy.setTime(hoy);
                if (checkCal.get(Calendar.YEAR) <= calHoy.get(Calendar.YEAR) &&
                    checkCal.get(Calendar.MONTH) <= calHoy.get(Calendar.MONTH)) {
                    cal.add(Calendar.MONTH, 1);
                    actualizarVista.run();
                }
            }
        });

        panelNav.add(btnPrev, BorderLayout.WEST);
        panelNav.add(lblMesAno, BorderLayout.CENTER);
        panelNav.add(btnNext, BorderLayout.EAST);

        dialog.add(panelNav, BorderLayout.NORTH);
        dialog.add(panelDias, BorderLayout.CENTER);

        actualizarVista.run();
        dialog.setVisible(true);
    }
}
