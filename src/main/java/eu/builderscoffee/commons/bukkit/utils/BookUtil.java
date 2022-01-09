package eu.builderscoffee.commons.bukkit.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.lang.reflect.Method;
import java.util.List;

import static eu.builderscoffee.commons.bukkit.utils.ReflectionUtils.*;

/**
 * Créer une livre virtuel ne requièrent pas un livre dans la main du joueur !
 * Requis la class ReflectionUtil
 *
 * @author Jed
 * @link https://www.spigotmc.org/threads/open-book-gui.131470/
 */
public class BookUtil {
    @Getter
    private static boolean initialised = false;
    private static Method getHandle;
    private static Method openBook;

    static {
        try {
            getHandle = getMethod("CraftPlayer", PackageType.CRAFTBUKKIT_ENTITY, "getHandle");
            openBook = getMethod("EntityPlayer", PackageType.MINECRAFT_SERVER, "a", PackageType.MINECRAFT_SERVER.getClass("ItemStack"), PackageType.MINECRAFT_SERVER.getClass("EnumHand"));
            initialised = true;
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            Bukkit.getServer().getLogger().warning("Cannot force open book!");
            initialised = false;
        }
    }

    /**
     * Ouvrir un livre virtuelle
     *
     * @param i Itemstack du livre.
     * @param p Le joueur qui ouvre le livre.
     * @return
     */
    public static boolean openBook(ItemStack i, Player p) {
        if (!initialised) return false;
        ItemStack held = p.getInventory().getItemInMainHand();
        try {
            p.getInventory().setItemInMainHand(i);
            sendPacket(i, p);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            initialised = false;
        }
        p.getInventory().setItemInMainHand(held);
        return initialised;
    }

    /***
     * Envoyer le packet aux joueurs
     * @param i
     * @param p
     * @throws ReflectiveOperationException
     */
    private static void sendPacket(ItemStack i, Player p) throws ReflectiveOperationException {
        Object entityplayer = getHandle.invoke(p);
        Class<?> enumHand = PackageType.MINECRAFT_SERVER.getClass("EnumHand");
        Object[] enumArray = enumHand.getEnumConstants();
        openBook.invoke(entityplayer, getItemStack(i), enumArray[0]);
    }

    /***
     * Retourne l'item stack
     * @param item
     * @return
     */
    public static Object getItemStack(ItemStack item) {
        try {
            Method asNMSCopy = getMethod(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), "asNMSCopy", ItemStack.class);
            return asNMSCopy.invoke(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"), item);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Définir les pages du livres aux format JSON
     *
     * @param metadata BookMeta de l'itemstack livre.
     * @param pages    Chaque page à ajouté aux livre.
     */
    @SuppressWarnings("unchecked")
    public static void setPages(BookMeta metadata, List<String> pages) {
        List<Object> p;
        Object page;

        try {
            p = (List<Object>) getField(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftMetaBook"), true, "pages").get(metadata);
            for (String text : pages) {
                page = invokeMethod(PackageType.MINECRAFT_SERVER.getClass("IChatBaseComponent$ChatSerializer").newInstance(), "a", text);
                p.add(page);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}