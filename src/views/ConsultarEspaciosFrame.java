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

import controllers.ComplejoDeportivoController;
import controllers.ReservaController;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.enums.TipoEspacio;

public class ConsultarEspaciosFrame extends JFrame {

    private JComboBox<String> cmbComplejo;
    private JTextField txtFecha;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFin;
    private JComboBox<String> cmbTipoEspacio;
    private DefaultTableModel tableModel;

    private List<ComplejoDeportivo> complejos;

    public ConsultarEspaciosFrame() {
        setTitle("Consultar Espacios Disponibles");
        setSize(750, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        cmbComplejo = new JComboBox<>();
        for (ComplejoDeportivo cp : complejos) {
            cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        }

        txtFecha      = new JTextField(10);
        txtHoraInicio = new JTextField(6);
        txtHoraFin    = new JTextField(6);

        String[] tipoOpciones = new String[TipoEspacio.values().length + 1];
        tipoOpciones[0] = "TODOS";
        for (int i = 0; i < TipoEspacio.values().length; i++) {
            tipoOpciones[i + 1] = TipoEspacio.values()[i].name();
        }
        cmbTipoEspacio = new JComboBox<>(tipoOpciones);

        JButton btnBuscar = new JButton("Buscar");

        filterPanel.add(new JLabel("Complejo:"));
        filterPanel.add(cmbComplejo);
        filterPanel.add(new JLabel("Fecha (yyyy-MM-dd):"));
        filterPanel.add(txtFecha);
        filterPanel.add(new JLabel("Hora inicio:"));
        filterPanel.add(txtHoraInicio);
        filterPanel.add(new JLabel("Hora fin:"));
        filterPanel.add(txtHoraFin);
        filterPanel.add(new JLabel("Tipo:"));
        filterPanel.add(cmbTipoEspacio);
        filterPanel.add(btnBuscar);

        String[] columns = {"Código", "Nombre", "Tipo", "Capacidad", "Precio/hora", "Superficie"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        btnBuscar.addActionListener(e -> onBuscar());

        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void onBuscar() {
        if (complejos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay complejos registrados.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fechaStr      = txtFecha.getText().trim();
        String horaInicioStr = txtHoraInicio.getText().trim();
        String horaFinStr    = txtHoraFin.getText().trim();

        if (fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fecha y horario son obligatorios.",
                    "Error de validación", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String error = Validador.primerError(
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
            String codigoComplejo = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
            Date fecha       = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
            Time horaInicio  = Time.valueOf(horaInicioStr + ":00");
            Time horaFin     = Time.valueOf(horaFinStr + ":00");
            String seleccion = (String) cmbTipoEspacio.getSelectedItem();
            TipoEspacio tipo = "TODOS".equals(seleccion) ? null : TipoEspacio.valueOf(seleccion);

            List<EspacioDeportivo> espacios = ReservaController.getInstance()
                    .consultarEspaciosDisponibles(codigoComplejo, fecha, horaInicio, horaFin, tipo);

            tableModel.setRowCount(0);

            if (espacios.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay espacios disponibles con esos criterios.",
                        "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (EspacioDeportivo e : espacios) {
                tableModel.addRow(new Object[]{
                    e.getCodigo(),
                    e.getNombre(),
                    e.getTipoEspacio(),
                    e.getCapacidad(),
                    "$" + e.getPrecioBaseHora(),
                    e.getSuperficie()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
