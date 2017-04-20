package com.edusoho.kuozhi.v3.ui.fragment.video;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.factory.FactoryManager;
import com.edusoho.kuozhi.v3.factory.provider.AppSettingProvider;
import com.edusoho.kuozhi.v3.model.bal.User;
import com.edusoho.kuozhi.v3.model.sys.School;
import com.edusoho.kuozhi.v3.service.M3U8DownService;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.M3U8Util;
import com.edusoho.kuozhi.v3.util.MediaUtil;
import com.edusoho.kuozhi.v3.util.helper.LocalLessonHelper;
import com.edusoho.kuozhi.v3.util.server.CacheServerFactory;
import com.edusoho.videoplayer.media.ILogoutListener;
import com.edusoho.videoplayer.ui.VideoPlayerFragment;
import com.edusoho.videoplayer.util.VLCOptions;
import com.umeng.analytics.MobclickAgent;
import org.videolan.libvlc.util.AndroidUtil;
import java.io.File;


/**
 * Created by suju on 16/12/16.
 */

public class InnerVideoPlayerFragment extends VideoPlayerFragment {

    private int mLessonId;
    private int mCourseId;
    private String mLessonTitle;
    private long mSaveSeekTime;
    private AlertDialog mErrorDialog;
    private SharedPreferences mSeekPositionSetting;
    private static final String SEEK_POSITION = "seek_position";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hasOptionsMenu();

        mSeekPositionSetting = getContext().getSharedPreferences(SEEK_POSITION, Context.MODE_PRIVATE);
        mSaveSeekTime = mSeekPositionSetting.getLong(String.format("%d-%d", mCourseId, mLessonId), 0);
        setSeekPosition(mSaveSeekTime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        if (mediaCoder == VLCOptions.NONE_RATE && AndroidUtil.isKitKatOrLater()) {
            mediaCoder = VLCOptions.SUPPORT_RATE;
            MediaUtil.saveMediaSupportType(getContext(), mediaCoder);
        }
        mLessonId = getArguments().getInt(Const.LESSON_ID);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
        mLessonTitle = getArguments().getString(Const.LESSON_NAME);
        getArguments().putInt(PLAY_MEDIA_CODER, mediaCoder);

        File localFile = new LocalLessonHelper(getContext(), mLessonId).createLocalPlayListFile();
        if (localFile != null && mediaCoder != VLCOptions.SUPPORT_RATE) {
            MobclickAgent.reportError(getContext(), String.format("localFile:%s", localFile.getAbsolutePath()));
            getArguments().putString(PLAY_URI, "file://" + localFile.getAbsolutePath());
        }
        School school = getAppSettingProvider().getCurrentSchool();
        if (school != null) {
            getArguments().putString(PLAY_DIGEST_KET, school.getDomain());
        }
    }

    private boolean checkCacheServerIsStarted(String host, int userId) {
        return CacheServerFactory.getInstance().cacheServerIsRuning(host, userId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem moreItem = menu.findItem(R.id.menu_more);
        if (moreItem != null) {
            moreItem.setVisible(false);
        }
    }

    private void initFragmentSize(int height) {
        int width = ViewGroup.LayoutParams.MATCH_PARENT;
        setVideoSize(width, height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        School school = getAppSettingProvider().getCurrentSchool();
        int mediaCoder = MediaUtil.getMediaSupportType(getContext());
        User user = getAppSettingProvider().getCurrentUser();
        if (mediaCoder == VLCOptions.SUPPORT_RATE && !checkCacheServerIsStarted(school.host, user.id)) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("播放错误")
                    .setMessage("本地播放服务启动失败,继续将不能播放本地缓存视频,是否重新进入课程?")
                    .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setPositiveButton("取消", null)
                    .create();
            return;
        }
        super.onViewCreated(view, savedInstanceState);
        initFragmentSize(getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height));
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp instanceof FrameLayout.LayoutParams) {
            ((FrameLayout.LayoutParams)lp).gravity = Gravity.CENTER;
            view.setLayoutParams(lp);
        }

        ((View)view.getParent()).setBackgroundColor(Color.BLACK);
        bindLogoutListener();
    }

    protected void bindLogoutListener() {
        addLogoutListener(new ILogoutListener() {
            @Override
            public void onLog(String tag, Object message) {
                if (message instanceof Throwable) {
                    MobclickAgent.reportError(getContext(), (Throwable) message);
                } else {
                    MobclickAgent.reportError(getContext(), message.toString());
                }
            }
        });
    }

    @Override
    protected void changeScreenLayout(int orientation) {
        if (orientation == getResources().getConfiguration().orientation) {
            return;
        }
        int screenOrientation = orientation == Configuration.ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        getActivity().setRequestedOrientation(screenOrientation);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        View playView = getView();
        ViewParent viewParent = playView.getParent();
        if (viewParent == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) viewParent;

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams lp = parent.getLayoutParams();
        int height = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ?
                wm.getDefaultDisplay().getHeight() : getContext().getResources().getDimensionPixelOffset(com.edusoho.videoplayer.R.dimen.video_height);
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;

        initFragmentSize(height);
        parent.setLayoutParams(lp);
    }

    @Override
    protected void savePosition(long seekTime) {
        super.savePosition(seekTime);

        SharedPreferences.Editor editor = mSeekPositionSetting.edit();
        editor.putLong(String.format("%d-%d", mCourseId, mLessonId), seekTime);
        editor.commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mErrorDialog != null) {
            mErrorDialog.dismiss();
            mErrorDialog = null;
        }
    }

    @Override
    public void onReceive(String type, String mesasge) {
        synchronized (this) {
            if (mErrorDialog != null && mErrorDialog.isShowing()) {
                return;
            }
        }

        if ("FileDataSourceException".equals(type)
                || "VideoFileNotFound".equals(type)) {
            pause();
            //delete file
            if ("VideoFileNotFound".equals(type) && !TextUtils.isEmpty(mesasge)) {
                File delFile = new File(mesasge);
                if (delFile.exists()) {
                    delFile.delete();
                }
            }
            mErrorDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("播放错误")
                    .setMessage("视频文件损坏,正在重新下载,请进入我的缓存里查看下载进度")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setPositiveButton("重新下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            updateM3U8ModelUnFinish(mLessonId, mCourseId);
                            getActivity().finish();
                        }
                    })
                    .create();
            mErrorDialog.show();
        }
    }

    private void updateM3U8ModelUnFinish(int lessonId, int courseId) {
        M3U8Util.deleteM3U8Model(getContext(), lessonId);
        School school = getAppSettingProvider().getCurrentSchool();
        User user = getAppSettingProvider().getCurrentUser();
        M3U8Util.saveM3U8Model(getContext(), lessonId, school.getDomain(), user.id);

        M3U8DownService.startDown(getContext(), lessonId, courseId, TextUtils.isEmpty(mLessonTitle) ? "更新视频文件" : mLessonTitle);
    }

    protected AppSettingProvider getAppSettingProvider() {
        return FactoryManager.getInstance().create(AppSettingProvider.class);
    }
}
