// ============================================================
// LoginController.java
// PACKAGE: com.ims.gui.controller
// PURPOSE:
// Login screen event handling
// ============================================================

package com.ims.gui.controller;

import com.ims.gui.util.SceneNavigator;
import com.ims.model.User;
import com.ims.service.AuthService;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService
            = new AuthService();

    // ========================================================
    // LOGIN BUTTON
    // ========================================================
    @FXML
    private void handleLogin(ActionEvent event) {

        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = authService.login(username, password);

        if (user == null) {

            errorLabel.setText(
                    "Invalid username or password."
            );

            return;
        }

        Stage stage = (Stage)
                ((Node) event.getSource())
                        .getScene()
                        .getWindow();

        // ====================================================
        // ROLE-BASED ROUTING
        // ====================================================

        if (user.isAdmin()) {

            SceneNavigator.switchScene(
                    stage,
                    "/com/ims/gui/view/admin-dashboard.fxml",
                    "Admin Dashboard"
            );

        } else {

            SceneNavigator.switchScene(
                    stage,
                    "/com/ims/gui/view/user-dashboard.fxml",
                    "User Dashboard"
            );
        }
    }
}