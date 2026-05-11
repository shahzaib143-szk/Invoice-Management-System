
package com.ims.gui.controller;

import com.ims.model.Invoice;
import com.ims.model.User;
import com.ims.service.AuthService;
import com.ims.service.InvoiceService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.stream.Collectors;

public class UserDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TableView<Invoice> invoiceTable;

    @FXML
    private TableColumn<Invoice, Integer> idColumn;

    @FXML
    private TableColumn<Invoice, String> dateColumn;

    @FXML
    private TableColumn<Invoice, Double> totalColumn;

    @FXML
    private TableColumn<Invoice, String> statusColumn;

    private final InvoiceService invoiceService
            = new InvoiceService();

    @FXML
    private void initialize() {

        User user = AuthService.getCurrentUser();

        if (user == null) {
            return;
        }

        welcomeLabel.setText(
                "Welcome, "
                        + user.getUsername()
        );

        setupTable();

        loadMyInvoices(
                user.getCustomerId()
        );
    }

    private void setupTable() {

        idColumn.setCellValueFactory(
                new PropertyValueFactory<>("invoiceId")
        );

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("invoiceDate")
        );

        totalColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalAmount")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );
    }

    private void loadMyInvoices(int customerId) {

        List<Invoice> invoices = invoiceService
                .getAllInvoices()
                .stream()
                .filter(inv ->
                        inv.getCustomer()
                                .getCustomerId()
                                == customerId
                )
                .collect(Collectors.toList());

        invoiceTable.setItems(
                FXCollections.observableArrayList(invoices)
        );
    }

    @FXML
    private void logout() {

        new AuthService().logout();

        System.out.println("Logged out.");
    }
}
