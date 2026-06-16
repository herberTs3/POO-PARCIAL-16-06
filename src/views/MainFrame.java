package views;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import controllers.ClienteController;
import controllers.ComplejoDeportivoController;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Reservas Deportivas");
        setSize(400, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        ClienteController.getInstance().cargarDatosDePrueba();
        ComplejoDeportivoController.getInstance().cargarDatosDePrueba();
        initComponents();
    }

    private void initComponents() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        Dimension btnSize = new Dimension(260, 33);

        JButton btnRegistrarCliente  = crearBoton("Registrar Cliente",         btnSize);
        JButton btnRegistrarComplejo = crearBoton("Registrar Complejo",        btnSize);
        JButton btnAgregarEspacio    = crearBoton("Agregar Espacio",           btnSize);
        JButton btnSolicitarReserva  = crearBoton("Solicitar Reserva",         btnSize);
        JButton btnConfirmarReserva  = crearBoton("Confirmar Reserva con Seña",btnSize);
        JButton btnCancelarReserva   = crearBoton("Cancelar Reserva",          btnSize);
        JButton btnFinalizarReserva  = crearBoton("Finalizar Reserva",         btnSize);
        JButton btnConsultarEspacios = crearBoton("Consultar Espacios",        btnSize);
        JButton btnSalir             = crearBoton("Salir",                     btnSize);

        btnRegistrarCliente.addActionListener(e  -> new RegistrarClienteDialog(this).setVisible(true));
        btnRegistrarComplejo.addActionListener(e -> new RegistrarComplejoDialog(this).setVisible(true));
        btnAgregarEspacio.addActionListener(e    -> new AgregarEspacioDialog(this).setVisible(true));
        btnSolicitarReserva.addActionListener(e  -> new SolicitarReservaDialog(this).setVisible(true));
        btnConfirmarReserva.addActionListener(e  -> new ConfirmarReservaDialog(this).setVisible(true));
        btnCancelarReserva.addActionListener(e   -> new CancelarReservaDialog(this).setVisible(true));
        btnFinalizarReserva.addActionListener(e  -> new FinalizarReservaDialog(this).setVisible(true));
        btnConsultarEspacios.addActionListener(e -> new ConsultarEspaciosFrame().setVisible(true));
        btnSalir.addActionListener(e             -> System.exit(0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnRegistrarCliente);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(btnRegistrarComplejo);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(btnAgregarEspacio);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(btnSolicitarReserva);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(btnConfirmarReserva);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(btnCancelarReserva);
        centerPanel.add(Box.createVerticalStrut(6));
        centerPanel.add(btnFinalizarReserva);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(btnConsultarEspacios);
        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(btnSalir);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto, Dimension size) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(size);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        return btn;
    }
}
