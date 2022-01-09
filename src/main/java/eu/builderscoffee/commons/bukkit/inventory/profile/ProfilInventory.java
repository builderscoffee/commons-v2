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
import eu.builderscoffee.api.common.data.*;
import eu.builderscoffee.commons.common.utils.LuckPermsUtils;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ProfilInventory implements InventoryProvider {

    public final SmartInventory INVENTORY;

    private final CommonsBukkit commonsBukkit = CommonsBukkit.getInstance();

    private final ProfilEntity profilEntity;

    private static final ClickableItem blackGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem grayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem lightgrayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)).setName("§a").build());

    private static final String GLOBE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzEwNzdlODcxZDkxYjdmZWEyNGIxZjY4MDhlMDg4ZDdiODQyZGE1MTZmNjM1NmNlOTE2MTM0OTQ0MzFhZThhMCJ9fX0=";


    public ProfilInventory(ProfilEntity profilEntity){
        if(profilEntity == null)
            throw new NullPointerException("Profile can't be null");

        this.profilEntity = profilEntity;

        INVENTORY = SmartInventory.builder()
                .id("profile")
                .provider(this)
                .size(6, 9)
                .title(ChatColor.WHITE + "Profil >> " + profilEntity.getName())
                .manager(CommonsBukkit.getInstance().getInventoryManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        val messages = MessageUtils.getMessageConfig(player);

        //Fill Black borders
        contents.fillRect(SlotPos.of(0, 0), SlotPos.of(5, 0), blackGlasses);
        contents.fillRect(SlotPos.of(0, 8), SlotPos.of(5, 8), blackGlasses);
        //Fill Grey borders
        contents.fillRect(SlotPos.of(0, 1), SlotPos.of(5, 1), grayGlasses);
        contents.fillRect(SlotPos.of(0, 7), SlotPos.of(5, 7), grayGlasses);
        // Fill Light Grey line
        contents.fillRect(SlotPos.of(0, 3), SlotPos.of(0, 5), lightgrayGlasses);
        contents.fillRect(SlotPos.of(5, 3), SlotPos.of(5, 5), grayGlasses);

        // Fill isolate grey glasses
        contents.set(SlotPos.of(0, 2), grayGlasses);
        contents.set(SlotPos.of(0, 6), grayGlasses);
        contents.set(SlotPos.of(5, 2), grayGlasses);
        contents.set(SlotPos.of(5, 6), grayGlasses);

        // Skull lore information
        //val participations = profilEntity.getNotes().stream().count();
        val hs = new HashSet<Integer>();
        profilEntity.getNotes().stream().forEach(note -> hs.add(note.getBuildbattle().getId()));
        val participations = hs.size();
        val lpu = LuckPermsUtils.getUser(UUID.fromString(profilEntity.getUniqueId()));
        val primaryGroup = lpu != null? lpu.getPrimaryGroup().substring(0, 1).toUpperCase() + lpu.getPrimaryGroup().substring(1).toLowerCase()
                : "Inconnue";
/*
SELECT sub.id_profil, `total`
FROM (SELECT DISTINCT n.id_buildbattle, n.id_profil, SUM(n.fun + n.amenagement + n.beaute + n.creativite + n.folklore) as `total`
    FROM notes n
    GROUP BY n.id_buildbattle, n.id_profil
    ORDER BY `total` DESC) sub
GROUP BY sub.id_buildbattle
 */
        val victoires = 0;


        // Profile skull builder
        val ibSkull = new ItemBuilder(SkullCreator.itemFromUuid(UUID.fromString(profilEntity.getUniqueId())))
                .setName(messages.getProfil().getSkullItem()
                        .replace("%player%", profilEntity.getName())
                        .replace("&", "§"));

        // Add replaced lore to skull
        messages.getProfil().getSkullLore().forEach(s -> ibSkull.addLoreLine(s.replace("%participations%", participations + "")
                .replace("%grade%", primaryGroup)
                .replace("&", "§")));

        // Add skul to inventory
        val skull = ibSkull.build();
        contents.set(SlotPos.of(1, 4), ClickableItem.empty(skull));

        // Saisons
        contents.set(3, 3, ClickableItem.of(new ItemBuilder(Material.PAINTING).setName(messages.getProfil().getResultItem().replace("&", "§")).build(),
                e -> {
                    List<BuildbattleEntity> buildbattles = new ArrayList<>();

                    // Store played saisons
                    profilEntity.getNotes().stream()
                            .forEach(note -> buildbattles.add(note.getBuildbattle()));


                    if(!buildbattles.isEmpty()){
                        // Sort saisons per date
                        Collections.sort(buildbattles, Comparator.comparing(BuildbattleEntity::getDate));

                        // Open invetory of the last saison played
                        new NoteInventory(profilEntity, buildbattles.get(0)).INVENTORY.open(player);
                    }
                    else{
                        player.sendMessage(messages.getProfil().getNotPlayedAnyBuildbattle().replace("&", "§"));
                    }
                }));

        // Derniers Résultats
        val saisonsStore = DataManager.getSaisonsStore();
        contents.set(3, 5, ClickableItem.of(new ItemBuilder(SkullCreator.itemFromBase64(GLOBE)).setName(messages.getProfil().getSeasonItem().replace("&", "§")).build(),
                e -> {
                    // Get saisons
                    try(val saisonsEntities = saisonsStore.select(SaisonEntity.class)
                            .where(SaisonEntity.BEGIN_DATE.lessThan(new Timestamp(new Date().getTime())))
                            .and(SaisonEntity.END_DATE.greaterThan(new Timestamp(new Date().getTime())))
                            .orderBy(SaisonEntity.BEGIN_DATE.asc())
                            .get()) {

                        if(saisonsEntities.stream().count() > 0){
                            // Open invetory of the last saison played
                            new SaisonInventory(profilEntity, saisonsEntities.toList().get(0)).INVENTORY.open(player);
                        }
                        else{
                            player.sendMessage(messages.getProfil().getNoSeasonStarted().replace("&", "§"));
                        }
                    }
                }));

        // Quitter
        contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).setName(messages.getNetwork().getCloseItem().replace("&", "§")).build(),
                e -> contents.inventory().close(player)));


    }

    @Override
    public void update(Player player, InventoryContents contents) {
        // Nothing to do here
    }

    private static <T> Predicate<T> distinctByKeys(Function<? super T, ?>... keyExtractors)
    {
        final Map<List<?>, Boolean> seen = new ConcurrentHashMap<>();

        return t ->
        {
            final List<?> keys = Arrays.stream(keyExtractors)
                    .map(ke -> ke.apply(t))
                    .collect(Collectors.toList());

            return seen.putIfAbsent(keys, Boolean.TRUE) == null;
        };
    }
}
