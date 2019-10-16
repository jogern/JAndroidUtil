package com.jogern.jutil.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;

import com.jogern.jutil.entity.PermissionInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Create on 2019-10-15.
 *
 * @author zujianliang
 */
public abstract class PermissionActivity extends AppCompatActivity {

    private final int                  PERMISSIONS_REQUEST = 0x0a;
    private       List<PermissionInfo> mPermissionInfoList = new ArrayList<>();
    private       AlertDialog          mPermissionDialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPermissionDialog != null) {
            mPermissionDialog.dismiss();
            mPermissionDialog = null;
        }
    }

    protected void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createInfo(mPermissionInfoList);
            String[] permission = getAllPermission();
            if (permission.length > 0) {
                for (String p : permission) {
                    int isGranted = ActivityCompat.checkSelfPermission(this, p);
                    if (isGranted == PackageManager.PERMISSION_GRANTED) {
                        removeGrantedPermission(p);
                    }
                }
                if (mPermissionInfoList.size() > 0) {
                    permission = getAllPermission();
                    beforeRequest();
                    ActivityCompat.requestPermissions(this, permission, PERMISSIONS_REQUEST);
                    return;
                }
            }
        }
        startLauncherActivity();
    }

    /**
     * 创建权限对象
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void createInfo(List<PermissionInfo> permissionInfoList) {
        PermissionInfo info = new PermissionInfo.Builder()
                .setTitle("读写内容权限")
                .addPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .build();
        permissionInfoList.add(info);
    }

    /**
     * 获取全部没有授权的权限
     *
     * @return
     */
    private String[] getAllPermission() {
        List<String> list = new ArrayList<>();
        for (PermissionInfo info : mPermissionInfoList) {
            String[] permission = info.getPermission();
            if (permission != null) {
                list.addAll(Arrays.asList(permission));
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 移除已授权的权限
     *
     * @param permission 权限
     */
    private void removeGrantedPermission(String permission) {
        if (permission == null) {
            return;
        }
        List<PermissionInfo> grantedInfo = null;
        for (PermissionInfo info : mPermissionInfoList) {
            info.removePermission(permission);
            if (info.getPermissionSize() <= 0) {
                if (grantedInfo == null) {
                    grantedInfo = new ArrayList<>();
                }
                grantedInfo.add(info);
            }
        }
        if (grantedInfo != null) {
            mPermissionInfoList.removeAll(grantedInfo);
        }
    }

    /**
     * 在checkPermission中,去申请权限前执行
     */
    protected void beforeRequest() {
    }

    /**
     * 权限授权完成,跳转到主界面
     */
    protected abstract void startLauncherActivity();

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST) {
            int len = grantResults.length;
            for (int i = 0; i < len; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    removeGrantedPermission(permissions[i]);
                    continue;
                }
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    removeGrantedPermission(permissions[i]);
                }
            }
            if (mPermissionInfoList.size() > 0) {
                showTipPermission();
            } else {
                startLauncherActivity();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 显示再申请的权限提示
     */
    private void showTipPermission() {
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
        String title = "";
        for (PermissionInfo info : mPermissionInfoList) {
            title += info.getTitle() + "\n";
        }
        title += "以上权限需要被允许应用才能正常运行,请点击允许";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("申请权限").setMessage(title);
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] allPermission = getAllPermission();
                if (allPermission.length > 0) {
                    ActivityCompat.requestPermissions(PermissionActivity.this,
                            allPermission, PERMISSIONS_REQUEST);
                } else {
                    startLauncherActivity();
                }
                if (mPermissionDialog != null) {
                    if (mPermissionDialog.isShowing()) {
                        mPermissionDialog.dismiss();
                    }
                    mPermissionDialog = null;
                }
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.setCancelable(false).create();
        alertDialog.setCanceledOnTouchOutside(false);
        mPermissionDialog = alertDialog;
        alertDialog.show();
    }
}
