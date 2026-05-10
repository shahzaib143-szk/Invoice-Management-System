
package com.ims.gui.controller;

import com.ims.gui.util.SceneNavigator;
import com.ims.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class AdminDashboardController {
    @FXML
    private void initialize() {
        if (!AuthService.isAdmin()) {
            System.out.println(
                    "ACCESS DENIED"
            );
        }
    }
    
    @FXML
    private void openCustomers(ActionEvent event) {
        SceneNavigator.switchScene(
                getStage(event),
                "/com/ims/gui/view/customer-form.fxml",
                "Customers"
        );
    }

    @FXML
    private void openProducts(ActionEvent event) {
        SceneNavigator.switchScene(
                getStage(event),
                "/com/ims/gui/view/product-table.fxml",
                "Products"
        );
    }

    @FXML
    private void openInvoices(ActionEvent event) {
        SceneNavigator.switchScene(
                getStage(event),
                "/com/ims/gui/view/invoice-form.fxml",
                "Invoices"
        );
    }

    @FXML
    private void logout(ActionEvent event) {
        new AuthService().logout();
        SceneNavigator.switchScene(
                getStage(event),
                "/com/ims/gui/view/login.fxml",
                "Login"
        );
    }

    private Stage getStage(ActionEvent event) {
        return (Stage)
                ((Node) event.getSource())
                        .getScene()
                        .getWindow();
    }
}