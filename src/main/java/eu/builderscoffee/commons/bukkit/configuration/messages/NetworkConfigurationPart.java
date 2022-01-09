package eu.builderscoffee.commons.bukkit.configuration.messages;

import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * This class stores network messages configuration
 */
@Data
public class NetworkConfigurationPart {

    // - Hub Item
    String hubItem = "&aHub";
    // - BuildBattle Item
    String buildBattleItem = "&aBuildBattle";
    // - Rules Book
    String rulesBookItem = "&aRégles du serveur";
    @Getter
    List<String> pages = Arrays.asList("Hello", "Builders Coffee");
    // - SupportUs Item
    String supportUsItem = "&fNous soutenir";
    // - SupportUs ChatMessage
    String supportChatMessage = "\n\n§fhttps://fr.tipeee.com/builders-coffee\n\n";
    // - SupportUs Link
    String supportLink = "https://fr.tipeee.com/builders-coffee";
    // - Expresso Item
    String expressoItem = "&6Expresso";
    // - Expresso ChatMessage
    String expressoChatMessage = "\n\n§fhttps://builderscoffee.eu/portfolio/les-expressos/\n\n";
    // - Expresso Link
    String expressoLink = "https://builderscoffee.eu/portfolio/les-expressos/";
    // - CloseMenu Item
    String closeItem = "&cQuitter le menu";
    // - ServerManager item
    String serverManagerItem = "&bGérer les serveurs";
    // - Cosmetics Item
    String CosmeticsItem = "&aCosmétiques";
    // - Cosmetics Item
    String languageItem = "&aLangues";
}
