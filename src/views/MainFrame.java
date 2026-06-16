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
        setSize(400, 420);
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

        JButton btnSolicitarReserva   = new JButton("Solicitar Reserva");
        JButton btnConfirmarReserva   = new JButton("Confirmar Reserva con Seña");
        JButton btnCancelarReserva    = new JButton("Cancelar Reserva");
        JButton btnFinalizarReserva   = new JButton("Finalizar Reserva");
        JButton btnConsultarEspacios  = new JButton("Consultar Espacios Disponibles");
        JButton btnSalir              = new JButton("Salir");

        Dimension btnSize = new Dimension(250, 35);
        btnSolicitarReserva.setMaximumSize(btnSize);
        btnConfirmarReserva.setMaximumSize(btnSize);
        btnCancelarReserva.setMaximumSize(btnSize);
        btnFinalizarReserva.setMaximumSize(btnSize);
        btnConsultarEspacios.setMaximumSize(btnSize);
        btnSalir.setMaximumSize(btnSize);

        for (JButton btn : new JButton[]{btnSolicitarReserva, btnConfirmarReserva,
                btnCancelarReserva, btnFinalizarReserva, btnConsultarEspacios, btnSalir}) {
            btn.setAlignmentX(CENTER_ALIGNMENT);
        }

        btnSolicitarReserva.addActionListener(e -> new SolicitarReservaDialog(this).setVisible(true));
        btnConfirmarReserva.addActionListener(e -> new ConfirmarReservaDialog(this).setVisible(true));
        btnCancelarReserva.addActionListener(e -> new CancelarReservaDialog(this).setVisible(true));
        btnFinalizarReserva.addActionListener(e -> new FinalizarReservaDialog(this).setVisible(true));
        btnConsultarEspacios.addActionListener(e -> new ConsultarEspaciosFrame().setVisible(true));
        btnSalir.addActionListener(e -> System.exit(0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnSolicitarReserva);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnConfirmarReserva);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnCancelarReserva);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnFinalizarReserva);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnConsultarEspacios);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(btnSalir);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }
}
