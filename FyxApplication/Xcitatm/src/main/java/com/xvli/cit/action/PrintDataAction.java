package com.xvli.cit.action;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.service.PrintDataService;


public class PrintDataAction implements OnClickListener {
    private Context context = null;
    private TextView deviceName = null;
    private TextView connectState = null;
    private EditText printData = null;
    private String deviceAddress = null;
    private PrintDataService printDataService = null;

    public PrintDataAction(Context context, String deviceAddress,
                           TextView deviceName, TextView connectState) {
        super();
        this.context = context;
        this.deviceAddress = deviceAddress;
        this.deviceName = deviceName;
        this.connectState = connectState;
        this.printDataService = new PrintDataService(this.context,
                this.deviceAddress);
        this.initView();

    }

    private void initView() {
        // 设置当前设备名称
        this.deviceName.setText(this.printDataService.getDeviceName());
        // 一上来就先连接蓝牙设备
        boolean flag = this.printDataService.connect();
        if (flag == false) {
            // 连接失败
            this.connectState.setText("连接失败！");
        } else {
            // 连接成功
            this.connectState.setText("连接成功！");

        }
    }

    public void setPrintData(EditText printData) {
        this.printData = printData;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            String sendData = this.printData.getText().toString();
            this.printDataService.send(sendData + "\n");
        } else if (v.getId() == R.id.command) {
            this.printDataService.selectCommand();

        }
    }
}