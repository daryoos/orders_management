package dataAccess;

import model.Bill;
import model.Client;
import model.Order;
import model.Product;

public class OrderDAO extends AbstractDAO<Order> {
    public boolean add(Order order, Client client, Product product, int quantity) {
        if (product.getStoc() >= order.getQuantity()) {
            super.add(order);
            ProductDAO productDAO = new ProductDAO();
            product.setStoc(product.getStoc() - quantity);
            productDAO.edit(product, product);
            return true;
        }
        else {
            return false;
        }
    }
}
