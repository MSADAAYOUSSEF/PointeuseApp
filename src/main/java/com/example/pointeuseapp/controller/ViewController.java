package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.model.*;
import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPointDTO;
import com.example.pointeuseapp.utils.ConfigManager;
import com.example.pointeuseapp.utils.TimeUtils;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

/**
 * Contrôleur principal de l'interface graphique de la Pointeuse (Client).
 * <p>
 * Cette classe agit comme le chef d'orchestre de l'application cliente.
 * Elle gère l'affichage en temps réel de l'horloge, l'interaction avec
 * l'utilisateur (sélection de l'employé, bouton de pointage) et lance
 * les processus d'arrière-plan comme la synchronisation automatique des
 * pointages en mode hors-ligne.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class ViewController {

    @FXML private Label dateLabel;
    @FXML private Label actualTimeLabel;
    @FXML private Label roundedTimeLabel;
    @FXML private ComboBox<EmployeeDTO> employeeComboBox;
    @FXML private RadioButton radioCheckIn;

    /** Contrôleur logique métier gérant l'envoi et la sauvegarde locale des pointages. */
    private CheckPointController logicController;

    /** Gestionnaire de configuration pour récupérer l'IP et le port du serveur. */
    private ConfigManager config;

    /**
     * Initialise l'interface au démarrage de l'application.
     * <p>
     * Cette méthode est appelée automatiquement par JavaFX. Elle effectue les tâches suivantes :
     * <ul>
     * <li>Chargement de la configuration réseau.</li>
     * <li>Initialisation des gestionnaires réseau et de stockage local.</li>
     * <li>Récupération de la liste des employés depuis le serveur pour peupler la liste déroulante.</li>
     * <li>Lancement de l'horloge interne (mise à jour chaque seconde).</li>
     * <li>Lancement d'un thread en arrière-plan (Timeline) essayant de renvoyer
     * les pointages stockés localement toutes les 15 secondes.</li>
     * </ul>
     */
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

    /**
     * Met à jour les labels de date et d'heure sur l'interface graphique.
     * <p>
     * Calcule également l'heure arrondie (au quart d'heure le plus proche)
     * en utilisant l'utilitaire {@link TimeUtils} et l'affiche à l'écran.
     * </p>
     */
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText(now.format(TimeUtils.DATE_FORMATTER));
        actualTimeLabel.setText(now.format(TimeUtils.TIME_FORMATTER));
        LocalDateTime rounded = TimeUtils.roundToNearestQuarter(now);
        roundedTimeLabel.setText(rounded.format(TimeUtils.TIME_FORMATTER));
    }

    /**
     * Déclenchée lors du clic sur le bouton "VALIDER" par l'employé.
     * <p>
     * Vérifie qu'un employé est bien sélectionné, crée un nouvel objet
     * {@link CheckPointDTO} avec l'heure arrondie actuelle, puis demande au
     * contrôleur logique de l'envoyer. Affiche ensuite une notification
     * de succès, ou un avertissement si le pointage a dû être stocké localement.
     * </p>
     */
    @FXML
    protected void onCheckButtonClick() {
        EmployeeDTO selected = employeeComboBox.getValue();

        if (selected != null) {
            boolean isCheckIn = radioCheckIn.isSelected();
            String typeAction = isCheckIn ? "Entrée" : "Sortie";

            LocalDateTime roundedTime = TimeUtils.roundToNearestQuarter(LocalDateTime.now());
            CheckPointDTO cp = new CheckPointDTO(selected.getId(), roundedTime, isCheckIn);

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

    /**
     * Déclenchée lors du clic sur le bouton "Paramètres" (Configuration).
     * <p>
     * Ouvre une nouvelle fenêtre modale (qui bloque l'interaction avec la fenêtre
     * principale) permettant à l'utilisateur de configurer l'adresse IP et le port
     * du serveur central.
     * </p>
     */
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