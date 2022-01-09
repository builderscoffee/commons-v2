package eu.builderscoffee.commons.bukkit.inventory.profile;

import eu.builderscoffee.api.bukkit.gui.ClickableItem;
import eu.builderscoffee.api.bukkit.gui.SmartInventory;
import eu.builderscoffee.api.bukkit.gui.content.InventoryContents;
import eu.builderscoffee.api.bukkit.gui.content.InventoryProvider;
import eu.builderscoffee.api.bukkit.gui.content.SlotIterator;
import eu.builderscoffee.api.bukkit.gui.content.SlotPos;
import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.BuildbattleEntity;
import eu.builderscoffee.api.common.data.tables.NoteEntity;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.bukkit.utils.SkullCreator;
import io.requery.query.NamedExpression;
import io.requery.query.Result;
import io.requery.query.Tuple;
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

public class NoteInventory implements InventoryProvider {

    public final SmartInventory INVENTORY;

    private final CommonsBukkit commonsBukkit = CommonsBukkit.getInstance();

    private final ProfilEntity profilEntity;
    private final BuildbattleEntity buildbattleEntity;

    private final static String GLOBE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzEwNzdlODcxZDkxYjdmZWEyNGIxZjY4MDhlMDg4ZDdiODQyZGE1MTZmNjM1NmNlOTE2MTM0OTQ0MzFhZThhMCJ9fX0=";
    private final static String LIME = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGI1OTljNjE4ZTkxNGMyNWEzN2Q2OWY1NDFhMjJiZWJiZjc1MTYxNTI2Mzc1NmYyNTYxZmFiNGNmYTM5ZSJ9fX0=";
    private final static String FIRST = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYzMDM0ZDI0YTg1ZGEzMWQ2NzkzMmMzM2U1ZjE4MjFlMjE5ZDVkY2Q5YzJiYTRmMjU1OWRmNDhkZWVhIn19fQ==";
    private final static String SECOND = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM2MWIwNGUxMmE4Nzk3NjdiM2I3MmQ2OTYyN2YyOWE4M2JkZWI2MjIwZjVkYzdiZWEyZWIyNTI5ZDViMDk3In19fQ==";
    private final static String THIRD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTI2Yjc3MjMyOWNmMzJmODY0M2M0OTI4NjI2YjZhMzI1MjMzZmY2MWFhOWM3NzI1ODczYTRiZDY2ZGIzZDY5MiJ9fX0=";
    private final static String TOP_10 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDEyNjk1ODRmNjI5MjI3NzEzMTA3YjRlMGEwMmRkNjVkZGZlNzgwZTdjNzExOGNiMWVjMjI3NWM1MTRjYzk1ZCJ9fX0=";
    private final static String TOP_20 = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGEzMTRiNjVkMzk1OWVmMTViOGEzNjQzNmZkYzlhZTgwNGYzODFiNDc4ZGViYzc3OGM0MGZmYmIwMmZiY2RkZiJ9fX0=";
    private final static String OTHER = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZhNzY0YjNjMWQ0NjJmODEyNDQ3OGZmNTQzYzc2MzNmYTE5YmFmOTkxM2VlMjI4NTEzZTgxYTM2MzNkIn19fQ==";

