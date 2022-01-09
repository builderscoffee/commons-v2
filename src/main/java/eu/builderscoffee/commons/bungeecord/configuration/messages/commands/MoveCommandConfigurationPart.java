package eu.builderscoffee.commons.bungeecord.configuration.messages.commands;

import lombok.Data;

@Data
public class MoveCommandConfigurationPart {

    String usage = "&6/move &e<joueur> <serveur>";
    String playersOnline = "&6Joueurs en ligne: %players%";
    String serversOnline = "&6Serveurs en ligne: %servers%";
    String playerNotOnline = "&cCe joueur n'est pas en ligne";
    String serverNotExist = "&cCe serveur n'existe pas";
    String movedByMessage = "&6Vous avez été déplacé par %player% dans le serveur %server%.";
    String movedPlayerMessage = "&6Vous avez déplacé %player% dans le serveur %server%.";
    String alreadyOnThisServer = "&cCe joueur est déja sur le server !";
}
