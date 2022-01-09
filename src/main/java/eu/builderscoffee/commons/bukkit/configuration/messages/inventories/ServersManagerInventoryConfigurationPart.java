package eu.builderscoffee.commons.bukkit.configuration.messages.inventories;

import lombok.Data;

@Data
public class ServersManagerInventoryConfigurationPart {

    String title = "Server Manager";
    String createServer = "Creer un serveur";
    String manageTournaments = "Gérer les tournois";
    String serversActivities = "Activités des serveurs";
    String serverLeftClick = "&aClic gauche pour gérer";
    String serverRightClick = "&aClic droit pour y aller";
    String serverStopOnDrop = "&aDrop pour stopper le serveur";
}
