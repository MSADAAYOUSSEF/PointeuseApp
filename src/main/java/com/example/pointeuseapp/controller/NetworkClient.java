package com.example.pointeuseapp.controller;

import com.example.pointeuseapp.model.CheckPoint;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetworkClient {

    private String host;
    private int port;

    public NetworkClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean send(CheckPoint cp) {
        try {
            Socket socket = new Socket(host, port);

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(cp);

            socket.close();

            return true; // envoi réussi

        } catch (Exception e) {
            return false; // envoi échoué
        }
    }
}