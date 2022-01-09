package eu.builderscoffee.commons.bukkit.commands.manage;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.BuildbattleThemeEntity;
import eu.builderscoffee.api.common.data.tables.BuildbattleThemeNameEntity;
import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.commands.ManageCommand;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import eu.builderscoffee.commons.common.utils.CommandUtils;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ManageThemesCommandPart extends ManageCommand.ManageCommandPart {

    @Override
    public String getName() {
        return "themes";
    }

    @Override
    public boolean base(CommandSender sender, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if (!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageTheme())) {
            sender.sendMessage(messages.getNoPremission());
            return false;
        }

        switch (CommandUtils.getArgument(args, 1).toLowerCase()) {
            case "l":
            case "list":
                return list(sender, args);
            case "c":
            case "create":
                return create(sender, args);
            case "e":
            case "edit":
                return update(sender, args);
            case "d":
            case "delete":
                return delete(sender, args);
        }
        sender.sendMessage(messages.getManage().getSubPartsOptions().replace("&", "§"));
        sender.sendMessage(messages.getManage().getThemes().getCommandList().replace("&", "§"));
        sender.sendMessage(messages.getManage().getThemes().getCommandCreate().replace("&", "§"));
        sender.sendMessage(messages.getManage().getThemes().getCommandEdit().replace("&", "§"));
        sender.sendMessage(messages.getManage().getThemes().getCommandDelete().replace("&", "§"));
        return true;
    }

    public boolean list(CommandSender sender, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if (!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageThemeList())) {
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        int page = 0;
        val arg2 = CommandUtils.getArgument(args, 2);
        val data = DataManager.getBuildbattleThemeStore().select(BuildbattleThemeEntity.class).get();

        if (!arg2.isEmpty() && isNumeric(arg2)) {
            page = Integer.valueOf(arg2);
            if (page < 0 || page > (data.stream().count() / 5)) page = 0;
        }

        sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getManage().getThemes().getList().replace("&", "§"));
        data.stream()
                .skip(page * 5)
                .limit(5)
                .forEach(theme -> {
                    sender.sendMessage(messages.getManage().getThemes().getListFormat()
                            .replace("%id%", String.valueOf(theme.getId()))
                            .replace("&", "§"));
                    theme.getNames().forEach(translation -> {
                        sender.sendMessage(messages.getManage().getThemes().getListFormatNames()
                                .replace("%lang%", translation.getLanguage().name())
                                .replace("%name%", translation.getName())
                                .replace("&", "§"));
                    });
                });
        return false;
    }

    public boolean create(CommandSender sender, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if (!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageThemeChange())) {
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        // DO NOT USE REQUERY
        // REQUERY DOES NOT ACCEPT AN ENTITY CREATION WITH ONLY AN AUTO INCREMENT
        DataManager.getBuildbattleThemeStore().raw("INSERT INTO `buildbattles_themes` (`id`) VALUES (NULL);");

        sender.sendMessage(messages.getManage().getThemes().getCreated().replace("&", "§"));

        return false;
    }

    public boolean update(CommandSender sender, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if (!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageThemeChange())) {
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        val arguments = new ArrayList<String>();
        for (int i = 4; i < args.length; i++) {
            arguments.add(args[i]);
        }

        val arg2 = CommandUtils.getArgument(args, 2);
        val arg3 = CommandUtils.getArgument(args, 3);
        val name = String.join(" ", arguments).trim();

        if (arg2.isEmpty()) {
            sender.sendMessage(messages.getManage().getThemes().getCommandEdit().replace("&", "§"));
            return false;
        }

        if (!isNumeric(arg2)) {
            sender.sendMessage(messages.getManage().getThemes().getSecondArgumentNoNumber().replace("&", "§"));
            return false;
        }

        val entity = DataManager.getBuildbattleThemeStore()
                .select(BuildbattleThemeEntity.class)
                .where(BuildbattleThemeEntity.ID.eq(Integer.valueOf(arg2)))
                .get().firstOrNull();

        if (Objects.isNull(entity)) {
            sender.sendMessage(messages.getManage().getThemes().getThemeNotFound().replace("&", "§"));
            return false;
        }

        if(arg3.isEmpty()){
            sender.sendMessage(messages.getManage().getThemes().getCommandEdit().replace("&", "§"));
            return false;
        }

        val language = Arrays.stream(Profil.Languages.values())
                .filter(lang -> lang.name().equalsIgnoreCase(arg3))
                .findFirst()
                .orElse(null);

        if (Objects.isNull(language)) {
            sender.sendMessage(messages.getManage().getThemes().getLanguageNotFound().replace("&", "§"));
            sender.sendMessage(messages.getManage().getThemes().getLanguagesList()
                    .replace("%languages%", String.join(", ", Arrays.stream(Profil.Languages.values())
                            .map(Profil.Languages::name)
                            .collect(Collectors.toList())))
                    .replace("&", "§"));
            return false;
        }

        if(name.isEmpty()){
            sender.sendMessage(messages.getManage().getThemes().getCommandEdit().replace("&", "§"));
            return false;
        }

        val defaultBtn = new BuildbattleThemeNameEntity();
        defaultBtn.setTheme(entity);
        defaultBtn.setLanguage(language);

        val translation = entity.getNames().stream()
                .filter(btn -> btn.getLanguage().equals(language))
                .findFirst()
                .orElse(defaultBtn);

        translation.setName(name);

        DataManager.getBuildbattleThemeNameStore().upsert(translation);

        sender.sendMessage(messages.getManage().getThemes().getEdited()
                .replace("%id%", String.valueOf(entity.getId()))
                .replace("%language%", language.name())
                .replace("%name%", name)
                .replace("&", "§"));

        return false;
    }

    public boolean delete(CommandSender sender, String[] args) {
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageThemeChange())){
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        val arg2 = CommandUtils.getArgument(args, 2);

        if (arg2.isEmpty()) {
            sender.sendMessage(messages.getManage().getThemes().getCommandEdit().replace("&", "§"));
            return false;
        }

        if (!isNumeric(arg2)) {
            sender.sendMessage(messages.getManage().getThemes().getSecondArgumentNoNumber().replace("&", "§"));
            return false;
        }

        val entity = DataManager.getBuildbattleThemeStore()
                .select(BuildbattleThemeEntity.class)
                .where(BuildbattleThemeEntity.ID.eq(Integer.valueOf(arg2)))
                .get().firstOrNull();

        if (Objects.isNull(entity)) {
            sender.sendMessage(messages.getManage().getThemes().getThemeNotFound().replace("&", "§"));
            return false;
        }

        sender.sendMessage(messages.getManage().getThemes().getDeleted()
                .replace("%id%", String.valueOf(entity.getId()))
                .replace("&", "§"));

        DataManager.getBuildbattleThemeStore().delete(entity);
        return false;
    }

    private boolean isNumeric(String text) {
        try {
            Integer.valueOf(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
