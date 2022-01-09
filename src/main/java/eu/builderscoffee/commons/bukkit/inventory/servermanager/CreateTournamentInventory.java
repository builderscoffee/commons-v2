package eu.builderscoffee.commons.bukkit.inventory.servermanager;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.commons.bukkit.inventory.templates.DefaultAdminTemplateInventory;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * This inventory allows players to create a tournament
 */
public class CreateTournamentInventory extends DefaultAdminTemplateInventory {

    private short teams = 10;
    private short plotsSize = 32;

    public CreateTournamentInventory(Player player) {
        super(MessageUtils.getMessageConfig(player).getInventory().getCreateTournament().getTitle(), new TournamentInventory(player).INVENTORY, 5, 9);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        super.init(player, contents);

        // Thèmes
        contents.set(2, 5, ClickableItem.of(new ItemBuilder(Material.PAINTING).setName("Thèmes").build(),
                e -> player.sendMessage("§cnot yet implemented")));

        // Temps d'une partie
        contents.set(2, 7, ClickableItem.of(new ItemBuilder(Material.CLOCK).setName("Temps d'une partie").build(),
                e -> player.sendMessage("§cnot yet implemented")));

        // Confirmer
        contents.set(rows - 1, 3, ClickableItem.of(new ItemBuilder(Material.GREEN_WOOL).setName("Confirmer").build(),
                e -> player.sendMessage("§cnot yet implemented")));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        super.update(player, contents);

        // Teams
        contents.set(2, 1, ClickableItem.of(new ItemBuilder(Material.PLAYER_HEAD, teams).setName("Teams: " + teams).build(),
                e -> {
                    val amount = e.getClick().isShiftClick()? 10 : 1;
                    System.out.println(e.getClick().isShiftClick() + " / " + e.getClick().isLeftClick() + " / " + e.getClick().isRightClick());

                    if(e.getClick().isLeftClick())
                        teams += amount;
                    else if(e.getClick().isRightClick())
                        teams -= amount;

                    if(teams < 2)
                        teams = 2;
                }));

        // Taille de plots
        contents.set(2, 3, ClickableItem.of(new ItemBuilder(Material.DIRT, plotsSize, (short) 2).setName("Taille de plots: " + plotsSize).build(),
                e -> {
                    val amount = e.getClick().isShiftClick()? 10 : 1;

                    if(e.getClick().isLeftClick())
                        plotsSize += amount;
                    else if(e.getClick().isRightClick())
                        plotsSize -= amount;

                    if(plotsSize < 16)
                        plotsSize = 16;
                }));
    }
}
