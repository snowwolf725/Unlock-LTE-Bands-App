package org.snowwolf725.unlockband;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        updateMessage();

        Switch togglebutton = (Switch) findViewById(R.id.swt_patch);
        togglebutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    PatchRMT patch = new PatchRMT(MainActivity.this.getFilesDir().getAbsolutePath());
                    patch.execute();
                    if(patch.getTestResult()) {
                        Toast.makeText(MainActivity.this, getString(R.string.status_stock), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.status_patched), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    FileCheck check = new FileCheck(MainActivity.this.getFilesDir().getAbsolutePath());
                    if(check.isBackupExist()) {
                        RestoreRMT restore = new RestoreRMT(MainActivity.this.getFilesDir().getAbsolutePath());
                        restore.execute();
                        Toast.makeText(MainActivity.this, getString(R.string.backup_restore), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.backup_not_exist), Toast.LENGTH_SHORT).show();
                    }
                }
                BackupRMT backup = new BackupRMT(MainActivity.this.getFilesDir().getAbsolutePath());
                backup.execute();

                updateMessage();
                showRebootDialog();
            }
        });
    }

    private void showRebootDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle(getString(R.string.reboot_dialog_title));
        dialog.setMessage(getString(R.string.reboot_dialog_msg));
        dialog.setPositiveButton(getString(R.string.reboot_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                RebootDevice reboot = new RebootDevice();
                reboot.execute();
            }
        })
        .setNegativeButton(getString(R.string.reboot_dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateMessage();
    }

    private void updateMessage() {
        TextView txt_msg = (TextView)findViewById(R.id.txt_msg);
        BackupRMT backup = new BackupRMT(MainActivity.this.getFilesDir().getAbsolutePath());
        backup.execute();
        FileCheck check = new FileCheck(MainActivity.this.getFilesDir().getAbsolutePath());
        check.execute();
        check.backupStockFile();
        String backupStatus = getString(R.string.backup_not_exist);
        if(check.isBackupExist()) {
            backupStatus = getString(R.string.backup_exist);
        }
        Button btn_setting = (Button)findViewById(R.id.btn_usbSetting);
        String diagModeStatus = "";
        if(check.isDiagEnable()) {
            diagModeStatus = getString(R.string.qualDiagMode) + ": " + getString(R.string.enable) + "\n ";
            btn_setting.setText(getString(R.string.btn_setting_disable));
        } else {
            diagModeStatus = getString(R.string.qualDiagMode) + ": " + getString(R.string.disable) + "\n ";
            btn_setting.setText(getString(R.string.btn_setting_enable));
        }
        Switch togglebutton = (Switch) findViewById(R.id.swt_patch);
        togglebutton.setEnabled(false);
        String version = getString(R.string.app_version) + "\n";
        String sysVersion = getString(R.string.system_version) + check.getVersion() + "\n";
        String rmtStatus = getString(R.string.rmt_status);
        if(check.checkFile() == FileCheck.STAT_STOCK) {
            togglebutton.setChecked(false);
            txt_msg.setText(version + sysVersion + diagModeStatus + rmtStatus + getString(R.string.status_stock) + "\n" + backupStatus);
        } else if(check.checkFile() == FileCheck.STAT_PATCH) {
            togglebutton.setChecked(true);
            txt_msg.setText(version + sysVersion + diagModeStatus + rmtStatus + getString(R.string.status_patched) + "\n" + backupStatus);
        } else {
            togglebutton.setChecked(false);
            txt_msg.setText(version + sysVersion + diagModeStatus + rmtStatus + getString(R.string.status_error) + "\n" + backupStatus);
        }
        togglebutton.setEnabled(true);
    }

    public void onOpenUsbSettingBtnClick(View _view) {
        Button btn_setting = (Button)findViewById(R.id.btn_usbSetting);
        FileCheck check = new FileCheck(MainActivity.this.getFilesDir().getAbsolutePath());
        if(check.isDiagEnable()) {
            DisableDiagMode disable = new DisableDiagMode();
            disable.execute();
            btn_setting.setText(getString(R.string.btn_setting_enable));
        } else {
            EnableDiagMode diag = new EnableDiagMode();
            diag.execute();
            btn_setting.setText(getString(R.string.btn_setting_disable));
        }
        updateMessage();
    }
}
