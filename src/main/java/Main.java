import dataAccess.OrderDAO;
import dataAccess.ProductDAO;
import model.Order;
import model.Product;
import presentation.MainMenuUI;
import presentation.OrderUI;
import presentation.ProductUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainMenuUI();
            }
        });
    }
}