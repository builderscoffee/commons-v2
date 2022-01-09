package eu.builderscoffee.commons.bukkit.listeners.redisson;

import eu.builderscoffee.api.common.redisson.listeners.PacketListener;
import eu.builderscoffee.api.common.redisson.listeners.ProcessPacket;
import eu.builderscoffee.commons.common.redisson.packets.StaffChatPacket;
import org.bukkit.Bukkit;

/**
 * This class catches {@link StaffChatPacket} to show them to staff in the chat
 */
public class StaffChatListener implements PacketListener {

    @ProcessPacket
    public void onStaffChatPacket(StaffChatPacket packet){
        // Loop players
        Bukkit.getOnlinePlayers().stream()
                .filter(player -> player.hasPermission("builderscoffee.staffchat"))
                .forEach(player -> {
                    // Send to staff player & console
                    player.sendMessage(packet.getMessage());
                    Bukkit.getConsoleSender().sendMessage(packet.getMessage());
                });
    }
}
