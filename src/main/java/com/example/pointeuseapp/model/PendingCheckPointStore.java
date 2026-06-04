package com.example.pointeuseapp.model;

import com.example.dto.CheckPoint;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de stockage local pour les pointages en attente (Mode Hors-ligne).
 * <p>
 * Cette classe gère la persistance des objets {@link CheckPoint} lorsque le
 * serveur central est injoignable. Elle utilise la sérialisation native de Java
 * pour écrire et lire les pointages dans un fichier binaire local ({@code pending.ser}).
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class PendingCheckPointStore {

    /** Liste en mémoire des pointages en attente de synchronisation. */
    private List<CheckPoint> pending = new ArrayList<>();

    /** Nom du fichier local utilisé pour la sérialisation des données. */
    private static final String FILE = "pending.ser";

    /**
     * Ajoute un nouveau pointage à la liste d'attente en mémoire.
     *
     * @param cp Le pointage (CheckPoint) n'ayant pas pu être envoyé au serveur.
     */
    public void add(CheckPoint cp) {
        pending.add(cp);
    }

    /**
     * Retourne la liste complète des pointages actuellement en attente.
     *
     * @return Une liste contenant les objets {@link CheckPoint} stockés.
     */
    public List<CheckPoint> getAll() {
        return pending;
    }

    /**
     * Sauvegarde la liste actuelle des pointages en attente sur le disque dur.
     * <p>
     * Utilise un {@link ObjectOutputStream} pour sérialiser la collection
     * dans le fichier défini par {@code FILE}.
     * </p>
     *
     * @throws IOException Si une erreur d'écriture survient (permissions, espace disque...).
     */
    public void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(pending);
        }
    }

    /**
     * Charge les pointages sauvegardés depuis le disque dur vers la mémoire.
     * <p>
     * Cette méthode est généralement appelée au démarrage de l'application.
     * Si le fichier n'existe pas encore ou qu'une erreur de lecture survient
     * (fichier corrompu), la liste est initialisée à vide de manière sécurisée.
     * </p>
     */
    @SuppressWarnings("unchecked")
    public void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            pending = (List<CheckPoint>) ois.readObject();
        } catch (Exception e) {
            pending = new ArrayList<>();
        }
    }

    /**
     * Remplace la liste complète des pointages en attente et force une sauvegarde.
     * <p>
     * Cette méthode est utile lors d'une synchronisation partielle, où seuls
     * certains pointages ont pu être envoyés au serveur et qu'il faut mettre
     * à jour la liste des éléments restants.
     * </p>
     *
     * @param newList La nouvelle liste des pointages restants en attente.
     */
    public void updateAll(List<CheckPoint> newList) {
        this.pending = newList;

        try {
            save();
            System.out.println("Stockage local mis à jour (Reste en attente : " + pending.size() + ")");
        } catch (IOException e) {
            System.err.println("Erreur lors de la mise à jour du fichier local : " + e.getMessage());
        }
    }

    /**
     * Vide complètement la liste des pointages en attente et met à jour le fichier local.
     * <p>
     * Cette méthode est appelée avec succès par le contrôleur logique une fois que
     * tous les pointages hors-ligne ont été synchronisés et reçus par le serveur.
     * </p>
     */
    public void clear() {
        pending.clear();
        try {
            save();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}