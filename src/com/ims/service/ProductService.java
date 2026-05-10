package com.ims.service;

import com.ims.dao.ProductDAO;
import com.ims.model.Product;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final ProductDAO productDAO = new ProductDAO();

    public boolean addProduct(String name, String description, double price, int stockQty) {
        if (name == null || name.trim().isEmpty()) {
            System.err.println("[ProductService] Product name cannot be empty.");
            return false;
        }
        if (price < 0) {
            System.err.println("[ProductService] Price cannot be negative.");
            return false;
        }
        if (stockQty < 0) {
            System.err.println("[ProductService] Stock cannot be negative.");
            return false;
        }

        Product p = new Product(name.trim(), description, price, stockQty);
        return productDAO.insertProduct(p);
    }

    public boolean isInStock(int productId, int requiredQty) {
        int available = productDAO.checkStock(productId);
        return available >= requiredQty;
    }

    public boolean updatePrice(int productId, double newPrice) {
        if (newPrice < 0) {
            System.err.println("[ProductService] Price cannot be negative.");
            return false;
        }
        return productDAO.updatePrice(productId, newPrice);
    }

    public boolean reduceStock(int productId, int qty) {
        int current = productDAO.checkStock(productId);
        if (current < qty) {
            System.err.println("[ProductService] Insufficient stock.");
            return false;
        }
        return productDAO.updateStock(productId, current - qty);
    }

    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }

    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    public List<Product> searchByName(String keyword) {
        return productDAO.getAllProducts().stream()
            .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
    }
}
