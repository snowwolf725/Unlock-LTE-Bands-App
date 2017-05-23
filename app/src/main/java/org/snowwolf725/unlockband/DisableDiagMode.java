package org.snowwolf725.unlockband;

import java.util.ArrayList;

/**
 * Created by snoww_000 on 2017-04-25.
 */

public class DisableDiagMode extends ExecuteAsRootBase {

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        String command = "setprop sys.usb.config adb";
        commands.add(command);
        return commands;
    }
}
