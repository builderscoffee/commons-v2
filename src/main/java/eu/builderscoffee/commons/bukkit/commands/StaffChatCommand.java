package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.redisson.packets.StaffChatPacket;
import eu.builderscoffee.commons.common.redisson.topics.CommonTopics;
import eu.builderscoffee.commons.common.utils.LuckPermsUtils;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * This command allows to send a message in the staffchat channel
 */
public class StaffChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender);

        // Check if has the permission to do it and is player
        if((sender instanceof Player && sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getStaffchat()))
                || sender instanceof ConsoleCommandSender){
            // If more arguments, send message
            if(args.length > 0){
                String message = "";
                for (String arg : args) {
                    message += (message.isEmpty()? "" : " ") + arg;
                }
                val prefix = (sender instanceof Player)? LuckPermsUtils.getPrefixOrEmpty(((Player)sender).getUniqueId()) : "";
                val suffix = (sender instanceof Player)? LuckPermsUtils.getSuffixOrEmpty(((Player)sender).getUniqueId()) : "";
                val packet = new StaffChatPacket()
                        .setPlayerName(sender.getName())
                        .setMessage(messages.getChat().getStaffChatFormat()
                                .replace("%player%", sender.getName())
                                .replace("%prefix%", prefix)
                                .replace("%suffix%", suffix)
                                .replace("%message%", message)
                                .replace("&", "§"));
                Redis.publish(CommonTopics.STAFFCHAT, packet);
            }
            // Else, enable/disable staffchat
            else {
                if(sender instanceof Player){
                    val player = (Player) sender;
                    if(CommonsBukkit.getInstance().getStaffchatPlayers().contains(player.getUniqueId())){
                        player.sendMessage(messages.getCommand().getStaffchat().getDisableStaffchat());
                        CommonsBukkit.getInstance().getStaffchatPlayers().remove(player.getUniqueId());
                    }
                    else {
                        player.sendMessage(messages.getCommand().getStaffchat().getEnableStaffchat());
                        CommonsBukkit.getInstance().getStaffchatPlayers().add(player.getUniqueId());
                    }
                }
                else {
                    sender.sendMessage("§cUsage: /staffchat <message>");
                }
            }
            return true;
        }
        sender.sendMessage(messages.getCommand().getNoPremission());
        return true;
    }
}
