package presentation;

import dataAccess.ClientDAO;
import dataAccess.ProductDAO;
import model.Client;
import model.Product;

import java.util.List;

public class ProductUI extends AbstractUI<Product> {
    ProductDAO productDAO;

    public ProductUI(List<Product> dataList, Class<Product> productClass) {
        super(dataList, productClass);
        productDAO = new ProductDAO();
    }

    @Override
    public boolean add(Product product) {
        productDAO.add(product);
        return true;
    }
    @Override
    public void edit(Product product, Product newProduct) {
        productDAO.edit(product, newProduct);
    }
    @Override
    public void delete(Product product) {
        productDAO.delete(product);
    }

}
