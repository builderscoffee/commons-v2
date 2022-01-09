package eu.builderscoffee.commons.bungeecord.configuration;


import eu.builderscoffee.api.common.configuration.annotation.Configuration;
import lombok.Data;

/**
 * This class stores permission configuration
 */
@Data
@Configuration("permissions")
public class PermissionConfiguration {
    // Permissions
    String globalPermission = "builderscoffee.bungeecord.*";
    String pbanPermission = "builderscoffee.bungeecord.pban";
    String movePermission = "builderscoffee.bungeecord.move";
    String databasePermission = "builderscoffee.bungeecord.database";
    String pbanByPassPermission = "builderscoffee.bungeecord.bypass.pban";
    String ppardonPermission = "builderscoffee.bungeecord.ppardon";
    String staffChatPermission = "builderscoffee.bungeecord.staffchat";
    String serverDefaultPermission = "builderscoffee.bungeecord.server.default";
}
