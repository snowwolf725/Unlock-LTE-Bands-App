package org.snowwolf725.unlockband;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by snoww_000 on 2017-04-25.
 */

public class BackupRMT extends ExecuteAsRootBase {

    private String filePath = "";

    public BackupRMT(String _filePath) {
        filePath = _filePath;
    }

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("cp /system/bin/rmt_storage " + filePath + "/rmt_storage");
        commands.add("chmod 0755 " + filePath + "/rmt_storage");
        commands.add("chcon u:object_r:app_data_file:s0:c512,c768 " + filePath + "/rmt_storage");
        File backupFile = new File(filePath + "/rmt_storage_backup");
        if(backupFile.exists() == false) {
            commands.add("cp /system/bin/rmt_storage " + filePath + "/rmt_storage");
        }
        return commands;
    }
}
