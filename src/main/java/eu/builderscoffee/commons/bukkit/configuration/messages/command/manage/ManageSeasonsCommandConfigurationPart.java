package eu.builderscoffee.commons.bukkit.configuration.messages.command.manage;

import lombok.Data;

@Data
public class ManageSeasonsCommandConfigurationPart {

    String idNotNumber = "&cVous devez entrer un chiffre, non une mot !";
    String dateNotEmpty = "&cVous devez entrer une date ! \n&cExemple: (10-01-2022)";
    String dateNotCorrect = "&cLa date entré n'est pas correcte ! \n&cExemple: (10-01-2022)";
    String alreadyExist = "&cLa saison existe déja !";
    String notExist = "&cLa saison n'existe pas !";
    String list = "&6List des saisons:";
    String listFormat = "&7 - %id% \n§7    Begin date: %begin_date% \n§7    End date: %end_date%";
    String added = "&aSaison ajouté";
    String updated = "&aSaison modifié";
    String deleted = "&aSaison supprimé";

    String commandList = "&6/manage seasons &elist &7: Voir la liste des saisons";
    String commandAdd = "&6/manage seasons &eadd <begin date> <end date> &7: Ajouter une saison";
    String commandUpdate = "&6/manage seasons &eupdate <id> <begin date> <end date> &7: Modifier une saison";
    String commandDelete = "&6/manage seasons &edelete <id> &7: Suprimmer une saison";
}
