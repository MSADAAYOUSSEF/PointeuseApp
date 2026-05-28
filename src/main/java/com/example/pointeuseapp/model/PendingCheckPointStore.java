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

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE))) {
            oos.writeObject(pending);
        }
    }


    public void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE))) {
            pending = (List<CheckPoint>) ois.readObject();
        } catch (Exception e) {
            pending = new ArrayList<>();
        }
    }


    public void updateAll(List<CheckPoint> newList) {

        this.pending = newList;

        try {
            save();
            System.out.println("Stockage local mis à jour (Reste en attente : " + pending.size() + ")");
        } catch (IOException e) {
            System.err.println("Erreur lors de la mise à jour du fichier local : " + e.getMessage());
        }
    }

    public void clear() {
        pending.clear();
        try {
            save();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}