package com.ims.gui.controller;

import com.ims.model.Customer;
import com.ims.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class CustomerController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> addressColumn;

    private final CustomerService customerService = new CustomerService();
    private final ObservableList<Customer> customerData = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        loadCustomers();
    }

    @FXML
    private void saveCustomer() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        boolean success = customerService.addCustomer(name, email, phone, address);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully.");
            clearFields();
            loadCustomers();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed", "Could not add customer. Check inputs.");
        }
    }

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

    @FXML
    private void fillFormFromSelection() {
        Customer selected = customerTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        nameField.setText(selected.getName());
        emailField.setText(selected.getEmail());
        phoneField.setText(selected.getPhone());
        addressField.setText(selected.getAddress());
    }

    @FXML
    private void refreshCustomers() {
        loadCustomers();
    }

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
