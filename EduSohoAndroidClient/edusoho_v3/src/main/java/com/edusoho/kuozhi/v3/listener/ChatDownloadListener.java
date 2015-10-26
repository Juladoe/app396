package com.edusoho.kuozhi.v3.listener;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/10/16.
 */
public interface ChatDownloadListener {
    public void updateVoiceDownloadStatus(long downId);

    public HashMap<Long, Integer> getDownloadList();
}
