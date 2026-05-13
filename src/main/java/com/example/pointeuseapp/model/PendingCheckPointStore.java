package com.example.pointeuseapp.model;

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

    // Sauvegarde fichier
    public void save() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE));
        oos.writeObject(pending);
        oos.close();
    }

    // Chargement fichier
    public void load() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE));
            pending = (List<CheckPoint>) ois.readObject();
            ois.close();
        } catch (Exception e) {
            pending = new ArrayList<>();
        }
    }
}
