package eu.builderscoffee.commons.bukkit.configuration;

import eu.builderscoffee.api.common.configuration.annotation.Configuration;
import eu.builderscoffee.commons.bukkit.configuration.messages.*;
import lombok.Data;

/**
 * This class stores messages configuration
 */
@Data
@Configuration("messages")
public final class MessageConfiguration {

    /* Global */
    String prefix = "[BC]";
    String serverCloseMessage = "§cLe serveur s'est fermé. Vous avez été déplacé dans le hub.";
    String retourItem = "§7Retour";

    ChatConfigurationPart chat = new ChatConfigurationPart();
    CommandConfigurationPart command = new CommandConfigurationPart();
    NetworkConfigurationPart network = new NetworkConfigurationPart();
    ProfilConfigurationPart profil = new ProfilConfigurationPart();
    JoinConfigurationPart join = new JoinConfigurationPart();
    QuitConfigurationPart quit = new QuitConfigurationPart();
    InventoryConfigurationPart inventory = new InventoryConfigurationPart();
}
