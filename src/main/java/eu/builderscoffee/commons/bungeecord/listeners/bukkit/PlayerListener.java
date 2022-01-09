package eu.builderscoffee.commons.bungeecord.listeners.bukkit;

import com.google.common.collect.Iterables;
import eu.builderscoffee.commons.bungeecord.CommonsBungeeCord;
import eu.builderscoffee.commons.bungeecord.utils.MessageUtils;
import eu.builderscoffee.commons.bungeecord.utils.TextComponentUtil;
import eu.builderscoffee.commons.common.configuration.SettingsConfig;
import lombok.val;
import lombok.var;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import static eu.builderscoffee.api.common.configuration.Configuration.writeConfiguration;

public class PlayerListener implements Listener {

    /*@EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        // Update Profil
        val profil = Main.getInstance().getProfilCache().get(player.getUniqueId().toString());
        if (profil == null) {
            player.disconnect(TextComponentUtil.decodeColor("§6§lBuilders Coffee Proxy \n§cUne erreur est survenue lors du chargement de données.\n§cVeuillez vous reconnecter"));
            return;
        }

        val banStore = DataManager.getBansStore();
        try (val query = banStore.select(BanEntity.class)
                .where(BanEntity.PROFILE.eq(profil))
                .get()) {

            val ban = query.firstOrNull();

            if (ban != null) {
                if (new Date().after(ban.getDateEnd())) {
                    banStore.delete(ban);
                } else {
                    String message = "";
                    for (String s : Main.getInstance().getMessages().getBanMessage()) {
                        String line = s.replace("%reason%", ban.getReason())
                                .replace("%time%", DateUtil.formatDateDiff(ban.getDateEnd().getTime()))
                                .replace("&", "§");
                        message += line + "\n";
                    }
                    player.disconnect(TextComponentUtil.decodeColor(message));
                    return;
                }
            }
        }
    }*/

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerKick(final ServerKickEvent event) {
        // When running in single-server mode, we can't kick people to the hub if they are on the hub.
        if (ProxyServer.getInstance().getServers().size() <= 1 && Iterables.getOnlyElement(ProxyServer.getInstance().getServers().values()).equals(event.getKickedFrom())) return;

        // Not connected before
        if (event.getPlayer().getServer() == null) return;

        // We aren't even on that server, so ignore it.
        if (!event.getPlayer().getServer().getInfo().equals(event.getKickedFrom())) return;

        boolean match = false;

        // Check kick reason if it is needed to connect the player to a hub
        for (BaseComponent baseComponent : event.getKickReasonComponent()) {
            for (String keyword : MessageUtils.getDefaultMessageConfig().getWhitelistRedirectMessagesKeywords()) {
                if (baseComponent.toLegacyText().toLowerCase().contains(keyword.toLowerCase())) {
                    match = true;
                }
            }
        }

        if (!match) return;

        // Get any hub server as backup server
        val anyHubServer = ProxyServer.getInstance().getServers().values().stream()
                .filter(s -> !event.getPlayer().getServer().getInfo().equals(s))
                .filter(s -> s.getName().contains("hub"))
                .findFirst().orElse(null);

        // Get the correct hub server depends on de server mode
        var hubServer = ProxyServer.getInstance().getServers().values().stream()
                .filter(s -> !event.getPlayer().getServer().getInfo().equals(s))
                .filter(s -> {
                    if(CommonsBungeeCord.getInstance().getSettings().getPluginMode().equals(SettingsConfig.PluginMode.DEVELOPMENT))
                        return s.getName().contains("hub") && s.getName().contains("dev");
                    return s.getName().contains("hub") && !s.getName().contains("dev");
                })
                .findFirst()
                .orElse(anyHubServer);

        if (hubServer == null) return;

        // Move player to the fallback server
        event.setCancelled(true);
        event.setCancelServer(hubServer);

        event.getPlayer().sendMessage(TextComponentUtil.decodeColor(MessageUtils.getDefaultMessageConfig().getServerRedirectionMessage().replace("%server%", hubServer.getName())));
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        // check if player was on a server before
        if (event.getPlayer().getServer() != null) return;

        // Get any hub server as backup server
        val anyHubServer = ProxyServer.getInstance().getServers().values().stream()
                .filter(s -> s.getName().contains("hub"))
                .findFirst().orElse(null);

        // Get the correct hub server depends on de server mode
        val server = ProxyServer.getInstance().getServers().values().stream()
                .filter(s -> {
                    if(CommonsBungeeCord.getInstance().getSettings().getPluginMode().equals(SettingsConfig.PluginMode.DEVELOPMENT))
                        return s.getName().contains("hub") && s.getName().contains("dev");
                    return s.getName().contains("hub") && !s.getName().contains("dev");
                })
                .findFirst().orElse(anyHubServer);

        if (server == null) return;

        // Connect player to correct server
        event.setTarget(server);
    }
}
