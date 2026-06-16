import javax.swing.SwingUtilities;
import views.MainFrame;

public class Main {

    public static void main(String[] args) {
        DataLoader.cargar();
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
