package com.sixe.idpandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.EditText;

import com.sixe.idp.Idp;
import com.sixe.idp.utils.ToastUtil;
import com.sixe.idpandroiddemo.activity.ResultActivity;
import com.sixe.idpandroiddemo.activity.RotateActivity;
import com.sixe.idpandroiddemo.activity.UploadActivity;
import com.sixe.idpandroiddemo.base.IdpApplication;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Permission list
     */
    private List<String> permissionsList = new ArrayList<>();

    /**
     * Permission request code
     */
    private static final int PERMISSION_REQUEST_RESULT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestMyPermissions();
        }

        // init
        EditText etToken = findViewById(R.id.et_token);
        findViewById(R.id.btn_init_token).setOnClickListener(view -> {
            String token = etToken.getText().toString();
            Idp.init(MainActivity.this, token);
            ToastUtil.show(MainActivity.this, "Init Success");
        });

        // submit task
        findViewById(R.id.btn_upload).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UploadActivity.class));
        });

        // get result
        findViewById(R.id.btn_result).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ResultActivity.class));
        });

        // rotate image
        findViewById(R.id.btn_rotate).setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, RotateActivity.class));
        });

    }

    /**
     * Request permission
     */
    private void requestMyPermissions() {
        // Read / write storage permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        // camera permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(Manifest.permission.CAMERA);
        }

        int size = permissionsList.size();
        if (size > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsList.toArray(new String[size]), PERMISSION_REQUEST_RESULT);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_RESULT) {

            if (grantResults.length > 0) {
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        ToastUtil.show(MainActivity.this, "Please agree to all permissions!");
                        finish();
                        return;
                    }
                }
                // For Android 11 and above, you need to apply for all file permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
                        && !Environment.isExternalStorageManager()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivity(intent);
                }
            } else {
                ToastUtil.show(MainActivity.this, "Error!");
                finish();
            }

        }
    }
}