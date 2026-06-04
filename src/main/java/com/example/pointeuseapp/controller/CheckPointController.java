package com.example.pointeuseapp.controller;

import com.example.dto.CheckPoint;
import com.example.pointeuseapp.model.PendingCheckPointStore;
import java.util.List;

/**
 * Contrôleur logique gérant l'émission des pointages vers le serveur.
 * <p>
 * Cette classe implémente un mécanisme de résilience réseau (mode hors-ligne).
 * Si le serveur central est injoignable au moment du pointage, elle délègue le
 * stockage du pointage à {@link PendingCheckPointStore}. Dès que la connexion
 * est rétablie, elle synchronise automatiquement les données en attente.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class CheckPointController {

    /** Le client réseau chargé de la communication TCP avec le serveur. */
    private NetworkClient networkClient;

    /** Le gestionnaire de persistance locale pour les pointages en échec. */
    private PendingCheckPointStore store;

    /**
     * Construit le contrôleur en injectant ses dépendances.
     *
     * @param networkClient Le client réseau utilisé pour envoyer les données.
     * @param store         Le système de stockage local pour le mode hors-ligne.
     */
    public CheckPointController(NetworkClient networkClient, PendingCheckPointStore store) {
        this.networkClient = networkClient;
        this.store = store;
    }

    /**
     * Tente d'envoyer un nouveau pointage au serveur central.
     * <p>
     * Si l'envoi échoue (serveur hors-ligne), le pointage est sauvegardé localement.
     * Si l'envoi réussit, le contrôleur profite de cette connexion valide pour
     * tenter de renvoyer silencieusement les anciens pointages qui étaient bloqués.
     * </p>
     *
     * @param cp Le pointage (CheckPoint) à envoyer.
     * @return {@code true} si le pointage a été envoyé immédiatement au serveur,
     * {@code false} s'il a été stocké localement suite à un échec réseau.
     */
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

    /**
     * Tente de renvoyer au serveur tous les pointages précédemment stockés en local.
     * <p>
     * Cette méthode parcourt la liste des pointages en attente et essaie de les
     * expédier un par un. Si tous les envois réussissent, le cache local est vidé.
     * </p>
     *
     * @return Le nombre exact de pointages en attente qui ont pu être envoyés avec succès
     * lors de cette tentative de synchronisation.
     */
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