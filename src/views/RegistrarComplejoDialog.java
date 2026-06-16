package views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ComplejoDeportivoController;

public class RegistrarComplejoDialog extends JDialog {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;

    public RegistrarComplejoDialog(Frame parent) {
        super(parent, "Registrar Complejo Deportivo", true);
        setSize(400, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodigo    = new JTextField(15);
        txtNombre    = new JTextField(15);
        txtDireccion = new JTextField(15);
        txtTelefono  = new JTextField(15);
        txtEmail     = new JTextField(15);

        String[] labels = {"Código:", "Nombre:", "Dirección:", "Teléfono:", "Email:"};
        JTextField[] fields = {txtCodigo, txtNombre, txtDireccion, txtTelefono, txtEmail};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnRegistrar = new JButton("Registrar Complejo");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRegistrar, gbc);

        btnRegistrar.addActionListener(e -> onRegistrar());
        add(panel, BorderLayout.CENTER);
    }

    private void onRegistrar() {
        String codigo    = txtCodigo.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String email     = txtEmail.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || direccion.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
            Validador.codigo("Código", codigo),
            Validador.soloLetras("Nombre", nombre),
            Validador.telefono(telefono),
            Validador.email(email)
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ComplejoDeportivoController.getInstance().buscarPorCodigo(codigo) != null) {
            JOptionPane.showMessageDialog(this, "Ya existe un complejo con ese código.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ComplejoDeportivoController.getInstance().registrarComplejo(codigo, nombre, direccion, telefono, email);
        JOptionPane.showMessageDialog(this, "Complejo \"" + nombre + "\" registrado correctamente.",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
