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

import java.util.List;

import controllers.ComplejoDeportivoController;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.enums.EstadoEspacio;
import models.enums.TipoEspacio;

public class AgregarEspacioDialog extends JDialog {

    private JComboBox<String> cmbComplejo;
    private JTextField txtCodigo;
    private List<ComplejoDeportivo> complejos;
    private JTextField txtNombre;
    private JTextField txtCapacidad;
    private JTextField txtPrecioBaseHora;
    private JTextField txtSuperficie;
    private JComboBox<TipoEspacio> cmbTipo;

    public AgregarEspacioDialog(Frame parent) {
        super(parent, "Agregar Espacio Deportivo", true);
        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbComplejo       = new JComboBox<>();
        for (ComplejoDeportivo cp : complejos) cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        txtCodigo         = new JTextField(15);
        txtNombre         = new JTextField(15);
        txtCapacidad      = new JTextField(15);
        txtPrecioBaseHora = new JTextField(15);
        txtSuperficie     = new JTextField(15);
        cmbTipo           = new JComboBox<>(TipoEspacio.values());

        String[] labels = {
            "Complejo:", "Código espacio:", "Nombre:",
            "Capacidad:", "Precio base/hora:", "Tipo:", "Superficie:"
        };
        java.awt.Component[] fields = {
            cmbComplejo, txtCodigo, txtNombre,
            txtCapacidad, txtPrecioBaseHora, cmbTipo, txtSuperficie
        };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnAgregar = new JButton("Agregar Espacio");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnAgregar, gbc);

        btnAgregar.addActionListener(e -> onAgregar());
        add(panel);
    }

    private void onAgregar() {
        if (complejos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay complejos registrados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String codigo       = txtCodigo.getText().trim();
        String nombre       = txtNombre.getText().trim();
        String capacidadStr = txtCapacidad.getText().trim();
        String precioStr    = txtPrecioBaseHora.getText().trim();
        String superficie   = txtSuperficie.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || capacidadStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios (superficie opcional).",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
            Validador.codigo("Código espacio", codigo),
            Validador.enteroPositivo("Capacidad", capacidadStr),
            Validador.decimalPositivo("Precio base/hora", precioStr)
        );
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String codigoComplejo = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
        int capacidad         = Integer.parseInt(capacidadStr);
        double precio         = Double.parseDouble(precioStr);
        TipoEspacio tipo      = (TipoEspacio) cmbTipo.getSelectedItem();

        EspacioDeportivo espacio = new EspacioDeportivo(
            codigo, nombre, capacidad, precio, EstadoEspacio.DISPONIBLE, tipo, superficie
        );
        ComplejoDeportivoController.getInstance().agregarEspacioAComplejo(codigoComplejo, espacio);

        JOptionPane.showMessageDialog(this, "Espacio \"" + nombre + "\" agregado a " + complejos.get(cmbComplejo.getSelectedIndex()).getNombre() + ".",
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
}
