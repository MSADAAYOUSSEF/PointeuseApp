package com.example.pointeuseapp.controller;


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


    public List<EmployeeDTO> getEmployees() {

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject("GET_EMPLOYEES");
            oos.flush();

            List<EmployeeDTO> employesRecus = (List<EmployeeDTO>) ois.readObject();

            return employesRecus;

        } catch (Exception e) {
            System.err.println("Impossible de récupérer la liste depuis le serveur : " + e.getMessage());
            return null;
        }
    }


    public boolean send(CheckPoint cp) {

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