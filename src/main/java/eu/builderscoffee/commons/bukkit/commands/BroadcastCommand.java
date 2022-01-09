package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * This command allows to broadcast a message on the server
 */
public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if ((sender instanceof Player && sender.hasPermission("builderscoffee.broadcast"))
                || sender instanceof ConsoleCommandSender) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            if (message.length() > 3) {
                Bukkit.broadcastMessage(MessageUtils.getMessageConfig(sender).getChat().getBroadcastFormat().replace("%message%", message)
                        .replace("&", "§")
                );
                return false;
            }

            sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getBroadcast().getMessageNeedsToBeLonger());
            return true;
        }

        sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getNoPremission());
        return true;
    }
}
