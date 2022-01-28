package eu.builderscoffee.commons.common.redisson.packets;

import eu.builderscoffee.api.bukkit.utils.ItemBuilder;
import eu.builderscoffee.api.bukkit.utils.ItemSerialisationUtils;
import eu.builderscoffee.api.common.redisson.packets.types.RequestPacket;
import eu.builderscoffee.api.common.redisson.packets.types.ResponsePacket;
import eu.builderscoffee.commons.common.utils.Quadlet;
import eu.builderscoffee.commons.common.utils.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to send the available configuration as response to a {@link ServerManagerRequest}
 */
@Getter
@Setter
public class ServerManagerResponse extends ResponsePacket {

    private boolean finished = false;
    @Setter(AccessLevel.NONE)
    private Set<Action> actions = new HashSet<>();

    protected ServerManagerResponse() {
        super();
    }

    public ServerManagerResponse(String packetId) {
        super(packetId);
    }

    public ServerManagerResponse(RequestPacket requestPacket) {
        super(requestPacket);
    }

    public static abstract class Action {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return getClass().equals(o.getClass());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getClass());
        }
    }

    public static class PageItems extends Action {
        @Setter
        @Getter
        private String type;
        @Setter
        @Getter
        private int maxPerPage = 27;
        private String nextPageItem = ItemSerialisationUtils.serialize(new ItemBuilder(Material.ARROW).setName("Page suivante").build());
        private String previousPageItem = ItemSerialisationUtils.serialize(new ItemBuilder(Material.ARROW).setName("Page précédente").build());
        private List<Tuple<String, String>> items = new ArrayList<>();

        public ItemStack getNextPageItem() {
            return ItemSerialisationUtils.deserialize(this.nextPageItem);
        }

        public void setNextPageItem(ItemStack item) {
            nextPageItem = ItemSerialisationUtils.serialize(item);
        }

        public ItemStack getPreviousPageItem() {
            return ItemSerialisationUtils.deserialize(this.previousPageItem);
        }

        public void setPreviousPageItem(ItemStack item) {
            previousPageItem = ItemSerialisationUtils.serialize(item);
        }

        public List<Tuple<ItemStack, String>> getItems() {
            return items.stream()
                    .map(tuple -> new Tuple<ItemStack, String>(ItemSerialisationUtils.deserialize(tuple.getLeft()), tuple.getRight()))
                    .collect(Collectors.toList());
        }

        public void addItem(ItemStack item, String action) {
            items.add(new Tuple(ItemSerialisationUtils.serialize(item), action));
        }
    }


    public static class Items extends Action {
        @Setter
        @Getter
        private String type;
        private List<Quadlet<Integer, Integer, String, String>> items = new ArrayList<>();

        public List<Quadlet<Integer, Integer, ItemStack, String>> getItems() {
            return items.stream()
                    .map(quadlet -> new Quadlet<>(quadlet.getFirst(), quadlet.getSecond(), ItemSerialisationUtils.deserialize(quadlet.getThird()), quadlet.getFourth()))
                    .collect(Collectors.toList());
        }

        public void addItem(int row, int column, ItemStack item, String action) {
            items.add(new Quadlet(row, column, ItemSerialisationUtils.serialize(item), action));
        }
    }

    @Getter
    @Setter
    public static class ChatRequest extends Action {
        private String type;
        private String message;
    }

    @Getter
    @Setter
    public static class ChatResponse extends Action {
        private String message;
    }
}
