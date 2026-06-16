package views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import tests.FlujoReservaTest;

public class PruebasDialog extends JDialog {

    public PruebasDialog(JFrame parent) {
        super(parent, "Pruebas Unitarias", true);
        setSize(520, 260);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JTextArea txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(new Font("Monospaced", Font.PLAIN, 13));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(baos));
        try {
            FlujoReservaTest.main(null);
        } finally {
            System.setOut(original);
        }

        txtResultados.setText(baos.toString());
        add(new JScrollPane(txtResultados), BorderLayout.CENTER);
    }
}
