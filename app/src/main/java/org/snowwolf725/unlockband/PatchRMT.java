package org.snowwolf725.unlockband;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snowwolf725  on 2017-04-25.
 */

public class PatchRMT extends ExecuteAsRootBase {

    private boolean isOK = false;

    private String filePath = "";

    public PatchRMT(String _filePath) {
        filePath = _filePath;
    }

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        File stockFile = new File(filePath + "/rmt_storage");
        File patchFile = new File(filePath + "/rmt_storage_patch");
        if(stockFile.exists()) {
            patch(stockFile, patchFile);
        }

        commands.add("mount -o rw,remount /system");
        commands.add("mv /system/bin/rmt_storage /system/bin/rmt_storage_backup");
        commands.add("cp -a " + filePath + "/rmt_storage_patch /system/bin/rmt_storage");
        commands.add("chmod 0755 /system/bin/rmt_storage");
        commands.add("chcon  u:object_r:rmt_storage_exec:s0  /system/bin/rmt_storage");
        commands.add("mount -o ro,remount /system");
        return commands;
    }

    public void patch(File src, File dst) {
        try {
            final int NOT_FOUND = -1, PATCH_OFFSET = 10;
            List<Byte> fileContent = new ArrayList<Byte>();
            // pattern = /oem/nvbk/static
            byte[] pattern = { 0x2F, 0x6F, 0x65, 0x6D, 0x2F, 0x6E, 0x76, 0x62, 0x6B, 0x2F, 0x73, 0x74, 0x61, 0x74, 0x69, 0x63};
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            isOK = false;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((len = in.read(buf)) > 0) {
                for(int i = 0; i < len; i++) {
                    fileContent.add(buf[i]);
                }
            }
            byte[] result = new byte[fileContent.size()];
            for(int i = 0; i < fileContent.size(); i++) {
                result[i] = fileContent.get(i);
            }
            int index = FileUtil.indexOf(result, pattern);
            if(index != NOT_FOUND) {
                result[index + PATCH_OFFSET] = 'v';
                isOK = true;
            }
            out.write(result, 0, result.length);
            in.close();
            out.close();
        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }

    public boolean getTestResult() {
        return isOK;
    }


}
