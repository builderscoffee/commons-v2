package eu.builderscoffee.commons.bukkit.inventory.templates;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.InventoryProvider;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * This class is a template inventory
 */
public class TexturedTemplateInventory implements InventoryProvider {

    public final SmartInventory INVENTORY;
    protected final SmartInventory previousInventory;

    protected int rows;
    protected int columns;
    protected TexturedOptions options;

    /***
     * @param title Title of inventory
     * @param previousInventory If not null, will show the back arrow at the bottom
     */
    public TexturedTemplateInventory(@NonNull String title, SmartInventory previousInventory, TexturedOptions options) {
        this(title, previousInventory, options, 6, 9);
    }

    /***
     * @param title Title of inventory
     * @param previousInventory If not null, will show the back arrow at the bottom
     * @param rows Define amounts of rows
     * @param columns Define the amounts of columns
     */
    public TexturedTemplateInventory(@NonNull String title, SmartInventory previousInventory, TexturedOptions options, int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.options = options;
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
        if(options.fillAllBlankItem)
            contents.fill(ClickableItem.empty(TexturedItems.INVENTORY_PART.getItem()));

        // Horizontal Borders
        contents.fillRect(0, 1, 0, columns - 2, ClickableItem.empty(TexturedItems.LIGHT_HORIZONTAL_LINE.getItem()));
        contents.fillRect(rows - 1, 1, rows - 1, columns - 2, ClickableItem.empty(TexturedItems.LIGHT_HORIZONTAL_LINE.getItem()));

        // Vertical Borders
        contents.fillRect(1, 0, rows - 2, 0, ClickableItem.empty(TexturedItems.LIGHT_VERTICAL_LINE.getItem()));
        contents.fillRect(1, columns - 1, rows - 2, columns - 1, ClickableItem.empty(TexturedItems.LIGHT_VERTICAL_LINE.getItem()));

        // Corners
        contents.set(0, 0, ClickableItem.empty(TexturedItems.LIGHT_CORNER_TOP_LEFT.getItem()));
        contents.set(0, columns - 1, ClickableItem.empty(TexturedItems.LIGHT_CORNER_TOP_RIGHT.getItem()));
        contents.set(rows - 1, 0, ClickableItem.empty(TexturedItems.LIGHT_CORNER_BOTTOM_LEFT.getItem()));
        contents.set(rows - 1, columns - 1, ClickableItem.empty(TexturedItems.LIGHT_CORNER_BOTTOM_RIGHT.getItem()));

        if(options.previousNextItem && !contents.pagination().isFirst())
            contents.set(rows - 1, 3, ClickableItem.of(TexturedItems.LIGHT_ARROW_1_LEFT.getItem(), e -> INVENTORY.open(player, contents.pagination().previous().getPage())));

        if(options.previousNextItem && !contents.pagination().isLast())
            contents.set(rows - 1, 5, ClickableItem.of(TexturedItems.LIGHT_ARROW_1_RIGHT.getItem(), e -> INVENTORY.open(player, contents.pagination().next().getPage())));

        if(options.refreshItem)
            contents.set(rows - 1, 4, ClickableItem.of(TexturedItems.LIGHT_REFRESH.getItem(), e -> init(player, contents)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Nothing to do
    }

    @Setter
    @Getter
    @Accessors(chain = true)
    public static class TexturedOptions{
        private boolean previousNextItem = false;
        private boolean refreshItem = false;
        private boolean fillAllBlankItem = false;

        public static TexturedOptions empty(){
            return new TexturedOptions();
        }
    }
}
