package eu.builderscoffee.commons.bukkit.configuration.messages;

import eu.builderscoffee.commons.bukkit.configuration.messages.command.BroadcastCommandConfigurationPart;
import eu.builderscoffee.commons.bukkit.configuration.messages.command.ManageCommandConfigurationPart;
import eu.builderscoffee.commons.bukkit.configuration.messages.command.StaffChatCommandConfigurationPart;
import lombok.Data;

/**
 * This class stores command messages configuration
 */
@Data
public class CommandConfigurationPart {

    BroadcastCommandConfigurationPart broadcast = new BroadcastCommandConfigurationPart();
    ManageCommandConfigurationPart manage = new ManageCommandConfigurationPart();
    StaffChatCommandConfigurationPart staffchat = new StaffChatCommandConfigurationPart();
    String mustBePlayer = "Vous devez être un joueur";
    String badSyntaxe = "bad syntaxe";
    String noPremission = "§cVous n'avez pas la permission !";
}
