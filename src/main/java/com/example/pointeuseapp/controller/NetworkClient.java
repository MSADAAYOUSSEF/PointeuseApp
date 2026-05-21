package com.example.pointeuseapp.controller;

// ✅ Import crucial de ta nouvelle classe allégée
import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPoint;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class NetworkClient {

    private String host;
    private int port;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * NOUVELLE MÉTHODE : Demande et télécharge la liste des employés DTO.
     */
    public List<EmployeeDTO> getEmployees() {
        // Le try-with-resources gère la fermeture propre du réseau
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            // 1. Demande au serveur
            oos.writeObject("GET_EMPLOYEES");
            oos.flush();

            // 2. Lecture de la liste de DTOs
            @SuppressWarnings("unchecked")
            List<EmployeeDTO> employesRecus = (List<EmployeeDTO>) ois.readObject();

            return employesRecus;

        } catch (Exception e) {
            System.err.println("❌ Impossible de récupérer la liste depuis le serveur : " + e.getMessage());
            return null;
        }
    }

    /**
     * MÉTHODE DE TON COLLÈGUE (Sécurisée) : Envoie le pointage au serveur.
     */
    public boolean send(CheckPoint cp) {
        // ✅ On ajoute le ObjectInputStream pour pouvoir écouter la réponse !
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject(cp);
            oos.flush();

            // ✅ On attend que le serveur dise que tout s'est bien passé
            Object response = ois.readObject();
            return "OK".equals(response);

        } catch (Exception e) {
            System.err.println("❌ Échec de l'envoi du pointage : " + e.getMessage());
            return false;
        }
    }
}