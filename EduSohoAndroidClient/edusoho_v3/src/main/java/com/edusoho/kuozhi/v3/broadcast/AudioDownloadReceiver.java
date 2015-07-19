package com.edusoho.kuozhi.v3.broadcast;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.edusoho.kuozhi.v3.adapter.ChatAdapter;

/**
 * Created by JesseHuang on 15/7/19.
 */
public class AudioDownloadReceiver extends BroadcastReceiver {

    private ChatAdapter mChatAdapter;

    public void setChatAdapter(ChatAdapter adapter) {
        mChatAdapter = adapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (mChatAdapter != null && mChatAdapter.getDownloadList() != null) {
                mChatAdapter.updateVoiceDownloadStatus(downId);
            }
        }
    }
}
