package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnConfirmar = new JButton("Confirmar");
        gbc.gridx = 0;
        gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnConfirmar, gbc);

        btnConfirmar.addActionListener(e -> onConfirmar());

        add(panel);
    }

    private void onConfirmar() {
        String dni = txtDni.getText().trim();
        String codigoComplejo = txtCodigoComplejo.getText().trim();
        String codigoEspacio = txtCodigoEspacio.getText().trim();
        String fechaStr = txtFecha.getText().trim();
        String horaInicioStr = txtHoraInicio.getText().trim();
        String horaFinStr = txtHoraFin.getText().trim();
        String usuario = txtUsuario.getText().trim();

        if (dni.isEmpty() || codigoComplejo.isEmpty() || codigoEspacio.isEmpty()
                || fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty()
                || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
            Validador.dni(dni),
            Validador.codigo("Código complejo", codigoComplejo),
            Validador.codigo("Código espacio", codigoEspacio),
            Validador.fecha(fechaStr),
            Validador.hora("Hora inicio", horaInicioStr),
            Validador.hora("Hora fin", horaFinStr),
            Validador.horasOrdenadas(horaInicioStr, horaFinStr)
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Time horaInicio = Time.valueOf(horaInicioStr + ":00");
            Time horaFin = Time.valueOf(horaFinStr + ":00");
            TipoReserva tipoReserva = (TipoReserva) cmbTipoReserva.getSelectedItem();

            models.Reserva reserva = ReservaController.getInstance().solicitarReserva(
                    dni, codigoComplejo, codigoEspacio, fecha, horaInicio, horaFin,
                    tipoReserva.name(), usuario);

            JOptionPane.showMessageDialog(this, "Reserva registrada con código: " + reserva.getCodigo(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
