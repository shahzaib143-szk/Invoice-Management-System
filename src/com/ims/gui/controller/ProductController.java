package com.ims.gui.controller;

import com.ims.model.Product;
import com.ims.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductController {

    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField priceField;
    @FXML private TextField stockField;
    @FXML private TextField searchField;

    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> idColumn;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> descriptionColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;

    private final ProductService productService = new ProductService();
    private final ObservableList<Product> productData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stockQty"));

        loadProducts();
    }

    @FXML
    private void saveProduct() {
        try {
            String name = nameField.getText().trim();
            String desc = descriptionField.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            boolean success = productService.addProduct(name, desc, price, stock);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Product added.");
                clearFields();
                loadProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Could not add product.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price and stock must be numbers.");
        }
    }

    @FXML
    private void updatePrice() {
        Product selected = productTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Select a product first.");
            return;
        }

        try {
            double newPrice = Double.parseDouble(priceField.getText().trim());
            boolean success = productService.updatePrice(selected.getProductId(), newPrice);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Price updated.");
                loadProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Could not update price.");
            }

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Enter a valid price.");
        }
    }

    @FXML
    private void searchProducts() {
        String keyword = searchField.getText() == null ? "" : searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }

        productData.setAll(productService.searchByName(keyword));
        productTable.setItems(productData);
    }

    @FXML
    private void fillFormFromSelection() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        nameField.setText(selected.getName());
        descriptionField.setText(selected.getDescription());
        priceField.setText(String.valueOf(selected.getPrice()));
        stockField.setText(String.valueOf(selected.getStockQty()));
    }

    @FXML
    private void refreshProducts() {
        searchField.clear();
        loadProducts();
    }

    private void loadProducts() {
        productData.setAll(productService.getAllProducts());
        productTable.setItems(productData);
    }

    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        stockField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
