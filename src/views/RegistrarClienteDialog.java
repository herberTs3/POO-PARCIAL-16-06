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

import controllers.ClienteController;

public class RegistrarClienteDialog extends JDialog {

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtEmail;

    public RegistrarClienteDialog(Frame parent) {
        super(parent, "Registrar Cliente", true);
        setSize(380, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtDni      = new JTextField(15);
        txtNombre   = new JTextField(15);
        txtApellido = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtEmail    = new JTextField(15);

        String[] labels = {"DNI:", "Nombre:", "Apellido:", "Teléfono:", "Email:"};
        JTextField[] fields = {txtDni, txtNombre, txtApellido, txtTelefono, txtEmail};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnRegistrar = new JButton("Registrar");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> onRegistrar());
        add(panel);
    }

    private void onRegistrar() {
        String dni      = txtDni.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email    = txtEmail.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
            Validador.dni(dni),
            Validador.soloLetras("Nombre", nombre),
            Validador.soloLetras("Apellido", apellido),
            Validador.telefono(telefono),
            Validador.email(email)
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ClienteController.getInstance().buscarPorDni(dni) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un cliente con ese DNI.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ClienteController.getInstance().registrarCliente(dni, nombre, apellido, telefono, email);
        JOptionPane.showMessageDialog(this, "Cliente registrado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

}
