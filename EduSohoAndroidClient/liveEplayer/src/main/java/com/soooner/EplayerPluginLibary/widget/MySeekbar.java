package com.soooner.EplayerPluginLibary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by zhaoxu2014 on 14-12-13.
 */
public class MySeekbar extends SeekBar {
    SizeChangedListener listener;
    public MySeekbar(Context context) {
        super(context);
    }

    public MySeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setListener(SizeChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(null!=listener){
            listener.sizeChanged(w);
        }

    }
    public interface SizeChangedListener  {
        public void sizeChanged(int w);
    }

}
