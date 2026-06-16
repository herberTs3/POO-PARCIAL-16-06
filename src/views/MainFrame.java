package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;

public class MainFrame extends JFrame {

    private String usuarioSesion;

    public MainFrame() {
        setTitle(Textos.Titulo.MAIN);
        setSize(420, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        pedirUsuarioSesion();
        initComponents();
    }

    private void pedirUsuarioSesion() {
        var txtUsuario = UI.field(15, "admin");
        Object[] msg = {UI.label("Ingresá tu usuario para esta sesión:"), txtUsuario};
        while (true) {
            int opt = UI.confirm(null, msg, Textos.Titulo.LOGIN);
            if (opt != JOptionPane.OK_OPTION) System.exit(0);
            String valor = txtUsuario.getText().trim();
            if (!valor.isEmpty()) { usuarioSesion = valor; return; }
            UI.error(null, Textos.Msg.USUARIO_VACIO);
        }
    }

    private void initComponents() {
        var header = new java.awt.Container();
        var lblUsuario = UI.label("Usuario: " + usuarioSesion);
        lblUsuario.setFont(lblUsuario.getFont().deriveFont(Font.ITALIC, 11f));
        header.add(lblUsuario);
        add(header, BorderLayout.NORTH);

        var centerPanel = UI.verticalBoxPanel();
        Dimension btnSize = new Dimension(240, 33);

        centerPanel.add(UI.verticalGlue());

        addSeccion(centerPanel, "ALTAS");
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Registrar Cliente",     btnSize, e -> new RegistrarClienteDialog(this).setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Registrar Complejo",    btnSize, e -> new RegistrarComplejoDialog(this).setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Agregar Espacio",       btnSize, e -> new AgregarEspacioDialog(this).setVisible(true));

        centerPanel.add(UI.verticalStrut(10));
        addSeccion(centerPanel, "RESERVAS");
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Solicitar Reserva",     btnSize, e -> new SolicitarReservaDialog(this, usuarioSesion).setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Confirmar con Seña",    btnSize, e -> new ConfirmarReservaDialog(this, usuarioSesion).setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, Textos.Btn.CANCELAR,     btnSize, e -> new CancelarReservaDialog(this, usuarioSesion).setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Iniciar / Finalizar",   btnSize, e -> new GestionarReservaDialog(this, usuarioSesion).setVisible(true));

        centerPanel.add(UI.verticalStrut(10));
        addSeccion(centerPanel, "CONSULTAS");
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Consultar Espacios",    btnSize, e -> new ConsultarEspaciosFrame().setVisible(true));
        centerPanel.add(UI.verticalStrut(4));
        addBoton(centerPanel, "Consultas del Sistema", btnSize, e -> new ConsultasFrame().setVisible(true));

        centerPanel.add(UI.verticalStrut(14));
        addBoton(centerPanel, "Salir",                 btnSize, e -> System.exit(0));
        centerPanel.add(UI.verticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    private void addBoton(java.awt.Container panel, String texto, Dimension size, ActionListener action) {
        var btn = UI.button(texto);
        btn.setMaximumSize(size);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.addActionListener(action);
        panel.add(btn);
    }

    private void addSeccion(java.awt.Container panel, String texto) {
        var lbl = UI.label(texto);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 11f));
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(lbl);
    }
}
