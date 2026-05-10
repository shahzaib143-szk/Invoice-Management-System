package com.ims.gui.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * SceneNavigator — Screen Switcher
 */
public final class SceneNavigator {

    private SceneNavigator() {}

    public static void switchScene(
            Stage stage,
            String fxmlPath,
            String title
    ) {

        try {

            Parent root = FXMLLoader.load(
                SceneNavigator.class.getResource(fxmlPath)
            );

            Scene scene = new Scene(root, 900, 650);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (Exception e) {

            System.err.println(
                "[SceneNavigator] Failed to load: " + fxmlPath
            );

            e.printStackTrace();
        }
    }
}