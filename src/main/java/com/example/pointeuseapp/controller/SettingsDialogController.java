package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsDialogController {

    @FXML private TextField ipField;
    @FXML private TextField portField;

    private ConfigManager config;

    @FXML
    public void initialize() {
        this.config = new ConfigManager();

        ipField.setText(config.getServerIp());
        portField.setText(String.valueOf(config.getServerPort()));
    }

    @FXML
    protected void handleSave() {
        try {
            String newIp = ipField.getText().trim();
            int newPort = Integer.parseInt(portField.getText().trim());

            config.setServerIp(newIp);
            config.setServerPort(newPort);
            config.saveConfig();

            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "✅ Paramètres sauvegardés !\nVeuillez relancer la pointeuse pour appliquer les changements.");
            alert.showAndWait();

            closeWindow();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "❌ Le port doit être un nombre valide (ex: 8080) !");
            alert.showAndWait();
        }
    }

    @FXML
    protected void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }
}