package com.wong.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author WongKyunban
 * description 6.0权限申请
 * created at 2019-03-07 下午5:14
 * @version 1.0
 */
public class BaseActivity extends AppCompatActivity {


    public static final int PERMISSIONS_GRANTED = 0; // 权限授权
    public static final int PERMISSIONS_DENIED = 1; // 权限拒绝
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}; // 待申请权限
    private String[] permissionDescs = new String[]{"读SD卡权限"};


    private Map<String, String> mapPermissionDescs = new HashMap<>();

    final int REQUEST_PERMISSIONS_CODE = 100;
    private boolean isRequesting;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initPermission();


    }

    protected void initPermission() {

        if (isAllPermissionsGranted(permissions)) {
            return;
        }

        if (permissions.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                mapPermissionDescs.put(permissions[i], permissionDescs[i]);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_CODE && !isAllPermissionsGranted(grantResults)) {
            requestPermissions();
        }
    }

    /**
     * 权限提示对话框
     *
     * @param isShowRationale
     */
    private void showMissingPermissionDialog(final boolean isShowRationale, String permissionDesc) {
        String formatStr = null;
        if (isShowRationale) {
            formatStr = getString(R.string.permission_desc1);
        } else {
            formatStr = getString(R.string.permission_desc2);
        }
        String message = String.format(formatStr, TextUtils.isEmpty(permissionDesc) ? "必要" : permissionDesc);

        Snackbar.make(getWindow().getDecorView(), message, Snackbar.LENGTH_LONG).setAction(R.string.settings, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowRationale) {
                    ActivityCompat.requestPermissions(BaseActivity.this, permissions, REQUEST_PERMISSIONS_CODE);
                } else {
                    startAppSettings();
                }
            }
        }).show();
    }

    /**
     * 所有权限都已通过
     *
     * @param permissions
     * @return
     */
    private boolean isAllPermissionsGranted(@NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 检测所有权限都已通过
     *
     * @param grantResults
     * @return
     */
    private boolean isAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false; //denied
            }
        }
        return true; //passed
    }

    /**
     * 请求权限
     */
    private void requestPermissions() {
        permissions = getAllDeniedPermissions();

        /**
         * 场景如下:
         * 1、如果app之前请求过该权限,被用户拒绝, 这个方法就会返回true.
         * 2、如果设备策略禁止应用拥有这条权限, 这个方法也返回false.
         * 3、如果用户之前拒绝权限的时候勾选了对话框中”Don’t ask again”的选项,那么这个方法会返回false.
         */
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[i])) // 如果非第一个权限用户勾选了”Don’t ask again”, 并且拒绝了。那么不会提示该类权限, 所以建议每次申请一个权限。
            {
                showMissingPermissionDialog(true, mapPermissionDescs.get(permissions[i]));
            } else {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_CODE);
            }
        }
    }


    /**
     * 返回所有被拒绝的权限
     *
     * @return
     */
    private String[] getAllDeniedPermissions() {
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }
        String[] permissions = new String[deniedPermissions.size()];
        deniedPermissions.toArray(permissions);
        return permissions;
    }

    /**
     * 启动应用设置
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
