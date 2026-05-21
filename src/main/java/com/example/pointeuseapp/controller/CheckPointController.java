package com.example.pointeuseapp.controller;

import com.example.dto.CheckPoint; // ✅ Import du bon DTO
import com.example.pointeuseapp.model.PendingCheckPointStore;

public class CheckPointController {

    private NetworkClient networkClient;
    private PendingCheckPointStore store;

    public CheckPointController(NetworkClient networkClient, PendingCheckPointStore store) {
        this.networkClient = networkClient;
        this.store = store;
    }

    // ✅ Reçoit l'objet complet et renvoie un boolean pour l'interface
    public boolean check(CheckPoint cp) {
        // 1. Essayer d'envoyer
        boolean sent = networkClient.send(cp);

        // 2. Si échec → stocker localement
        if (!sent) {
            store.add(cp);
            System.out.println("❌ Réseau injoignable : Stocké localement !");
            return false;
        } else {
            System.out.println("✅ Envoyé au serveur !");
            return true;
        }
    }

    public void resendPending() {
        if (store.getAll().isEmpty()) return;

        boolean allSent = true;
        for (CheckPoint cp : store.getAll()) {
            boolean sent = networkClient.send(cp);
            if (sent) {
                System.out.println("✅ Pointage en attente renvoyé !");
            } else {
                System.out.println("❌ Toujours pas de réseau...");
                allSent = false;
            }
        }


    }
}