package com.example.pointeuseapp.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitaire pour la manipulation et le formatage des dates et heures.
 * <p>
 * Cette classe centralise les formats d'affichage utilisés dans l'interface
 * graphique de la pointeuse et fournit la logique mathématique pour
 * l'arrondi temporel des pointages.
 * </p>
 * * @author Youssef M'SADAA, Ahmed DEBBACH, Youssef RIANI, Mohamed Yassine BEN ABDA, Youssef ELYAHYAOUI
 */
public class TimeUtils {

    /** * Formateur standardisé pour l'affichage de la date sur l'écran principal.
     * <p>Exemple de format généré : "Mai 13, 2026" ou "June 04, 2026".</p>
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    /** * Formateur standardisé pour l'affichage de l'heure.
     * <p>Exemple de format généré : "11:45" ou "08:00".</p>
     */
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Arrondit une heure donnée au quart d'heure le plus proche.
     * <p>
     * Cette méthode est essentielle pour le calcul de l'heure "estimée" du pointage.
     * Elle permet de lisser les minutes (ex: 11:07 devient 11:00, mais 11:08
     * devient 11:15). Si l'arrondi bascule sur la 60ème minute, la méthode
     * incrémente intelligemment l'heure d'une unité.
     * </p>
     *
     * @param time L'heure exacte capturée par le système (souvent {@code LocalDateTime.now()}).
     * @return Une nouvelle instance de {@link LocalDateTime} arrondie à 00, 15, 30 ou 45 minutes.
     * Les secondes et nanosecondes sont systématiquement remises à zéro pour
     * garantir une heure "propre".
     */
    public static LocalDateTime roundToNearestQuarter(LocalDateTime time) {
        int minutes = time.getMinute();

        int roundedMinutes = ((minutes + 7) / 15) * 15;

        if (roundedMinutes == 60) {
            return time.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        }

        return time.withMinute(roundedMinutes).withSecond(0).withNano(0);
    }
}