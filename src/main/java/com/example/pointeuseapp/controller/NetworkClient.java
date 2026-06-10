package com.example.pointeuseapp.controller;

import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPointDTO;
import com.example.pointeuseapp.utils.ConfigManager;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Client réseau responsable de la communication TCP avec le serveur central.
 * <p>
 * Cette classe gère l'ouverture des sockets et la transmission des objets sérialisés
 * (DTO). Elle adopte une architecture "Stateless" (sans état) en lisant la configuration
 * réseau (IP et port) de manière dynamique sur le disque via {@link ConfigManager} à chaque
 * requête. Cela garantit l'application immédiate des changements de paramètres sans redémarrage.
 * </p>
 *
 * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class NetworkClient {

    /**
     * Constructeur par défaut du client réseau.
     * <p>
     * Ce constructeur est volontairement vide. Le client ne stocke aucune configuration
     * en mémoire vive afin de respecter le principe de source de vérité unique
     * (Single Source of Truth) dicté par le fichier de configuration physique.
     * </p>
     */
    public NetworkClient() {
        // Constructeur vide : on ne stocke plus la configuration en mémoire globale
    }

    /**
     * Interroge le serveur central pour récupérer la liste complète des employés.
     * <p>
     * La méthode force une lecture à chaud de la configuration réseau. Elle établit ensuite
     * une connexion TCP avec un délai d'attente (timeout) de 2 secondes. Si la connexion
     * réussit, elle envoie la commande textuelle {@code "GET_EMPLOYEES"} et désérialise la réponse.
     * </p>
     *
     * @return Une liste contenant les instances de {@link EmployeeDTO} récupérées depuis le serveur,
     * ou {@code null} si le délai d'attente est dépassé ou si le serveur est injoignable.
     */
    public List<EmployeeDTO> getEmployees() {
        // 1. On force la lecture du fichier sur le disque à l'instant T
        ConfigManager currentConfig = new ConfigManager();
        String currentHost = currentConfig.getServerIp();
        int currentPort = currentConfig.getServerPort();

        try (Socket socket = new Socket()) {

            socket.connect(new InetSocketAddress(currentHost, currentPort), 2000);

            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                oos.writeObject("GET_EMPLOYEES");
                oos.flush();

                @SuppressWarnings("unchecked")
                List<EmployeeDTO> employesRecus = (List<EmployeeDTO>) ois.readObject();

                return employesRecus;
            }

        } catch (Exception e) {
            System.err.println("Serveur injoignable sur " + currentHost + ":" + currentPort);
            return null;
        }
    }

    /**
     * Transmet un nouveau pointage au serveur central pour enregistrement.
     * <p>
     * Comme pour la récupération, la méthode lit dynamiquement la configuration pour
     * assurer une flexibilité à chaud. Elle établit une connexion TCP sécurisée par
     * un timeout de 2 secondes, transfère l'objet {@link CheckPointDTO} et attend
     * l'accusé de réception.
     * </p>
     *
     * @param cp Le pointage (Data Transfer Object) contenant l'ID de l'employé, l'heure et le type d'action.
     * @return {@code true} si le serveur a bien reçu la donnée et a répondu {@code "OK"},
     * {@code false} en cas d'échec réseau, de timeout, ou de rejet par le serveur.
     */
    public boolean send(CheckPointDTO cp) {
        // 1. On force la lecture du fichier sur le disque à l'instant T
        ConfigManager currentConfig = new ConfigManager();
        String currentHost = currentConfig.getServerIp();
        int currentPort = currentConfig.getServerPort();

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(currentHost, currentPort), 2000);

            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                oos.writeObject(cp);
                oos.flush();

                Object reponse = ois.readObject();
                return "OK".equals(reponse);
            }

        } catch (Exception e) {
            return false;
        }
    }
}