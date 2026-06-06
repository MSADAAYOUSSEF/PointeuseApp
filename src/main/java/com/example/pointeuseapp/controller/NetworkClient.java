package com.example.pointeuseapp.controller;

import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPointDTO;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.net.InetSocketAddress;

/**
 * Client réseau responsable de la communication TCP avec le serveur central.
 * <p>
 * Cette classe encapsule la logique d'ouverture des sockets, d'envoi et de
 * réception d'objets sérialisés. Elle est conçue pour remonter proprement
 * les échecs de connexion afin de permettre au système de basculer en
 * mode hors-ligne si nécessaire.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class NetworkClient {

    /** Adresse IP ou nom de domaine du serveur central. */
    private String host;

    /** Port d'écoute du serveur central. */
    private int port;

    /**
     * Initialise un nouveau client réseau avec les paramètres de connexion.
     *
     * @param host L'adresse IP du serveur (ex: "127.0.0.1").
     * @param port Le port sur lequel le serveur écoute (ex: 8080).
     */
    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Interroge le serveur pour récupérer la liste de tous les employés enregistrés.
     * <p>
     * Le client envoie la commande texte "GET_EMPLOYEES" et attend en retour une
     * liste d'objets {@link EmployeeDTO}. Cette liste sert généralement à peupler
     * les menus déroulants de l'interface graphique.
     * </p>
     *
     * @return La liste des employés récupérée depuis le serveur, ou {@code null}
     * si le serveur est injoignable ou qu'une erreur de communication survient.
     */
    public List<EmployeeDTO> getEmployees() {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject("GET_EMPLOYEES");
            oos.flush();

            @SuppressWarnings("unchecked")
            List<EmployeeDTO> employesRecus = (List<EmployeeDTO>) ois.readObject();

            return employesRecus;

        } catch (Exception e) {
            System.err.println("Impossible de récupérer la liste depuis le serveur : " + e.getMessage());
            return null;
        }
    }

    /**
     * Envoie un pointage (CheckPoint) au serveur pour enregistrement.
     * <p>
     * Cette méthode utilise un délai d'attente (timeout) de 2000 millisecondes
     * lors de la connexion. Si le serveur ne répond pas dans ce délai, la méthode
     * considère que le réseau est indisponible et retourne {@code false}.
     * </p>
     *
     * @param cp L'objet {@link CheckPointDTO} contenant les données du pointage (employé, heure, type).
     * @return {@code true} si le serveur a bien reçu le pointage et a répondu "OK",
     * {@code false} si le serveur est injoignable ou a refusé le pointage.
     */
    public boolean send(CheckPointDTO cp) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
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