package com.example.pointeuseapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        String fxmlPath = "/com/example/pointeuseapp/view/MainView.fxml";
        var resource = getClass().getResource(fxmlPath);

        if (resource == null) {

            System.err.println("ERREUR : Fichier FXML introuvable au chemin : " + fxmlPath);
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(resource);
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        stage.setTitle("Émulateur de suivi du temps");
        stage.setScene(scene);
        stage.show();
    }
}
