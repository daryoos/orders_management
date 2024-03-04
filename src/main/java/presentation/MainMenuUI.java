package presentation;

import dataAccess.ClientDAO;
import dataAccess.OrderDAO;
import dataAccess.ProductDAO;
import model.Client;
import model.Order;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainMenuUI extends JFrame {
    public MainMenuUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JButton clientButton = new JButton("Client");
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openClientUI();
                dispose(); // Close the main menu
            }
        });

        JButton productButton = new JButton("Product");
        productButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProductUI();
                dispose(); // Close the main menu
            }
        });

        JButton orderButton = new JButton("Order");
        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openOrderUI();
                dispose(); // Close the main menu
            }
        });

        add(clientButton);
        add(productButton);
        add(orderButton);

        setVisible(true);
    }

    private void openClientUI() {
        ClientDAO clientDAO = new ClientDAO();
        List<Client> clientList = clientDAO.extractAll();
        ClientUI clientUI = new ClientUI(clientList, Client.class);
        clientUI.setVisible(true);
    }

    private void openProductUI() {
        ProductDAO productDAO = new ProductDAO();
        List<Product> productList = productDAO.extractAll();
        ProductUI productUI = new ProductUI(productList, Product.class);
        productUI.setVisible(true);
    }

    private void openOrderUI() {
        OrderDAO orderDAO = new OrderDAO();
        List<Order> orderList = orderDAO.extractAll();
        OrderUI orderUI = new OrderUI(orderList, Order.class);
        orderUI.setVisible(true);
    }
}