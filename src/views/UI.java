package views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

public final class UI {

    private UI() {}

    public static JLabel label(String text) {
        return new JLabel(text);
    }

    public static JTextField field(int columns) {
        return new JTextField(columns);
    }

    public static JTextField field(int columns, String initialText) {
        JTextField f = new JTextField(columns);
        f.setText(initialText);
        return f;
    }

    public static JButton button(String text) {
        return new JButton(text);
    }

    public static JComboBox<String> combo() {
        return new JComboBox<>();
    }

    public static JTabbedPane tabs() {
        return new JTabbedPane();
    }

    public static JPanel formPanel() {
        return new JPanel(new GridBagLayout());
    }

    public static JPanel verticalBoxPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }

    public static Component verticalGlue() {
        return Box.createVerticalGlue();
    }

    public static Component verticalStrut(int height) {
        return Box.createVerticalStrut(height);
    }

    public static JPanel wrapNorth(JPanel inner) {
        JPanel outer = new JPanel(new BorderLayout());
        outer.add(inner, BorderLayout.NORTH);
        return outer;
    }

    public static GridBagConstraints gbc(int inset) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(inset, inset + 2, inset, inset + 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    public static void addFila(JPanel panel, GridBagConstraints gbc, int row,
                                String labelText, Component field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    public static void addButton(JPanel panel, GridBagConstraints gbc, int row, JButton btn) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btn, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
    }

    public static JScrollPane scrollPane(JTable table) {
        return new JScrollPane(table);
    }

    public static DefaultTableModel tableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                Textos.Titulo.ERROR, JOptionPane.ERROR_MESSAGE);
    }

    public static void errorValidacion(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                Textos.Titulo.ERROR_VALIDACION, JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warning(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static int confirm(Component parent, Object message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
