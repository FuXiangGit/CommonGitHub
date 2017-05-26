package com.fyx.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.fyx.lovemovie.R;

/**
 * Created by Mrfu on 2017/5/22.
 */

public class DrawPaint extends SurfaceView {

    private static final int BACKGROUND_COLOR = 0x50555555;
    private static final int PAINT_COLOR = 0xFFFFFFFF;
    private static final float PAINT_WIDTH = 8f;
//    private static final float BACKGROUND_WIDTH = 8f;
//    private static final float BACKGROUND_HEIGHT = 8f;

    private int backgroundColor;
    private int paintColor;
    private float paintWidth;
    private float backgroundWidth;
    private float backgroundHeight;

    private int screenwidth;
    private int screenheight;

    Paint backGroundPaint;
    Paint penGroundPaint;


    public DrawPaint(Context context) {
        super(context);
    }

    public DrawPaint(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttr(context,attrs);
        init();
    }

    public DrawPaint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context,attrs);
        init();
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.DrawPaint);
//        backgroundColor = typeArray.getColor(R.styleable.DrawPaint_paint_background_color,BACKGROUND_COLOR);
        paintColor = typeArray.getColor(R.styleable.DrawPaint_paint_color,PAINT_COLOR);
        paintWidth = typeArray.getDimension(R.styleable.DrawPaint_paint_width,PAINT_WIDTH);
//        backgroundWidth = typeArray.getDimension(R.styleable.DrawPaint_paint_background_width,BACKGROUND_WIDTH);
//        backgroundHeight = typeArray.getDimension(R.styleable.DrawPaint_paint_background_height,BACKGROUND_HEIGHT);
        WindowManager wm = (WindowManager) getContext() .getSystemService(Context.WINDOW_SERVICE);
        screenwidth = wm.getDefaultDisplay().getWidth();
        screenheight = wm.getDefaultDisplay().getHeight();
    }

    private void init() {
        backGroundPaint = new Paint();
        backGroundPaint.setAntiAlias(true);
        backGroundPaint.setDither(true);
        backGroundPaint.setColor(backgroundColor);
        backGroundPaint.setStyle(Paint.Style.FILL);

       /* penGroundPaint = new Paint();
        penGroundPaint.setAntiAlias(true);
        penGroundPaint.setDither(true);
        penGroundPaint.setColor(backgroundColor);
        penGroundPaint.setStyle(Paint.Style.FILL);*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode!=MeasureSpec.EXACTLY){
            width = screenwidth;
        }
        if(heightMode!= MeasureSpec.EXACTLY){
            height = width/3;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
//        canvas.drawRect(0,0,width,height,backGroundPaint);

    }
}
