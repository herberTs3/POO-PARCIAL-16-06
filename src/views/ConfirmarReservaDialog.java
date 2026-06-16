package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;
import models.enums.MedioPago;

public class ConfirmarReservaDialog extends JDialog {

    private JComboBox<String> cmbReserva;
    private JTextField txtImporteSena;
    private JComboBox<MedioPago> cmbMedioPago;
    private JTextField txtUsuario;

    private List<Reserva> reservas;

    public ConfirmarReservaDialog(Frame parent) {
        super(parent, "Confirmar Reserva con Seña", true);
        setSize(440, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        reservas = ReservaController.getInstance().listarPorEstado(EstadoReserva.INGRESADA);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbReserva   = new JComboBox<>();
        for (Reserva r : reservas) {
            cmbReserva.addItem(labelReserva(r));
        }
        txtImporteSena = new JTextField(15);
        cmbMedioPago   = new JComboBox<>(MedioPago.values());
        txtUsuario     = new JTextField(15);

        String[] labels = {"Reserva (INGRESADA):", "Importe seña ($):", "Medio de pago:", "Usuario:"};
        java.awt.Component[] fields = {cmbReserva, txtImporteSena, cmbMedioPago, txtUsuario};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnConfirmar = new JButton("Confirmar Seña");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnConfirmar, gbc);
        btnConfirmar.addActionListener(e -> onConfirmar());
        add(panel);
    }

    private void onConfirmar() {
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas en estado INGRESADA.",
                    "Sin reservas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String importeStr = txtImporteSena.getText().trim();
        String usuario    = txtUsuario.getText().trim();

        if (importeStr.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.decimalPositivo("Importe seña", importeStr);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Reserva reserva   = reservas.get(cmbReserva.getSelectedIndex());
            double importe    = Double.parseDouble(importeStr);
            MedioPago medio   = (MedioPago) cmbMedioPago.getSelectedItem();

            ReservaController.getInstance().confirmarReservaConSena(reserva.getCodigo(), importe, medio, usuario);

            JOptionPane.showMessageDialog(this,
                    "Reserva " + reserva.getCodigo() + " confirmada.\nSeña registrada: $" + importe,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " — " + r.obtenerCliente().getNombre() + " "
                + r.obtenerCliente().getApellido() + " — " + r.getEspacio().getNombre()
                + " — " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(r.getFecha());
    }
}
