package eu.builderscoffee.commons.bukkit.events.builderscoffee;

import eu.builderscoffee.api.common.data.tables.Profil;
import eu.builderscoffee.api.common.events.Event;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class LanguageChangeEvent extends Event {

    private Player player;
    private Profil.Languages language;

    public LanguageChangeEvent(Player player, Profil.Languages language) {
        this.player = player;
        this.language = language;
    }
}
