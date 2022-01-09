package eu.builderscoffee.commons.bukkit.inventory;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import lombok.NonNull;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.function.BiConsumer;

/**
 * This class is used to asks to make a choice between 2 options (yes or no) in an inventory
 */
public class OptionInventory extends DefaultAdminTemplateInventory {

    private final BiConsumer<Player, InventoryClickEvent> acceptedAction;
    private final BiConsumer<Player, InventoryClickEvent> rejectedAction;
    private final String option;

    /**
     *
     * @param title Title of inventory
     * @param option Question to be asked to player
     * @param previousInventory If not null, will show the back arrow at the bottom
     * @param acceptedAction Action executed if user clicked on "Yes" item
     * @param rejectedAction Action executed if user clicked on "No" item
     */
    public OptionInventory(@NonNull String title, @NonNull String option, SmartInventory previousInventory, @NonNull BiConsumer<Player, InventoryClickEvent> acceptedAction, @NonNull BiConsumer<Player, InventoryClickEvent> rejectedAction) {
        super(title, previousInventory, 5, 9);
        this.option = option;
        this.acceptedAction = acceptedAction;
        this.rejectedAction = rejectedAction;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        // Fill Grey panes
        contents.fillRect(SlotPos.of(1, 0), SlotPos.of(1, 8), grayGlasses);
        contents.fillRect(SlotPos.of(2, 0), SlotPos.of(2, 2), grayGlasses);
        contents.fillRect(SlotPos.of(2, 6), SlotPos.of(2, 8), grayGlasses);
        contents.fillRect(SlotPos.of(3, 0), SlotPos.of(3, 8), grayGlasses);

        // Option item on multiple lines
        val item = new ItemBuilder(Material.OAK_SIGN);
        if(option.contains("\n")){
            val split = option.split("\n");
            item.setName("§c" + split[0]);
            for(int i = 1; i < split.length; i++){
                item.addLoreLine("§c" + split[i]);
            }
        }
        // Option item on one line
        else{
            item.setName("§c" + option);
        }

        // Option item
        contents.set(0, 4, ClickableItem.empty(item.build()));

        // Yes item
        contents.set(2, 3, ClickableItem.of(new ItemBuilder(Material.GREEN_WOOL).setName("§aOui").build(),
                e -> acceptedAction.accept(player, e)));

        // No item
        contents.set(2, 5, ClickableItem.of(new ItemBuilder(Material.RED_WOOL).setName("§cNon").build(),
                e -> rejectedAction.accept(player, e)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

    }
}
