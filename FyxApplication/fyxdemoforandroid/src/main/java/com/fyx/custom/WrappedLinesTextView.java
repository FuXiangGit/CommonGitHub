package com.fyx.custom;

import android.content.Context;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/12/18
 * 描述 ：
 */
public class WrappedLinesTextView extends AppCompatTextView {

    /**
     * Measuring type based on dividing.
     * <br>
     * <b>Note: It's not correctly work with different lines height, like in spannable text!</b>
     */
    public static final int CALC_TYPE_DIVIDE = 0;
    /**
     * Measuring like {@link #CALC_TYPE_DIVIDE} with ignoring last line extra spacing.
     */
    public static final int CALC_TYPE_DIVIDE_WITHOUT_LAST_EXTRA_SPACING = 1;
    /**
     * Measuring type based on {@link android.text.Layout#getLineForVertical(int)}.
     * <br>
     * <b>Note: It's not correctly handling height changing!</b>
     */
    public static final int CALC_TYPE_GET_FOR_VERTICAL = 2;
    /**
     * Tag for logging
     */
    public static final String TAG = "WrappedLinesTextView";
    /**
     * Last measured line count
     */
    private int lastLinesInHeight = -1;
    /**
     * Flag for avoid unnecessary requestLayout() call
     */
    private boolean isRequestLayoutDenied;
    /**
     * Current type of lines measuring algorithm
     */
    @CalcType
    private int currentCalcType = CALC_TYPE_DIVIDE;

    public WrappedLinesTextView(Context context) {
        super(context);
        init();
    }

    //region Constructors

    public WrappedLinesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WrappedLinesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListenerImpl());
    }

    //endregion

    @Override
    public void requestLayout() {
        if (isRequestLayoutDenied) {
            isRequestLayoutDenied = false;
        } else {
            super.requestLayout();
        }
    }

    /**
     * Set type of lines counting
     */
    public void setLinesCalcType(@CalcType int type) {
        if (type != currentCalcType) {
            currentCalcType = type;
            invalidate();
        }
    }

    /**
     * See {@link #CALC_TYPE_GET_FOR_VERTICAL}
     */
    //TODO it's not handling height changing
    private void calcLinesCountFromVertical() {
        // subtract and increase by one for special cases, it's not mistake
        int linesInHeight = getLayout().getLineForVertical(getHeightWithoutTopPadding() - getOneLineHeight()) + 1;
        setNewLinesCntIfNeeded(linesInHeight);
    }

    /**
     * See {@link #CALC_TYPE_DIVIDE}
     */
    private void calcLinesCountFromDivide(boolean isLastLineSpacingIgnored) {
        int linesInHeight = 0;
        if (isLastLineSpacingIgnored && Build.VERSION.SDK_INT >= 16) {
            linesInHeight = (int) ((getHeightWithoutTopPadding() + getLineSpacingExtra()) / getOneLineHeight());
        } else {
            linesInHeight = getHeightWithoutTopPadding() / getOneLineHeight();
        }
        setNewLinesCntIfNeeded(linesInHeight);
    }

    private int getHeightWithoutTopPadding() {
//        return getHeight() - getPaddingTop();
        return getHeight();
    }

    private void setNewLinesCntIfNeeded(int linesInHeight) {
        if (linesInHeight != lastLinesInHeight) {
            lastLinesInHeight = linesInHeight;
            isRequestLayoutDenied = true;
            setMaxLines(linesInHeight);
        }
    }

    private int getOneLineHeight() {
        Layout layout = getLayout();
        int height = layout.getHeight();
        int linesCnt = layout.getLineCount();
        if (linesCnt == 0) {
            return 0;
        }
        return height / linesCnt;
    }

    @IntDef({CALC_TYPE_DIVIDE, CALC_TYPE_DIVIDE_WITHOUT_LAST_EXTRA_SPACING, CALC_TYPE_GET_FOR_VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CalcType {
    }

    private class OnPreDrawListenerImpl implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            switch (currentCalcType) {

                case CALC_TYPE_DIVIDE:
                    calcLinesCountFromDivide(false);
                    break;

                case CALC_TYPE_DIVIDE_WITHOUT_LAST_EXTRA_SPACING:
                    calcLinesCountFromDivide(true);
                    break;

                case CALC_TYPE_GET_FOR_VERTICAL:
                    calcLinesCountFromVertical();
                    break;

                default:
                    break;
            }
            return true;
        }
    }
}
