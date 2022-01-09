package eu.builderscoffee.commons.bukkit.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BungeeUtils {

    /**
     * Envoyer un joueur sur un serveur spécifique
     *
     * @param plugin
     * @param player
     * @param server
     */
    public static void sendPlayerToServer(JavaPlugin plugin, Player player, String server) {

        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

        try {
            dataOutputStream.writeUTF("Connect");
            dataOutputStream.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        player.sendPluginMessage(plugin, "BungeeCord", byteArrayOutputStream.toByteArray());
    }
}
