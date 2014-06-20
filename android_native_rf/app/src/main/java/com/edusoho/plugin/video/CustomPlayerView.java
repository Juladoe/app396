package com.edusoho.plugin.video;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by howzhi on 14-5-30.
 */
public class CustomPlayerView extends VideoView {
    private Context context;
    public int videoWidth;
    public int videoHeight;

    public CustomPlayerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }
    public CustomPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }
    public CustomPlayerView(Context context) {
        super(context);
        this.context = context;
    }
}
