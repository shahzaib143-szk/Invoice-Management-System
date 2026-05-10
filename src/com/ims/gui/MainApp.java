package com.ims.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
            MainApp.class.getResource(
                "/com/ims/gui/view/login.fxml"
            )
        );

        Scene scene = new Scene(loader.load());

        stage.setTitle("Invoice Management System");

        stage.setScene(scene);

        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}