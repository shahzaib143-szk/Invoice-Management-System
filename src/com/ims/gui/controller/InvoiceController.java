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

/**
 * InvoiceController — Invoice Screen Event Handler
 *
 * REPLACES: InvoiceMenu class inside Menus.java
 *
 * OLD console code:
 *   System.out.print("Customer ID: ");
 *   int customerId = Integer.parseInt(sc.nextLine());
 *   Map<Integer, Integer> items = new LinkedHashMap<>();
 *   // loop adding products...
 *   Invoice inv = svc.createInvoice(customerId, items, tax, disc);
 *
 * NEW: ComboBox dropdowns + TableView cart + Button click
 *
 * SERVICE BINDING:
 *   Calls InvoiceService — same service your console menu called.
 *   InvoiceService → CustomerService / ProductService → DAO → MySQL
 */
public class InvoiceController {

    // ── FXML fields ───────────────────────────────────────────────────────────

    // Dropdowns — replace: System.out.print("Customer ID: ") + parseInt
    @FXML private ComboBox<Customer> customerComboBox;
    @FXML private ComboBox<Product>  productComboBox;

    // Inputs
    @FXML private TextField quantityField;
    @FXML private TextField taxField;
    @FXML private TextField discountField;

    // Cart table — shows products added before invoice is created
    @FXML private TableView<CartRow>              cartTable;
    @FXML private TableColumn<CartRow, Integer>   cartProductIdColumn;
    @FXML private TableColumn<CartRow, String>    cartProductNameColumn;
    @FXML private TableColumn<CartRow, Integer>   cartQuantityColumn;
    @FXML private TableColumn<CartRow, Double>    cartUnitPriceColumn;
    @FXML private TableColumn<CartRow, Double>    cartSubtotalColumn;

    // Total display — replaces: System.out.println("Total: " + total)
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    // ── Service binding ───────────────────────────────────────────────────────
    private final CustomerService customerService = new CustomerService();
    private final ProductService  productService  = new ProductService();
    private final InvoiceService  invoiceService  = new InvoiceService();

    private final ObservableList<CartRow> cartData = FXCollections.observableArrayList();

    // ── initialize() ──────────────────────────────────────────────────────────
    @FXML
    private void initialize() {
        // Wire cart table columns
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

    // ── ADD TO CART button ─────────────────────────────────────────────────────
    // REPLACES: the while loop in createInvoice() that collected product IDs
    //
    // OLD:
    //   System.out.print("Product ID (0 to stop): ");
    //   int pid = Integer.parseInt(sc.nextLine());
    //   System.out.print("Quantity: ");
    //   int qty = Integer.parseInt(sc.nextLine());
    //   items.put(pid, qty);
    //
    // NEW: Select from dropdown + enter quantity + click Add
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

            // If product already in cart → increase quantity
            for (CartRow row : cartData) {
                if (row.getProductId() == product.getProductId()) {
                    row.setQuantity(row.getQuantity() + qty);
                    cartTable.refresh();
                    updateTotalLabel();
                    quantityField.clear();
                    return;
                }
            }

            // New product → add new row
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

    // ── REMOVE FROM CART button ────────────────────────────────────────────────
    @FXML
    private void removeFromCart() {
        CartRow selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartData.remove(selected);
            updateTotalLabel();
        }
    }

    // ── CREATE INVOICE button ──────────────────────────────────────────────────
    // REPLACES: createInvoice() in InvoiceMenu
    //
    // OLD: svc.createInvoice(customerId, items, tax, disc)
    // NEW: same call — reads customer from ComboBox, items from cart
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
            double taxRate      = parseDoubleOrZero(taxField.getText());
            double discountRate = parseDoubleOrZero(discountField.getText());

            // Build items map — same structure InvoiceMenu built
            Map<Integer, Integer> items = new LinkedHashMap<>();
            for (CartRow row : cartData) {
                items.put(row.getProductId(), row.getQuantity());
            }

            // Service binding — EXACT same call as InvoiceMenu.createInvoice()
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

                // Clear cart after success
                cartData.clear();
                updateTotalLabel();
                loadProducts(); // refresh stock display

            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Invoice creation failed. Check stock and customer.");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong: " + e.getMessage());
        }
    }

    // ── VIEW ALL INVOICES button ───────────────────────────────────────────────
    // REPLACES: viewAll() in InvoiceMenu
    @FXML
    private void viewAllInvoices() {
        invoiceService.getAllInvoices().forEach(inv ->
            System.out.println(inv) // can be replaced with a separate list screen
        );
        showAlert(Alert.AlertType.INFORMATION, "Invoices",
            invoiceService.getAllInvoices().size() + " invoices found.\nSee console for details.");
    }

    // ── REFRESH button ─────────────────────────────────────────────────────────
    @FXML
    private void refreshData() {
        loadCustomers();
        loadProducts();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────

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

        double tax      = parseDoubleOrZero(taxField.getText());
        double discount = parseDoubleOrZero(discountField.getText());

        double afterDiscount = subtotal * (1 - discount);
        double total         = afterDiscount * (1 + tax);

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

    // ═════════════════════════════════════════════════════════════════════════
    // CartRow — inner class for the cart TableView
    // Represents one product line before invoice is finalized
    // ═════════════════════════════════════════════════════════════════════════

    public static class CartRow {

        private int    productId;
        private String productName;
        private int    quantity;
        private double unitPrice;
        private double subtotal;

        public CartRow(int productId, String productName, int quantity, double unitPrice) {
            this.productId   = productId;
            this.productName = productName;
            this.quantity    = quantity;
            this.unitPrice   = unitPrice;
            recalculate();
        }

        public void recalculate() {
            this.subtotal = this.unitPrice * this.quantity;
        }

        // Getters
        public int    getProductId()   { return productId; }
        public String getProductName() { return productName; }
        public int    getQuantity()    { return quantity; }
        public double getUnitPrice()   { return unitPrice; }
        public double getSubtotal()    { return subtotal; }

        // Setter with auto-recalculate
        public void setQuantity(int qty) {
            this.quantity = qty;
            recalculate();
        }
    }
}
