package eu.builderscoffee.commons.common.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;

import java.util.Objects;
import java.util.UUID;

public class LuckPermsUtils {

    @Getter
    private static LuckPerms luckPerms;

    public static void init(LuckPerms luckPermsInstance){
        luckPerms = Objects.requireNonNull(luckPermsInstance);
    }

    @SneakyThrows
    public static User getUser(UUID uuid){
        return luckPerms.getUserManager().loadUser(uuid).get();
        //return luckPerms.getUserManager().getUser(uuid);
    }

    public static QueryOptions getQueryOptions(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        return (luckPerms.getContextManager().getQueryOptions(getUser(uuid)).isPresent()? luckPerms.getContextManager().getQueryOptions(getUser(uuid)).get() : null);
    }

    public static String getPrimaryGroup(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        return getUser(uuid).getPrimaryGroup();
    }

    public static CachedMetaData getCachedMetaData(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        final String primaryGroup = getPrimaryGroup(uuid);
        final QueryOptions queryOptions = getQueryOptions(uuid);
        return (queryOptions != null? luckPerms.getGroupManager().getGroup(primaryGroup).getCachedData().getMetaData(queryOptions) : null);
    }

    public static String getPrefix(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        val cachedMetaData = getCachedMetaData(uuid);
        return (cachedMetaData != null? cachedMetaData.getPrefix() : null);
    }

    public static String getPrefixOrEmpty(UUID uuid){
        return getPrefix(uuid) != null? getPrefix(uuid) : "";
    }

    public static String getSuffix(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        val cachedMetaData = getCachedMetaData(uuid);
        return (cachedMetaData != null? cachedMetaData.getSuffix() : null);
    }

    public static String getSuffixOrEmpty(UUID uuid){
        return getSuffix(uuid) != null? getSuffix(uuid) : "";
    }

    public static int getWeight(UUID uuid){
        if(luckPerms == null) throw new NullPointerException("LuckPermsUtils not initialized");

        final String primaryGroup = getPrimaryGroup(uuid);
        return luckPerms.getGroupManager().getGroup(primaryGroup).getWeight().getAsInt();
    }

    public static boolean hasPermission(UUID uuid, String permission){
        return getUser(uuid).getCachedData().getPermissionData().checkPermission(permission).asBoolean();
    }
}
