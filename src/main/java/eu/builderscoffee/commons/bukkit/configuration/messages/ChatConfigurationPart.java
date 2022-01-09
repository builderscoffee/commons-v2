package eu.builderscoffee.commons.bukkit.configuration.messages;

import lombok.Data;

/**
 * This class stores chat messages configuration
 */
@Data
public class ChatConfigurationPart {

    String format = "&7%prefix%%player%%suffix% &8>> &f%message%";
    String staffChatFormat = "&b[SC] &7%prefix%%player%%suffix% &8>> &f%message%";
    String broadcastFormat = "&8&m----------&8&m------\n\n&6&lBuilders Coffee &8>> &e%message%\n\n&8&m----------&8&m------";
}
