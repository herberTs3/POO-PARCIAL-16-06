package views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
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
        setTitle("Consultas del Sistema");
        setSize(620, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        complejos      = ComplejoDeportivoController.getInstance().listarTodos();
        clientes       = ClienteController.getInstance().listarTodos();
        todasReservas  = ReservaController.getInstance().listarTodas();
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Total Recaudado",       buildTabRecaudado());
        tabs.addTab("Reservas por Cliente",  buildTabReservasCliente());
        tabs.addTab("Recargo / Descuento",   buildTabRecargo());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildTabRecaudado() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbComplejo = new JComboBox<>();
        for (ComplejoDeportivo cp : complejos) cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        JTextField txtDesde  = new JTextField("2026-01-01", 12);
        JTextField txtHasta  = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 12);
        JLabel lblResultado  = new JLabel("—");
        JButton btnCalcular  = new JButton("Calcular");

        int r = 0;
        addFila(panel, gbc, r++, "Complejo:", cmbComplejo);
        addFila(panel, gbc, r++, "Desde (yyyy-MM-dd):", txtDesde);
        addFila(panel, gbc, r++, "Hasta (yyyy-MM-dd):", txtHasta);
        addFila(panel, gbc, r++, "Total recaudado:", lblResultado);

        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCalcular, gbc);

        btnCalcular.addActionListener(e -> {
            if (complejos.isEmpty()) { showError(panel, "No hay complejos registrados."); return; }
            String err = Validador.primerError(Validador.fecha(txtDesde.getText().trim()), Validador.fecha(txtHasta.getText().trim()));
            if (err != null) { showError(panel, err); return; }
            try {
                String cod   = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
                Date desde   = new SimpleDateFormat("yyyy-MM-dd").parse(txtDesde.getText().trim());
                Date hasta   = new SimpleDateFormat("yyyy-MM-dd").parse(txtHasta.getText().trim());
                double total = ReservaController.getInstance().totalRecaudadoPorComplejo(cod, desde, hasta);
                lblResultado.setText("$" + String.format("%.2f", total));
            } catch (Exception ex) { showError(panel, ex.getMessage()); }
        });

        return panel;
    }

    private JPanel buildTabReservasCliente() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));

        JPanel top = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbCliente = new JComboBox<>();
        for (Cliente c : clientes) cmbCliente.addItem(c.getDni() + " — " + c.getNombre() + " " + c.getApellido());
        JButton btnBuscar = new JButton("Buscar");
        addFila(top, gbc, 0, "Cliente:", cmbCliente);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        top.add(btnBuscar, gbc);

        String[] cols = {"Código", "Espacio", "Fecha", "Hora inicio", "Hora fin", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);

        btnBuscar.addActionListener(e -> {
            if (clientes.isEmpty()) { showError(panel, "No hay clientes registrados."); return; }
            model.setRowCount(0);
            String dni = clientes.get(cmbCliente.getSelectedIndex()).getDni();
            List<Reserva> res = ReservaController.getInstance().reservasConfirmadasPorCliente(dni);
            if (res.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "El cliente no tiene reservas CONFIRMADAS.", "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
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
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildTabRecargo() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbReserva = new JComboBox<>();
        for (Reserva r : todasReservas) cmbReserva.addItem(labelReserva(r));
        JLabel lblTipo      = new JLabel("—");
        JLabel lblRecargo   = new JLabel("—");
        JLabel lblDescuento = new JLabel("—");
        JLabel lblTotal     = new JLabel("—");
        JButton btnCalcular = new JButton("Calcular");

        int r = 0;
        addFila(panel, gbc, r++, "Reserva:", cmbReserva);
        addFila(panel, gbc, r++, "Tipo reserva:", lblTipo);
        addFila(panel, gbc, r++, "% Recargo:", lblRecargo);
        addFila(panel, gbc, r++, "% Descuento cliente:", lblDescuento);
        addFila(panel, gbc, r++, "Total estimado:", lblTotal);
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCalcular, gbc);

        btnCalcular.addActionListener(e -> {
            if (todasReservas.isEmpty()) { showError(panel, "No hay reservas registradas."); return; }
            Reserva rv = todasReservas.get(cmbReserva.getSelectedIndex());
            double precioBase  = rv.calcularPrecioBase();
            double recargo     = rv.calcularRecargo();
            double pctRecargo  = precioBase > 0 ? (recargo / precioBase) * 100 : 0;
            models.Descuento desc = rv.obtenerCliente().obtenerDescuentoVigente(rv.getFecha());
            double pctDesc     = desc != null ? desc.getPorcentaje() : 0;
            rv.aplicarDescuento(pctDesc);
            double total       = rv.calcularTotal();

            lblTipo.setText(rv.getClass().getSimpleName());
            lblRecargo.setText(String.format("%.0f%%", pctRecargo));
            lblDescuento.setText(pctDesc > 0 ? String.format("%.0f%%", pctDesc) : "Sin descuento");
            lblTotal.setText("$" + String.format("%.2f", total));
        });

        return panel;
    }

    private void addFila(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void showError(java.awt.Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String labelReserva(Reserva r) {
        return r.getCodigo() + " — " + r.obtenerCliente().getNombre() + " "
                + r.obtenerCliente().getApellido() + " [" + r.getEstado() + "] — "
                + r.getEspacio().getNombre();
    }
}
