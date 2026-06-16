package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JDialog;
import javax.swing.JTextField;

import controllers.ComplejoDeportivoController;

public class RegistrarComplejoDialog extends JDialog {

    private JTextField txtCodigo;
    private JTextField txtNombre;
    private JTextField txtDireccion;
    private JTextField txtTelefono;
    private JTextField txtEmail;

    public RegistrarComplejoDialog(Frame parent) {
        super(parent, Textos.Titulo.REGISTRAR_COMPLEJO, true);
        setSize(400, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        txtCodigo    = UI.field(15);
        txtNombre    = UI.field(15);
        txtDireccion = UI.field(15);
        txtTelefono  = UI.field(15);
        txtEmail     = UI.field(15);

        String[] labels = {Textos.Lbl.CODIGO, Textos.Lbl.NOMBRE, "Dirección:",
                           Textos.Lbl.TELEFONO, Textos.Lbl.EMAIL};
        JTextField[] fields = {txtCodigo, txtNombre, txtDireccion, txtTelefono, txtEmail};

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(panel, gbc, i, labels[i], fields[i]);
        }

        var btnRegistrar = UI.button(Textos.Btn.REGISTRAR_COMPLEJO);
        UI.addButton(panel, gbc, labels.length, btnRegistrar);
        btnRegistrar.addActionListener(e -> onRegistrar());
        add(panel);
    }

    private void onRegistrar() {
        String codigo    = txtCodigo.getText().trim();
        String nombre    = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String telefono  = txtTelefono.getText().trim();
        String email     = txtEmail.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty() || direccion.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            UI.errorValidacion(this, Textos.Msg.CAMPOS_OBLIGATORIOS);
            return;
        }

        String error = Validador.primerError(
            Validador.codigo("Código", codigo),
            Validador.soloLetras("Nombre", nombre),
            Validador.telefono(telefono),
            Validador.email(email)
        );
        if (error != null) { UI.errorValidacion(this, error); return; }

        if (ComplejoDeportivoController.getInstance().buscarPorCodigo(codigo) != null) {
            UI.error(this, Textos.Msg.CODIGO_DUPLICADO);
            return;
        }

        ComplejoDeportivoController.getInstance().registrarComplejo(codigo, nombre, direccion, telefono, email);
        UI.info(this, "Complejo \"" + nombre + "\" registrado correctamente.", Textos.Titulo.EXITO);
        dispose();
    }
}
