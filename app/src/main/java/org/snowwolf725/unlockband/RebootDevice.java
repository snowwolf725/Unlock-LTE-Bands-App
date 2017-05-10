package org.snowwolf725.unlockband;

import java.util.ArrayList;

/**
 * Created by snowwolf725 on 2017-04-25.
 */

public class RebootDevice extends ExecuteAsRootBase {

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("reboot");
        return commands;
    }
}
