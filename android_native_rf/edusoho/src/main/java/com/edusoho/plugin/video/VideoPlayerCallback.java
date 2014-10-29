package com.edusoho.plugin.video;

import com.edusoho.listener.NormalCallback;

/**
 * Created by howzhi on 14-7-11.
 */
public interface VideoPlayerCallback {
    public void clear(NormalCallback normalCallback);
    public boolean isFullScreen();
    public void exitFullScreen();
}
