package eu.builderscoffee.commons.bukkit.listeners.redisson;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.ServerActivityEntity;
import eu.builderscoffee.api.common.events.EventHandler;
import eu.builderscoffee.api.common.events.events.HeartBeatEvent;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.api.common.redisson.listeners.PacketListener;
import eu.builderscoffee.api.common.redisson.listeners.ProcessPacket;
import eu.builderscoffee.api.common.redisson.packets.types.common.HeartBeatPacket;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import lombok.val;
import org.bukkit.Bukkit;
import org.redisson.api.RSortedSet;

import java.util.Objects;

/**
 * This class is used to catch {@link HeartBeatPacket} and add the heartbeat into the servers list
 */
public class HeartBeatListener implements PacketListener {

    private Server.ServerStatus lastStatus = Server.ServerStatus.STARTING;

    @ProcessPacket
    public void onHeartBeat(HeartBeatPacket packet){
        // Create server data container
        val server = new Server();
        server.setHostName(CommonsBukkit.getInstance().getSettings().getName());
        server.setHostAddress(Bukkit.getIp());
        server.setHostPort(Bukkit.getPort());
        server.setServerType(Server.ServerType.SPIGOT);
        server.setServerStatus(Server.ServerStatus.RUNNING);
        server.setStartingMethod(CommonsBukkit.getInstance().getSettings().getStartingMethod());
        server.setPlayerCount(Bukkit.getOnlinePlayers().size());
        server.setPlayerMaximum(Bukkit.getMaxPlayers());

        // Create an event for eventually be modified by another sub-plugin
        val event = new HeartBeatEvent(server);
        EventHandler.getInstance().callEvent(event);

        // Stop if canceled
        if (event.isCanceled()) return;

        // Add server to servers list
        final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");
        if(Objects.nonNull(servers) && Objects.nonNull(event) && Objects.nonNull(event.getServer())) {
            val matchServer = servers.stream().filter(s -> s.getHostName().equals(event.getServer().getHostName())).findFirst().orElse(null);
            if(Objects.nonNull(matchServer)) servers.remove(matchServer);
            servers.add(event.getServer());
        }

        if(event.getServer().getServerStatus() != lastStatus){
            val entity = new ServerActivityEntity();
            entity.setServerName(event.getServer().getHostName());
            entity.setMessage("Server status of " + event.getServer().getHostName() + " changed from ''" + lastStatus + " to ''" + event.getServer().getServerStatus());
            DataManager.getServerActivityStore().insert(entity);
            lastStatus = event.getServer().getServerStatus();
        }
    }
}
