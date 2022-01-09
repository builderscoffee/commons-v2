package eu.builderscoffee.commons.bukkit.inventory.servermanager;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.SlotIterator;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.ServerActivityEntity;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * This inventory allows players to manage multiple servers
 */
public class ServersActivitiesInventory extends DefaultAdminTemplateInventory {

    private int state;

    public ServersActivitiesInventory(Player player) {
        super(MessageUtils.getMessageConfig(player).getInventory().getServersActivities().getTitle(), new ServersManagerInventory(player).INVENTORY, 5, 9);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);
    }

    @SneakyThrows
    @Override
    public void update(Player player, InventoryContents contents) {
        if (state++ % 20 == 0) return;

        val results = DataManager.getServerActivityStore().select(ServerActivityEntity.class).orderBy(ServerActivityEntity.DATE.desc()).get();
        if (contents.pagination().getItems().length == results.stream().count())
            return;

        val items = new ArrayList<ClickableItem>();

        results.forEach(activity -> {
            val lore = new ArrayList<String>();
            // Get list of each word
            val split = ("§b" + activity.getMessage().replaceAll(activity.getServerName(), "§f" + activity.getServerName() + "§b")).trim().split(" ");

            // Colorize at needed and limit characters per line
            String line = "";
            for (int i = 0; i < split.length; i++) {
                val word = split[i];

                if (word.contains("''"))
                    line += (line.isEmpty() ? "" : " ") + "§f" + word.replaceAll("''", "") + "§b";
                else
                    line += (line.isEmpty() ? "" : " ") + word;

                if (line.length() > 30 && i != split.length - 1) {
                    lore.add("§b" + line);
                    line = "";
                }
            }

            lore.add("§b" + line);

            // Add date to lore
            lore.add("§f" + new SimpleDateFormat("EEE dd MMM yyyy à hh:mm:ss", Locale.FRANCE).format(activity.getDate()));

            // Create item
            items.add(ClickableItem.empty(new ItemBuilder(Material.PAPER).setName(activity.getServerName()).addLoreLine(lore).build()));
        });

        // Add items to inventory
        contents.pagination().setItems(items.toArray(new ClickableItem[0]));
        contents.pagination().setItemsPerPage(27);

        // Define how items needs to be displayed
        contents.pagination().addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)));

        // Previous page
        if (!contents.pagination().isFirst())
            contents.set(rows - 1, 3, ClickableItem.of(new ItemBuilder(Material.ARROW).setName("Précédent").build(),
                    e -> INVENTORY.open(player, contents.pagination().previous().getPage())));

        // Next page
        if (!contents.pagination().isLast())
            contents.set(rows - 1, 5, ClickableItem.of(new ItemBuilder(Material.ARROW).setName("Suivant").build(),
                    e -> INVENTORY.open(player, contents.pagination().next().getPage())));
    }
}
