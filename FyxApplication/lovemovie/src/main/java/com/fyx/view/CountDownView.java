package com.fyx.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.fyx.lovemovie.R;

/**
 * Created by Mrfu on 2017/5/17.
 */

public class CountDownView extends View{
    private static final int BACKGROUND_COLOR = 0x50555555;
    private static final float BORDER_WIDTH = 8f;
    private static final int BORDER_COLOR = 0xFF6ADBFE;
    private static final String TEXT = "跳过广告";
    private static final float TEXT_SIZE = 50f;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    private int backgroundColor;
    private float borderWidth;
    private int borderColor;
    private String text;
    private int textColor;
    private float textSize;

    private Paint circlePaint;
    private TextPaint textPaint;
    private Paint borderPaint;
    private float progress = 135;
    private int txtWidth = 0;

    private StaticLayout staticLayout;

    public CountDownView(Context context) {
        super(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
        init();
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        init();
    }


    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        backgroundColor = typedArray.getColor(R.styleable.CountDownView_background_color,BACKGROUND_COLOR);
        borderWidth = typedArray.getDimension(R.styleable.CountDownView_border_width,BORDER_WIDTH);
        borderColor = typedArray.getColor(R.styleable.CountDownView_border_color,BORDER_COLOR);
        text = typedArray.getString(R.styleable.CountDownView_text);
        if(TextUtils.isEmpty(text)){
            text = TEXT;
        }
        textSize = typedArray.getDimension(R.styleable.CountDownView_text_size,TEXT_SIZE);
        textColor = typedArray.getColor(R.styleable.CountDownView_text_color,TEXT_COLOR);
        typedArray.recycle();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);//抗锯齿
        circlePaint.setDither(true);//抖动 显示更柔和
        circlePaint.setColor(backgroundColor);
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);

        borderPaint  = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setDither(true);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setStyle(Paint.Style.STROKE);

        txtWidth = (int)textPaint.measureText(text.substring(0,(text.length()+1)/2));
        staticLayout = new StaticLayout(text,textPaint,txtWidth, Layout.Alignment.ALIGN_CENTER,1f,0,false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode != MeasureSpec.EXACTLY){
            width = staticLayout.getWidth();
            if(widthMode==MeasureSpec.AT_MOST){
                width = width+txtWidth/2+(int)borderWidth*2;
            }
        }


        if(heightMode != MeasureSpec.EXACTLY){
            height = staticLayout.getHeight();
            if(heightMode==MeasureSpec.AT_MOST){
                height = height+txtWidth/2+(int)borderWidth*2;
            }
        }
        setMeasuredDimension(width,height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();//60
        int height = getMeasuredHeight();//40
        int min = Math.min(width,height);
        canvas.drawCircle(width/2,height/2,min/2,circlePaint);
        //画边框
        RectF rectF;
        if (width > height) {
            rectF = new RectF(width / 2 - min / 2 + borderWidth / 2, 0 + borderWidth / 2, width / 2 + min / 2 - borderWidth / 2, height - borderWidth / 2);
        } else {
            rectF = new RectF(borderWidth / 2, height / 2 - min / 2 + borderWidth / 2, width - borderWidth / 2, height / 2 - borderWidth / 2 + min / 2);
        }
        canvas.drawArc(rectF,-90,progress,false,borderPaint);
        //画字体
        canvas.translate(width/2-staticLayout.getWidth()/2,height/2-staticLayout.getHeight()/2);
        staticLayout.draw(canvas);
    }

    public void start(final long time){

        CountDownTimer countDownTimer = new CountDownTimer(time,10) {
            @Override
            public void onTick(long millisUntilFinished) {
                progress = 360*(time-millisUntilFinished)/time;
                invalidate();
            }

            @Override
            public void onFinish() {
                progress = 360;
                invalidate();
            }
        };
        countDownTimer.start();
    }

}
