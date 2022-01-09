package eu.builderscoffee.commons.bungeecord.utils;

import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.commons.bungeecord.CommonsBungeeCord;
import eu.builderscoffee.commons.bungeecord.configuration.MessageConfiguration;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

@UtilityClass
public class MessageUtils {

    public static Profil.Languages getLang(ProxiedPlayer player) {
        val profil = CommonsBungeeCord.getInstance().getProfilCache().get(player.getUniqueId().toString());
        return profil.getLang();
    }

    public static Profil.Languages getLang(UUID uuid) {
        val profil = CommonsBungeeCord.getInstance().getProfilCache().get(uuid.toString());
        return profil.getLang();
    }

    public static MessageConfiguration getMessageConfig(ProxiedPlayer player) {
        return CommonsBungeeCord.getInstance().getMessages().get(getLang(player));
    }

    public static MessageConfiguration getMessageConfig(UUID uuid) {
        return CommonsBungeeCord.getInstance().getMessages().get(getLang(uuid));
    }

    public static MessageConfiguration getMessageConfig(CommandSender sender) {
        if (sender instanceof ProxiedPlayer) {
            return getMessageConfig((ProxiedPlayer) sender);
        }
        else{
            return getDefaultMessageConfig();
        }
    }

    public static MessageConfiguration getDefaultMessageConfig() {
        return CommonsBungeeCord.getInstance().getMessages().get(Profil.Languages.FR);
    }
}
