package eu.builderscoffee.commons.bukkit.configuration.messages;

import lombok.Data;

/**
 * This class stores join messages configuration
 */
@Data
public class JoinConfigurationPart {

    String message = "&8[&2+&8] &7%prefix%%player%%suffix% ";
    int weight = 750;
}