    private static final ClickableItem blackGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.BLACK_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem grayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).setName("§a").build());
    private static final ClickableItem lightgrayGlasses = ClickableItem.empty(new ItemBuilder(new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)).setName("§a").build());

    final ItemStack limeSkull = SkullCreator.itemFromBase64(LIME);
    final ItemStack limeConcrete = new ItemStack(Material.LIME_CONCRETE);



    public NoteInventory(ProfilEntity profilEntity, BuildbattleEntity buildbattleEntity){
        if(profilEntity == null)
            throw new NullPointerException("Profile can't be null");

        if(buildbattleEntity == null)
            throw new NullPointerException("Buildbattle can't be null");

        this.profilEntity = profilEntity;
        this.buildbattleEntity = buildbattleEntity;

        INVENTORY = SmartInventory.builder()
                .id("profile")
                .provider(this)
                .size(6, 9)
                .title(ChatColor.WHITE + "Note")
                .manager(CommonsBukkit.getInstance().getInventoryManager())
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        val messages = MessageUtils.getMessageConfig(player);
        val page = contents.pagination();

        //Fill Black borders
        contents.fillRect(SlotPos.of(0, 0), SlotPos.of(5, 0), blackGlasses);
        contents.fillRect(SlotPos.of(0, 8), SlotPos.of(5, 8), blackGlasses);
        //Fill Grey borders
        contents.fillRect(SlotPos.of(0, 1), SlotPos.of(5, 1), grayGlasses);
        contents.fillRect(SlotPos.of(0, 7), SlotPos.of(5, 7), grayGlasses);
        // Fill Light Grey line
        contents.fillRect(SlotPos.of(0, 3), SlotPos.of(0, 5), lightgrayGlasses);
        contents.fillRect(SlotPos.of(5, 3), SlotPos.of(5, 5), lightgrayGlasses);

        // Fill isolate grey glasses
        contents.fillRect(SlotPos.of(0, 2), SlotPos.of(1, 2), grayGlasses);
        contents.fillRect(SlotPos.of(0, 6), SlotPos.of(1, 6), grayGlasses);
        contents.fillRect(SlotPos.of(4, 2), SlotPos.of(5, 2), grayGlasses);
        contents.fillRect(SlotPos.of(4, 6), SlotPos.of(5, 6), grayGlasses);

        // Buildbattle Skull
        ItemStack bbSkull = new ItemBuilder(buildbattleEntity.isStep()? limeConcrete : limeSkull)
                .setName("§b" + buildbattleEntity.getId() + ". " + buildbattleEntity.getType().getName())
                .addGLow()
                .build();
        contents.set(SlotPos.of(0, 4), ClickableItem.empty(bbSkull));

        // Position
        try(Result<Tuple> query = DataManager.getNotesStore()
                .select(
                        NamedExpression.ofInteger("id_profil"),
                        NamedExpression.ofInteger("fun + amenagement + beaute + creativite + folklore").sum().as("total"))
                .from(NoteEntity.class)
                .where(NoteEntity.BUILDBATTLE.eq(buildbattleEntity))
                .groupBy(NoteEntity.PROFIL)
                .orderBy(NamedExpression.ofInteger("total").desc())
                .get()){

            int position = 0;
            for (Tuple tuple : query) {
                position++;
                final int id = tuple.get("id_profil");
                if(profilEntity.getId() == id){
                    break;
                }
            }

            ItemStack itPosition;
            if(position == 1){
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(FIRST))
                        .setName("1er").build();
            }
            else if(position == 2){
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(SECOND))
                        .setName("2ieme").build();
            }
            else if(position == 3){
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(THIRD))
                        .setName("3ieme").build();
            }
            else if(position <= 10){
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(TOP_10))
                        .setName("Top 10").build();
            }
            else if(position <= 20){
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(TOP_20))
                        .setName("Top 20").build();
            }
            else {
                itPosition = new ItemBuilder(SkullCreator.itemFromBase64(OTHER))
                        .setName("Non classé").build();
            }
            contents.set(SlotPos.of(1, 4), ClickableItem.empty(itPosition));

            // Jury
            List<NoteEntity> notes = new ArrayList<>();

            profilEntity.getNotes().stream()
                    .filter(note -> note.getBuildbattle().getId() == buildbattleEntity.getId())
                    .forEach(note -> notes.add(note));


            int beaute = 0,creativite = 0,amenagement = 0,folkore = 0,fun = 0;
            ClickableItem[] juryItems = new ClickableItem[notes.size()];
            for(int i = 0; i < juryItems.length; i++){
                final NoteEntity note = (NoteEntity) notes.get(i);
                final ProfilEntity jury = note.getJury();
                beaute += note.getBeaute();
                creativite += note.getCreativite();
                amenagement += note.getAmenagement();
                folkore += note.getFolklore();
                fun += note.getFun();
                final int total = note.getAmenagement() + note.getBeaute() + note.getCreativite() + note.getFolklore() + note.getFun();
                juryItems[i] = ClickableItem.empty(new ItemBuilder(SkullCreator.itemFromUuid(UUID.fromString(jury.getUniqueId())))
                        .setName("§6" + jury.getName())
                        .addLoreLine("§aAménagement §8/ §aFinalité: §f" + note.getAmenagement())
                        .addLoreLine("§aBeauté §8/ §aTechnicité: §f" + note.getBeaute())
                        .addLoreLine("§aCreativité §8/ §aOriginalité: §f" + note.getCreativite())
                        .addLoreLine("§aFolklore: §f" + note.getFolklore())
                        .addLoreLine("§aFun (bonus): §f" + note.getFun())
                        .addLoreLine("§a")
                        .addLoreLine("§6Total: §f" + total)
                        .build());
            }

            page.setItems(juryItems);

            page.setItemsPerPage(5);
            page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(3, 2)));

            int total = beaute + creativite + amenagement + folkore + fun;
            // Résultat Général
            val itGlobalScore = new ItemBuilder(SkullCreator.itemFromBase64(GLOBE))
                    .setName("§6" + messages.getProfil().getGlobalResultItem().replace("&", "§"))
                    .addLoreLine("§aAménagement §8/ §aFinalité: §f" + (Double.valueOf(amenagement) / (juryItems.length != 0? juryItems.length : 1)))
                    .addLoreLine("§aBeauté §8/ §aTechnicité: §f" + (Double.valueOf(beaute) / (juryItems.length != 0? juryItems.length : 1)))
                    .addLoreLine("§aCreativité §8/ §aOriginalité: §f" + (Double.valueOf(creativite) / (juryItems.length != 0? juryItems.length : 1)))
                    .addLoreLine("§aFolklore: §f" + (Double.valueOf(folkore) / (juryItems.length != 0? juryItems.length : 1)))
                    .addLoreLine("§aFun (bonus): §f" + (Double.valueOf(fun) / (juryItems.length != 0? juryItems.length : 1)))
                    .addLoreLine("§a")
                    .addLoreLine("§6Total: §f" + (Double.valueOf(total) / (juryItems.length != 0? juryItems.length : 1)))
                    .build();
            contents.set(4, 4, ClickableItem.empty(itGlobalScore));

            // Retour
            contents.set(5, 0, ClickableItem.of(new ItemBuilder(Material.ARROW).setName(messages.getRetourItem().replace("&", "§")).build(),
                    e -> new SaisonInventory(profilEntity, buildbattleEntity.getSaison()).INVENTORY.open(player)));

            // Quitter
            contents.set(5, 4, ClickableItem.of(new ItemBuilder(Material.BARRIER).setName(messages.getNetwork().getCloseItem().replace("&", "§")).build(),
                    e -> contents.inventory().close(player)));
        }
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
