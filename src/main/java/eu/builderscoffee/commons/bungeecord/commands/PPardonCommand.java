package eu.builderscoffee.commons.bungeecord.commands;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.BanEntity;
import eu.builderscoffee.api.common.data.tables.ProfilEntity;
import eu.builderscoffee.commons.bungeecord.CommonsBungeeCord;
import eu.builderscoffee.commons.bungeecord.utils.MessageUtils;
import eu.builderscoffee.commons.bungeecord.utils.TextComponentUtil;
import lombok.val;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class PPardonCommand extends Command {

    public PPardonCommand() {
        super("ppardon", CommonsBungeeCord.getInstance().getPermissions().getPpardonPermission());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.hasPermission(CommonsBungeeCord.getInstance().getPermissions().getPpardonPermission())){
            sender.sendMessage(TextComponentUtil.decodeColor(MessageUtils.getMessageConfig(sender).getNoPermission()));
            return;
        }

        if(args.length < 1){
            sender.sendMessage(TextComponentUtil.decodeColor("§c/ppardon <player>"));
            return;
        }

        ProfilEntity profil = DataManager.getProfilStore().select(ProfilEntity.class)
                .where(ProfilEntity.NAME.lower().eq(args[0].toLowerCase()))
                .get().firstOrNull();
        if(profil == null){
            sender.sendMessage(TextComponentUtil.decodeColor("§cCe joueur n'existe pas"));
            return;
        }

        val banStore = DataManager.getBansStore();
        val ban = banStore.select(BanEntity.class)
                .where(BanEntity.PROFILE.eq(profil))
                .get().firstOrNull();

        if(ban == null){
            sender.sendMessage(TextComponentUtil.decodeColor("§cCe joueur n'est pas banni"));
            return;
        }

        banStore.delete(ban);
        sender.sendMessage(TextComponentUtil.decodeColor("§aVous avez débanni " + profil.getName()));
        CommonsBungeeCord.getInstance().getLogger().info("§7" + sender.getName() + " §8débanni §7" + profil.getName());
    }
}
