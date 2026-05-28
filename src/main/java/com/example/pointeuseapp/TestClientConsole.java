package com.example.pointeuseapp;


import com.example.dto.EmployeeDTO;
import com.example.pointeuseapp.controller.NetworkClient;
import java.util.List;

public class TestClientConsole {

    public static void main(String[] args) {
        System.out.println("=== TENTATIVE DE CONNEXION AU SERVEUR ===");

        NetworkClient client = new NetworkClient("localhost", 8080);

        System.out.println("⏳ Demande de la liste des employés...");
        List<EmployeeDTO> listeEmployes = client.getEmployees();

        System.out.println("\n=== RÉSULTAT DU SERVEUR ===");

        if (listeEmployes != null) {
            System.out.println("Connexion réussie !");
            System.out.println("Nombre d'employés reçus : " + listeEmployes.size());
            System.out.println("----------------------------------------");

            if (listeEmployes.isEmpty()) {
                System.out.println("La liste est vide. Pense à ajouter des employés côté serveur !");
            } else {

                for (EmployeeDTO emp : listeEmployes) {
                    System.out.println("ID: " + emp.getId());
                    System.out.println("Nom complet : " + emp.toString());
                    System.out.println("----------------------------------------");
                }
            }
        } else {
            System.out.println("Le serveur n'a pas répondu. Vérifie qu'il est bien allumé !");
        }
    }
}