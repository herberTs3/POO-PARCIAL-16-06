package views;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Reservas Deportivas");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JButton btnSolicitarReserva = new JButton("Solicitar Reserva");
        JButton btnConsultarEspacios = new JButton("Consultar Espacios");
        JButton btnSalir = new JButton("Salir");

        Dimension btnSize = new Dimension(200, 35);
        btnSolicitarReserva.setMaximumSize(btnSize);
        btnConsultarEspacios.setMaximumSize(btnSize);
        btnSalir.setMaximumSize(btnSize);

        btnSolicitarReserva.setAlignmentX(CENTER_ALIGNMENT);
        btnConsultarEspacios.setAlignmentX(CENTER_ALIGNMENT);
        btnSalir.setAlignmentX(CENTER_ALIGNMENT);

        btnSolicitarReserva.addActionListener(e -> new SolicitarReservaDialog(this).setVisible(true));
        btnConsultarEspacios.addActionListener(e -> new ConsultarEspaciosFrame().setVisible(true));
        btnSalir.addActionListener(e -> System.exit(0));

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(btnSolicitarReserva);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(btnConsultarEspacios);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(btnSalir);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }
}
