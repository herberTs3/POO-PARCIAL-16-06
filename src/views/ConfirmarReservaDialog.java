package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ReservaController;
import models.enums.MedioPago;

public class ConfirmarReservaDialog extends JDialog {

    private JTextField txtCodigoReserva;
    private JTextField txtImporteSena;
    private JComboBox<MedioPago> cmbMedioPago;
    private JTextField txtUsuario;

    public ConfirmarReservaDialog(Frame parent) {
        super(parent, "Confirmar Reserva con Seña", true);
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigoReserva = new JTextField(15);
        txtImporteSena = new JTextField(15);
        cmbMedioPago = new JComboBox<>(MedioPago.values());
        txtUsuario = new JTextField(15);

        String[] labels = {"Código reserva:", "Importe seña ($):", "Medio de pago:", "Usuario:"};
        java.awt.Component[] fields = {txtCodigoReserva, txtImporteSena, cmbMedioPago, txtUsuario};

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
        String codigo = txtCodigoReserva.getText().trim();
        String importeStr = txtImporteSena.getText().trim();
        String usuario = txtUsuario.getText().trim();

        if (codigo.isEmpty() || importeStr.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double importe = Double.parseDouble(importeStr);
            MedioPago medioPago = (MedioPago) cmbMedioPago.getSelectedItem();

            ReservaController.getInstance().confirmarReservaConSena(codigo, importe, medioPago, usuario);

            JOptionPane.showMessageDialog(this, "Reserva confirmada correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El importe debe ser un número válido.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
