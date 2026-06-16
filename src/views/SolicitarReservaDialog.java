package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ReservaController;
import models.enums.TipoReserva;

public class SolicitarReservaDialog extends JDialog {

    private static final Pattern PATRON_DNI    = Pattern.compile("^\\d{7,8}$");
    private static final Pattern PATRON_FECHA  = Pattern.compile(
            "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");
    private static final Pattern PATRON_HORA   = Pattern.compile(
            "^([01]\\d|2[0-3]):[0-5]\\d$");
    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Za-z0-9]+$");

    private JTextField txtDni;
    private JTextField txtCodigoComplejo;
    private JTextField txtCodigoEspacio;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JComboBox<TipoReserva> cmbTipoReserva;
    private JTextField txtUsuario;

    public SolicitarReservaDialog(Frame parent) {
        super(parent, "Solicitar Reserva", true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDni = new JTextField(15);
        txtCodigoComplejo = new JTextField(15);
        txtCodigoEspacio = new JTextField(15);
        txtFecha = new JTextField(15);
        txtHoraInicio = new JTextField(15);
        txtHoraFin = new JTextField(15);
        cmbTipoReserva = new JComboBox<>(TipoReserva.values());
        txtUsuario = new JTextField(15);

        String[] labels = {
            "DNI cliente:", "Código complejo:", "Código espacio:",
            "Fecha (yyyy-MM-dd):", "Hora inicio (HH:mm):", "Hora fin (HH:mm):",
            "Tipo reserva:", "Usuario:"
        };
        java.awt.Component[] fields = {
            txtDni, txtCodigoComplejo, txtCodigoEspacio,
            txtFecha, txtHoraInicio, txtHoraFin,
            cmbTipoReserva, txtUsuario
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnConfirmar = new JButton("Confirmar");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnConfirmar, gbc);

        btnConfirmar.addActionListener(e -> onConfirmar());
        add(panel);
    }

    private void onConfirmar() {
        String dni            = txtDni.getText().trim();
        String codigoComplejo = txtCodigoComplejo.getText().trim();
        String codigoEspacio  = txtCodigoEspacio.getText().trim();
        String fechaStr       = txtFecha.getText().trim();
        String horaInicioStr  = txtHoraInicio.getText().trim();
        String horaFinStr     = txtHoraFin.getText().trim();
        String usuario        = txtUsuario.getText().trim();

        if (dni.isEmpty() || codigoComplejo.isEmpty() || codigoEspacio.isEmpty()
                || fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty()
                || usuario.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!PATRON_DNI.matcher(dni).matches()) {
            mostrarError("DNI inválido: debe contener entre 7 y 8 dígitos numéricos.");
            return;
        }
        if (!PATRON_CODIGO.matcher(codigoComplejo).matches()) {
            mostrarError("Código de complejo inválido: solo letras y números.");
            return;
        }
        if (!PATRON_CODIGO.matcher(codigoEspacio).matches()) {
            mostrarError("Código de espacio inválido: solo letras y números.");
            return;
        }
        if (!PATRON_FECHA.matcher(fechaStr).matches()) {
            mostrarError("Fecha inválida: use el formato yyyy-MM-dd (ej: 2026-07-15).");
            return;
        }
        if (!PATRON_HORA.matcher(horaInicioStr).matches()) {
            mostrarError("Hora de inicio inválida: use el formato HH:mm (ej: 09:30).");
            return;
        }
        if (!PATRON_HORA.matcher(horaFinStr).matches()) {
            mostrarError("Hora de fin inválida: use el formato HH:mm (ej: 11:00).");
            return;
        }

        try {
            Date fecha        = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Time horaInicio   = Time.valueOf(horaInicioStr + ":00");
            Time horaFin      = Time.valueOf(horaFinStr + ":00");
            TipoReserva tipo  = (TipoReserva) cmbTipoReserva.getSelectedItem();

            models.Reserva reserva = ReservaController.getInstance()
                    .solicitarReserva(dni, codigoComplejo, codigoEspacio,
                            fecha, horaInicio, horaFin, tipo.name(), usuario);

            JOptionPane.showMessageDialog(this,
                    "Reserva registrada con código: " + reserva.getCodigo(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de validación", JOptionPane.ERROR_MESSAGE);
    }
}
