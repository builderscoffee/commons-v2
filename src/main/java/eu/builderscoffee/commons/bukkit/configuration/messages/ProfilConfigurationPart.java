package eu.builderscoffee.commons.bukkit.configuration.messages;

import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * This class stores profil messages configuration
 */
@Data
public class ProfilConfigurationPart {

    // - Skull Item
    String skullItem = "&b%player%";
    @Getter
    List<String> skullLore = Arrays.asList("&9Grade: &f%grade%", "&9Participations: &f%participations%", "&9Victoires: &f%victoires%");
    // - Resultat Item
    String resultItem = "Derniers résultat";
    // - Not played any buildbattle
    String notPlayedAnyBuildbattle = "&cVous n'avez pas encore joué une seule partie !";
    // - No Season started
    String noSeasonStarted = "&cAucune saison est en cours !";
    // - Saisons Item
    String seasonItem = "Saison Actuelle";
    // - Historique Item
    String historyItem = "Historique";
    // - Global Result Item
    String globalResultItem = "Résultat Général";
}
