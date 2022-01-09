package eu.builderscoffee.commons.bukkit.listeners.bukkit;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.events.bukkit.DataStatueEvent;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnexionListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        Bukkit.getServer().getPluginManager().callEvent(new DataStatueEvent.Load(event.getUniqueId().toString()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent e) {
        val whitelist = CommonsBukkit.getInstance().getWhitelist();
        for (final Player player : CommonsBukkit.getInstance().getServer().getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(e.getName()) ||
                    player.getUniqueId().equals(e.getUniqueId()) ||
                    player.getUniqueId().toString().toLowerCase().replaceAll("-", "").equalsIgnoreCase(e.getUniqueId().toString().toLowerCase().replaceAll("-", ""))) {
                e.setKickMessage(whitelist.getKick_message());
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        val whitelist = CommonsBukkit.getInstance().getWhitelist();
        if(!whitelist.getIp_whitelist().contains(e.getRealAddress().getHostAddress())){
            e.setKickMessage(whitelist.getKick_message());
            e.setResult(PlayerLoginEvent.Result.KICK_WHITELIST);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(CommonsBukkit.getInstance(), () -> Bukkit.getServer().getPluginManager().callEvent(new DataStatueEvent.Save(event.getPlayer().getUniqueId().toString())));
    }

    @EventHandler
    public void onLoad(DataStatueEvent.Load event) {
        val instance = CommonsBukkit.getInstance();
        val uniqueId = event.getUniqueId();
        // Récupère ou créer une nouvelle entité
        try(val query =  DataManager.getProfilStore().select(ProfilEntity.class).where(ProfilEntity.UNIQUE_ID.eq(uniqueId))
                .get()) {
            ProfilEntity entity = query.firstOrNull();
            if (entity == null) {
                entity = CommonsBukkit.getInstance().getProfilCache().getOrCreate(uniqueId);
                DataManager.getProfilStore().insert(entity);
            }
            instance.getProfilCache().put(uniqueId, entity);
        }
    }

    @EventHandler
    public void onSave(DataStatueEvent.Save event) {
        val instance = CommonsBukkit.getInstance();
        val uniqueId = event.getUniqueId();
        val entity = instance.getProfilCache().get(uniqueId);
        if(entity == null) {
            int currentLine = new Throwable().getStackTrace()[0].getLineNumber() + 1;
            CommonsBukkit.getInstance().getLogger().warning("§cLe joueur n'avait pas de donnée (" + this.getClass().getName() + ".java:" + currentLine + ")");
            return;
        }
        val store = DataManager.getProfilStore();
        try{
            val query = store.update(entity);
        } catch (Exception e){
            e.printStackTrace();
        }
        instance.getProfilCache().remove(uniqueId);
    }
}
