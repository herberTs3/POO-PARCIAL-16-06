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

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;

public class CancelarReservaDialog extends JDialog {

    private JComboBox<String> cmbReserva;

    private List<Reserva> reservas;
    private String usuario;

    public CancelarReservaDialog(Frame parent, String usuario) {
        super(parent, "Cancelar Reserva", true);
        this.usuario = usuario;
        setSize(500, 160);
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

        cmbReserva = new JComboBox<>();
        for (Reserva r : reservas) cmbReserva.addItem(labelReserva(r));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Reserva:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cmbReserva, gbc);

        JButton btnCancelar = new JButton("Cancelar Reserva");
        gbc.gridx = 0; gbc.gridy = 1;
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

        try {
            Reserva reserva     = reservas.get(cmbReserva.getSelectedIndex());
            double creditado    = ReservaController.getInstance().cancelarReserva(reserva.getCodigo(), new Date(), usuario);

            String msg = "Reserva " + reserva.getCodigo() + " cancelada.\n";
            msg += creditado > 0
                    ? "Seña de $" + creditado + " reintegrada como crédito a favor."
                    : "Sin reintegro de seña (menos de 24h de anticipación o sin seña registrada).";

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
