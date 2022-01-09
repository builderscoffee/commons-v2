package eu.builderscoffee.commons.bukkit.configuration;

import eu.builderscoffee.api.common.configuration.annotation.Configuration;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores ip to whitelist configuration
 */
@Data
@Configuration("whitelist")
public class WhitelistConfiguration {

    private List<String> ip_whitelist = new ArrayList<>();
    private String kick_message = "Vous devez passer par le bungeecord !";
}
