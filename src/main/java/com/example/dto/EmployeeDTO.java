package com.example.dto;

import java.io.Serializable;
import java.util.UUID;

/**
 * Objet de Transfert de Données (DTO) représentant une version allégée d'un Employé.
 * <p>
 * Cette classe est utilisée pour transmettre la liste des employés depuis le serveur
 * vers la pointeuse. Cela permet de ne pas surcharger le réseau avec des données
 * inutiles à la pointeuse (comme les plannings, départements, ou les soldes).
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class EmployeeDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** L'identifiant unique (UUID) de l'employé. */
    private final UUID id;

    /** Le nom complet (Nom + Prénom) formaté pour l'affichage. */
    private final String fullName;

    /**
     * Construit un DTO pour un employé.
     *
     * @param id       L'identifiant unique de l'employé.
     * @param fullName Le nom complet concaténé de l'employé.
     */
    public EmployeeDTO(UUID id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    public UUID getId() { return id; }

    /**
     * Retourne le nom complet de l'employé.
     * <p>
     * Cette méthode est particulièrement importante car elle est appelée nativement
     * par le composant {@code ComboBox} de JavaFX pour déterminer le texte à
     * afficher dans la liste déroulante de l'interface de la Pointeuse.
     * </p>
     *
     * @return Le nom complet de l'employé.
     */
    @Override
    public String toString() {
        return fullName;
    }
}