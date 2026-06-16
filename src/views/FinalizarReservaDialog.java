package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ReservaController;

public class FinalizarReservaDialog extends JDialog {

    private JTextField txtCodigoReserva;
    private JTextField txtUsuario;

    public FinalizarReservaDialog(Frame parent) {
        super(parent, "Finalizar Reserva", true);
        setSize(400, 200);
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
        txtUsuario = new JTextField(15);

        String[] labels = {"Código reserva:", "Usuario:"};
        java.awt.Component[] fields = {txtCodigoReserva, txtUsuario};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnFinalizar = new JButton("Finalizar");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnFinalizar, gbc);

        btnFinalizar.addActionListener(e -> onFinalizar());
        add(panel);
    }

    private void onFinalizar() {
        String codigo = txtCodigoReserva.getText().trim();
        String usuario = txtUsuario.getText().trim();

        if (codigo.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double saldo = ReservaController.getInstance().finalizarReserva(codigo, usuario);

            JOptionPane.showMessageDialog(this,
                    "Reserva finalizada. Saldo pendiente: $" + String.format("%.2f", saldo),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
