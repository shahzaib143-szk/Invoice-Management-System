package com.ims.gui.controller;

import com.ims.gui.util.SceneNavigator;
import com.ims.model.User;
import com.ims.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        User user = AuthService.login(username, password);

        if (user == null) {
            errorLabel.setText("Invalid username or password.");
            passwordField.clear();
            return;
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        String path = AuthService.getDashboardPath();
        String title = AuthService.isAdmin() ? "Admin Dashboard" : "User Dashboard";

        SceneNavigator.switchScene(stage, path, title);
    }
}