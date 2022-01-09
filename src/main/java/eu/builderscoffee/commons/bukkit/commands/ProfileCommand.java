package eu.builderscoffee.commons.bukkit.commands;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.inventory.profile.ProfilInventory;
import lombok.val;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This command allows to see the profile of a specific player
 */
public class ProfileCommand implements CommandExecutor {

    private static boolean openProfile(Player player, String targetName) {
        val profilEntity = DataManager.getProfilStore()
                .select(ProfilEntity.class)
                .where(ProfilEntity.NAME.lower().like(targetName.toLowerCase() + "%"))
                .get().firstOrNull();
        if (profilEntity != null) {
            new ProfilInventory(profilEntity).INVENTORY.open(player);
            return true;
        }
        player.sendMessage("§cCe joueur n'existe pas");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            openProfile(player, args.length == 1 ? args[0] : player.getName());
            return true;
        }

        sender.sendMessage(CommonsBukkit.getInstance().getMessages().get(Profil.Languages.FR).getCommand().getMustBePlayer());
        return true;
    }
}