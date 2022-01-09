package eu.builderscoffee.commons.bungeecord.utils;

import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@UtilityClass
public class TextComponentUtil {

    /**
     * This function will decode the characters & and § to common colors (needed for BungeeCord only)
     * @param message
     * @return
     */
    public static BaseComponent decodeColor(String message){
        // Replace all & to §
        message = message.replace("&", "§");

        // Returns the message if no colors has been set
        if(!message.contains("§")) {
            return new TextComponent(message);
        }

        // Split by §
        String[] split = message.split("§");
        TextComponent textComponent = new TextComponent();

        for (String s : split) {
            if(s.length() > 0){
                TextComponent extra = new TextComponent(s.substring(1));
                ChatColor chatColor = ChatColor.getByChar(s.charAt(0));
                extra.setColor(chatColor != null? chatColor: ChatColor.RESET);
                textComponent.addExtra(extra);
            }
        }

        return textComponent;
    }
}
