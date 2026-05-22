package com.example.pointeuseapp.controller;

// ✅ Import crucial de ta nouvelle classe allégée
import com.example.dto.EmployeeDTO;
import com.example.dto.CheckPoint;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.net.InetSocketAddress;

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
        // ✅ 1. On crée le socket vide
        try (Socket socket = new Socket()) {

            // ✅ 2. On tente de se connecter avec un délai maximum de 2 secondes !
            socket.connect(new InetSocketAddress(host, port), 2000);

            try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                // On envoie le pointage
                oos.writeObject(cp);
                oos.flush();

                // On attend l'accusé de réception
                Object reponse = ois.readObject();
                return "OK".equals(reponse);
            }

        } catch (Exception e) {
            // Le serveur est éteint ou injoignable, on échoue silencieusement
            return false;
        }
    }
}