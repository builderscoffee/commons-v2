package eu.builderscoffee.commons.bukkit.listeners.bukkit;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.inventory.servermanager.ServerManagerInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.redisson.packets.ServerManagerRequest;
import eu.builderscoffee.commons.common.redisson.packets.StaffChatPacket;
import eu.builderscoffee.commons.common.redisson.topics.CommonTopics;
import eu.builderscoffee.commons.common.utils.LuckPermsUtils;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        val player = event.getPlayer();

        // Check si le profil est bien crées
        val profil = CommonsBukkit.getInstance().getProfilCache().get(player.getUniqueId().toString());
        if (profil == null) {
            player.kickPlayer("§6§lBuilders Coffee Server \n§cUne erreur est survenue lors du chargement de données.\n§cVeuillez vous reconnecter");
            return;
        }

        // Mettre à jour le pseudo si ce n'est pas correcte
        if (!player.getName().equalsIgnoreCase(profil.getName())){
            profil.setName(player.getName());
            DataManager.getProfilStore().update(profil);
        }
        if(Objects.isNull(profil.getLang())){
            profil.setLang(Profil.Languages.FR);
            DataManager.getProfilStore().update(profil);
        }

        // Suppression des équipes précédemment enregistrés
        Bukkit.getOnlinePlayers().forEach(loopPlayer -> {
            loopPlayer.getScoreboard().getTeams().forEach(team -> team.unregister());
        });

        // Creation des équipes Minecraft pour ordonner dans le tab
        for (Player loopPlayer : Bukkit.getOnlinePlayers()) {
            val primaryGroup = LuckPermsUtils.getPrimaryGroup(loopPlayer.getUniqueId());
            val weight = Math.abs(1000 - LuckPermsUtils.getWeight(loopPlayer.getUniqueId()));

            String teamName = primaryGroup.length() > 13 ? weight + primaryGroup.substring(0, 13) : weight + primaryGroup;
            for (int i = 0; i < 3 - String.valueOf(weight).length(); i++) {
                teamName = "0" + teamName;
            }

            val prefix = LuckPermsUtils.getPrefixOrEmpty(loopPlayer.getUniqueId());
            val suffix = LuckPermsUtils.getSuffixOrEmpty(loopPlayer.getUniqueId());
            loopPlayer.setPlayerListName((prefix + loopPlayer.getName() + suffix).replace("&", "§"));

            for (Player loopPlayer2 : Bukkit.getOnlinePlayers()) {
                val scoreboard = loopPlayer2.getScoreboard();
                val team = scoreboard.getTeam(teamName) == null ? scoreboard.registerNewTeam(teamName) : scoreboard.getTeam(teamName);

                team.addPlayer(loopPlayer);
            }
        }

        // Message de join
        if (LuckPermsUtils.getWeight(player.getUniqueId()) > MessageUtils.getMessageConfig(player).getJoin().getWeight()) {
            event.setJoinMessage(MessageUtils.getMessageConfig(player).getJoin().getMessage()
                .replace("%player%", player.getName())
                .replace("%prefix%", LuckPermsUtils.getPrefixOrEmpty(player.getUniqueId()))
                .replace("%suffix%", LuckPermsUtils.getSuffixOrEmpty(player.getUniqueId()))
                .replace("&", "§"));
        } else {
            event.setJoinMessage(null);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        val player = event.getPlayer();

        // Message de leave
        if (LuckPermsUtils.getWeight(player.getUniqueId()) > MessageUtils.getMessageConfig(player).getQuit().getWeight()) {
            event.setQuitMessage(MessageUtils.getMessageConfig(player).getQuit().getMessage()
                .replace("&", "§")
                .replace("%player%", player.getName()));
        } else {
            event.setQuitMessage(null);
        }

        // Supression de l'équipe chez le joueur
        player.getScoreboard().getTeams().forEach(team -> {
            boolean online = false;
            for (OfflinePlayer offlinePlayer : team.getPlayers())
                if (offlinePlayer.isOnline())
                    online = true;
            if (!online)
                team.unregister();
        });
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        val player = event.getPlayer();
        val message = event.getMessage();
        val prefix = LuckPermsUtils.getPrefixOrEmpty(player.getUniqueId());
        val suffix = LuckPermsUtils.getSuffixOrEmpty(player.getUniqueId());

        // StaffChat
        if (CommonsBukkit.getInstance().getStaffchatPlayers().contains(event.getPlayer().getUniqueId())) {
            val packet = new StaffChatPacket()
                .setPlayerName(player.getName())
                .setMessage(MessageUtils.getMessageConfig(player).getChat().getStaffChatFormat()
                    .replace("%player%", player.getName())
                    .replace("%prefix%", prefix)
                    .replace("%suffix%", suffix)
                    .replace("%message%", message)
                    .replace("&", "§"));
            Redis.publish(CommonTopics.STAFFCHAT, packet);
            event.setCancelled(true);
        }
        // Server manager chat request
        else if(ServerManagerInventory.getChatRequests().stream().anyMatch(t -> t.getLeft().equals(player))){
            event.setCancelled(true);

            val triplet = ServerManagerInventory.getChatRequests().stream()
                    .filter(t -> t.getLeft().equals(player))
                    .findFirst().get();

            ServerManagerInventory.getChatRequests().remove(triplet);

            //triplet.getCenter().sendConfigRequest(player, triplet.getRight(), event.getMessage(), ServerManagerRequest.ItemAction.NONE, contetns);
            //triplet.getCenter().INVENTORY.open(player);
        }
        // Normal chat
        else {
            event.setFormat(MessageUtils.getMessageConfig(player).getChat().getFormat()
                .replace("%player%", player.getName())
                .replace("%prefix%", prefix)
                .replace("%suffix%", suffix)
                .replace("%message%", message)
                .replace("&", "§"));
        }
    }
}
