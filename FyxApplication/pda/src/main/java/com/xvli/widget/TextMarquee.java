package com.xvli.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * TextView 跑马灯效果一个布局可用多个效果
 * 
 * @author Administrator
 * 
 */
public class TextMarquee extends TextView {
	public TextMarquee(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		if (focused)
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		if (hasWindowFocus)
			super.onWindowFocusChanged(hasWindowFocus);
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
