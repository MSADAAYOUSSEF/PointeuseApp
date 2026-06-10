package com.example.pointeuseapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principale de l'application cliente (Pointeuse).
 * <p>
 * Cette classe hérite de {@link Application} et sert de point d'entrée
 * pour le framework JavaFX. Elle est responsable du chargement de
 * l'interface graphique initiale et de la configuration de la fenêtre principale.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class MainApplication extends Application {

    /**
     * Point de départ de l'interface graphique JavaFX.
     * <p>
     * Cette méthode est appelée automatiquement lors du lancement de l'application.
     * Elle localise et charge le fichier {@code MainView.fxml}, configure la
     * scène principale avec des dimensions prédéfinies (400x500), définit le titre
     * de la fenêtre et l'affiche à l'écran.
     * </p>
     *
     * @param stage La fenêtre principale (Stage) fournie par la plateforme JavaFX.
     * @throws IOException Si le fichier FXML spécifié est introuvable ou illisible.
     */
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