package org.snowwolf725.unlockband;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by snoww_000 on 2017-04-25.
 */

public class RestoreRMT extends ExecuteAsRootBase {

    private String filePath = "";

    public RestoreRMT(String _filePath) {
        filePath = _filePath;
    }

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        commands.add("mount -o rw,remount /system");
        commands.add("rm /system/bin/rmt_storage_backup");
        commands.add("mv /system/bin/rmt_storage /system/bin/rmt_storage_backup");
        commands.add("cp -a " + filePath + "/rmt_storage_backup /system/bin/rmt_storage");
        commands.add("chmod 0755 /system/bin/rmt_storage");
        commands.add("chcon  u:object_r:rmt_storage_exec:s0  /system/bin/rmt_storage");
        commands.add("mount -o ro,remount /system");
        return commands;
    }
}
