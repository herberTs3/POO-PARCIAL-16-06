package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;

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
        super(parent, Textos.Titulo.GESTIONAR_RESERVA, true);
        this.usuario = usuario;
        setSize(480, 260);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        java.util.Calendar hoy = java.util.Calendar.getInstance();
        confirmadas = new java.util.ArrayList<>();
        for (Reserva r : ReservaController.getInstance().listarPorEstado(EstadoReserva.CONFIRMADA)) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(r.getFecha());
            if (cal.get(java.util.Calendar.YEAR)        == hoy.get(java.util.Calendar.YEAR)
             && cal.get(java.util.Calendar.DAY_OF_YEAR) == hoy.get(java.util.Calendar.DAY_OF_YEAR)) {
                confirmadas.add(r);
            }
        }
        enCurso = ReservaController.getInstance().listarPorEstado(EstadoReserva.EN_CURSO);

        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(UI.label(Textos.Lbl.SEPARADOR_INICIAR), gbc);
        gbc.gridwidth = 1;

        cmbReservaIniciar = UI.combo();
        for (Reserva r : confirmadas) cmbReservaIniciar.addItem(labelReserva(r));
        UI.addFila(panel, gbc, 1, Textos.Lbl.RESERVA, cmbReservaIniciar);

        var btnIniciar = UI.button(Textos.Btn.INICIAR_USO);
        UI.addButton(panel, gbc, 2, btnIniciar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(UI.label(Textos.Lbl.SEPARADOR_FINALIZAR), gbc);
        gbc.gridwidth = 1;

        cmbReservaFinalizar = UI.combo();
        for (Reserva r : enCurso) cmbReservaFinalizar.addItem(labelReserva(r));
        UI.addFila(panel, gbc, 4, Textos.Lbl.RESERVA, cmbReservaFinalizar);

        var btnFinalizar = UI.button(Textos.Btn.FINALIZAR);
        UI.addButton(panel, gbc, 5, btnFinalizar);

        btnIniciar.addActionListener(e -> onIniciar());
        btnFinalizar.addActionListener(e -> onFinalizar());
        add(panel);
    }

    private void onIniciar() {
        if (confirmadas.isEmpty()) {
            UI.warning(this, Textos.Msg.SIN_CONFIRMADAS, Textos.Titulo.SIN_RESERVAS);
            return;
        }
        try {
            Reserva r = confirmadas.get(cmbReservaIniciar.getSelectedIndex());
            ReservaController.getInstance().iniciarUso(r.getCodigo(), usuario);
            UI.info(this, "Reserva " + r.getCodigo() + " pasó a EN CURSO.", Textos.Titulo.EXITO);
            dispose();
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }

    private void onFinalizar() {
        if (enCurso.isEmpty()) {
            UI.warning(this, Textos.Msg.SIN_EN_CURSO, Textos.Titulo.SIN_RESERVAS);
            return;
        }
        try {
            Reserva r    = enCurso.get(cmbReservaFinalizar.getSelectedIndex());
            double saldo = ReservaController.getInstance().finalizarReserva(r.getCodigo(), usuario);
            double total = r.calcularTotal();
            double sena  = r.obtenerImporteSena();

            String msg = "Reserva " + r.getCodigo() + " finalizada.\n\n"
                    + "Precio base:  $" + r.calcularPrecioBase() + "\n"
                    + "Recargo:      $" + r.calcularRecargo() + "\n"
                    + "Total:        $" + total + "\n"
                    + "Seña pagada:  $" + sena + "\n"
                    + "Saldo pendiente: $" + saldo;

            UI.info(this, msg, Textos.Titulo.RESERVA_FINALIZADA);
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
