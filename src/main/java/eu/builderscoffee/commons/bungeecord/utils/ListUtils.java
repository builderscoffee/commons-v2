package eu.builderscoffee.commons.bungeecord.utils;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class ListUtils {

    public static boolean containsIgnoreCase(List<String> list, String string){
        return list.stream().filter(column -> column.equalsIgnoreCase(string)).count() > 0;
    }

    public static String getIgnoreCase(List<String> list, String string){
        return containsIgnoreCase(list, string)? list.stream().filter(column -> column.equalsIgnoreCase(string)).findFirst().get() : null;
    }
}
