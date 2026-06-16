package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import controllers.ReservaController;
import models.Reserva;
import models.enums.EstadoReserva;
import models.enums.MedioPago;

public class ConfirmarReservaDialog extends JDialog {

    private JComboBox<String> cmbReserva;
    private JTextField txtImporteSena;
    private JComboBox<MedioPago> cmbMedioPago;

    private List<Reserva> reservas;
    private String usuario;

    public ConfirmarReservaDialog(Frame parent, String usuario) {
        super(parent, Textos.Titulo.CONFIRMAR_RESERVA, true);
        this.usuario = usuario;
        setSize(440, 240);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        reservas = ReservaController.getInstance().listarPorEstado(EstadoReserva.INGRESADA);

        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(7);

        cmbReserva = UI.combo();
        for (Reserva r : reservas) cmbReserva.addItem(labelReserva(r));

        txtImporteSena = UI.field(15);
        cmbMedioPago   = new JComboBox<>(MedioPago.values());

        String[] labels = {"Reserva (INGRESADA):", Textos.Lbl.IMPORTE_SENA, Textos.Lbl.MEDIO_PAGO};
        java.awt.Component[] fields = {cmbReserva, txtImporteSena, cmbMedioPago};

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(panel, gbc, i, labels[i], fields[i]);
        }

        var btnConfirmar = UI.button(Textos.Btn.CONFIRMAR_SENA);
        UI.addButton(panel, gbc, labels.length, btnConfirmar);
        btnConfirmar.addActionListener(e -> onConfirmar());
        add(panel);
    }

    private void onConfirmar() {
        if (reservas.isEmpty()) {
            UI.warning(this, Textos.Msg.SIN_INGRESADAS, Textos.Titulo.SIN_RESERVAS);
            return;
        }

        String importeStr = txtImporteSena.getText().trim();
        if (importeStr.isEmpty()) {
            UI.errorValidacion(this, Textos.Msg.IMPORTE_OBLIGATORIO);
            return;
        }

        String error = Validador.decimalPositivo("Importe seña", importeStr);
        if (error != null) { UI.errorValidacion(this, error); return; }

        try {
            Reserva reserva = reservas.get(cmbReserva.getSelectedIndex());
            double importe  = Double.parseDouble(importeStr);
            MedioPago medio = (MedioPago) cmbMedioPago.getSelectedItem();

            ReservaController.getInstance().confirmarReservaConSena(reserva.getCodigo(), importe, medio, usuario);

            UI.info(this, "Reserva " + reserva.getCodigo() + " confirmada.\nSeña registrada: $" + importe,
                    Textos.Titulo.EXITO);
            dispose();
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " — " + r.obtenerCliente().getNombre() + " "
                + r.obtenerCliente().getApellido() + " — " + r.getEspacio().getNombre()
                + " — " + new SimpleDateFormat("yyyy-MM-dd").format(r.getFecha());
    }
}
