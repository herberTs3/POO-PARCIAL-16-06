package views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Time;
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
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controllers.ReservaController;
import models.EspacioDeportivo;
import models.enums.TipoEspacio;

public class ConsultarEspaciosFrame extends JFrame {

    private JTextField txtCodigoComplejo;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JComboBox<TipoEspacio> cmbTipoEspacio;
    private DefaultTableModel tableModel;

    public ConsultarEspaciosFrame() {
        setTitle("Consultar Espacios Disponibles");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        txtCodigoComplejo = new JTextField(8);
        txtFecha          = new JTextField(10);
        txtHoraInicio     = new JTextField(6);
        txtHoraFin        = new JTextField(6);

        cmbTipoEspacio = new JComboBox<>(TipoEspacio.values());

        JButton btnBuscar = new JButton("Buscar");

        filterPanel.add(new JLabel("Código complejo:"));
        filterPanel.add(txtCodigoComplejo);
        filterPanel.add(new JLabel("Fecha (yyyy-MM-dd):"));
        filterPanel.add(txtFecha);
        filterPanel.add(new JLabel("Hora inicio:"));
        filterPanel.add(txtHoraInicio);
        filterPanel.add(new JLabel("Hora fin:"));
        filterPanel.add(txtHoraFin);
        filterPanel.add(new JLabel("Tipo actividad:"));
        filterPanel.add(cmbTipoEspacio);
        filterPanel.add(btnBuscar);

        String[] columns = {"Código", "Nombre", "Capacidad", "Precio/hora", "Superficie"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        btnBuscar.addActionListener(e -> onBuscar());

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void onBuscar() {
        String codigoComplejo = txtCodigoComplejo.getText().trim();
        String fechaStr       = txtFecha.getText().trim();
        String horaInicioStr  = txtHoraInicio.getText().trim();
        String horaFinStr     = txtHoraFin.getText().trim();

        if (codigoComplejo.isEmpty() || fechaStr.isEmpty()
                || horaInicioStr.isEmpty() || horaFinStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos de búsqueda son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
            Validador.codigo("Código complejo", codigoComplejo),
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
            Date fecha      = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Time horaInicio = Time.valueOf(horaInicioStr + ":00");
            Time horaFin    = Time.valueOf(horaFinStr + ":00");
            TipoEspacio tipo = (TipoEspacio) cmbTipoEspacio.getSelectedItem();

            List<EspacioDeportivo> espacios = ReservaController.getInstance()
                    .consultarEspaciosDisponibles(codigoComplejo, fecha, horaInicio, horaFin, tipo);

            tableModel.setRowCount(0);

            if (espacios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay espacios disponibles.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (EspacioDeportivo espacio : espacios) {
                tableModel.addRow(new Object[]{
                    espacio.getCodigo(),
                    espacio.getNombre(),
                    espacio.getCapacidad(),
                    espacio.getPrecioBaseHora(),
                    espacio.getSuperficie()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
