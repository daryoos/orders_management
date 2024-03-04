package presentation;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class PopupWindow {
    private JFrame frame;

    public PopupWindow() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void showMessage(String message) {
        // Display the pop-up window
        JOptionPane.showMessageDialog(frame, message, "Alert", JOptionPane.WARNING_MESSAGE);
    }
}

