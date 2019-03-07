package com.wong.myapp;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends BaseActivity {


    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.btn);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PermissionsActivity.actionStartForResult(MainActivity.this, 99, "读SD卡权限", new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});


            }
        });

    }


    private void installApk() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        //重新构造Uri：content://
        Uri fileUri;
        File apkFile = new File(Environment.getExternalStorageDirectory(), "update_folder/update.apk");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            fileUri = FileProvider.getUriForFile(MainActivity.this, "com.wong.myapp.UPDATE_APP_FILE_PROVIDER", apkFile);
            //授予目录临时共享权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {

            fileUri = Uri.fromFile(apkFile);
        }

        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");


        //使用Intent传递Uri
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 99:
                if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
                    installApk();
                }

                break;
        }

    }
}
