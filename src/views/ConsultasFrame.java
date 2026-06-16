package views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.Cliente;
import models.ComplejoDeportivo;
import models.Reserva;

public class ConsultasFrame extends JFrame {

    private List<ComplejoDeportivo> complejos;
    private List<Cliente> clientes;
    private List<Reserva> todasReservas;

    public ConsultasFrame() {
        setTitle(Textos.Titulo.CONSULTAS);
        setSize(620, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        complejos     = ComplejoDeportivoController.getInstance().listarTodos();
        clientes      = ClienteController.getInstance().listarTodos();
        todasReservas = ReservaController.getInstance().listarTodas();

        var tabs = UI.tabs();
        tabs.addTab(Textos.Tab.TOTAL_RECAUDADO,   buildTabRecaudado());
        tabs.addTab(Textos.Tab.RESERVAS_CLIENTE,  buildTabReservasCliente());
        tabs.addTab(Textos.Tab.RECARGO_DESCUENTO, buildTabRecargo());
        add(tabs, BorderLayout.CENTER);
    }

    private java.awt.Container buildTabRecaudado() {
        var inner = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(10);

        var cmbComplejo = UI.combo();
        for (ComplejoDeportivo cp : complejos) cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        var txtDesde   = UI.field(12);
        var txtHasta   = UI.field(12);
        var lblResultado = UI.label(Textos.Lbl.SIN_VALOR);
        var btnCalcular  = UI.button(Textos.Btn.CALCULAR);

        int r = 0;
        UI.addFila(inner, gbc, r++, Textos.Lbl.COMPLEJO, cmbComplejo);
        UI.addFila(inner, gbc, r++, Textos.Lbl.DESDE_OPCIONAL, txtDesde);
        UI.addFila(inner, gbc, r++, Textos.Lbl.HASTA_OPCIONAL, txtHasta);
        UI.addFila(inner, gbc, r++, Textos.Lbl.TOTAL_RECAUDADO, lblResultado);
        UI.addButton(inner, gbc, r, btnCalcular);

        btnCalcular.addActionListener(e -> {
            if (complejos.isEmpty()) { UI.error(inner, Textos.Msg.SIN_COMPLEJOS); return; }
            String desdeStr = txtDesde.getText().trim();
            String hastaStr = txtHasta.getText().trim();
            if (!desdeStr.isEmpty()) {
                String err = Validador.fecha(desdeStr);
                if (err != null) { UI.error(inner, err); return; }
            }
            if (!hastaStr.isEmpty()) {
                String err = Validador.fecha(hastaStr);
                if (err != null) { UI.error(inner, err); return; }
            }
            try {
                String cod   = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
                Date desde   = desdeStr.isEmpty() ? null : new SimpleDateFormat("yyyy-MM-dd").parse(desdeStr);
                Date hasta   = hastaStr.isEmpty() ? null : new SimpleDateFormat("yyyy-MM-dd").parse(hastaStr);
                double total = ReservaController.getInstance().totalRecaudadoPorComplejo(cod, desde, hasta);
                lblResultado.setText("$" + String.format("%.2f", total));
            } catch (Exception ex) { UI.error(inner, ex.getMessage()); }
        });

        return UI.wrapNorth(inner);
    }

    private java.awt.Container buildTabReservasCliente() {
        var panel = new javax.swing.JPanel(new BorderLayout(0, 8));

        var top = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(10);

        var cmbCliente = UI.combo();
        for (Cliente c : clientes) cmbCliente.addItem(c.getDni() + " — " + c.getNombre() + " " + c.getApellido());
        var btnBuscar = UI.button(Textos.Btn.BUSCAR);
        UI.addFila(top, gbc, 0, Textos.Lbl.CLIENTE, cmbCliente);
        UI.addButton(top, gbc, 1, btnBuscar);

        DefaultTableModel model = UI.tableModel(Textos.Tabla.RESERVAS);
        var table = new JTable(model);

        btnBuscar.addActionListener(e -> {
            if (clientes.isEmpty()) { UI.error(panel, Textos.Msg.SIN_CLIENTES); return; }
            model.setRowCount(0);
            String dni = clientes.get(cmbCliente.getSelectedIndex()).getDni();
            List<Reserva> res = ReservaController.getInstance().reservasConfirmadasPorCliente(dni);
            if (res.isEmpty()) {
                UI.info(panel, Textos.Msg.SIN_RESERVAS_CLIENTE, Textos.Titulo.SIN_RESULTADOS);
                return;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Reserva rv : res) {
                model.addRow(new Object[]{
                    rv.getCodigo(), rv.getEspacio().getNombre(),
                    sdf.format(rv.getFecha()), rv.getHoraInicio(), rv.getHoraFin(), rv.getEstado()
                });
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(UI.scrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private java.awt.Container buildTabRecargo() {
        var inner = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(10);

        var cmbReserva   = UI.combo();
        for (Reserva r : todasReservas) cmbReserva.addItem(labelReserva(r));
        JLabel lblTipo      = UI.label(Textos.Lbl.SIN_VALOR);
        JLabel lblRecargo   = UI.label(Textos.Lbl.SIN_VALOR);
        JLabel lblDescuento = UI.label(Textos.Lbl.SIN_VALOR);
        JLabel lblTotal     = UI.label(Textos.Lbl.SIN_VALOR);
        var btnCalcular     = UI.button(Textos.Btn.CALCULAR);

        int r = 0;
        UI.addFila(inner, gbc, r++, Textos.Lbl.RESERVA, cmbReserva);
        UI.addFila(inner, gbc, r++, Textos.Lbl.TIPO_RESERVA_LABEL, lblTipo);
        UI.addFila(inner, gbc, r++, Textos.Lbl.PORCENTAJE_RECARGO, lblRecargo);
        UI.addFila(inner, gbc, r++, Textos.Lbl.DESCUENTO_CLIENTE, lblDescuento);
        UI.addFila(inner, gbc, r++, Textos.Lbl.TOTAL_ESTIMADO, lblTotal);
        UI.addButton(inner, gbc, r, btnCalcular);

        btnCalcular.addActionListener(e -> {
            if (todasReservas.isEmpty()) { UI.error(inner, Textos.Msg.SIN_RESERVAS); return; }
            Reserva rv = todasReservas.get(cmbReserva.getSelectedIndex());
            double precioBase = rv.calcularPrecioBase();
            double recargo    = rv.calcularRecargo();
            double pctRecargo = precioBase > 0 ? (recargo / precioBase) * 100 : 0;
            models.Descuento desc = rv.obtenerCliente().obtenerDescuentoVigente(rv.getFecha());
            double pctDesc   = desc != null ? desc.obtenerPorcentaje() : 0;
            rv.aplicarDescuento(pctDesc);
            double total     = rv.calcularTotal();

            lblTipo.setText(rv.getClass().getSimpleName());
            lblRecargo.setText(String.format("%.0f%%", pctRecargo));
            lblDescuento.setText(pctDesc > 0 ? String.format("%.0f%%", pctDesc) : Textos.Lbl.SIN_DESCUENTO);
            lblTotal.setText("$" + String.format("%.2f", total));
        });

        return UI.wrapNorth(inner);
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " — " + r.obtenerCliente().getNombre() + " "
                + r.obtenerCliente().getApellido() + " [" + r.getEstado() + "] — "
                + r.getEspacio().getNombre();
    }
}
