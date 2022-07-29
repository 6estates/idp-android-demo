package com.sixe.idpandroiddemo.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sixe.idp.core.ResultCallback;
import com.sixe.idp.core.ResultExtractor;
import com.sixe.idp.utils.ToastUtil;
import com.sixe.idpandroiddemo.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // task id
        EditText etId = findViewById(R.id.et_id);
        String taskId = etId.getText().toString();

        TextView tvResult = findViewById(R.id.tv_result);
        // get json result by task id
        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> {

            ResultExtractor.extractResultByTaskId(taskId, new ResultCallback() {
                @Override
                public void success(String data) {
                    tvResult.setText(data);
                }

                @Override
                public void failure(String error) {
                    tvResult.setText(error);
                }
            });


        });

        // show excel path
        TextView tvExcel = findViewById(R.id.tv_excel);
        // get excel result by task id
        Button btnExcel = findViewById(R.id.btn_excel);
        btnExcel.setOnClickListener(view -> {

            ResultExtractor.getTaskExcel(ResultActivity.this, taskId, new ResultCallback() {
                @Override
                public void success(String data) {
                    // data is excel path
                    tvExcel.setText(data);
                }

                @Override
                public void failure(String error) {
                    tvExcel.setText(error);
                }
            });

        });

        // send email
        EditText etEmail = findViewById(R.id.et_email);
        Button btnSendEmail = findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            ResultExtractor.sendEmail(ResultActivity.this, taskId, taskId + ".excel", email, new ResultCallback() {
                @Override
                public void success(String data) {
                    ToastUtil.show(ResultActivity.this, "Success");
                }

                @Override
                public void failure(String error) {
                    ToastUtil.show(ResultActivity.this, error);
                }
            });
        });
    }
}