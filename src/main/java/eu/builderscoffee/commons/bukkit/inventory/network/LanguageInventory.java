package eu.builderscoffee.commons.bukkit.inventory.network;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.events.EventHandler;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.events.builderscoffee.LanguageChangeEvent;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This inventory allows players to create a tournament
 */
public class LanguageInventory extends DefaultTemplateInventory {

    public LanguageInventory(Player player) {
        super(MessageUtils.getMessageConfig(player).getInventory().getLanguage().getTitle(), new NetworkInventory(player).INVENTORY);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        // Loop all languages
        for (int i = 0; i < Profil.Languages.values().length; i++) {
            val value = Profil.Languages.values()[i];
            // Add item
            contents.set(2, i, ClickableItem.of(new ItemBuilder(Material.PAINTING).setName(value.name).build(),
                    e -> {
                        // Change language
                        val profil = CommonsBukkit.getInstance().getProfilCache().get(player.getUniqueId().toString());
                        profil.setLang(value);
                        DataManager.getProfilStore().update(profil);

                        // Call event for any update
                        EventHandler.getInstance().callEvent(new LanguageChangeEvent(player, value));

                        // Open network inventory
                        new NetworkInventory(player).INVENTORY.open(player);
                    }));
        }
    }
}
