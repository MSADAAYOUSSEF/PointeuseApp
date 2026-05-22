package com.example.pointeuseapp.controller;

import com.example.dto.CheckPoint;
import com.example.pointeuseapp.model.PendingCheckPointStore;
import java.util.List;

public class CheckPointController {

    private NetworkClient networkClient;
    private PendingCheckPointStore store;

    public CheckPointController(NetworkClient networkClient, PendingCheckPointStore store) {
        this.networkClient = networkClient;
        this.store = store;
    }

    public boolean check(CheckPoint cp) {
        boolean sent = networkClient.send(cp);

        if (!sent) {
            store.add(cp);
            System.out.println("❌ Réseau injoignable : Stocké localement !");
            return false;
        } else {
            System.out.println("✅ Envoyé au serveur !");

            // 🚀 LA MAGIE AUTOMATIQUE EST ICI :
            // Puisque ça a marché, on sait que le serveur est en ligne.
            // On en profite pour envoyer tout ce qui était bloqué !
            int synced = resendPending();
            if (synced > 0) {
                System.out.println("🔄 " + synced + " anciens pointages ont été synchronisés silencieusement !");
            }

            return true;
        }
    }

    // ✅ LA CORRECTION EST ICI : on remplace "void" par "int"
    public int resendPending() {
        if (store.getAll().isEmpty()) return 0; // Ajout du 0 ici aussi

        int countSent = 0;
        boolean allSent = true;
        List<CheckPoint> pointagesEnAttente = new java.util.ArrayList<>(store.getAll());

        for (CheckPoint cp : pointagesEnAttente) {
            boolean sent = networkClient.send(cp);
            if (sent) {
                countSent++;
            } else {
                allSent = false;
            }
        }

        if (allSent) {
            store.clear();
        }

        return countSent;
    }
}