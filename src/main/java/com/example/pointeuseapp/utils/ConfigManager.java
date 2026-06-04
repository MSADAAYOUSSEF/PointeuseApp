package com.example.pointeuseapp.utils;

import java.io.*;
import java.util.Properties;

/**
 * Gestionnaire de configuration locale pour l'application Pointeuse (Client).
 * <p>
 * Cette classe utilitaire permet de lire et d'écrire les paramètres réseau
 * (Adresse IP et Port du serveur) dans un fichier local {@code .properties}.
 * Ainsi, l'utilisateur n'a pas besoin de reconfigurer la pointeuse à chaque
 * démarrage de l'application.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class ConfigManager {

    /** Le nom du fichier de configuration sauvegardé sur le disque. */
    private static final String CONFIG_FILE = "client_config.properties";

    /** L'objet contenant les paires clé-valeur de la configuration. */
    private Properties properties;

    /**
     * Initialise le gestionnaire de configuration.
     * <p>
     * Ce constructeur tente immédiatement de charger le fichier existant.
     * S'il n'existe pas, il crée automatiquement un fichier avec les valeurs par défaut.
     * </p>
     */
    public ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    /**
     * Charge les paramètres depuis le fichier local.
     * <p>
     * En cas d'absence du fichier (par exemple lors du tout premier lancement),
     * les valeurs par défaut ({@code localhost} et {@code 8080}) sont appliquées
     * et le fichier est créé sur le disque.
     * </p>
     */
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

    /**
     * Sauvegarde la configuration actuelle en mémoire vers le fichier physique.
     * <p>
     * Cette méthode doit être appelée après avoir utilisé les méthodes "set"
     * (comme {@link #setServerIp(String)}) pour que les changements soient persistants.
     * </p>
     */
    public void saveConfig() {
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, "Configuration de la Pointeuse (Client)");
        } catch (IOException io) {
            System.err.println("Erreur lors de la sauvegarde de la configuration client : " + io.getMessage());
        }
    }

    /**
     * Récupère l'adresse IP du serveur configurée.
     *
     * @return L'adresse IP (ex: "192.168.1.15"), ou "localhost" si aucune n'est définie.
     */
    public String getServerIp() {
        return properties.getProperty("server.ip", "localhost");
    }

    /**
     * Modifie l'adresse IP du serveur en mémoire.
     *
     * @param ip La nouvelle adresse IP à configurer.
     */
    public void setServerIp(String ip) {
        properties.setProperty("server.ip", ip);
    }

    /**
     * Récupère le port de communication du serveur.
     * <p>
     * Si la valeur contenue dans le fichier n'est pas un nombre valide,
     * l'exception est interceptée et le port 8080 est renvoyé par sécurité.
     * </p>
     *
     * @return Le port du serveur (entier).
     */
    public int getServerPort() {
        try {
            return Integer.parseInt(properties.getProperty("server.port", "8080"));
        } catch (NumberFormatException e) {
            System.err.println("Port invalide dans la configuration, utilisation du port 8080 par défaut.");
            return 8080;
        }
    }

    /**
     * Modifie le port de communication du serveur en mémoire.
     *
     * @param port Le nouveau port à configurer (ex: 8080).
     */
    public void setServerPort(int port) {
        properties.setProperty("server.port", String.valueOf(port));
    }
}