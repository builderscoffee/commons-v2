package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.BungeeUtils;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.configuration.SettingsConfig;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.redisson.api.RSortedSet;

/**
 * This command allows to navigate back to the main hub server
 */
public class HubCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");

            // Vérifie que la liste existe
            if (servers == null) {
                BungeeUtils.sendPlayerToServer(CommonsBukkit.getInstance(), (Player) sender, "hub");
                return false;
            }

            // Get any hub server as backup server
            val anyHubServer = servers.stream()
                    .filter(s -> s.getHostName().contains("hub"))
                    .findFirst().orElse(null);

            // Get the correct hub server depends on de server mode
            val server = servers.stream()
                    .filter(s -> {
                        if (CommonsBukkit.getInstance().getSettings().getPluginMode().equals(SettingsConfig.PluginMode.DEVELOPMENT))
                            return s.getHostName().contains("hub") && s.getHostName().contains("dev");
                        return s.getHostName().contains("hub") && !s.getHostName().contains("dev");
                    })
                    .findFirst().orElse(anyHubServer);

            BungeeUtils.sendPlayerToServer(CommonsBukkit.getInstance(), (Player) sender, server.getHostName());
            return true;
        }

        sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getMustBePlayer());
        return true;
    }
}
