package eu.builderscoffee.commons.bukkit.events.bukkit;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class DataStatueEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public DataStatueEvent() {
        super(true);
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Load extends DataStatueEvent {
        @Getter
        private static final HandlerList handlerList = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return handlerList;
        }

        private final String uniqueId;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class Save extends DataStatueEvent {
        @Getter
        private static final HandlerList handlerList = new HandlerList();

        @Override
        public HandlerList getHandlers() {
            return handlerList;
        }

        private final String uniqueId;
    }
}
