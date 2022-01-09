package eu.builderscoffee.commons.bungeecord.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.plugin.Event;

public abstract class DataStatueEvent extends Event {
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Load extends DataStatueEvent {
        private final String uniqueId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Save extends DataStatueEvent {
        private final String uniqueId;
    }
}
