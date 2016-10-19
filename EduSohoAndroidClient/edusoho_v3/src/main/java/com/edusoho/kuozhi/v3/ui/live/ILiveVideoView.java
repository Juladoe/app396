package com.edusoho.kuozhi.v3.ui.live;

/**
 * Created by suju on 16/10/18.
 */
public interface ILiveVideoView {

    void setNotice(String notice);

    void setLivePlayStatus(boolean playStatus);

    void checkLivePlayStatus();
}
