package com.example.pointeuseapp.controller;


import com.example.pointeuseapp.model.*;
import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPoint;
import java.util.List;
import com.example.pointeuseapp.utils.TimeUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.util.UUID;

public class ViewController {
    @FXML private Label dateLabel;
    @FXML private Label actualTimeLabel;
    @FXML private Label roundedTimeLabel;
    @FXML private ComboBox<EmployeeDTO> employeeComboBox;
    @FXML private RadioButton radioCheckIn;

    private CheckPointController logicController;

    @FXML
    public void initialize() {
        NetworkClient network = new NetworkClient("localhost", 8080);
        PendingCheckPointStore store = new PendingCheckPointStore();
        store.load();
        this.logicController = new CheckPointController(network, store);

        List<EmployeeDTO> listeEmployes = network.getEmployees();

        if (listeEmployes != null && !listeEmployes.isEmpty()) {
            employeeComboBox.getItems().addAll(listeEmployes);
            System.out.println("✅ " + listeEmployes.size() + " employés chargés dans l'interface !");
        } else {
            System.err.println("⚠️ Serveur injoignable ou liste vide.");
        }

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> updateTime()),
                new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        Timeline autoSyncTimer = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
            int synced = logicController.resendPending();
            if (synced > 0) {
                // ✅ On utilise .show() et pas .showAndWait() pour ne pas bloquer l'écran
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Reconnexion automatique : " + synced + " pointages envoyés en arrière-plan !");
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
        roundedTimeLabel.setText("let's say " + rounded.format(TimeUtils.TIME_FORMATTER));
    }

    @FXML
    protected void onCheckButtonClick() {
        EmployeeDTO selected = employeeComboBox.getValue();

        if (selected != null) {
            boolean isCheckIn = radioCheckIn.isSelected();
            String typeAction = isCheckIn ? "Entrée" : "Sortie";

            LocalDateTime roundedTime = TimeUtils.roundToNearestQuarter(LocalDateTime.now());
            CheckPoint cp = new CheckPoint(selected.getId(), roundedTime, isCheckIn);

            // 🚀 LA CORRECTION EST ICI : On utilise ton contrôleur intelligent (qui gère le hors-ligne)
            // au lieu de recréer un NetworkClient direct !
            boolean success = logicController.check(cp);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "✅ Pointage d'" + typeAction + " enregistré et envoyé pour " + selected.toString());
                alert.showAndWait();
            } else {
                // Voici la fameuse alerte jaune !
                Alert alert = new Alert(Alert.AlertType.WARNING,
                        "⚠️ Serveur injoignable. Le pointage a été sauvegardé localement !");
                alert.showAndWait();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un employé avant de pointer.");
            alert.showAndWait();
        }
    }

}