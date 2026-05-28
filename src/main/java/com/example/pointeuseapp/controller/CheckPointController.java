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
            System.out.println("Réseau injoignable : Stocké localement !");
            return false;
        } else {
            System.out.println("Envoyé au serveur !");


            int synced = resendPending();
            if (synced > 0) {
                System.out.println(synced + " anciens pointages ont été synchronisés silencieusement !");
            }

            return true;
        }
    }

    public int resendPending() {
        if (store.getAll().isEmpty()) return 0;

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