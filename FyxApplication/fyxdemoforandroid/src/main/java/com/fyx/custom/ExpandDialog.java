package com.fyx.custom;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fyx.andr.R;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/12/26
 * 描述 ：
 */
public class ExpandDialog extends BaseDialog {
    private EditText expand_input;
    private Button input_ok;
    private IexpandInputBack iexpandInputBack;

    public void show(Activity activity, IexpandInputBack iexpandInputBack) {
        if (activity == null) return;
        this.iexpandInputBack = iexpandInputBack;
        show(activity, "ExpandDialog");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.expand_dialog;
    }

    @Override
    protected void initView(View view) {
        expand_input = view.findViewById(R.id.expand_input);
        input_ok = view.findViewById(R.id.input_ok);
        input_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(expand_input.getText().toString())) {
                    String aa = expand_input.getText().toString();
                    iexpandInputBack.inputReturn(aa);
                    dismiss();
                }
            }
        });
    }

    @Override
    protected void loadData(Bundle bundle) {
    }

    public void setIexpandInputBack(IexpandInputBack iexpandInputBack) {
        this.iexpandInputBack = iexpandInputBack;
    }

    public interface IexpandInputBack {
        void inputReturn(String CCT);
    }
}
