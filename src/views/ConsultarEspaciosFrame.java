package views;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
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
        setTitle(Textos.Titulo.CONSULTAR_ESPACIOS);
        setSize(750, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        var filterPanel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        cmbComplejo = UI.combo();
        for (ComplejoDeportivo cp : complejos) {
            cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        }

        txtFecha      = UI.field(12);
        txtHoraInicio = UI.field(8);
        txtHoraFin    = UI.field(8);

        String[] tipoOpciones = new String[TipoEspacio.values().length + 1];
        tipoOpciones[0] = Textos.Lbl.TODOS;
        for (int i = 0; i < TipoEspacio.values().length; i++) {
            tipoOpciones[i + 1] = TipoEspacio.values()[i].name();
        }
        cmbTipoEspacio = new JComboBox<>(tipoOpciones);

        String[] labels = {
            Textos.Lbl.COMPLEJO, Textos.Lbl.FECHA_OPCIONAL,
            Textos.Lbl.HORA_INICIO_OPCIONAL, Textos.Lbl.HORA_FIN_OPCIONAL, Textos.Lbl.TIPO
        };
        java.awt.Component[] fields = {cmbComplejo, txtFecha, txtHoraInicio, txtHoraFin, cmbTipoEspacio};

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(filterPanel, gbc, i, labels[i], fields[i]);
        }

        var btnBuscar = UI.button(Textos.Btn.BUSCAR);
        UI.addButton(filterPanel, gbc, labels.length, btnBuscar);
        btnBuscar.addActionListener(e -> onBuscar());

        tableModel = UI.tableModel(Textos.Tabla.ESPACIOS);
        var table = new JTable(tableModel);

        add(filterPanel, BorderLayout.NORTH);
        add(UI.scrollPane(table), BorderLayout.CENTER);
    }

    private void onBuscar() {
        if (complejos.isEmpty()) { UI.error(this, Textos.Msg.SIN_COMPLEJOS); return; }

        String fechaStr      = txtFecha.getText().trim();
        String horaInicioStr = txtHoraInicio.getText().trim();
        String horaFinStr    = txtHoraFin.getText().trim();

        boolean tieneFiltroHorario = !fechaStr.isEmpty() || !horaInicioStr.isEmpty() || !horaFinStr.isEmpty();

        if (tieneFiltroHorario) {
            if (fechaStr.isEmpty() || horaInicioStr.isEmpty() || horaFinStr.isEmpty()) {
                UI.error(this, Textos.Msg.FILTRO_HORARIO_INCOMPLETO);
                return;
            }
            String error = Validador.primerError(
                Validador.fecha(fechaStr),
                Validador.hora("Hora inicio", horaInicioStr),
                Validador.hora("Hora fin", horaFinStr),
                Validador.horasOrdenadas(horaInicioStr, horaFinStr)
            );
            if (error != null) { UI.errorValidacion(this, error); return; }
        }

        try {
            String codigoComplejo = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
            String seleccion      = (String) cmbTipoEspacio.getSelectedItem();
            TipoEspacio tipo      = Textos.Lbl.TODOS.equals(seleccion) ? null : TipoEspacio.valueOf(seleccion);

            Date fecha      = null;
            Time horaInicio = null;
            Time horaFin    = null;
            if (tieneFiltroHorario) {
                fecha      = new SimpleDateFormat("yyyy-MM-dd").parse(fechaStr);
                horaInicio = Time.valueOf(horaInicioStr + ":00");
                horaFin    = Time.valueOf(horaFinStr + ":00");
            }

            List<EspacioDeportivo> espacios = ReservaController.getInstance()
                    .consultarEspaciosDisponibles(codigoComplejo, fecha, horaInicio, horaFin, tipo);

            tableModel.setRowCount(0);

            if (espacios.isEmpty()) {
                UI.info(this, Textos.Msg.SIN_ESPACIOS_RESULTADO, Textos.Titulo.SIN_RESULTADOS);
                return;
            }

            for (EspacioDeportivo e : espacios) {
                tableModel.addRow(new Object[]{
                    e.getCodigo(), e.getNombre(), e.getTipoEspacio(),
                    e.getCapacidad(), "$" + e.getPrecioBaseHora(), e.getSuperficie()
                });
            }
        } catch (Exception ex) {
            UI.error(this, ex.getMessage());
        }
    }
}
