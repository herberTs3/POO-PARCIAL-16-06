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

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;

public class GestionarReservaDialog extends JDialog {

    private JComboBox<String> cmbReservaIniciar;
    private JComboBox<String> cmbReservaFinalizar;

    private List<Reserva> confirmadas;
    private List<Reserva> enCurso;
    private String usuario;

    public GestionarReservaDialog(Frame parent, String usuario) {
        super(parent, "Gestionar Uso de Reserva", true);
        this.usuario = usuario;
        setSize(480, 260);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        confirmadas = ReservaController.getInstance().listarPorEstado(EstadoReserva.CONFIRMADA);
        enCurso     = ReservaController.getInstance().listarPorEstado(EstadoReserva.EN_CURSO);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JLabel("── Iniciar uso (CONFIRMADA → EN CURSO) ──"), gbc);
        gbc.gridwidth = 1;

        row++;
        cmbReservaIniciar = new JComboBox<>();
        for (Reserva r : confirmadas) cmbReservaIniciar.addItem(labelReserva(r));
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Reserva:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cmbReservaIniciar, gbc);

        row++;
        JButton btnIniciar = new JButton("Iniciar Uso");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnIniciar, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panel.add(new JLabel("── Finalizar uso (EN CURSO → FINALIZADA) ──"), gbc);
        gbc.gridwidth = 1;

        row++;
        cmbReservaFinalizar = new JComboBox<>();
        for (Reserva r : enCurso) cmbReservaFinalizar.addItem(labelReserva(r));
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Reserva:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(cmbReservaFinalizar, gbc);

        row++;
        JButton btnFinalizar = new JButton("Finalizar y Calcular Saldo");
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnFinalizar, gbc);

        btnIniciar.addActionListener(e -> onIniciar());
        btnFinalizar.addActionListener(e -> onFinalizar());
        add(panel);
    }

    private void onIniciar() {
        if (confirmadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas en estado CONFIRMADA.",
                    "Sin reservas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Reserva r = confirmadas.get(cmbReservaIniciar.getSelectedIndex());
            ReservaController.getInstance().iniciarUso(r.getCodigo(), usuario);
            JOptionPane.showMessageDialog(this,
                    "Reserva " + r.getCodigo() + " pasó a EN CURSO.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFinalizar() {
        if (enCurso.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay reservas en estado EN CURSO.",
                    "Sin reservas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Reserva r      = enCurso.get(cmbReservaFinalizar.getSelectedIndex());
            double saldo   = ReservaController.getInstance().finalizarReserva(r.getCodigo(), usuario);
            double total   = r.calcularTotal();
            double sena    = r.obtenerImporteSena();

            String msg = "Reserva " + r.getCodigo() + " finalizada.\n\n"
                    + "Precio base:  $" + r.calcularPrecioBase() + "\n"
                    + "Recargo:      $" + r.calcularRecargo() + "\n"
                    + "Total:        $" + total + "\n"
                    + "Seña pagada:  $" + sena + "\n"
                    + "Saldo pendiente: $" + saldo;

            JOptionPane.showMessageDialog(this, msg, "Reserva finalizada", JOptionPane.INFORMATION_MESSAGE);
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
