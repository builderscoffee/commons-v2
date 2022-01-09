package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.commands.manage.ManageSeasonsCommandPart;
import eu.builderscoffee.commons.bukkit.commands.manage.ManageThemesCommandPart;
import eu.builderscoffee.commons.common.utils.CommandUtils;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This command allows to broadcast a message on the server
 */
public class ManageCommand implements CommandExecutor {

    @Getter
    private static final List<ManageCommandPart> parts = Arrays.asList(new ManageThemesCommandPart(), new ManageSeasonsCommandPart());

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManage())){
            sender.sendMessage(messages.getNoPremission());
            return false;
        }

        val arg0 = CommandUtils.getArgument(args, 0).toLowerCase();
        val part = parts.stream()
                .filter(p -> p.getName().startsWith(arg0))
                .findFirst().orElse(null);

        if(Objects.isNull(part) || arg0.isEmpty()){
            sender.sendMessage(messages.getManage().getSubPartsOptions());
            parts.forEach(p -> sender.sendMessage(messages.getManage().getSubParts()
                    .replace("&", "§")
                    .replace("%name%", p.getName())));
            return true;
        }

        return part.base(sender, args);
    }

    public static abstract class ManageCommandPart {
        public abstract String getName();
        public abstract boolean base(CommandSender sender, String[] args);
    }
}
