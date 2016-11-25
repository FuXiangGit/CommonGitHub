package com.catchmodel.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Created by Administrator on 12:48.
 */
public class InstantAutoComplete extends AutoCompleteTextView {
    private int myThreshold;

    public InstantAutoComplete(Context context) {
        super(context);
    }

    public InstantAutoComplete(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused){
            if(getText()!=null){
                performFiltering(getText(),0);
                showDropDown();
            }
//            PDALogger.d("getText()------"+getText());

        }
    }

    @Override
    public void setThreshold(int threshold) {
        if(threshold < 0){
            threshold = 0;
        }
        myThreshold = threshold;
    }

    public int getThreshold() {
        return myThreshold;
    }

}
