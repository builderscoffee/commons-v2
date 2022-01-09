package eu.builderscoffee.commons.bukkit.configuration.messages;

import eu.builderscoffee.commons.bukkit.configuration.messages.command.BroadcastCommandConfigurationPart;
import eu.builderscoffee.commons.bukkit.configuration.messages.command.ManageCommandConfigurationPart;
import eu.builderscoffee.commons.bukkit.configuration.messages.command.StaffChatCommandConfigurationPart;
import eu.builderscoffee.commons.bukkit.configuration.messages.inventories.*;
import lombok.Data;

/**
 * This class stores command messages configuration
 */
@Data
public class InventoryConfigurationPart {

    CreateplotInventoryConfigurationPart createPlot = new CreateplotInventoryConfigurationPart();
    CreateServerInventoryConfigurationPart createServer = new CreateServerInventoryConfigurationPart();
    CreateTournamentInventoryConfigurationPart createTournament = new CreateTournamentInventoryConfigurationPart();
    LanguageInventoryConfigurationPart language = new LanguageInventoryConfigurationPart();
    NetworkInventoryConfigurationPart network = new NetworkInventoryConfigurationPart();
    ServerManagerInventoryConfigurationPart serverManager = new ServerManagerInventoryConfigurationPart();
    ServersActivitiesInventoryConfigurationPart serversActivities = new ServersActivitiesInventoryConfigurationPart();
    ServersManagerInventoryConfigurationPart serversManager = new ServersManagerInventoryConfigurationPart();
    TournamentManagerInventoryConfigurationPart tournamentManager = new TournamentManagerInventoryConfigurationPart();
}
