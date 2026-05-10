package com.ims.gui.controller;

import com.ims.model.Customer;
import com.ims.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * CustomerController — Customer Screen Event Handler
 *
 * REPLACES: CustomerMenu class inside Menus.java
 *
 * OLD console code:
 *   System.out.print("Name: ");   String name = sc.nextLine();
 *   System.out.print("Email: ");  String email = sc.nextLine();
 *   boolean ok = svc.addCustomer(name, email, phone, address);
 *   System.out.println(ok ? "Customer added." : "Failed.");
 *
 * NEW: TextField inputs + Button click + Alert popup
 *
 * SERVICE BINDING:
 *   This controller calls CustomerService — same service your console menu called.
 *   CustomerService → CustomerDAO → MySQL
 *   Nothing in the backend changes.
 */
public class CustomerController {

    // ── FXML fields — connected to customer-form.fxml ─────────────────────────
    // These replace Scanner input lines

    @FXML private TextField nameField;      // replaces: sc.nextLine() for name
    @FXML private TextField emailField;     // replaces: sc.nextLine() for email
    @FXML private TextField phoneField;     // replaces: sc.nextLine() for phone
    @FXML private TextField addressField;   // replaces: sc.nextLine() for address

    // Table — replaces: list.forEach(System.out::println)
    @FXML private TableView<Customer>              customerTable;
    @FXML private TableColumn<Customer, Integer>   idColumn;
    @FXML private TableColumn<Customer, String>    nameColumn;
    @FXML private TableColumn<Customer, String>    emailColumn;
    @FXML private TableColumn<Customer, String>    phoneColumn;
    @FXML private TableColumn<Customer, String>    addressColumn;

    // ── Service binding — same service as console menu ────────────────────────
    private final CustomerService customerService = new CustomerService();
    private final ObservableList<Customer> customerData = FXCollections.observableArrayList();

    // ── initialize() — runs automatically when screen loads ───────────────────
    @FXML
    private void initialize() {
        // Wire table columns to Customer object fields
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadCustomers(); // load existing customers into table
    }

    // ── SAVE CUSTOMER button ───────────────────────────────────────────────────
    // REPLACES: addCustomer() method in CustomerMenu
    //
    // OLD: svc.addCustomer(name, email, phone, address)
    // NEW: same call — just reads from TextFields instead of Scanner
    @FXML
    private void saveCustomer() {
        String name    = nameField.getText().trim();
        String email   = emailField.getText().trim();
        String phone   = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // Service binding — same method your console menu called
        boolean success = customerService.addCustomer(name, email, phone, address);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully.");
            clearFields();
            loadCustomers(); // refresh table
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Could not add customer. Check inputs.");
        }
    }

    // ── UPDATE CUSTOMER button ─────────────────────────────────────────────────
    @FXML
    private void updateCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }

        selected.setName(nameField.getText().trim());
        selected.setEmail(emailField.getText().trim());
        selected.setPhone(phoneField.getText().trim());
        selected.setAddress(addressField.getText().trim());

        boolean success = customerService.updateCustomer(selected);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated.");
            clearFields();
            loadCustomers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Could not update customer.");
        }
    }

    // ── DELETE CUSTOMER button ─────────────────────────────────────────────────
    @FXML
    private void deleteCustomer() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to delete.");
            return;
        }

        boolean success = customerService.deleteCustomer(selected.getCustomerId());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Deleted", "Customer removed.");
            clearFields();
            loadCustomers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Could not delete customer.");
        }
    }

    // ── TABLE ROW CLICK — fill form with selected customer ────────────────────
    @FXML
    private void fillFormFromSelection() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        nameField.setText(selected.getName());
        emailField.setText(selected.getEmail());
        phoneField.setText(selected.getPhone());
        addressField.setText(selected.getAddress());
    }

    // ── REFRESH button ────────────────────────────────────────────────────────
    @FXML
    private void refreshCustomers() {
        loadCustomers();
    }

    // ── PRIVATE HELPERS ───────────────────────────────────────────────────────

    // Loads all customers from DB via service → populates table
    // REPLACES: viewAll() in CustomerMenu
    private void loadCustomers() {
        customerData.setAll(customerService.getAllCustomers());
        customerTable.setItems(customerData);
    }

    private void clearFields() {
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
