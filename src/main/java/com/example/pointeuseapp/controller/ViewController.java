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
 * Cette classe agit comme le chef d'orchestre de l'application cliente JavaFX.
 * Elle gère l'affichage de l'horloge en temps réel, l'interaction avec l'utilisateur
 * (sélection de l'employé, validation de pointage), et orchestre les processus
 * d'arrière-plan tels que la resynchronisation automatique des pointages mis en cache
 * lors d'une perte de connexion.
 * </p>
 *
 * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class ViewController {

    /** Label affichant la date du jour. */
    @FXML private Label dateLabel;

    /** Label affichant l'heure exacte en temps réel. */
    @FXML private Label actualTimeLabel;

    /** Label affichant l'heure arrondie au quart d'heure le plus proche. */
    @FXML private Label roundedTimeLabel;

    /** Liste déroulante contenant les employés récupérés depuis le serveur. */
    @FXML private ComboBox<EmployeeDTO> employeeComboBox;

    /** Bouton radio définissant si le pointage est une entrée (Check-in). */
    @FXML private RadioButton radioCheckIn;

    /** Contrôleur logique métier gérant le flux d'envoi et la sauvegarde locale. */
    private CheckPointController logicController;

    /** Client réseau "Stateless" dédié à la communication TCP. */
    private NetworkClient network;

    /**
     * Initialise le contrôleur au démarrage de la vue JavaFX.
     * <p>
     * Configure les dépendances réseau et de stockage local, déclenche la première
     * tentative de chargement des employés, et démarre deux processus asynchrones :
     * <ul>
     * <li>L'horloge locale, mise à jour chaque seconde.</li>
     * <li>Le processus de resynchronisation réseau (autoSyncTimer), exécuté toutes les 15 secondes.</li>
     * </ul>
     * </p>
     */
    @FXML
    public void initialize() {
        this.network = new NetworkClient();
        PendingCheckPointStore store = new PendingCheckPointStore();
        store.load();

        this.logicController = new CheckPointController(network, store);

        // Chargement initial des employés
        loadEmployeesData();

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> updateTime()),
                new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);

        Timeline autoSyncTimer = new Timeline(new KeyFrame(Duration.seconds(15), e -> {
            int synced = logicController.resendPending();
            if (synced > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Reconnexion automatique : " + synced + " pointages envoyés en arrière-plan !");
                alert.show();
                // Si la liste était vide (dû à une déconnexion initiale), on tente de la recharger
                if (employeeComboBox.getItems().isEmpty()) {
                    loadEmployeesData();
                }
            }
        }));
        autoSyncTimer.setCycleCount(Timeline.INDEFINITE);

        autoSyncTimer.play();
        clock.play();
    }

    /**
     * Interroge le serveur via le client réseau pour récupérer et afficher la liste des employés.
     * <p>
     * Met à jour la {@link ComboBox} si la requête réussit. En cas d'échec (serveur éteint
     * ou IP incorrecte), un message d'erreur est tracé dans la console système.
     * </p>
     */
    private void loadEmployeesData() {
        List<EmployeeDTO> listeEmployes = network.getEmployees();

        if (listeEmployes != null && !listeEmployes.isEmpty()) {
            employeeComboBox.getItems().setAll(listeEmployes);
            System.out.println(listeEmployes.size() + " employés chargés dans l'interface !");
        } else {
            System.err.println("Serveur injoignable ou liste vide. Vérifiez l'IP et le Port dans les paramètres.");
        }
    }

    /**
     * Rafraîchit les composants graphiques liés au temps.
     * <p>
     * Cette méthode est appelée dynamiquement par le thread de l'horloge.
     * Elle formate l'heure courante et calcule l'arrondi métier via {@link TimeUtils}.
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
     * Déclenche la création et l'envoi d'un nouveau pointage.
     * <p>
     * Vérifie la sélection de l'employé, génère un {@link CheckPointDTO} avec l'heure
     * arrondie, puis délègue le traitement au {@link CheckPointController}.
     * Affiche une notification de succès ou un avertissement de mise en cache si le serveur est injoignable.
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
     * Ouvre la fenêtre modale de configuration des paramètres réseau.
     * <p>
     * Suspend l'exécution du contrôleur principal jusqu'à la fermeture de la modale.
     * À sa fermeture, lance immédiatement une tentative de rechargement des données
     * avec les nouveaux paramètres réseau.
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

            // Modality bloque l'interaction avec l'application parente
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);

            // Bloque l'exécution ici jusqu'à la fermeture de la fenêtre des paramètres
            dialogStage.showAndWait();

            // Tentative immédiate de rechargement avec la nouvelle adresse IP configurée
            loadEmployeesData();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture des paramètres : " + e.getMessage());
            e.printStackTrace();
        }
    }
}