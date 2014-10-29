package com.edusoho.plugin.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.fragment.video.EduVideoViewFragment;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.listener.EduVideoViewListener;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.Timer;

/**
 * Created by howzhi on 14-9-26.
 */
public class MediaPlayerFragment extends BaseFragment implements EduVideoViewListener {

    private int mCourseId;
    private int mLessonId;
    private String mUrl;
    private FrameLayout mFragmentLayout;
    private CustomMediaController mMediaController;

    @Override
    public String getTitle() {
        return "视频课时";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.plugin_mediaplayer_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle = getArguments();
        mUrl = bundle.getString(Const.MEDIA_URL);
        mCourseId = bundle.getInt(Const.COURSE_ID);
        mLessonId = bundle.getInt(Const.LESSON_ID);
        autoHideTimer = new Timer();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mMediaController = (CustomMediaController) view.findViewById(R.id.custom_mediaController);
        mMediaController.setActivity(mActivity);
        mFragmentLayout = (FrameLayout) view.findViewById(R.id.plugin_fragment_layout);

        loadEduVideoViewFragment(mUrl, 0);
    }

    private void loadEduVideoViewFragment(final String url, int pos)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        EduVideoViewFragment fragment = (EduVideoViewFragment) app.mEngine.runPluginWithFragment(
                "EduVideoViewFragment", mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(Const.MEDIA_URL, url);
            }
        });

        fragment.setController(mMediaController);
        fragment.setPos(pos);
        fragment.setOnErrorListener(this);

        fragmentTransaction.replace(R.id.plugin_fragment_layout, fragment);
        fragmentTransaction.commit();
    }

    private void changeBDPlayFragment(final String url)
    {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                "BDVideoLessonFragment", mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(Const.MEDIA_URL, url);
            }
        });
        fragmentTransaction.replace(R.id.lesson_content, fragment);
        fragmentTransaction.setCustomAnimations(
                FragmentTransaction.TRANSIT_FRAGMENT_FADE, FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();
    }

    private void reloadLessonMediaUrl(final NormalCallback<String> callback)
    {
        RequestUrl requestUrl = app.bindUrl(Const.COURSELESSON, true);
        requestUrl.setParams(new String[] {
                "courseId", mCourseId + "",
                "lessonId", mLessonId + ""
        });

        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                LessonItem lessonItem = mActivity.parseJsonValue(
                        object, new TypeToken<LessonItem<String>>() {
                });
                if (lessonItem == null) {
                    showErrorDialog();
                    return;
                }

                callback.success(lessonItem.mediaUri);
            }
        });
    }

    private void showErrorDialog()
    {
        PopupDialog popupDialog = PopupDialog.createNormal(mActivity, "播放提示", "该视频播放出现了问题！请联系网站管理员!");
        popupDialog.setOkListener(new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                getActivity().finish();
            }
        });
        popupDialog.show();
    }

    private Timer autoHideTimer;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (autoHideTimer != null) {
            autoHideTimer.cancel();
        }
    }

    @Override
    public void error(int what) {
        switch (what){
            case EduVideoViewFragment.ERROR:
                showErrorDialog();
                break;
            case EduVideoViewFragment.RELOAD:
                reloadLessonMediaUrl(new NormalCallback<String>() {
                    @Override
                    public void success(String url) {
                        loadEduVideoViewFragment(url, mMediaController.getLastPos());
                    }
                });
                break;
            case EduVideoViewFragment.CHANGE_PLAYER:
                reloadLessonMediaUrl(new NormalCallback<String>() {
                    @Override
                    public void success(String url) {
                        changeBDPlayFragment(url);
                    }
                });
                break;
        }
    }
}
