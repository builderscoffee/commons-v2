package eu.builderscoffee.commons.bungeecord.listeners.redisson;

import eu.builderscoffee.api.common.redisson.listeners.PacketListener;
import eu.builderscoffee.api.common.redisson.listeners.ProcessPacket;
import eu.builderscoffee.api.common.redisson.packets.types.common.BungeecordPacket;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;

import java.net.InetSocketAddress;

/**
 * This class catches {@link BungeecordPacket} from playpen when a server has to be added to the BungeeCord servers list or removed
 */
public class ServersListListener implements PacketListener {

    @ProcessPacket
    public void onBungeecordPacket(BungeecordPacket packet){
        // Check if started
        if(packet.getServerStatus() == BungeecordPacket.ServerStatus.STARTED){
            // Add to BungeeCord servers list
            val si = ProxyServer.getInstance().constructServerInfo(packet.getHostName(), new InetSocketAddress(packet.getHostAddress(), packet.getHostPort()), "", false);
            ProxyServer.getInstance().getServers().put(si.getName(), si);
        }
        else {
            // Remove from BungeeCord servers list
            ProxyServer.getInstance().getServers().remove(packet.getHostName());
        }
    }
}
