package com.sixe.idpandroiddemo.activity;

import static androidx.core.content.FileProvider.getUriForFile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matrix.spinner.MaterialSpinner;
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
    private String mFileTypeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        findViewById(R.id.iv_back).setOnClickListener(view -> {
            finish();
        });

        String[] fileTypeArray = getResources().getStringArray(R.array.file_type);
        String[] fileTypeAliasArray = getResources().getStringArray(R.array.file_type_alias);

        MaterialSpinner spType = findViewById(R.id.sp_type);
        spType.setItems(fileTypeAliasArray);
        spType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                mFileTypeStr = fileTypeArray[position];
            }
        });
        spType.setSelectedIndex(0);
        mFileTypeStr = FileMimeType.FILE_ACRA_BIZFILE;

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
                    .fileType(mFileTypeStr)
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
                Uri uri = data.getData();
                // request parameter
                String path = FilePathUtils.getFilePath(UploadActivity.this, uri);
                TaskInfo taskInfo = new TaskInfo.Builder()
                        .filePath(path)
                        .fileType(mFileTypeStr)
                        .hitl(false)
                        .build();
                // submit PDF
                ExtractSubmitter.submitPdf(taskInfo, new TaskCallback() {
                    @Override
                    public void success(int id) {
                        // id is task id
                        ToastUtil.show(UploadActivity.this, "id is " + id);
                    }

                    @Override
                    public void failure(String error) {
                        ToastUtil.show(UploadActivity.this, error);
                    }
                });
            } else if (requestCode == 100) {
                // crop photo
                Intent intent = new Intent(UploadActivity.this, CropActivity.class);
                intent.putExtra(Idp.IMAGE_PATH, currentPhotoPath);
                startActivityForResult(intent, 101);
            } else if (requestCode == 101 && data != null) {
                // result of crop photo
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