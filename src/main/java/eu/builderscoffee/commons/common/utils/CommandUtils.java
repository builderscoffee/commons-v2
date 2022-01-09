package eu.builderscoffee.commons.common.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommandUtils {

    /**
     *
     * @param args
     * @param pos Get arg at pos (pos begin at 0)
     * @return
     */
    public static String getArgument(@NonNull String[] args, int pos){
        if(args.length > pos)
            return args[pos];
        return "";
    }
}
