package eu.builderscoffee.commons.bukkit.inventory.servermanager;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.RedisTopic;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.api.common.redisson.packets.types.playpen.actions.ProvisionServerPacket;
import eu.builderscoffee.commons.bukkit.inventory.OptionInventory;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.configuration.SettingsConfig;
import lombok.val;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.redisson.api.RSortedSet;

/**
 * This inventory allows players to create a server
 */
public class CreateServerInventory extends DefaultAdminTemplateInventory {

    public CreateServerInventory(Player player) {
        super(MessageUtils.getMessageConfig(player).getInventory().getCreateServer().getTitle(), new ServersManagerInventory(player).INVENTORY, 5, 9);
    }

    private SettingsConfig.PluginMode newServerMode = SettingsConfig.PluginMode.PRODUCTION;

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        // Fill Grey panes
        contents.fillRect(SlotPos.of(1, 0), SlotPos.of(1, 8), grayGlasses);
        contents.fillRect(SlotPos.of(2, 0), SlotPos.of(2, 1), grayGlasses);
        contents.fillRect(SlotPos.of(2, 7), SlotPos.of(2, 8), grayGlasses);
        contents.fillRect(SlotPos.of(3, 0), SlotPos.of(3, 8), grayGlasses);

        // Proxy
        contents.set(2, 2, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).setName("Proxy").addLoreLine("§bServer mode: " + newServerMode).addLoreLine("").addLoreLine("§bShift click pour changer de mode").build(),
                e -> {
                    if(e.isShiftClick()){
                        newServerMode = newServerMode.equals(SettingsConfig.PluginMode.PRODUCTION)? SettingsConfig.PluginMode.DEVELOPMENT : SettingsConfig.PluginMode.PRODUCTION;
                        init(player, contents);
                    }
                    else{
                        new OptionInventory("Démarer un serveur",
                                "Êtes vous sûr de vouloir démarer \nun serveur §fproxy §cen §f" + newServerMode.toString() + "§c?",
                                this.INVENTORY,
                                (e1, p1) -> {
                                    createServer("proxy");
                                    new ServersManagerInventory(player).INVENTORY.open(player);
                                },
                                (e2, p2) ->{
                                    this.INVENTORY.open(player);
                                }).INVENTORY.open(player);
                    }
                }));

        // Hub
        contents.set(2, 4, ClickableItem.of(new ItemBuilder(Material.MAGENTA_WOOL).setName("Hub").addLoreLine("§bServer mode: " + newServerMode).addLoreLine("").addLoreLine("§bShift click pour changer de mode").build(),
                e -> {
                    if(e.isShiftClick()){
                        newServerMode = newServerMode.equals(SettingsConfig.PluginMode.PRODUCTION)? SettingsConfig.PluginMode.DEVELOPMENT : SettingsConfig.PluginMode.PRODUCTION;
                        init(player, contents);
                    }
                    else{
                        new OptionInventory("Démarer un serveur",
                                "Êtes vous sûr de vouloir démarer \nun serveur §fhub §cen §f" + newServerMode.toString() + "§c?",
                                this.INVENTORY,
                                (e1, p1) -> {
                                    createServer("hub");
                                    new ServersManagerInventory(player).INVENTORY.open(player);
                                },
                                (e2, p2) ->{
                                    this.INVENTORY.open(player);
                                })
                                .INVENTORY.open(player);
                    }
                }));

        // Plot
        contents.set(2, 6, ClickableItem.of(new ItemBuilder(Material.GRAY_WOOL).setName("Plot").addLoreLine("§bServer mode: " + newServerMode).addLoreLine("").addLoreLine("§bShift click pour changer de mode").build(),
                e -> {
                    if(e.isShiftClick()){
                        newServerMode = newServerMode.equals(SettingsConfig.PluginMode.PRODUCTION)? SettingsConfig.PluginMode.DEVELOPMENT : SettingsConfig.PluginMode.PRODUCTION;
                        init(player, contents);
                    }
                    else{
                        new OptionInventory("Démarer un serveur",
                                "Êtes vous sûr de vouloir démarer \nun serveur §fplot §cen §f" + newServerMode.toString() + "§c?",
                                this.INVENTORY,
                                (e1, p1) -> {
                                    createServer("plot");
                                    new ServersManagerInventory(player).INVENTORY.open(player);
                                },
                                (e2, p2) ->{
                                    this.INVENTORY.open(player);
                                })
                                .INVENTORY.open(player);
                    }
                }));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        super.update(player, contents);
    }

    private void createServer(String type){
        final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");
        val name = type + (newServerMode.equals(SettingsConfig.PluginMode.DEVELOPMENT)? "-dev" : "") + "-";
        var exist = true;
        for(var i = 1; exist; i++){
            int finalI = i;
            exist = servers.stream().anyMatch(server -> server.getHostName().equalsIgnoreCase(name + finalI));
            if(!exist){
                val packet = new ProvisionServerPacket();
                packet.setNewServerName(name + i);
                packet.setNewServerPacketId(type);
                packet.setNewServerVersion(newServerMode.toString());

                Redis.publish(RedisTopic.PLAYPEN, packet);
            }
        }
    }
}
