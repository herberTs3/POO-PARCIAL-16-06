package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ReservaController;

public class CancelarReservaDialog extends JDialog {

    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern PATRON_FECHA  = Pattern.compile(
            "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$");

    private JTextField txtCodigoReserva;
    private JTextField txtFechaCancelacion;
    private JTextField txtUsuario;

    public CancelarReservaDialog(Frame parent) {
        super(parent, "Cancelar Reserva", true);
        setSize(400, 240);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigoReserva    = new JTextField(15);
        txtFechaCancelacion = new JTextField(15);
        txtUsuario          = new JTextField(15);

        String[] labels = {"Código reserva:", "Fecha cancelación (yyyy-MM-dd):", "Usuario:"};
        java.awt.Component[] fields = {txtCodigoReserva, txtFechaCancelacion, txtUsuario};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnCancelar = new JButton("Cancelar Reserva");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCancelar, gbc);

        btnCancelar.addActionListener(e -> onCancelar());
        add(panel);
    }

    private void onCancelar() {
        String codigo   = txtCodigoReserva.getText().trim();
        String fechaStr = txtFechaCancelacion.getText().trim();
        String usuario  = txtUsuario.getText().trim();

        if (codigo.isEmpty() || fechaStr.isEmpty() || usuario.isEmpty()) {
            mostrarError("Todos los campos son obligatorios.");
            return;
        }
        if (!PATRON_CODIGO.matcher(codigo).matches()) {
            mostrarError("Código de reserva inválido: solo letras y números.");
            return;
        }
        if (!PATRON_FECHA.matcher(fechaStr).matches()) {
            mostrarError("Fecha inválida: use el formato yyyy-MM-dd (ej: 2026-07-15).");
            return;
        }

        try {
            Date fechaCancelacion = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            ReservaController.getInstance().cancelarReserva(codigo, fechaCancelacion, usuario);

            JOptionPane.showMessageDialog(this, "Reserva cancelada correctamente.",
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
