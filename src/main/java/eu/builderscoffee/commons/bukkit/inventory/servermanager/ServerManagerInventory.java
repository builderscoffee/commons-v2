package eu.builderscoffee.commons.bukkit.inventory.servermanager;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.redisson.Redis;
import eu.builderscoffee.api.common.redisson.infos.Server;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.inventory.OptionInventory;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.redisson.packets.ServerManagerRequest;
import eu.builderscoffee.commons.common.redisson.packets.ServerManagerResponse;
import eu.builderscoffee.commons.common.redisson.topics.CommonTopics;
import eu.builderscoffee.commons.common.utils.Triplet;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.redisson.api.RSortedSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This inventory allows players to manager a specific server
 */
public class ServerManagerInventory extends DefaultAdminTemplateInventory {

    @Getter
    private static ArrayList<Triplet<Player, ServerManagerInventory, String>> chatRequests = new ArrayList<>();

    private final Server server;
    private ServerManagerResponse response;
    private int page;

    public ServerManagerInventory(Player player, Server server) {
        super(server.getHostName(), new ServersManagerInventory(player).INVENTORY, 5, 9);
        this.server = server;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        val messages = MessageUtils.getMessageConfig(player).getInventory().getServerManager();

        // Server item
        setServerItem(contents, server);

        // Stop item
        val stopItem = new ItemBuilder(Material.RED_CONCRETE).setName(messages.getStopServer().replace("&", "§"));
        if (!server.getStartingMethod().equals(Server.ServerStartingMethod.DYNAMIC))
            stopItem.addLoreLine("§cImpossible de stopper ce type de serveur pour le moment.");
        contents.set(0, 8, ClickableItem.of(stopItem.build(),
                e -> {
                    if (server.getStartingMethod().equals(Server.ServerStartingMethod.DYNAMIC)) {
                        new OptionInventory(messages.getTitle()
                                .replace("&", "§")
                                .replace("%server%", server.getHostName()),
                                messages.getStopServerConfirmationQuestion()
                                        .replace("&", "§")
                                        .replace("%server%", server.getHostName()),
                                this.INVENTORY,
                                (e1, p1) -> {
                                    server.stop();
                                    new ServersManagerInventory(player).INVENTORY.open(player);
                                },
                                (e2, p2) ->{
                                    this.INVENTORY.open(player);
                                }).INVENTORY.open(player);

                    }
                }));

        // Demander au serveur si une configuration est possible ou néscessaire
        if(Objects.isNull(response)){
            contents.fillSquare(SlotPos.of(1, 0), SlotPos.of(3, columns - 1), grayGlasses);
            sendConfigRequest(player, "request_config", "", ServerManagerRequest.ItemAction.NONE, contents);
        }
        else {
            contents.fillSquare(SlotPos.of(1, 0), SlotPos.of(3, columns - 1), lightGrayGlasses);

            // loop all items
            response.getActions().forEach(action -> {
                if (action instanceof ServerManagerResponse.Items) {
                    val itemsAction = (ServerManagerResponse.Items) action;

                    itemsAction.getItems().forEach(itemInfo -> contents.set(itemInfo.getFirst(), itemInfo.getSecond(), item(player, contents, itemInfo.getThird(), itemsAction.getType(), itemInfo.getFourth())));
                } else if (action instanceof ServerManagerResponse.PageItems) {
                    val pageItemsAction = (ServerManagerResponse.PageItems) action;

                    // Set items in pagination system
                    val iterator = pageItemsAction.getItems().stream()
                            .map(tuple -> item(player, contents, tuple.getLeft(), pageItemsAction.getType(), tuple.getRight()))
                            .skip(pageItemsAction.getMaxPerPage() * page)
                            .limit(pageItemsAction.getMaxPerPage())
                            .collect(Collectors.toList()).iterator();

                    for(int row = 1; row <= 3 && iterator.hasNext(); row++){
                        for(int column = 0; column < 9 && iterator.hasNext(); column++){
                            contents.set(row, column, iterator.next());
                        }
                    }

                    // Previous
                    if(page != 0)
                        contents.set(rows - 1, 3, ClickableItem.of(pageItemsAction.getPreviousPageItem(), e -> {
                            page--;
                            init(player, contents);
                        }));

                    // Next
                    if(pageItemsAction.getItems().size() / pageItemsAction.getMaxPerPage() > page)
                        contents.set(rows - 1, 5, ClickableItem.of(pageItemsAction.getNextPageItem(), e -> {
                            page++;
                            init(player, contents);
                        }));
                } else if (action instanceof ServerManagerResponse.ChatRequest) {
                    val chatRequestAction = (ServerManagerResponse.ChatRequest) action;

                    if (Objects.nonNull(chatRequestAction.getMessage()))
                        player.sendMessage(chatRequestAction.getMessage());

                    chatRequests.add(new Triplet<>(player, this, chatRequestAction.getType()));
                    Bukkit.getScheduler().runTask(CommonsBukkit.getInstance(), player::closeInventory);
                    return;
                } else if (action instanceof ServerManagerResponse.ChatResponse) {
                    val chatresponseAction = (ServerManagerResponse.ChatResponse) action;

                    if (Objects.nonNull(chatresponseAction.getMessage()))
                        player.sendMessage(chatresponseAction.getMessage());
                }
            });

            // If true will ignore
            if (response.isFinished()) {
                Bukkit.getScheduler().runTask(CommonsBukkit.getInstance(), player::closeInventory);
                return;
            }
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        final RSortedSet<Server> servers = Redis.getRedissonClient().getSortedSet("servers");

        // Vérifie que la liste existe
        if (servers == null) return;

        servers.stream().filter(s -> s.getHostName().equals(server.getHostName())).forEach(s -> setServerItem(contents, s));
    }

    public void sendConfigRequest(@NonNull Player player, @NonNull String type, @NonNull String data, @NonNull ServerManagerRequest.ItemAction itemAction, InventoryContents contents) {
        // Create request
        val configPacket = new ServerManagerRequest();

        // Define target server & action
        configPacket.setTargetServerName(server.getHostName());
        configPacket.setUuid(player.getUniqueId().toString());
        configPacket.setType(type);
        configPacket.setData(data);
        configPacket.setItemAction(itemAction);

        // Show items on response
        configPacket.onResponse = response -> {
            this.response = response;
            //contents.fill(null);
            init(player, contents);
        };

        // Send request
        Redis.publish(CommonTopics.SERVER_MANAGER, configPacket);
    }

    private ClickableItem item(Player player, InventoryContents contents, ItemStack itemstack, String type, String action){
        return ClickableItem.of(itemstack, e -> {
            ServerManagerRequest.ItemAction currentItemAction = ServerManagerRequest.ItemAction.NONE;
            switch (e.getClick()){
                case LEFT:
                    currentItemAction = ServerManagerRequest.ItemAction.LEFT_CLICK;
                    break;
                case SHIFT_LEFT:
                    currentItemAction = ServerManagerRequest.ItemAction.SHIFT_LEFT_CLICK;
                    break;
                case RIGHT:
                    currentItemAction = ServerManagerRequest.ItemAction.RIGHT_CLICK;
                    break;
                case SHIFT_RIGHT:
                    currentItemAction = ServerManagerRequest.ItemAction.SHIFT_RIGHT_CLICK;
                    break;
                case MIDDLE:
                    currentItemAction = ServerManagerRequest.ItemAction.MIDDLE_CLICK;
                    break;
                case DROP:
                    currentItemAction = ServerManagerRequest.ItemAction.DROP;
                    break;
            }
            sendConfigRequest(player, type, action, currentItemAction, contents);
        });
    }

    private void setServerItem(InventoryContents contents, Server s){
        contents.set(0, 4, ClickableItem.empty(new ItemBuilder(Material.OBSERVER)
                .setName("État")
                .addLoreLine("§bLast heartbeat at §a" + new SimpleDateFormat("EEE dd MMM yyyy à hh:mm:ss", Locale.FRANCE).format(s.getLastHeartbeat()))
                .addLoreLine("§bServerType: §a" + s.getServerType())
                .addLoreLine("§bStarting method: §a" + s.getStartingMethod())
                .addLoreLine("§bServer status: §a" + s.getServerStatus())
                .addLoreLine("§bPlayers: §a" + s.getPlayerCount())
                .addLoreLine("§bMaximum players: §a" + s.getPlayerMaximum())
                .addLoreLine(s.getProperties().entrySet().stream()
                        .map(entry -> "§b" + entry.getKey() + ": §a" + entry.getValue())
                        .sorted(String::compareTo)
                        .collect(Collectors.toList()))
                .build()));
    }
}
