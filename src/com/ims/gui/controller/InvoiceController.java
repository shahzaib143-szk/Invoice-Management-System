package com.ims.gui.controller;

import com.ims.model.*;
import com.ims.service.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.LinkedHashMap;
import java.util.Map;

public class InvoiceController {

    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Product> productComboBox;

    @FXML private TextField quantityField;
    @FXML private TextField taxField;
    @FXML private TextField discountField;

    @FXML private TableView<CartRow> cartTable;
    @FXML private TableColumn<CartRow, Integer> cartProductIdColumn;
    @FXML private TableColumn<CartRow, String> cartProductNameColumn;
    @FXML private TableColumn<CartRow, Integer> cartQuantityColumn;
    @FXML private TableColumn<CartRow, Double> cartUnitPriceColumn;
    @FXML private TableColumn<CartRow, Double> cartSubtotalColumn;

    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    private final CustomerService customerService = new CustomerService();
    private final ProductService productService = new ProductService();
    private final InvoiceService invoiceService = new InvoiceService();

    private final ObservableList<CartRow> cartData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        cartProductIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        cartProductNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cartUnitPriceColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        cartSubtotalColumn.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        cartTable.setItems(cartData);

        loadCustomers();
        loadProducts();
        updateTotalLabel();
    }

    @FXML
    private void addToCart() {
        Product product = productComboBox.getValue();

        if (product == null) {
            showAlert(Alert.AlertType.WARNING, "No Product", "Please select a product.");
            return;
        }

        try {
            int qty = Integer.parseInt(quantityField.getText().trim());

            if (qty <= 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be positive.");
                return;
            }

            for (CartRow row : cartData) {
                if (row.getProductId() == product.getProductId()) {
                    row.setQuantity(row.getQuantity() + qty);
                    cartTable.refresh();
                    updateTotalLabel();
                    quantityField.clear();
                    return;
                }
            }

            cartData.add(new CartRow(
                product.getProductId(),
                product.getName(),
                qty,
                product.getPrice()
            ));

            quantityField.clear();
            updateTotalLabel();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid quantity.");
        }
    }

    @FXML
    private void removeFromCart() {
        CartRow selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartData.remove(selected);
            updateTotalLabel();
        }
    }

    @FXML
    private void createInvoice() {
        Customer customer = customerComboBox.getValue();

        if (customer == null) {
            showAlert(Alert.AlertType.WARNING, "No Customer", "Please select a customer.");
            return;
        }

        if (cartData.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Empty Cart", "Add at least one product.");
            return;
        }

        try {
            double taxRate = parseDoubleOrZero(taxField.getText());
            double discountRate = parseDoubleOrZero(discountField.getText());

            Map<Integer, Integer> items = new LinkedHashMap<>();
            for (CartRow row : cartData) {
                items.put(row.getProductId(), row.getQuantity());
            }

            Invoice invoice = invoiceService.createInvoice(
                customer.getCustomerId(),
                items,
                taxRate,
                discountRate
            );

            if (invoice != null) {
                showAlert(Alert.AlertType.INFORMATION, "Invoice Created",
                    "Invoice ID: " + invoice.getInvoiceId()
                    + "\nCustomer: " + invoice.getCustomer().getName()
                    + "\nTotal: " + invoice.getTotalAmount()
                    + "\nStatus: " + invoice.getStatus()
                );

                cartData.clear();
                updateTotalLabel();
                loadProducts();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Invoice creation failed. Check stock and customer.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong: " + e.getMessage());
        }
    }

    @FXML
    private void viewAllInvoices() {
        invoiceService.getAllInvoices().forEach(inv ->
            System.out.println(inv)
        );
        showAlert(Alert.AlertType.INFORMATION, "Invoices",
            invoiceService.getAllInvoices().size() + " invoices found.\nSee console for details.");
    }

    @FXML
    private void refreshData() {
        loadCustomers();
        loadProducts();
    }

    private void loadCustomers() {
        customerComboBox.setItems(
            FXCollections.observableArrayList(customerService.getAllCustomers())
        );
    }

    private void loadProducts() {
        productComboBox.setItems(
            FXCollections.observableArrayList(productService.getAllProducts())
        );
    }

    private void updateTotalLabel() {
        double subtotal = 0;
        for (CartRow row : cartData) {
            subtotal += row.getSubtotal();
        }

        double tax = parseDoubleOrZero(taxField.getText());
        double discount = parseDoubleOrZero(discountField.getText());

        double afterDiscount = subtotal * (1 - discount);
        double total = afterDiscount * (1 + tax);

        totalLabel.setText(String.format("Total: %.2f", total));
    }

    private double parseDoubleOrZero(String value) {
        try {
            return (value == null || value.trim().isEmpty())
                ? 0.0
                : Double.parseDouble(value.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class CartRow {

        private int productId;
        private String productName;
        private int quantity;
        private double unitPrice;
        private double subtotal;

        public CartRow(int productId, String productName, int quantity, double unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            recalculate();
        }

        public void recalculate() {
            this.subtotal = this.unitPrice * this.quantity;
        }

        public int getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getSubtotal() {
            return subtotal;
        }

        public void setQuantity(int qty) {
            this.quantity = qty;
            recalculate();
        }
    }
}
