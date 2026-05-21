package com.example.pointeuseapp.model;

import com.example.dto.CheckPoint;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PendingCheckPointStore {

    private List<CheckPoint> pending = new ArrayList<>();
    private static final String FILE = "pending.ser";

    public void add(CheckPoint cp) {
        pending.add(cp);
    }

    public List<CheckPoint> getAll() {
        return pending;
    }

    public void save() throws IOException {
        // ✅ Utilisation du try-with-resources pour fermer proprement le fichier quoi qu'il arrive
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(pending);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            pending = (List<CheckPoint>) ois.readObject();
        } catch (Exception e) {
            pending = new ArrayList<>();
        }
    }

    /**
     * Remplace toute la liste des pointages en attente par une nouvelle liste.
     * Très utile après une tentative de renvoi au serveur.
     */
    public void updateAll(List<CheckPoint> newList) {
        // 1. On remplace la liste en mémoire vive par la liste des restants
        // ✅ Corrigé : On utilise 'this.pending'
        this.pending = newList;

        // 2. On sauvegarde immédiatement cette nouvelle liste sur le disque dur
        // ✅ Corrigé : On appelle la vraie méthode 'save()' dans un bloc try-catch
        try {
            save();
            System.out.println("💾 Stockage local mis à jour (Reste en attente : " + pending.size() + ")");
        } catch (IOException e) {
            System.err.println("❌ Erreur lors de la mise à jour du fichier local : " + e.getMessage());
        }
    }
}