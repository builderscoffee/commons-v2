package eu.builderscoffee.commons.bukkit.commands.manage;

import eu.builderscoffee.api.common.data.DataManager;
import eu.builderscoffee.api.common.data.tables.SaisonEntity;
import eu.builderscoffee.commons.bukkit.CommonsBukkit;
import eu.builderscoffee.commons.bukkit.commands.ManageCommand;
import eu.builderscoffee.commons.common.utils.CommandUtils;
import eu.builderscoffee.commons.bukkit.utils.MessageUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.command.CommandSender;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.regex.Pattern;

public class ManageSeasonsCommandPart extends ManageCommand.ManageCommandPart {

    private final Pattern datePattern = Pattern.compile("^\\d{2}-\\d{2}-\\d{4}$");
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public String getName() {
        return "seasons";
    }

    @Override
    public boolean base(CommandSender sender, String[] args){
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageSeason())){
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        switch (CommandUtils.getArgument(args, 1).toLowerCase()){
            case "l":
            case "list":
                return list(sender, args);
            case "a":
            case "add":
                return add(sender, args);
            case "u":
            case "update":
                return update(sender, args);
            case "d":
            case "delete":
                return delete(sender, args);
        }
        sender.sendMessage(messages.getManage().getSubPartsOptions().replace("&", "§"));
        sender.sendMessage(messages.getManage().getSeasons().getCommandList().replace("&", "§"));
        sender.sendMessage(messages.getManage().getSeasons().getCommandAdd().replace("&", "§"));
        sender.sendMessage(messages.getManage().getSeasons().getCommandUpdate().replace("&", "§"));
        sender.sendMessage(messages.getManage().getSeasons().getCommandDelete().replace("&", "§"));
        return true;
    }

    public boolean list(CommandSender sender, String[] args){
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageSeasonList())){
            sender.sendMessage(messages.getNoPremission().replace("&", "§"));
            return false;
        }

        sender.sendMessage(messages.getManage().getSeasons().getList().replace("&", "§"));
        val data = DataManager.getSaisonsStore().select(SaisonEntity.class).get();
        data.stream().forEach(season -> {
            sender.sendMessage(messages.getManage().getSeasons().getListFormat()
                    .replace("&", "§")
                    .replace("%id%", String.valueOf(season.getId()))
                    .replace("%begin_date%", dateFormatter.format(season.getBeginDate()))
                    .replace("%end_date%", dateFormatter.format(season.getEndDate())));
        });
        return false;
    }

    @SneakyThrows
    public boolean add(CommandSender sender, String[] args){
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageSeasonChange())){
            sender.sendMessage(messages.getNoPremission());
            return false;
        }

        val beginDate = CommandUtils.getArgument(args, 2);
        val endDate = CommandUtils.getArgument(args, 3);

        if(beginDate.isEmpty()
                || endDate.isEmpty()
                || !datePattern.matcher(beginDate).matches()
                || !datePattern.matcher(endDate).matches()){
            sender.sendMessage(messages.getManage().getSeasons().getDateNotEmpty().replace("&", "§"));
            sender.sendMessage("§cExample: /manage seasons add 10-01-2021 10-06-2021");
            return true;
        }

        val format = "dd-MM-yyyy";
        final Timestamp beginTimestamp;
        final Timestamp endTimestamp;
        try {
            beginTimestamp = new Timestamp(new SimpleDateFormat(format).parse(beginDate).getTime());
            endTimestamp = new Timestamp(new SimpleDateFormat(format).parse(endDate).getTime());
        } catch (ParseException e) {
            sender.sendMessage(messages.getManage().getSeasons().getDateNotCorrect().replace("&", "§"));
            sender.sendMessage("§cExample: /manage seasons add 10-01-2021 10-06-2021");
            return false;
        }

        val entity = new SaisonEntity();
        entity.setBeginDate(beginTimestamp);
        entity.setEndDate(endTimestamp);

        DataManager.getSaisonsStore().insert(entity);

        sender.sendMessage(messages.getManage().getSeasons().getAdded().replace("&", "§"));

        return false;
    }

    public boolean update(CommandSender sender, String[] args){
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageSeasonChange())){
            sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getNoPremission());
            return false;
        }

        val id = CommandUtils.getArgument(args, 2);
        val beginDate = CommandUtils.getArgument(args, 3);
        val endDate = CommandUtils.getArgument(args, 4);

        if(!isInteger(id)){
            sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getManage().getSeasons().getIdNotNumber());
            return false;
        }

        val entity = DataManager.getSaisonsStore().select(SaisonEntity.class).where(SaisonEntity.ID.eq(Integer.parseInt(id))).get().firstOrNull();
        if(Objects.isNull(entity)){
            sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getManage().getSeasons().getNotExist());
            return false;
        }

        if(beginDate.isEmpty()
                || endDate.isEmpty()
                || !datePattern.matcher(beginDate).matches()
                || !datePattern.matcher(endDate).matches()){
            sender.sendMessage(messages.getManage().getSeasons().getDateNotEmpty().replace("&", "§"));
            sender.sendMessage("§cExample: /manage seasons update 1 10-01-2021 10-06-2021");
            return false;
        }

        val format = "dd-MM-yyyy";
        final Timestamp beginTimestamp;
        final Timestamp endTimestamp;
        try {
            beginTimestamp = new Timestamp(new SimpleDateFormat(format).parse(beginDate).getTime());
            endTimestamp = new Timestamp(new SimpleDateFormat(format).parse(endDate).getTime());
        } catch (ParseException e) {
            sender.sendMessage(messages.getManage().getSeasons().getDateNotCorrect().replace("&", "§"));
            sender.sendMessage("§cExample: /manage seasons update 1 10-01-2021 10-06-2021");
            return false;
        }

        entity.setBeginDate(beginTimestamp);
        entity.setEndDate(endTimestamp);

        DataManager.getSaisonsStore().update(entity);

        sender.sendMessage(messages.getManage().getSeasons().getUpdated().replace("&", "§"));
        return false;
    }

    public boolean delete(CommandSender sender, String[] args){
        val messages = MessageUtils.getMessageConfig(sender).getCommand();
        if(!sender.hasPermission(CommonsBukkit.getInstance().getPermissions().getCommandManageSeasonChange())){
            sender.sendMessage(MessageUtils.getMessageConfig(sender).getCommand().getNoPremission());
            return false;
        }

        val id = CommandUtils.getArgument(args, 2);

        if(!isInteger(id)){
            sender.sendMessage(messages.getManage().getSeasons().getIdNotNumber());
            return false;
        }

        val entity = DataManager.getSaisonsStore().select(SaisonEntity.class).where(SaisonEntity.ID.eq(Integer.parseInt(id))).get().firstOrNull();
        if(Objects.isNull(entity)){
            sender.sendMessage(messages.getManage().getSeasons().getNotExist());
            return true;
        }

        DataManager.getSaisonsStore().delete(entity);

        sender.sendMessage(messages.getManage().getSeasons().getDeleted().replace("&", "§"));
        return false;
    }

    private static boolean isInteger(@NonNull String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
