package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.commons.bukkit.annotation.CommandRegister;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * This command allows to see all commands available to common players
 */
@CommandRegister(command = "help")
public class HelpCommand implements CommandExecutor {

    private static final Pattern PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final TreeSet<Class<? extends CommandExecutor>> COMMANDS = new TreeSet<>(Comparator.comparing(c -> c.getAnnotation(CommandRegister.class).command()));

    public static void registerCommand(Class<? extends CommandExecutor> clazz) {
        if (!HelpCommand.class.isAnnotationPresent(CommandRegister.class))
            throw new RuntimeException("Tried to register a non-annotated command");
        COMMANDS.add(clazz);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int page = 1;

        if (args.length > 0) {
            val longArgs = String.valueOf(args);
            if (!PATTERN.matcher(longArgs).matches()) {
                sender.sendMessage("§cLa valeur §f" + longArgs + "§c n'est pas un chiffre !");
                return true;
            }

            page = Integer.parseInt(longArgs);
            page = (page < 1 ? 1 : page);
        }

        int min = ((page - 1) * 10);
        int max = (page * 10);

        if (COMMANDS.size() > 0) {
            sender.sendMessage("§6Commandes possible: ");
            for (int i = min; i < COMMANDS.size() && i < max; i++) {
                val commandClass = (Class<? extends CommandExecutor>) COMMANDS.toArray()[i];
                val annotation = commandClass.getAnnotation(CommandRegister.class);
                sender.sendMessage("§7 - §6/" + annotation.command() + " §e" + annotation.description());
            }
        } else {
            sender.sendMessage("§cAucune commande enregistré");
        }
        return true;
    }
}
