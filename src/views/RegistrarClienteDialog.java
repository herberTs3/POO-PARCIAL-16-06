package views;

import java.awt.Frame;
import java.awt.GridBagConstraints;

import javax.swing.JDialog;
import javax.swing.JTextField;

import controllers.ClienteController;

public class RegistrarClienteDialog extends JDialog {

    private JTextField txtDni;
    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtTelefono;
    private JTextField txtEmail;

    public RegistrarClienteDialog(Frame parent) {
        super(parent, Textos.Titulo.REGISTRAR_CLIENTE, true);
        setSize(380, 320);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        var panel = UI.formPanel();
        GridBagConstraints gbc = UI.gbc(6);

        txtDni      = UI.field(15);
        txtNombre   = UI.field(15);
        txtApellido = UI.field(15);
        txtTelefono = UI.field(15);
        txtEmail    = UI.field(15);

        String[] labels = {Textos.Lbl.DNI, Textos.Lbl.NOMBRE, Textos.Lbl.APELLIDO,
                           Textos.Lbl.TELEFONO, Textos.Lbl.EMAIL};
        JTextField[] fields = {txtDni, txtNombre, txtApellido, txtTelefono, txtEmail};

        for (int i = 0; i < labels.length; i++) {
            UI.addFila(panel, gbc, i, labels[i], fields[i]);
        }

        var btnRegistrar = UI.button(Textos.Btn.REGISTRAR);
        UI.addButton(panel, gbc, labels.length, btnRegistrar);
        btnRegistrar.addActionListener(e -> onRegistrar());
        add(panel);
    }

    private void onRegistrar() {
        String dni      = txtDni.getText().trim();
        String nombre   = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String email    = txtEmail.getText().trim();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty()
                || telefono.isEmpty() || email.isEmpty()) {
            UI.errorValidacion(this, Textos.Msg.CAMPOS_OBLIGATORIOS);
            return;
        }

        String error = Validador.primerError(
            Validador.dni(dni),
            Validador.soloLetras("Nombre", nombre),
            Validador.soloLetras("Apellido", apellido),
            Validador.telefono(telefono),
            Validador.email(email)
        );
        if (error != null) { UI.errorValidacion(this, error); return; }

        if (ClienteController.getInstance().buscarPorDni(dni) != null) {
            UI.error(this, Textos.Msg.DNI_DUPLICADO);
            return;
        }

        ClienteController.getInstance().registrarCliente(dni, nombre, apellido, telefono, email);
        UI.info(this, "Cliente registrado correctamente.", Textos.Titulo.EXITO);
        dispose();
    }
}
