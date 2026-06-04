package com.example.pointeuseapp.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * Modèle représentant un employé au sein de l'application cliente (Pointeuse).
 * <p>
 * Cette classe encapsule les informations de base d'un employé (son identifiant unique
 * et son nom) nécessaires au fonctionnement local de l'interface de pointage.
 * Elle implémente {@link Serializable} pour permettre une éventuelle sauvegarde
 * locale ou un transfert de données.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class Employee implements Serializable {

    /** L'identifiant unique de l'employé, généralement généré par le serveur central. */
    private final UUID id;

    /** Le nom de l'employé destiné à l'affichage sur l'interface. */
    private final String name;

    /**
     * Construit une instance locale d'un employé.
     *
     * @param id   L'identifiant (UUID) de l'employé.
     * @param name Le nom de l'employé (généralement Nom + Prénom).
     */
    public Employee(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() { return id; }

    public String getName() { return name; }

    /**
     * Retourne le nom de l'employé sous forme de chaîne de caractères.
     * <p>
     * Cette méthode est particulièrement importante en JavaFX : elle est appelée
     * automatiquement par les composants graphiques (comme les {@code ComboBox}
     * ou {@code ListView}) pour savoir quel texte afficher à l'écran pour cet objet.
     * </p>
     *
     * @return Le nom complet de l'employé.
     */
    @Override
    public String toString() { return name; }
}