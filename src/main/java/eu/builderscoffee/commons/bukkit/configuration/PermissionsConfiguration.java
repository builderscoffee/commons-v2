package eu.builderscoffee.commons.bukkit.configuration;

import eu.builderscoffee.api.common.configuration.annotation.Configuration;
import lombok.Data;

/**
 * This class stores permission configuration
 */
@Data
@Configuration("permissions")
public final class PermissionsConfiguration {

    /* Network Inventory */
    String serverManagerSee = "builderscoffee.bukkit.servermanager.see";
    String staffchat = "builderscoffee.staffchat";

    /* Manage */
    String commandManage = "builderscoffee.bukkit.manage";
    String commandManageTheme = "builderscoffee.bukkit.manage.theme";
    String commandManageThemeChange = "builderscoffee.bukkit.manage.theme.change";
    String commandManageThemeList = "builderscoffee.bukkit.manage.theme.list";
    String commandManageSeason = "builderscoffee.bukkit.manage.season";
    String commandManageSeasonChange = "builderscoffee.bukkit.manage.season.change";
    String commandManageSeasonList = "builderscoffee.bukkit.manage.season.list";
}
