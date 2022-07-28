package com.sixe.idpandroiddemo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sixe.idp.core.ResultCallback;
import com.sixe.idp.core.ResultExtractor;
import com.sixe.idp.utils.ToastUtil;
import com.sixe.idpandroiddemo.R;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView tvResult = findViewById(R.id.tv_result);
        // get result by task id
        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> {

            ResultExtractor.extractResultByTaskId("1069555", new ResultCallback() {
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

        // send email
        EditText etEmail = findViewById(R.id.et_email);
        Button btnSendEmail = findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(view -> {
            String email = etEmail.getText().toString();
            ResultExtractor.sendEmail(ResultActivity.this, "1069555", "1069555_Result.excel", email, new ResultCallback() {
                @Override
                public void success(String data) {
                    ToastUtil.show(ResultActivity.this,"Success");
                }

                @Override
                public void failure(String error) {
                    ToastUtil.show(ResultActivity.this,error);
                }
            });
        });
    }
}