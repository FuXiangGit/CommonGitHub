package com.fyx.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fyx.andr.R;
import com.fyx.custom.CustomDialog;
import com.fyx.service.CommDialogService;

public class CustomDialogActivity extends AppCompatActivity {

    private Button btnShowDialog,showQuanJuDialog;
    private CustomDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_dialog);
        btnShowDialog = (Button) findViewById(R.id.btn_show_dialog);
        showQuanJuDialog = (Button) findViewById(R.id.btn_show_quanjudialog);

        btnShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.Builder customBuilder = new
                        CustomDialog.Builder(CustomDialogActivity.this);
                customBuilder.setTitle("标题")
                        .setMessage("提示内容")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                dialog = customBuilder.create();
                dialog.show();
            }
        });

        showQuanJuDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(CustomDialogActivity.this, CommDialogService.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(CustomDialogActivity.this, CommDialogService.class));
    }
}
