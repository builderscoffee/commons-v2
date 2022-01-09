package eu.builderscoffee.commons.bungeecord.configuration.messages;

import eu.builderscoffee.commons.bungeecord.configuration.messages.commands.MoveCommandConfigurationPart;
import lombok.Data;

@Data
public class CommandConfigurationPart {

    MoveCommandConfigurationPart move = new MoveCommandConfigurationPart();
}
