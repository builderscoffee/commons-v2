package eu.builderscoffee.commons.bukkit.inventory.network;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.commons.bukkit.inventory.servermanager.ServersManagerInventory;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultTemplateInventory;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.inventory.templates.TexturedGUI;
import eu.builderscoffee.commons.bukkit.inventory.templates.TexturedItems;
import eu.builderscoffee.commons.bukkit.inventory.templates.TexturedTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This inventory allows players to navigate through servers
 */
public class NetworkInventory extends TexturedTemplateInventory {

    /*public NetworkInventory(Player player) {
        // Todo Title being translated
        super("Menu de Builders Coffee", null, 5, 9);
    }*/

    public NetworkInventory(Player player) {
        super("Menu de Builders Coffee", null, new TexturedTemplateInventory.TexturedOptions()
                .setFillAllBlankItem(true), 5, 9);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        //super.init(player, contents);
        val messages = MessageUtils.getMessageConfig(player);

        contents.set(0, 0, ClickableItem.empty(TexturedGUI.NETWORK.getItem()));

        // WIP
        contents.set(2, 3, ClickableItem.empty(TexturedItems.LIGHT_LETTER_W.getItemBuilder().setName("§6Work in progress").build()));
        contents.set(2, 4, ClickableItem.empty(TexturedItems.LIGHT_LETTER_I.getItemBuilder().setName("§6Work in progress").build()));
        contents.set(2, 5, ClickableItem.empty(TexturedItems.LIGHT_LETTER_P.getItemBuilder().setName("§6Work in progress").build()));

        if(player.hasPermission(CommonsBukkit.getInstance().getPermissions().getServerManagerSee())){
            // ServerManager
            contents.set(4, 0, ClickableItem.of(TexturedItems.LIGHT_MANAGE.getItemBuilder().setName(messages.getNetwork().getServerManagerItem().replace("&", "§")).build(),
                    e -> new ServersManagerInventory(player).INVENTORY.open(player)));
        }

        // Langues
        /*contents.set(4, 7, ClickableItem.of(new ItemBuilder(Material.PAINTING).setName(messages.getNetwork().getLanguageItem().replace("&", "§")).build(),
                e -> new LanguageInventory(player).INVENTORY.open(player)));*/

        // Cosmétiques
        /*contents.set(4, 8, ClickableItem.of(new ItemBuilder(Material.CHEST).setName(messages.getNetwork().getCosmeticsItem().replace("&", "§")).build(),
                e -> player.sendMessage("§cIl n'y a pas de grains de café en stock")));*/
    }
}
