package com.sixe.idpandroiddemo.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.sixe.idp.utils.ImageTools;
import com.sixe.idpandroiddemo.R;

import java.io.File;

public class RotateActivity extends AppCompatActivity {

    private ImageView mImageView;

    private String currentPhotoPath;
    private int angle = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 10) {
                mImageView.setImageBitmap(BitmapFactory.decodeFile(currentPhotoPath));
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);

        findViewById(R.id.btn_take_photo).setOnClickListener(view -> {
            takePhoto();
        });

        findViewById(R.id.iv_rotate).setOnClickListener(view -> {
            angle = angle + 90;
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            mImageView.setImageBitmap(ImageTools.rotateBitmap(bitmap, angle % 360));
        });

        mImageView = findViewById(R.id.image_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            mHandler.sendEmptyMessage(10);
        }
    }

    /**
     * Turn up the camera to take photos
     */
    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = ImageTools.createImageFile(this);
            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri photoUri = getUriForFile(this, "com.sixe.idp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, 100);
            }
        }
    }
}