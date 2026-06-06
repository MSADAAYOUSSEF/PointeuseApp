package com.example.dto;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Objet de Transfert de Données (DTO) représentant un pointage (entrée ou sortie).
 * <p>
 * Cette classe est utilisée pour transférer les données de pointage entre
 * l'application cliente (la pointeuse) et le serveur central. Elle implémente
 * {@link Serializable} pour permettre son envoi via le réseau (Sockets).
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */

public class CheckPointDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Identifiant unique de l'employé effectuant le pointage. */
    private final UUID employeeId;

    /** Date et heure exactes (ou arrondies) de l'action de pointage. */
    private final LocalDateTime time;

    /** Indique s'il s'agit d'une entrée (true) ou d'une sortie (false). */
    private final boolean isCheckIn;

    /** Statut calculé du pointage (ex: Normal, Retard, Heures supp., Doublon). */
    private String statut;

    /**
     * Construit un nouveau pointage.
     * Lors de la création par la pointeuse, le statut est initialisé par défaut à "normal".
     *
     * @param employeeId L'identifiant (UUID) de l'employé concerné.
     * @param time       La date et l'heure du pointage.
     * @param isCheckIn  Vrai pour un Check-In (Entrée), Faux pour un Check-Out (Sortie).
     */

    public CheckPointDTO(UUID employeeId, LocalDateTime time, boolean isCheckIn) {
        this.employeeId = employeeId;
        this.time = time;
        this.isCheckIn = isCheckIn;
        this.statut = "normal";
    }

    public UUID getEmployeeId() { return employeeId; }
    public LocalDateTime getTime() { return time; }
    public boolean isCheckIn() { return isCheckIn; }
    public String getStatut() { return statut; }

    /**
     * Modifie le statut du pointage.
     * <p>
     * Cette méthode est généralement appelée côté Serveur, après analyse des horaires
     * de l'employé, pour appliquer une tolérance et marquer les retards ou les avances.
     * </p>
     *
     * @param statut Le nouveau statut à assigner au pointage.
     */
    public void setStatut(String statut) { this.statut = statut; }
}



