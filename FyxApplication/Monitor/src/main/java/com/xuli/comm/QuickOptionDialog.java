package com.xuli.comm;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.xuli.monitor.MonitorActivity;
import com.xuli.monitor.MonotorSelectorActivity;
import com.xuli.monitor.R;
import com.xuli.monitor.TraceActivity;
import com.xuli.monitor.WebScoketActivity;

public class QuickOptionDialog extends Dialog implements
        View.OnClickListener {

    private ImageView mClose;
//    private Context  mContext;
    private MonitorActivity monitorActivity;

    public interface OnQuickOptionformClick {
        void onQuickOptionClick(int id);
    }

    private OnQuickOptionformClick mListener;

    private QuickOptionDialog(Context context, boolean flag,
            OnCancelListener listener) {
        super(context, flag, listener);
    }

    @SuppressLint("InflateParams")
    private QuickOptionDialog(Context context, int defStyle) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.dialog_quick_option, null);
        contentView.findViewById(R.id.rell_truck).setOnClickListener(
                this);
        contentView.findViewById(R.id.rell_more)
                .setOnClickListener(this);
        contentView.findViewById(R.id.rell_monitor)
                .setOnClickListener(this);
        contentView.findViewById(R.id.rell_path)
                .setOnClickListener(this);
        mClose = (ImageView) contentView.findViewById(R.id.iv_close);

        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.quick_option_close);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        mClose.startAnimation(operatingAnim);

        mClose.setOnClickListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                QuickOptionDialog.this.dismiss();
                return true;
            }
        });
        super.setContentView(contentView);

    }

    public QuickOptionDialog(Context context) {
        this(context, R.style.quick_option_dialog);
        monitorActivity = (MonitorActivity)context;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
        mListener = lis;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
        case R.id.iv_close:
            dismiss();
            break;
        case R.id.rell_truck:
            Intent  monitor_selector = new Intent(monitorActivity, MonotorSelectorActivity.class);
            monitorActivity.startActivityForResult(monitor_selector,1);
            break;
        case R.id.rell_more:

            break;
        case R.id.rell_monitor:
            Intent  intentMonitor = new Intent(monitorActivity, WebScoketActivity.class);
            monitorActivity.startActivity(intentMonitor);
            break;
        case R.id.rell_path:
            Intent  intent = new Intent(monitorActivity, TraceActivity.class);
            monitorActivity.startActivity(intent);
            break;
        default:
            break;
        }
        if (mListener != null) {
            mListener.onQuickOptionClick(id);
        }


        dismiss();
        monitorActivity.overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

  /*  private void onClickTweetPub(int id) {
        Bundle bundle = new Bundle();
        int type = -1;
        switch (id) {
        case R.id.ly_quick_option_album:
            type = TweetPubFragment.ACTION_TYPE_ALBUM;
            break;
        case R.id.ly_quick_option_photo:
            type = TweetPubFragment.ACTION_TYPE_PHOTO;
            break;
        default:
            break;
        }
        bundle.putInt(TweetPubFragment.ACTION_TYPE, type);
        UIHelper.showTweetActivity(getContext(), SimpleBackPage.TWEET_PUB,
                bundle);
    }

    private void onClickNote() {
        Bundle bundle = new Bundle();
        bundle.putInt(NoteEditFragment.NOTE_FROMWHERE_KEY,
                NoteEditFragment.QUICK_DIALOG);
        UIHelper.showSimpleBack(getContext(), SimpleBackPage.NOTE_EDIT, bundle);
    }*/
}
