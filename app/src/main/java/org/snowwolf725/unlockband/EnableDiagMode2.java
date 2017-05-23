package org.snowwolf725.unlockband;

import java.util.ArrayList;

/**
 * Created by snowwolf725 on 2017-04-25.
 */

public class EnableDiagMode2 extends ExecuteAsRootBase {

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        String command = "setprop sys.usb.config adb,diag";
        commands.add(command);
        return commands;
    }
}
