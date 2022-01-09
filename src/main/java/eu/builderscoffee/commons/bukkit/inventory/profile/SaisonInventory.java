package eu.builderscoffee.commons.bukkit.inventory.profile;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.InventoryProvider;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.data.tables.BuildbattleEntity;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.api.common.data.tables.SaisonEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.bukkit.utils.SkullCreator;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SaisonInventory implements InventoryProvider {


    private static final String LIME = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI1OTljNjE4ZTkxNGMyNWEzN2Q2OWY1NDFhMjJiZWJiZjc1MTYxNTI2Mzc1NmYyNTYxZmFiNGNmYTM5ZSJ9fX0=";
    private static final String RED = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjE4NTZjN2IzNzhkMzUwMjYyMTQzODQzZDFmOWZiYjIxOTExYTcxOTgzYmE3YjM5YTRkNGJhNWI2NmJlZGM2In19fQ==";
    private static final String HISTORIQUE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTg4N2NjMzg4YzhkY2ZjZjFiYThhYTVjM2MxMDJkY2U5Y2Y3YjFiNjNlNzg2YjM0ZDRmMWMzNzk2ZDNlOWQ2MSJ9fX0=";
    private static final ClickableItem blackGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem grayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem orangeGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.ORANGE_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ItemStack limeConcrete = new ItemStack(Material.LIME_CONCRETE);
    private static final ItemStack redConcrete = new ItemStack(Material.RED_CONCRETE);
    private static final ItemStack historiqueSkull = SkullCreator.itemFromBase64(HISTORIQUE);
    private static final ItemStack limeSkull = SkullCreator.itemFromBase64(LIME);
    private static final ItemStack redSkull = SkullCreator.itemFromBase64(RED);
    public final SmartInventory INVENTORY;
    private final CommonsBukkit commonsBukkit = CommonsBukkit.getInstance();
    private final ProfilEntity profilEntity;
    private final SaisonEntity saisonEntity;

    public SaisonInventory(ProfilEntity profilEntity, SaisonEntity saisonEntity) {
        if (profilEntity == null)
            throw new NullPointerException("Profile can't be null");

        if (saisonEntity == null)
            throw new NullPointerException("Profile can't be null");

        this.saisonEntity = saisonEntity;
        this.profilEntity = profilEntity;

        INVENTORY = SmartInventory.builder()
                .id("profile_saison")
                .provider(this)
                .size(6, 9)
                .title(ChatColor.WHITE + "Saison " + saisonEntity.getId())
                .manager(CommonsBukkit.getInstance().getInventoryManager())
                .build();
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors) {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }

    private static boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        val messages = MessageUtils.getMessageConfig(player);

        //Fill Black top and bottom
        contents.fillRow(0, grayGlasses);
        contents.fillRow(5, grayGlasses);

        //Fill Timeline
        contents.fillRect(SlotPos.of(2, 0), SlotPos.of(2, 8), blackGlasses);

        short i = 0;
        short iTimeline = 0;

        for (BuildbattleEntity bb : saisonEntity.getBuildbattles()) {
            val date = new Date();
            if (bb.getDate().before(date)) {
                if (profilEntity.getNotes().stream().filter(note -> note.getBuildbattle().getId() == bb.getId()).count() > 0) {
                    ItemStack skull = new ItemBuilder(bb.isStep() ? new ItemStack(limeConcrete) : new ItemStack(limeSkull))
                            .setName("§b" + bb.getNum() + ". " + bb.getType().getName())
                            .addGLow()
                            .build();
                    contents.set(SlotPos.of(bb.isStep() ? 1 : 3, i), ClickableItem.of(skull, e -> {
                        new NoteInventory(profilEntity, bb).INVENTORY.open(player);
                    }));
                } else {
                    ItemStack skull = new ItemBuilder(bb.isStep() ? new ItemStack(redConcrete) : new ItemStack(redSkull)).setName("§c" + bb.getNum() + ". " + bb.getType().getName()).build();
                    contents.set(SlotPos.of(bb.isStep() ? 1 : 3, i), ClickableItem.empty(skull));
                }
                i++;
                iTimeline++;
                if (i == 9) break;
            } else if (sameDay(bb.getDate(), date)) {
                iTimeline++;
            }
        }

        //Fill Timeline
        contents.fillRect(SlotPos.of(2, 0), SlotPos.of(2, iTimeline - 1), orangeGlasses);

        // Historique
        contents.set(5, 8, ClickableItem.of(new ItemBuilder(historiqueSkull).setName(messages.getProfil().getHistoryItem().replace("&", "§")).build(),
                e -> {
                    new SaisonsInventory(profilEntity).INVENTORY.open(player);
                }));

        // Retour
        contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(messages.getRetourItem().replace("&", "§")).build(),
                e -> new SaisonsInventory(profilEntity).INVENTORY.open(player)));

        // Quitter
        contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).setName(messages.getNetwork().getCloseItem().replace("&", "§")).build(),
                e -> contents.inventory().close(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Nothing to do here
    }
}
