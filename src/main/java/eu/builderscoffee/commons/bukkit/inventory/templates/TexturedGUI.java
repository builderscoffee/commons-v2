package eu.builderscoffee.commons.bukkit.inventory.templates;

import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum TexturedGUI {

    NETWORK(100),
    NETWORK2(101);

    int modelData;

    TexturedGUI(int modelData) {
        this.modelData = modelData;
    }

    public ItemStack getItem() {
        return getItemBuilder().build();
    }

    public ItemBuilder getItemBuilder() {
        return new ItemBuilder(Material.POPPY)
                .setCustomModelData(modelData);
    }

    public int getModelData() {
        return modelData;
    }
}