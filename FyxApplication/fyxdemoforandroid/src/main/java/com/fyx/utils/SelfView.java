package com.fyx.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.fyx.andr.R;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Administrator on 2016/6/15 0015.
 */
public class SelfView extends View {

    private Context mContext;

    /**
     * 文本
     */
    private String mText;
    /**
     * 文本的颜色
     */
    private int mTextColor;
    /**
     * 文本的大小
     */
    private int mTextSize;
    /**
     * 文本的大小
     */
    private int mBackColor;
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;


    public SelfView(Context context) {
        super(context);
        this.mContext= context;
    }

    public SelfView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext= context;
        TypedArray a = context.getTheme().obtainStyledAttributes(  attrs,  R.styleable.SelfView,  0, 0);
        try {
            mText = a.getString(R.styleable.SelfView_textTitle);
            mTextColor = a.getColor(R.styleable.SelfView_textColor, Color.BLACK);
            mBackColor = a.getColor(R.styleable.SelfView_backColor,Color.YELLOW);
            mTextSize = a.getDimensionPixelSize(R.styleable.SelfView_textSize,16);
        } finally {
            a.recycle();
        }

       ;//初始化
        init();

    }

    public SelfView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext= context;
    }
    /**
     * 这个支持的最低版本是API 21
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    /*public SelfView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext= context;
    }*/

    /**
     * 初始化图像
     */
    private void init(){
        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        mBound = new Rect();
        mPaint.getTextBounds(mText, 0, mText.length(), mBound);

        //初始化时候添加了一个点击事件
        this.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                mText = randomText();
                postInvalidate();
            }

        });
    }

    private String randomText()
    {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4)
        {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set)
        {
            sb.append("" + i);
        }

        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mBackColor);
        mPaint.setAntiAlias(true); //去锯齿
//        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        canvas.drawCircle(getMeasuredWidth() / 2-Math.max(getPaddingLeft(),getPaddingRight()), getMeasuredHeight() / 2, getWidth()/2-Math.max(getPaddingLeft(),getPaddingRight()), mPaint);

        mPaint.setColor(mTextColor);
        canvas.drawText(mText, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
    }

    /**
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;

        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                width = getPaddingLeft() + getPaddingRight() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                width = getPaddingLeft() + getPaddingRight() + mBound.width();
                break;
        }

        /**
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode)
        {
            case MeasureSpec.EXACTLY:// 明确指定了
                height = getPaddingTop() + getPaddingBottom() + specSize;
                break;
            case MeasureSpec.AT_MOST:// 一般为WARP_CONTENT
                height = getPaddingTop() + getPaddingBottom() + mBound.height();
                break;
        }

        setMeasuredDimension(width, height);
    }
}
