package com.example.pointeuseapp.utils;

import java.io.*;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE = "client_config.properties";
    private Properties properties;

    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    private void loadConfig() {
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            System.out.println("Fichier de configuration client introuvable, création avec les valeurs par défaut.");
            properties.setProperty("server.ip", "localhost");
            properties.setProperty("server.port", "8080");
            saveConfig();
        }
    }

    public void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuration de la Pointeuse (Client)");
        } catch (IOException io) {
            System.err.println("Erreur lors de la sauvegarde de la configuration client : " + io.getMessage());
        }
    }


    public String getServerIp() {
        return properties.getProperty("server.ip", "localhost");
    }

    public void setServerIp(String ip) {
        properties.setProperty("server.ip", ip);
    }


    public int getServerPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port", "8080"));
        } catch (NumberFormatException e) {
            System.err.println("Port invalide dans la configuration, utilisation du port 8080 par défaut.");
            return 8080;
        }
    }

    public void setServerPort(int port) {
        properties.setProperty("server.port", String.valueOf(port));
    }
}