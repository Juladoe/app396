package com.edusoho.plugin.video;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.NormalCallback;
import com.plugin.edusoho.bdvideoplayer.BdVideoPlayerFragment;

import java.util.Map;

public class EdusohoVideoManagerActivity extends FragmentActivity implements VideoPlayerCallback {

    public static final String SUPPORT_VIDEO = "support_video";
    public static final String SUPPORTMAP_CHANGE = "support_map_change";

    private Map<String, String> mNotSupprotVideoMap;
    private Context mContext;
    private String mFileExt;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.edusoho_video_manager);
        readConfig();
        initView();

        EdusohoApp.app.addMessageListener(SUPPORTMAP_CHANGE, new CoreEngineMsgCallback() {
            @Override
            public void invoke(MessageModel obj) {
                saveConfig(mFileExt);
                EdusohoBdVideoPlayerFragment fragment = new EdusohoBdVideoPlayerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url", mUrl);
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, fragment).commit();
            }
        });
    }

    public static void start(Context context, String url)
    {
        Intent intent = new Intent(context, EdusohoVideoManagerActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private void initView()
    {
        Intent data = getIntent();
        if (!data.hasExtra("url")) {
            PopupDialog.createNormal(mContext, "视频播放", "视频播放网址无效！").show();
            return;
        }

        mUrl = data.getStringExtra("url");
        mFileExt = getFileExt(mUrl);
        if (mNotSupprotVideoMap.containsKey(mFileExt)) {
            EdusohoBdVideoPlayerFragment fragment = new EdusohoBdVideoPlayerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", mUrl);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        } else {
            EduSohoVideoFragment fragment = new EduSohoVideoFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", mUrl);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    private String getFileExt(String fileName)
    {
        String content = "";
        int questionPos = fileName.lastIndexOf("?");
        if (questionPos != -1) {
            content = fileName.substring(0, questionPos);
        }

        int lastPos = content.lastIndexOf(".");
        if (lastPos != -1) {
            content = content.substring(lastPos);
        } else {
            lastPos = content.lastIndexOf("/");
            if (lastPos != -1) {
                content = content.substring(lastPos);
            }
        }
        return content;
    }

    private void saveConfig(String key)
    {
        SharedPreferences sp = getSharedPreferences(SUPPORT_VIDEO, MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, "support");
        editor.commit();
    }
    private void readConfig()
    {
        SharedPreferences sp = getSharedPreferences(SUPPORT_VIDEO, MODE_APPEND);
        mNotSupprotVideoMap = (Map<String, String>)sp.getAll();
    }

    @Override
    public void clear(NormalCallback normalCallback) {
        normalCallback.success(null);
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void exitFullScreen() {

    }
}
