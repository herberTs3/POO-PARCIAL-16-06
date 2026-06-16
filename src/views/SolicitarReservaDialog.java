package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Cliente;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.enums.TipoReserva;

public class SolicitarReservaDialog extends JDialog {

    private JComboBox<String> cmbCliente;
    private JComboBox<String> cmbComplejo;
    private JComboBox<String> cmbEspacio;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JComboBox<TipoReserva> cmbTipoReserva;
    private JTextField txtUsuario;

    private List<Cliente> clientes;
    private List<ComplejoDeportivo> complejos;

    public SolicitarReservaDialog(Frame parent) {
        super(parent, "Solicitar Reserva", true);
        setSize(460, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        clientes  = ClienteController.getInstance().listarTodos();
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbCliente = new JComboBox<>();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c.getDni() + " — " + c.getNombre() + " " + c.getApellido());
        }

        cmbComplejo = new JComboBox<>();
        for (ComplejoDeportivo cp : complejos) {
            cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        }

        cmbEspacio = new JComboBox<>();
        cargarEspacios();
        cmbComplejo.addActionListener(e -> cargarEspacios());

        txtFecha       = new JTextField(15);
        txtHoraInicio  = new JTextField(15);
        txtHoraFin     = new JTextField(15);
        cmbTipoReserva = new JComboBox<>(TipoReserva.values());
        txtUsuario     = new JTextField(15);

        String[] labels = {
            "Cliente:", "Complejo:", "Espacio:",
            "Fecha (yyyy-MM-dd):", "Hora inicio (HH:mm):", "Hora fin (HH:mm):",
            "Tipo reserva:", "Usuario:"
        };
        java.awt.Component[] fields = {
            cmbCliente, cmbComplejo, cmbEspacio,
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

    private void cargarEspacios() {
        cmbEspacio.removeAllItems();
        int idx = cmbComplejo.getSelectedIndex();
        if (idx < 0 || idx >= complejos.size()) return;
        for (EspacioDeportivo e : complejos.get(idx).obtenerEspacios()) {
            cmbEspacio.addItem(e.getCodigo() + " — " + e.getNombre() + " (" + e.getTipoEspacio() + ")");
        }
    }

    private void onConfirmar() {
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay clientes registrados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (complejos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay complejos registrados.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (cmbEspacio.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "El complejo seleccionado no tiene espacios registrados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fechaStr      = txtFecha.getText().trim();
        String horaInicioStr = txtHoraInicio.getText().trim();
        String horaFinStr    = txtHoraFin.getText().trim();
        String usuario       = txtUsuario.getText().trim();

        if (fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty() || usuario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
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
            String dni            = clientes.get(cmbCliente.getSelectedIndex()).getDni();
            String codigoComplejo = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
            String codigoEspacio  = ((String) cmbEspacio.getSelectedItem()).split(" — ")[0];
            Date fecha            = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Time horaInicio       = Time.valueOf(horaInicioStr + ":00");
            Time horaFin          = Time.valueOf(horaFinStr + ":00");
            TipoReserva tipo      = (TipoReserva) cmbTipoReserva.getSelectedItem();

            models.Reserva reserva = ReservaController.getInstance().solicitarReserva(
                    dni, codigoComplejo, codigoEspacio, fecha, horaInicio, horaFin,
                    tipo.name(), usuario);

            JOptionPane.showMessageDialog(this, "Reserva registrada con código: " + reserva.getCodigo(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
