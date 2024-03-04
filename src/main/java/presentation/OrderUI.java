package presentation;

import dataAccess.BillDAO;
import dataAccess.OrderDAO;
import dataAccess.ProductDAO;
import model.Bill;
import model.Order;
import model.Product;

import java.util.List;

public class OrderUI extends AbstractUI<Order> {
    OrderDAO orderDAO;

    public OrderUI(List<Order> dataList, Class<Order> orderClass) {
        super(dataList, orderClass);
        orderDAO = new OrderDAO();
    }

    @Override
    public boolean add(Order order) {
        ProductDAO productDAO = new ProductDAO();
        Product product = new Product();
        BillDAO billDAO = new BillDAO();
        Bill bill = new Bill();

        product.setId(order.getProductId());
        product = productDAO.extract(product).get(0);
        if (product.getStoc() >= order.getQuantity()) {
            orderDAO.add(order);
            bill.setOrderId(order.getId());
            bill.setClientId(order.getClientId());
            bill.setProductId(order.getProductId());
            billDAO.add(bill);
            product.setStoc(product.getStoc() - order.getQuantity());
            productDAO.edit(product, product);
            return true;
        }
        else {
            PopupWindow popup = new PopupWindow();
            popup.showMessage("Insufficient stock");
            return false;
        }
    }
    @Override
    public void edit(Order order, Order newOrder) {
        orderDAO.edit(order, newOrder);
    }
    @Override
    public void delete(Order order) {
        ProductDAO productDAO = new ProductDAO();
        Product product = new Product();

        product.setId(order.getProductId());
        product = productDAO.extract(product).get(0);

        product.setStoc(product.getStoc() + order.getQuantity());
        productDAO.edit(product, product);
        orderDAO.delete(order);
    }
}
