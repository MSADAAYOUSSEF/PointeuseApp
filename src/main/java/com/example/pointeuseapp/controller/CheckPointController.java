package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.model.CheckPoint;
import com.example.pointeuseapp.model.PendingCheckPointStore;

import java.time.LocalDateTime;

public class CheckPointController {

    private NetworkClient networkClient;
    private PendingCheckPointStore store;

    public CheckPointController(NetworkClient networkClient, PendingCheckPointStore store) {
        this.networkClient = networkClient;
        this.store = store;
    }

    public void check(String employeeId) {

        // 1. créer un pointage
        CheckPoint cp = new CheckPoint(employeeId, LocalDateTime.now());

        // 2. essayer d'envoyer
        boolean sent = networkClient.send(cp);

        // 3. si échec → stocker
        if (!sent) {
            store.add(cp);
            System.out.println("Stocké localement !");
        } else {
            System.out.println("Envoyé au serveur !");
        }
    }

    public void resendPending() {

        for (CheckPoint cp : store.getAll()) {
            boolean sent = networkClient.send(cp);

            if (sent) {
                System.out.println("Pointage renvoyé !");
            } else {
                System.out.println("Toujours pas envoyé...");
            }
        }
    }
}
