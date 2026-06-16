package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JTextField;

import controllers.ComplejoDeportivoController;
import models.ComplejoDeportivo;
import models.EspacioDeportivo;
import models.enums.EstadoEspacio;
import models.enums.TipoEspacio;

public class AgregarEspacioDialog extends JDialog {

    private JComboBox<String> cmbComplejo;
    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtCapacidad;
    private JTextField txtPrecioBaseHora;
    private JTextField txtSuperficie;
    private JComboBox<TipoEspacio> cmbTipo;

    private List<ComplejoDeportivo> complejos;

    public AgregarEspacioDialog(Frame parent) {
        super(parent, Textos.Titulo.AGREGAR_ESPACIO, true);
        setSize(420, 380);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        complejos = ComplejoDeportivoController.getInstance().listarTodos();

        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        cmbComplejo       = UI.combo();
        for (ComplejoDeportivo cp : complejos) cmbComplejo.addItem(cp.getCodigo() + " — " + cp.getNombre());
        txtCodigo         = UI.field(15);
        txtNombre         = UI.field(15);
        txtCapacidad      = UI.field(15);
        txtPrecioBaseHora = UI.field(15);
        txtSuperficie     = UI.field(15);
        cmbTipo           = new JComboBox<>(TipoEspacio.values());

        String[] labels = {
            Textos.Lbl.COMPLEJO, Textos.Lbl.CODIGO_ESPACIO, Textos.Lbl.NOMBRE,
            Textos.Lbl.CAPACIDAD, Textos.Lbl.PRECIO_BASE, Textos.Lbl.TIPO, Textos.Lbl.SUPERFICIE
        };
        java.awt.Component[] fields = {
            cmbComplejo, txtCodigo, txtNombre,
            txtCapacidad, txtPrecioBaseHora, cmbTipo, txtSuperficie
        };

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(panel, gbc, i, labels[i], fields[i]);
        }

        var btnAgregar = UI.button(Textos.Btn.AGREGAR);
        UI.addButton(panel, gbc, labels.length, btnAgregar);
        btnAgregar.addActionListener(e -> onAgregar());
        add(panel);
    }

    private void onAgregar() {
        if (complejos.isEmpty()) { UI.error(this, Textos.Msg.SIN_COMPLEJOS); return; }

        String codigo       = txtCodigo.getText().trim();
        String nombre       = txtNombre.getText().trim();
        String capacidadStr = txtCapacidad.getText().trim();
        String precioStr    = txtPrecioBaseHora.getText().trim();
        String superficie   = txtSuperficie.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || capacidadStr.isEmpty() || precioStr.isEmpty()) {
            UI.errorValidacion(this, Textos.Msg.CAMPOS_OBLIGATORIOS_SUP);
            return;
        }

        String error = Validador.primerError(
            Validador.codigo("Código espacio", codigo),
            Validador.enteroPositivo("Capacidad", capacidadStr),
            Validador.decimalPositivo("Precio base/hora", precioStr)
        );
        if (error != null) { UI.errorValidacion(this, error); return; }

        int capacidad = Integer.parseInt(capacidadStr);
        if (capacidad > 10000) {
            UI.errorValidacion(this, Textos.Msg.CAPACIDAD_MAXIMA);
            return;
        }

        String codigoComplejo = complejos.get(cmbComplejo.getSelectedIndex()).getCodigo();
        double precio         = Double.parseDouble(precioStr);
        TipoEspacio tipo      = (TipoEspacio) cmbTipo.getSelectedItem();

        EspacioDeportivo espacio = new EspacioDeportivo(
            codigo, nombre, capacidad, precio, EstadoEspacio.DISPONIBLE, tipo, superficie
        );
        ComplejoDeportivoController.getInstance().agregarEspacioAComplejo(codigoComplejo, espacio);

        UI.info(this, "Espacio \"" + nombre + "\" agregado a "
                + complejos.get(cmbComplejo.getSelectedIndex()).getNombre() + ".",
                Textos.Titulo.EXITO);
        dispose();
    }
}
