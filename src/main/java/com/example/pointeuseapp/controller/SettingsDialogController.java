package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.utils.ConfigManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Contrôleur de la boîte de dialogue des paramètres réseau (Configuration).
 * <p>
 * Cette classe gère l'interface permettant à l'utilisateur de configurer
 * l'adresse IP et le port du serveur central. Elle s'appuie sur le
 * {@link ConfigManager} pour lire et sauvegarder ces informations de
 * manière persistante sur le poste local de la pointeuse.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class SettingsDialogController {

    /** Champ de saisie pour l'adresse IP du serveur. */
    @FXML private TextField ipField;

    /** Champ de saisie pour le port de communication du serveur. */
    @FXML private TextField portField;

    /** Gestionnaire utilitaire pour la lecture et l'écriture du fichier de configuration. */
    private ConfigManager config;

    /**
     * Initialise la boîte de dialogue au moment de son ouverture.
     * <p>
     * Cette méthode est appelée automatiquement par le framework JavaFX.
     * Elle instancie le gestionnaire de configuration et pré-remplit les champs
     * de saisie avec les valeurs de connexion actuellement sauvegardées.
     * </p>
     */
    @FXML
    public void initialize() {
        this.config = new ConfigManager();

        ipField.setText(config.getServerIp());
        portField.setText(String.valueOf(config.getServerPort()));
    }

    /**
     * Traite l'action de sauvegarde déclenchée par le bouton "ENREGISTRER".
     * <p>
     * La méthode récupère les valeurs saisies par l'utilisateur. Elle vérifie
     * que le port est bien un entier (gestion de l'exception {@link NumberFormatException}),
     * puis sauvegarde la nouvelle configuration. Une alerte d'information est
     * ensuite affichée pour confirmer l'action.
     * </p>
     */
    @FXML
    protected void handleSave() {
        try {
            String newIp = ipField.getText().trim();
            int newPort = Integer.parseInt(portField.getText().trim());

            config.setServerIp(newIp);
            config.setServerPort(newPort);
            config.saveConfig();

            // Le message est mis à jour pour refléter l'application immédiate
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Paramètres sauvegardés avec succès !\nLa nouvelle adresse sera utilisée au prochain pointage.");
            alert.showAndWait();

            closeWindow();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Le port doit être un nombre valide (ex: 8080) !");
            alert.showAndWait();
        }
    }
    /**
     * Annule la saisie en cours et ferme la fenêtre de configuration sans sauvegarder.
     */
    @FXML
    protected void handleCancel() {
        closeWindow();
    }

    /**
     * Méthode utilitaire interne pour fermer proprement la fenêtre (Stage) actuelle.
     */
    private void closeWindow() {
        Stage stage = (Stage) ipField.getScene().getWindow();
        stage.close();
    }
}