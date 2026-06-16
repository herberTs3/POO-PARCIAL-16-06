package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
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

    private List<Cliente> clientes;
    private List<ComplejoDeportivo> complejos;
    private String usuario;

    public SolicitarReservaDialog(Frame parent, String usuario) {
        super(parent, Textos.Titulo.SOLICITAR_RESERVA, true);
        this.usuario = usuario;
        setSize(460, 370);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        clientes  = ClienteController.getInstance().listarTodos();
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        cmbCliente = UI.combo();
        for (Cliente c : clientes) {
            cmbCliente.addItem(c.getDni() + " — " + c.getNombre() + " " + c.getApellido());
        }

        cmbComplejo = UI.combo();
        for (ComplejoDeportivo cp : complejos) {
            cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        }

        cmbEspacio = UI.combo();
        cargarEspacios();
        cmbComplejo.addActionListener(e -> cargarEspacios());

        txtFecha       = UI.field(15);
        txtHoraInicio  = UI.field(15);
        txtHoraFin     = UI.field(15);
        cmbTipoReserva = new JComboBox<>(TipoReserva.values());

        String[] labels = {
            Textos.Lbl.CLIENTE, Textos.Lbl.COMPLEJO, Textos.Lbl.ESPACIO,
            Textos.Lbl.FECHA, Textos.Lbl.HORA_INICIO, Textos.Lbl.HORA_FIN,
            Textos.Lbl.TIPO_RESERVA
        };
        java.awt.Component[] fields = {
            cmbCliente, cmbComplejo, cmbEspacio,
            txtFecha, txtHoraInicio, txtHoraFin,
            cmbTipoReserva
        };

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(panel, gbc, i, labels[i], fields[i]);
        }

        var btnConfirmar = UI.button(Textos.Btn.CONFIRMAR);
        UI.addButton(panel, gbc, labels.length, btnConfirmar);
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
        if (clientes.isEmpty())             { UI.error(this, Textos.Msg.SIN_CLIENTES); return; }
        if (complejos.isEmpty())            { UI.error(this, Textos.Msg.SIN_COMPLEJOS); return; }
        if (cmbEspacio.getItemCount() == 0) { UI.error(this, Textos.Msg.SIN_ESPACIOS_COMPLEJO); return; }

        String fechaStr      = txtFecha.getText().trim();
        String horaInicioStr = txtHoraInicio.getText().trim();
        String horaFinStr    = txtHoraFin.getText().trim();

        if (fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty()) {
            UI.errorValidacion(this, Textos.Msg.CAMPOS_OBLIGATORIOS);
            return;
        }

        String error = Validador.primerError(
            Validador.fecha(fechaStr),
            Validador.hora("Hora inicio", horaInicioStr),
            Validador.hora("Hora fin", horaFinStr),
            Validador.horasOrdenadas(horaInicioStr, horaFinStr)
        );
        if (error != null) { UI.errorValidacion(this, error); return; }

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

            UI.info(this, "Reserva registrada con código: " + reserva.getCodigo(), Textos.Titulo.EXITO);
            dispose();
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }
}
