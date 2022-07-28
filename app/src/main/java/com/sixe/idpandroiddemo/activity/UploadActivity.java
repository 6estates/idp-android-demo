package com.sixe.idpandroiddemo.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

import com.sixe.idp.Idp;
import com.sixe.idp.activity.CropActivity;
import com.sixe.idp.bean.FileMimeType;
import com.sixe.idp.bean.TaskInfo;
import com.sixe.idp.core.ExtractSubmitter;
import com.sixe.idp.core.TaskCallback;
import com.sixe.idp.utils.FilePathUtils;
import com.sixe.idp.utils.ImageTools;
import com.sixe.idp.utils.ToastUtil;
import com.sixe.idpandroiddemo.R;

import java.io.File;
import java.util.ArrayList;

public class UploadActivity extends AppCompatActivity {

    private String currentPhotoPath;
    private ArrayList<String> mPhotos = new ArrayList<>();

    private PhotoListAdapter mPhotoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        findViewById(R.id.iv_back).setOnClickListener(view -> {
            finish();
        });

        // submit pdf
        findViewById(R.id.btn_pdf).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, 10);
        });

        // take photo
        findViewById(R.id.btn_photo).setOnClickListener(view -> {
            takePhoto();
        });

        // submit photos
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(view -> {
            TaskInfo taskInfo = new TaskInfo.Builder()
                    .imagePaths(mPhotos)
                    .fileType(FileMimeType.FILE_BANK_STATEMENT)
                    .hitl(false)
                    .build();
            ExtractSubmitter.submitImages(taskInfo, new TaskCallback() {
                @Override
                public void success(int id) {
                    ToastUtil.show(UploadActivity.this, "id is " + id);
                }

                @Override
                public void failure(String error) {
                    ToastUtil.show(UploadActivity.this, error);
                }
            });
        });

        RecyclerView rvPhoto = findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(UploadActivity.this, 2));
        mPhotoListAdapter = new PhotoListAdapter(UploadActivity.this, mPhotos);
        rvPhoto.setAdapter(mPhotoListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 10 && data != null) {
                // pdf
                Uri uri = data.getData();

                String path = FilePathUtils.getFilePath(UploadActivity.this, uri);
                TaskInfo taskInfo = new TaskInfo.Builder()
                        .filePath(path)
                        .fileType(FileMimeType.FILE_BANK_STATEMENT)
                        .hitl(false)
                        .build();

                ExtractSubmitter.submitPdf(taskInfo, new TaskCallback() {
                    @Override
                    public void success(int id) {
                        ToastUtil.show(UploadActivity.this, "id is " + id);
                    }

                    @Override
                    public void failure(String error) {
                        ToastUtil.show(UploadActivity.this, error);
                    }
                });
            } else if (requestCode == 100) {
                // photo
                Intent intent = new Intent(UploadActivity.this, CropActivity.class);
                intent.putExtra(Idp.IMAGE_PATH, currentPhotoPath);
                startActivityForResult(intent, 101);
            } else if (requestCode == 101 && data != null) {
                String imgPath = data.getStringExtra(Idp.CROP_IMAGE_PATH);
                mPhotos.add(imgPath);
                mPhotoListAdapter.notifyDataSetChanged();
            }

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