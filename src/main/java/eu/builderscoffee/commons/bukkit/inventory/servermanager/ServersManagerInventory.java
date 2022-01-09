package eu.builderscoffee.commons.bukkit.inventory.servermanager;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.SlotIterator;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.inventory.network.NetworkInventory;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.BungeeUtils;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.redisson.api.RSortedSet;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This inventory allows players to manage multiple servers
 */
public class ServersManagerInventory extends DefaultAdminTemplateInventory {

    public ServersManagerInventory(Player player) {
        super(MessageUtils.getMessageConfig(player).getInventory().getServerManager().getTitle(), new NetworkInventory(player).INVENTORY, 5, 9);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        val messages = MessageUtils.getMessageConfig(player).getInventory().getServersManager();

        // Create server
        contents.set(rows - 1, 3, ClickableItem.of(new ItemBuilder(Material.NETHER_STAR).setName(messages.getCreateServer().replace("&", "§")).build(),
                e -> new CreateServerInventory(player).INVENTORY.open(player)));

        // Tournaments
        contents.set(rows - 1, 5, ClickableItem.of(new ItemBuilder(Material.BLACK_BANNER).setName(messages.getManageTournaments().replace("&", "§")).build(),
                e -> new TournamentInventory(player).INVENTORY.open(player)));

        // Server Activities
        contents.set(rows - 1, 7, ClickableItem.of(new ItemBuilder(Material.OAK_SIGN).setName(messages.getServersActivities().replace("&", "§")).build(),
                e -> new ServersActivitiesInventory(player).INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        val messages = MessageUtils.getMessageConfig(player).getInventory().getServersManager();

        val serverItems = new ArrayList<ClickableItem>();
        // Get data from redisson
        final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");

        // Check if server list exists
        if (servers == null) return;

        // Creating temporary servers list to avoid changes
        val tempServers = servers.stream().collect(Collectors.toList());

        // Loop each server
        if (tempServers.stream().count() > 0)
            tempServers.stream()
                    .sorted()
                    .forEach(s -> {
                        val itemB = new ItemBuilder(Material.OBSERVER)
                                .setName(s.getHostName() + " §7(" + s.getServerStatus().name().toLowerCase() + ")");
                        if (s.getServerStatus().equals(Server.ServerStatus.STARTING))
                            itemB.addLoreLine("§fEn attente d'une réponse")
                                    .addLoreLine("")
                                    .addLoreLine(messages.getServerStopOnDrop().replace("&", "§"));
                        else {
                            itemB.addLoreLine("§f" + capitalizeFirstLetter(s.getStartingMethod().name()) + " " + s.getServerType().name().toLowerCase() + " §bserver")
                                    .addLoreLine("§f" + s.getPlayerCount() + "§b/§f" + s.getPlayerMaximum() + " §bjoueurs");
                            if (s.getProperties().entrySet().size() > 0) {
                                itemB.addLoreLine("§bDonnées supplémentaires: ");
                                itemB.addLoreLine(s.getProperties().entrySet().stream()
                                        .map(entry -> "  §b" + entry.getKey() + ": §f" + entry.getValue())
                                        .sorted(String::compareTo)
                                        .collect(Collectors.toList()));
                            }
                            itemB.addLoreLine("")
                                    .addLoreLine(messages.getServerLeftClick().replace("&", "§"))
                                    .addLoreLine(messages.getServerRightClick().replace("&", "§"));
                        }

                        serverItems.add(ClickableItem.of(itemB.build(),
                                e -> {
                                    if (s.getServerStatus().equals(Server.ServerStatus.STARTING)){
                                        if (e.getClick().equals(ClickType.DROP)){
                                            s.stop();
                                        }
                                    }
                                    else{
                                        if (e.isLeftClick())
                                            new ServerManagerInventory(player, s).INVENTORY.open(player);
                                        else if (e.isRightClick())
                                            BungeeUtils.sendPlayerToServer(CommonsBukkit.getInstance(), player, s.getHostName());
                                    }
                                }));
                    });
        // Add items in inventory
        contents.pagination().setItems(serverItems.toArray(new ClickableItem[0]));
        contents.pagination().setItemsPerPage(27);

        // Define how items are arranged
        contents.pagination().addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)));
    }

    private String capitalizeFirstLetter(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
