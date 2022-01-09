package eu.builderscoffee.commons.bukkit.configuration.messages.command.manage;

import lombok.Data;

@Data
public class ManageThemesCommandConfigurationPart {

    String newNameNotEmpty = "&cVous devez aussi entrer le nouveau nom";
    String nameAlreadyExist = "&c\"&f%name%&c\" existe déja !";
    String list = "&6List des themes:";
    String listFormat = "&7 - %id%";
    String listFormatNames = "&7   %lang% : %name%";
    String created = "&aTheme ajouté";
    String edited = "&aTraduction du theme(%id%) mis pour %language% à %name%";
    String deleted = "&aTheme %id% supprimé";
    String secondArgumentNoNumber = "&cLe second argument doit être un chiffre.";
    String themeNotFound = "&cCe theme n'existe pas.";
    String languageNotFound = "&cCette langue n'existe pas.";
    String languagesList = "&6Langues: %languages%";

    String commandList = "&6/manage themes &elist &7: Voir la liste des themes";
    String commandCreate = "&6/manage themes &ecreate &7: Creer un theme";
    String commandEdit = "&6/manage themes &eedit <id> <language> <name> &7: Modifier une theme selon la langue";
    String commandDelete = "&6/manage themes &edelete <id> &7: Suprimmer une theme";
}
