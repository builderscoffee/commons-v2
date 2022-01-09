package eu.builderscoffee.commons.bungeecord.listeners.bukkit;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bungeecord.CommonsBungeeCord;
import eu.builderscoffee.commons.bungeecord.events.DataStatueEvent;
import lombok.val;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ConnexionListener implements Listener {

    @EventHandler
    public void onPreLogin(LoginEvent event) {
        ProxyServer.getInstance().getPluginManager().callEvent(new DataStatueEvent.Load(event.getConnection().getUniqueId().toString()));
    }

    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        ProxyServer.getInstance().getScheduler().runAsync(CommonsBungeeCord.getInstance(),
                () -> ProxyServer.getInstance().getPluginManager().callEvent(new DataStatueEvent.Save(player.getUniqueId().toString())));
    }

    @EventHandler
    public void onLoad(DataStatueEvent.Load event) {
        val instance = CommonsBungeeCord.getInstance();
        val uniqueId = event.getUniqueId();
        // Récupère ou créer une nouvelle entité
        try(val query =  DataManager.getProfilStore().select(ProfilEntity.class).where(ProfilEntity.UNIQUE_ID.eq(uniqueId))
                .get()) {
            ProfilEntity entity = query.firstOrNull();
            if (entity == null) {
                entity = CommonsBungeeCord.getInstance().getProfilCache().getOrCreate(uniqueId);
                entity = DataManager.getProfilStore().insert(entity);
            }
            instance.getProfilCache().put(uniqueId, entity);
        }
    }

    @EventHandler
    public void onSave(DataStatueEvent.Save event) {
        val instance = CommonsBungeeCord.getInstance();
        val uniqueId = event.getUniqueId();
        val entity = instance.getProfilCache().get(uniqueId);
        if(entity == null) {
            int currentLine = new Throwable().getStackTrace()[0].getLineNumber() + 1;
            CommonsBungeeCord.getInstance().getLogger().warning("§cLe joueur n'avait pas de donnée (" + this.getClass().getName() + ".java:" + currentLine + ")");
            return;
        }
        try{
            val query = DataManager.getProfilStore().update(entity);
        } catch (Exception e){
            e.printStackTrace();
        }
        instance.getProfilCache().remove(uniqueId);
    }
}
