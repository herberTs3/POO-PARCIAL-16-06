package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;

public class CancelarReservaDialog extends JDialog {

    private JComboBox<String> cmbReserva;

    private List<Reserva> reservas;
    private String usuario;

    public CancelarReservaDialog(Frame parent, String usuario) {
        super(parent, Textos.Titulo.CANCELAR_RESERVA, true);
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

        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(7);

        cmbReserva = UI.combo();
        for (Reserva r : reservas) cmbReserva.addItem(labelReserva(r));

        UI.addFila(panel, gbc, 0, Textos.Lbl.RESERVA, cmbReserva);

        var btnCancelar = UI.button(Textos.Btn.CANCELAR);
        UI.addButton(panel, gbc, 1, btnCancelar);
        btnCancelar.addActionListener(e -> onCancelar());
        add(panel);
    }

    private void onCancelar() {
        if (reservas.isEmpty()) {
            UI.warning(this, Textos.Msg.SIN_RESERVAS_ACTIVAS, Textos.Titulo.SIN_RESERVAS);
            return;
        }

        try {
            Reserva reserva  = reservas.get(cmbReserva.getSelectedIndex());
            double creditado = ReservaController.getInstance().cancelarReserva(reserva.getCodigo(), new Date(), usuario);

            String msg = "Reserva " + reserva.getCodigo() + " cancelada.\n\n";
            msg += creditado > 0
                    ? "Seña de $" + creditado + " reintegrada como crédito a favor."
                    : "Cancelación sin reembolso: la reserva tenía menos de 24hs de anticipación.";

            UI.info(this, msg, Textos.Titulo.RESERVA_CANCELADA);
            dispose();
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " [" + r.getEstado() + "] — "
                + r.obtenerCliente().getNombre() + " " + r.obtenerCliente().getApellido()
                + " — " + r.getEspacio().getNombre()
                + " — " + new SimpleDateFormat("yyyy-MM-dd").format(r.getFecha());
    }
}
