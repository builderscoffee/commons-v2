package eu.builderscoffee.commons.bukkit.inventory.templates;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.InventoryProvider;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class is a template inventory
 */
public class DefaultTemplateInventory implements InventoryProvider {

    public final SmartInventory INVENTORY;
    protected static final ClickableItem blackGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).setName("§a").build());
    protected static final ClickableItem grayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setName("§a").build());
    protected static final ClickableItem lightGrayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)).setName("§a").build());

    protected final SmartInventory previousInventory;

    protected int rows;
    protected int columns;

    /***
     * @param title Title of inventory
     * @param previousInventory If not null, will show the back arrow at the bottom
     */
    public DefaultTemplateInventory(@NonNull String title, SmartInventory previousInventory) {
        this(title, previousInventory, 6, 9);
    }

    /***
     * @param title Title of inventory
     * @param previousInventory If not null, will show the back arrow at the bottom
     * @param rows Define amounts of rows
     * @param columns Define the amounts of columns
     */
    public DefaultTemplateInventory(@NonNull String title, SmartInventory previousInventory, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.INVENTORY = SmartInventory.builder()
                .id(this.getClass().getName())
                .provider(this)
                .size(rows, columns)
                .title(ChatColor.WHITE + title)
                .manager(CommonsBukkit.getInstance().getInventoryManager())
                .build();
        this.previousInventory = previousInventory;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //Fill Grey borders
        if(rows > 2){
            contents.fillRect(SlotPos.of(0, 0), SlotPos.of(0, columns - 1), blackGlasses);
            contents.fillRect(SlotPos.of(rows - 1, 0), SlotPos.of(rows - 1, columns - 1), blackGlasses);

            if(previousInventory != null){
                // Retour
                contents.set(rows - 1, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(MessageUtils.getMessageConfig(player).getRetourItem().replace("&", "§")).build(),
                        e -> previousInventory.open(player)));
            }
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Nothing to do
    }
}
