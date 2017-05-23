package org.snowwolf725.unlockband;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by snoww_000 on 2017-04-25.
 */

public class FileCheck extends ExecuteAsRootBase {

    public static final int STAT_NOT_EXIST = 0;

    public static final int STAT_STOCK = 1;

    public static final int STAT_PATCH = 2;

    public static final int STAT_ERROR = 3;

    private int checkResult = STAT_NOT_EXIST;

    private String filePath = "";

    public FileCheck(String _filePath) {
        filePath = _filePath;
    }

    @Override
    protected ArrayList<String> getCommandsToExecute() {
        ArrayList<String> commands = new ArrayList<String>();
        return commands;
    }

    public String getVersion() {
        return propReader("ro.oxygen.version");
    }

    public boolean isDiagEnable() {
        boolean isEnable= false;
        String result = propReader("sys.usb.config");
        if(result.contains("diag")) {
            isEnable = true;
        } else {
            isEnable = false;
        }
        return isEnable;
    }

    public int checkFile() {
        checkResult = STAT_NOT_EXIST;
        try {
            File src = new File(filePath +"/rmt_storage");
            if(src.exists() == false) {
                return checkResult;
            }
            InputStream in = new FileInputStream(src);
            String fileConent = "";

            byte[] buf = new byte[1024];
            while (in.read(buf) > 0) {
                fileConent = fileConent + new String(buf);
            }
            if(fileConent.contains("/oem/nvbk/vtatic")) {
                checkResult = STAT_PATCH;
            } else if(fileConent.contains("/oem/nvbk/static")) {
                checkResult = STAT_STOCK;
            } else {
                checkResult = STAT_ERROR;
            }
            in.close();
        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
        return checkResult;
    }

    public void backupStockFile() {
        final int NOT_FOUND = -1, PATCH_OFFSET = 10;
        List<Byte> fileContent = new ArrayList<Byte>();
        // pattern = /oem/nvbk/vtatic
        byte[] pattern = { 0x2F, 0x6F, 0x65, 0x6D, 0x2F, 0x6E, 0x76, 0x62, 0x6B, 0x2F, 0x76, 0x74, 0x61, 0x74, 0x69, 0x63};
        File src = new File(filePath +"/rmt_storage");
        File dst = new File(filePath +"/rmt_storage_backup");
        try {
            if(dst.exists() || src.exists() == false) {
                return;
            }
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

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
                result[index + PATCH_OFFSET] = 's';
            }
            out.write(result, 0, result.length);
            in.close();
            out.close();
        } catch (IOException _ex) {
            _ex.printStackTrace();
        }
    }

    private String propReader(String _propName) {
        String result = "";
        Process process = null;
        try {
            process = new ProcessBuilder().command("/system/bin/getprop", _propName).redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            result = bufferedReader.readLine();
            if(result == null) {
                result = "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
        return result;
    }

    public boolean isBackupExist() {
        File dst = new File(filePath +"/rmt_storage_backup");
        return dst.exists();
    }

    public int getRmtPid() {
        int pid = 0;
        String result = "";
        Process process = null;
        try {
            process = new ProcessBuilder().command("su","-c","ps | grep rmt_storage").redirectErrorStream(true).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        try {
            while ((result = bufferedReader.readLine()) != null) {
                if(result.contains("rmt_storage")) {
                    String[] segments = result.split(" ");
                    for(String seg : segments) {

                    }
                    pid = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        process.destroy();
        return pid;
    }
}
