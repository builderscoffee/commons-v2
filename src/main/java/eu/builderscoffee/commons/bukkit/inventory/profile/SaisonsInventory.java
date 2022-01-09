package eu.builderscoffee.commons.bukkit.inventory.profile;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.InventoryProvider;
import eu.builderscoffee.api.bukkit.gui.content.SlotIterator;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.api.common.data.tables.SaisonEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;

public class SaisonsInventory implements InventoryProvider {

    public final SmartInventory INVENTORY;

    private final CommonsBukkit commonsBukkit = CommonsBukkit.getInstance();

    private final ProfilEntity profilEntity;

    private static final ClickableItem blackGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).setName("§a").build());

    private static final ItemStack cyanConcrete = new ItemStack(Material.CYAN_CONCRETE);

    public SaisonsInventory(ProfilEntity profilEntity){
        if(profilEntity == null)
            throw new NullPointerException("Profile can't be null");

        this.profilEntity = profilEntity;

        INVENTORY = SmartInventory.builder()
                .id("profile_saisons")
                .provider(this)
                .size(6, 9)
                .title(ChatColor.WHITE + "Liste des saisons")
                .manager(CommonsBukkit.getInstance().getInventoryManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        val pagination = contents.pagination();
        val messages = MessageUtils.getMessageConfig(player);

        //Fill Black top and bottom
        contents.fillRow(0, blackGlasses);
        contents.fillRow(5, blackGlasses);

        //Fill Timeline
        contents.fillRect(SlotPos.of(2, 0), SlotPos.of(2, 8), blackGlasses);

        // Get saisons
        try(val saison = DataManager.getSaisonsStore().select(SaisonEntity.class)
                .orderBy(SaisonEntity.ID)
                .get()){

            final short size = (short) saison.stream().count();
            ClickableItem[] saisonsItems = new ClickableItem[size];


            // TODO Corriger problème de liste
            for (int i = 0; i < size; i++) {
                val saisonEntity = saison.toList().get(i);
                if(saisonEntity.getBeginDate().before(new Date())){
                    saisonsItems[i] = ClickableItem.of(new ItemBuilder(new ItemStack(cyanConcrete)).setName("Saison " + saisonEntity.getId()).build(),
                            e -> {
                                new SaisonInventory(profilEntity, saisonEntity).INVENTORY.open(player);
                            });
                }
            }


            // Retour
            contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(messages.getRetourItem().replace("&", "§")).build(),
                    e -> new ProfilInventory(profilEntity).INVENTORY.open(player)));


            // Quitter
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).setName(messages.getNetwork().getCloseItem().replace("&", "§")).build(),
                    e -> contents.inventory().close(player)));

            pagination.setItems(saisonsItems);
            pagination.setItemsPerPage(36);

            //Fill Plots Item
            pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Nothing to do here
    }
}
