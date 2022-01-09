package eu.builderscoffee.commons.bungeecord.commands;

import eu.builderscoffee.commons.bungeecord.CommonsBungeeCord;
import eu.builderscoffee.commons.bungeecord.utils.MessageUtils;
import eu.builderscoffee.commons.bungeecord.utils.TextComponentUtil;
import eu.builderscoffee.commons.common.utils.CommandUtils;
import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class MoveCommand extends Command implements TabExecutor {

    public MoveCommand() {
        super("move", CommonsBungeeCord.getInstance().getPermissions().getMovePermission());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission(CommonsBungeeCord.getInstance().getPermissions().getMovePermission())) {
            sender.sendMessage(TextComponentUtil.decodeColor(MessageUtils.getMessageConfig(sender).getNoPermission()));
            return;
        }

        val messages = MessageUtils.getMessageConfig(sender).getCommands();

        val arg0 = CommandUtils.getArgument(args, 0);
        val arg1 = CommandUtils.getArgument(args, 1);

        if (arg0.isEmpty()) {
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getUsage()));
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getPlayersOnline()
                    .replace("%players%", String.join(", ", CommonsBungeeCord.getInstance().getProxy().getPlayers().stream()
                            .sorted(Comparator.comparing(ProxiedPlayer::getName))
                            .map(ProxiedPlayer::getName)
                            .collect(Collectors.toList())))));
            return;
        }

        if (arg1.isEmpty()) {
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getUsage()));
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getServersOnline()
                    .replace("%servers%", String.join(", ", CommonsBungeeCord.getInstance().getProxy().getServers().values().stream()
                            .sorted(Comparator.comparing(ServerInfo::getName))
                            .map(ServerInfo::getName)
                            .collect(Collectors.toList())))));
            return;
        }

        val targetPlayer = CommonsBungeeCord.getInstance().getProxy().getPlayers().stream()
                .sorted(Comparator.comparing(ProxiedPlayer::getName))
                .filter(p -> p.getName().toLowerCase().startsWith(arg0.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(targetPlayer)) {
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getPlayerNotOnline()));
            return;
        }

        val targetServer = CommonsBungeeCord.getInstance().getProxy().getServers().values().stream()
                .sorted(Comparator.comparing(ServerInfo::getName))
                .filter(p -> p.getName().toLowerCase().startsWith(arg1.toLowerCase()))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(targetServer)) {
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getServerNotExist()));
            return;
        }

        if(targetPlayer.getServer().getInfo().getName().equals(targetServer.getName())){
            sender.sendMessage(TextComponentUtil.decodeColor(messages.getMove().getAlreadyOnThisServer()));
            return;
        }


        sender.sendMessage(TextComponentUtil.decodeColor(MessageUtils.getMessageConfig(sender).getCommands().getMove().getMovedPlayerMessage()
                .replace("%player%", targetPlayer.getName())
                .replace("%server%", targetServer.getName())));
        targetPlayer.sendMessage(TextComponentUtil.decodeColor(MessageUtils.getMessageConfig(targetPlayer).getCommands().getMove().getMovedByMessage()
                .replace("%player%", sender.getName())
                .replace("%server%", targetServer.getName())));
        targetPlayer.connect(targetServer);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1)
            return CommonsBungeeCord.getInstance().getProxy().getPlayers().stream()
                    .map(ProxiedPlayer::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        else if (args.length == 2)
            return CommonsBungeeCord.getInstance().getProxy().getServers().values().stream()
                    .map(ServerInfo::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        return null;
    }
}
