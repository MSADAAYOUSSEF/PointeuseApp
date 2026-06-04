package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.model.*;
import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPoint;
import com.example.pointeuseapp.utils.ConfigManager;
import com.example.pointeuseapp.utils.TimeUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class ViewController {

    @FXML private Label dateLabel;
    @FXML private Label actualTimeLabel;
    @FXML private Label roundedTimeLabel;
    @FXML private ComboBox<EmployeeDTO> employeeComboBox;
    @FXML private RadioButton radioCheckIn;

    private CheckPointController logicController;
    private ConfigManager config;

    @FXML
    public void initialize() {
        this.config = new ConfigManager();

        NetworkClient network = new NetworkClient(config.getServerIp(), config.getServerPort());
        PendingCheckPointStore store = new PendingCheckPointStore();
        store.load();

        this.logicController = new CheckPointController(network, store);
        List<EmployeeDTO> listeEmployes = network.getEmployees();

        if (listeEmployes != null && !listeEmployes.isEmpty()) {
            employeeComboBox.getItems().addAll(listeEmployes);
            System.out.println(listeEmployes.size() + " employés chargés dans l'interface !");
        } else {
            System.err.println("Serveur injoignable ou liste vide. Vérifiez l'IP et le Port dans les paramètres.");
        }

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> updateTime()),
                new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);

        Timeline autoSyncTimer = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
            int synced = logicController.resendPending();
            if (synced > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reconnexion automatique : " + synced + " pointages envoyés en arrière-plan !");
                alert.show();
            }
        }));
        autoSyncTimer.setCycleCount(Timeline.INDEFINITE);

        autoSyncTimer.play();
        clock.play();
    }

    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText(now.format(TimeUtils.DATE_FORMATTER));
        actualTimeLabel.setText(now.format(TimeUtils.TIME_FORMATTER));

        LocalDateTime rounded = TimeUtils.roundToNearestQuarter(now);
        roundedTimeLabel.setText(rounded.format(TimeUtils.TIME_FORMATTER));
    }

    @FXML
    protected void onCheckButtonClick() {
        EmployeeDTO selected = employeeComboBox.getValue();

        if (selected != null) {
            boolean isCheckIn = radioCheckIn.isSelected();
            String typeAction = isCheckIn ? "Entrée" : "Sortie";

            LocalDateTime roundedTime = TimeUtils.roundToNearestQuarter(LocalDateTime.now());
            CheckPoint cp = new CheckPoint(selected.getId(), roundedTime, isCheckIn);

            boolean success = logicController.check(cp);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Pointage d'" + typeAction + " enregistré et envoyé pour " + selected.toString());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "Serveur injoignable. Le pointage a été sauvegardé localement !");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un employé avant de pointer.");
            alert.showAndWait();
        }
    }

    @FXML
    protected void onSettingsButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/pointeuseapp/view/settings-dialog.fxml"));
            Parent parent = fxmlLoader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Paramètres Réseau");
            dialogStage.setScene(new Scene(parent));

            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture des paramètres : " + e.getMessage());
            e.printStackTrace();
        }
    }
}