package eu.builderscoffee.commons.common.redisson.topics;

import eu.builderscoffee.api.common.redisson.RedisTopic;
import lombok.experimental.UtilityClass;

/**
 * This class contains some {@link RedisTopic} that are used into the most sub-plugins
 */
@UtilityClass
public class CommonTopics {

    public static RedisTopic STAFFCHAT = new RedisTopic("staffchat", "Chat privée pour le staff");
    public static RedisTopic SERVER_MANAGER = new RedisTopic("server_manager", "");
}
