package views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Reservas Deportivas");
        setSize(420, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        Dimension btnSize = new Dimension(240, 33);

        centerPanel.add(Box.createVerticalGlue());

        centerPanel.add(seccion("ALTAS"));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Registrar Cliente",    btnSize, e -> new RegistrarClienteDialog(this).setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Registrar Complejo",   btnSize, e -> new RegistrarComplejoDialog(this).setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Agregar Espacio",      btnSize, e -> new AgregarEspacioDialog(this).setVisible(true)));

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(seccion("RESERVAS"));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Solicitar Reserva",    btnSize, e -> new SolicitarReservaDialog(this).setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Confirmar con Seña",   btnSize, e -> new ConfirmarReservaDialog(this).setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Cancelar Reserva",     btnSize, e -> new CancelarReservaDialog(this).setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Iniciar / Finalizar",  btnSize, e -> new GestionarReservaDialog(this).setVisible(true)));

        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(seccion("CONSULTAS"));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Consultar Espacios",   btnSize, e -> new ConsultarEspaciosFrame().setVisible(true)));
        centerPanel.add(Box.createVerticalStrut(4));
        centerPanel.add(boton("Consultas del Sistema",btnSize, e -> new ConsultasFrame().setVisible(true)));

        centerPanel.add(Box.createVerticalStrut(14));
        centerPanel.add(boton("Salir",                btnSize, e -> System.exit(0)));
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton boton(String texto, Dimension size, java.awt.event.ActionListener action) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(size);
        btn.setAlignmentX(CENTER_ALIGNMENT);
        btn.addActionListener(action);
        return btn;
    }

    private JLabel seccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 11f));
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        return lbl;
    }
}
