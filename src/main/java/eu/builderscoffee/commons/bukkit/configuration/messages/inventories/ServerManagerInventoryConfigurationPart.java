package eu.builderscoffee.commons.bukkit.configuration.messages.inventories;

import lombok.Data;

@Data
public class ServerManagerInventoryConfigurationPart {

    String title = "Server Manager";
    String stopServer = "Stopper le serveur";
    String stopServerConfirmationTitle = "Stopper %server%";
    String stopServerConfirmationQuestion = "&cÊtes vous sûr de vouloir stopper \n&cle serveur &f%server% &c?";
}
