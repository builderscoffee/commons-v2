package eu.builderscoffee.commons.common.configuration;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import eu.builderscoffee.api.common.configuration.annotation.Configuration;
import eu.builderscoffee.api.common.data.MySQLCredentials;
import eu.builderscoffee.api.common.redisson.RedisCredentials;
import eu.builderscoffee.api.common.redisson.infos.Server;
import lombok.Data;

/**
 * This class stores common settings data configuration
 */
@Data
@Configuration("settings")
public class SettingsConfig {

    private Server.ServerStartingMethod startingMethod = Server.ServerStartingMethod.STATIC;
    private PluginMode pluginMode = PluginMode.DEVELOPMENT;
    private LoadMode loadMode = LoadMode.NORMAL;
    private String name = "Unknown";
    private MySQLCredentials mySQL = new MySQLCredentials();
    private RedisCredentials redis = new RedisCredentials();

    public enum PluginMode{
        @JsonEnumDefaultValue
        DEVELOPMENT,
        PRODUCTION
    }

    public enum LoadMode{
        @JsonEnumDefaultValue
        NORMAL,
        LAZY
    }
}
