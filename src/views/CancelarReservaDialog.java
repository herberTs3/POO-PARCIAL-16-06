package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;

public class CancelarReservaDialog extends JDialog {

    private JComboBox<String> cmbReserva;
    private JTextField txtFechaCancelacion;

    private List<Reserva> reservas;
    private String usuario;

    public CancelarReservaDialog(Frame parent, String usuario) {
        super(parent, "Cancelar Reserva", true);
        this.usuario = usuario;
        setSize(460, 200);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        reservas = new java.util.ArrayList<>();
        reservas.addAll(ReservaController.getInstance().listarPorEstado(EstadoReserva.INGRESADA));
        reservas.addAll(ReservaController.getInstance().listarPorEstado(EstadoReserva.CONFIRMADA));

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 10, 7, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cmbReserva          = new JComboBox<>();
        for (Reserva r : reservas) cmbReserva.addItem(labelReserva(r));
        txtFechaCancelacion = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 15);

        String[] labels = {"Reserva:", "Fecha cancelación (yyyy-MM-dd):"};
        java.awt.Component[] fields = {cmbReserva, txtFechaCancelacion};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.weightx = 1;
            panel.add(fields[i], gbc);
        }

        JButton btnCancelar = new JButton("Cancelar Reserva");
        gbc.gridx = 0; gbc.gridy = labels.length;
        gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCancelar, gbc);
        btnCancelar.addActionListener(e -> onCancelar());
        add(panel);
    }

    private void onCancelar() {
        if (reservas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas activas para cancelar.",
                    "Sin reservas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fechaStr = txtFechaCancelacion.getText().trim();

        if (fechaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La fecha de cancelación es obligatoria.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.fecha(fechaStr);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Reserva reserva  = reservas.get(cmbReserva.getSelectedIndex());
            Date fechaCancel = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            double senaAntes = reserva.obtenerImporteSena();

            ReservaController.getInstance().cancelarReserva(reserva.getCodigo(), fechaCancel, usuario);

            long horas = reserva.calcularHorasAnticipacion(fechaCancel);
            String msg = "Reserva " + reserva.getCodigo() + " cancelada.\n";
            if (senaAntes <= 0) {
                msg += "Sin seña registrada — no hay importe a reintegrar.";
            } else if (horas > 24) {
                msg += "Seña de $" + senaAntes + " reintegrada como crédito a favor.";
            } else {
                msg += "Cancelación con menos de 24hs — seña no reintegrada.";
            }

            JOptionPane.showMessageDialog(this, msg, "Reserva cancelada", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " [" + r.getEstado() + "] — "
                + r.obtenerCliente().getNombre() + " " + r.obtenerCliente().getApellido()
                + " — " + r.getEspacio().getNombre()
                + " — " + new SimpleDateFormat("yyyy-MM-dd").format(r.getFecha());
    }
}
