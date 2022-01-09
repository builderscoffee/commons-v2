package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.commons.bukkit.inventory.network.NetworkInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This command allows to open the network inventory
 */
public class NetworkCommands implements CommandExecutor {

    public static boolean argLength0(Player player) {
        new NetworkInventory(player).INVENTORY.open(player);
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender);
        if (sender instanceof Player) {
            boolean ret = false;
            switch (args.length) {
                case 0:
                    ret = argLength0((Player) sender);
                    break;
                default:
                    break;
            }

            if (!ret) {
                sender.sendMessage(messages.getPrefix() + messages.getCommand().getBadSyntaxe());
            }

            return ret;
        }

        sender.sendMessage(messages.getCommand().getMustBePlayer());
        return true;
    }
}
