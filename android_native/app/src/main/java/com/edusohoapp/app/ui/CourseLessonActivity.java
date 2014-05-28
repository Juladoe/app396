package com.edusohoapp.app.ui;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.LessonAdapter;
import com.edusohoapp.app.entity.ChaptersResult;
import com.edusohoapp.app.entity.CourseLessonItem;
import com.edusohoapp.app.entity.CourseLessonType;
import com.edusohoapp.app.model.LessonInfo;
import com.edusohoapp.app.model.LessonItem;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.LoadDialog;
import com.edusohoapp.app.view.PopupDialog;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import com.edusohoapp.plugin.video.CustomPlayActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CourseLessonActivity extends BaseActivity{

    private WebView normal_lesson_content;
    private View audio_layout;
    private ViewGroup video_layout;
    private TextView audio_play_time;
    private TextView audio_total_time;
    private ViewGroup lesson_status_btn;

    //时间从1970-0-0 08:00:00开始
    private static final int DEFAULT_TIME = 3600 * 1000 * 16;
    private static SimpleDateFormat dateFromat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_lesson);
        initView();
    }

    private SlidingMenu menu;
    private int mLessonId;

    private void initView()
    {
        Intent dataIntent = getIntent();
        if (!dataIntent.hasExtra("courseId")) {
            return;
        }

        mLessonId = dataIntent.getIntExtra("lessonId", 0);
        setBackMode(dataIntent.getStringExtra("lessonTitle"), true, null);
        setMenu(R.layout.course_lesson_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                View view = menuView.findViewById(R.id.bar_menu_btn);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        menu.toggle();
                    }
                });
            }
        });

        lesson_status_btn = (ViewGroup) findViewById(R.id.lesson_status_btn);
        video_layout = (ViewGroup) findViewById(R.id.video_layout);
        audio_layout = findViewById(R.id.audio_layout);
        customViewContainer = (FrameLayout) findViewById(R.id.customViewContainer);
        normal_lesson_content =(WebView) findViewById(R.id.normal_lesson_content);
        normal_lesson_content.getSettings().setJavaScriptEnabled(true);
        normal_lesson_content.getSettings().setAllowFileAccess(true);
        normal_lesson_content.getSettings().setPluginState(WebSettings.PluginState.ON);
        normal_lesson_content.getSettings().setDefaultTextEncodingName("UTF-8");

        normal_lesson_content.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

        mWebViewClient = new myWebViewClient();
        normal_lesson_content.setWebViewClient(new myWebViewClient());
        mWebChromeClient = new myWebChromeClient();
        normal_lesson_content.setWebChromeClient(new myWebChromeClient());

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.SLIDING_CONTENT);

        menu.setBehindWidth(getResources().getDimensionPixelSize(R.dimen.slidingMenuWidth));
        menu.setShadowDrawable(R.drawable.card_bg);

        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.lesson_menu);

        loadCourseLesson(dataIntent.getIntExtra("courseId", 0));
    }

    private void finishLesson(int courseId, int lessonId)
    {
        StringBuffer param = new StringBuffer();
        param.append("courses/").append(courseId);
        param.append("/lessons/").append(lessonId);
        param.append("/learn?");

        String url = app.bindToken2Url(param.toString(), true);
        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                if ("true".equals(object)) {
                    setLayoutEnable(lesson_status_btn, false);
                } else {
                    setLayoutEnable(lesson_status_btn, true);
                }
            }
        });
    }

    private void cancelLesson(int courseId, int lessonId)
    {
        StringBuffer param = new StringBuffer();
        param.append("courses/").append(courseId);
        param.append("/lessons/").append(lessonId);
        param.append("/unlearn?");

        String url = app.bindToken2Url(param.toString(), true);
        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                if ("true".equals(object)) {
                    setLayoutEnable(lesson_status_btn, true);
                } else {
                    setLayoutEnable(lesson_status_btn, false);
                }
            }
        });
    }

    private void setLearnStatus(final int courseId, final int lessonId)
    {
        StringBuffer param = new StringBuffer();
        param.append("courses/").append(courseId);
        param.append("/lessons/").append(lessonId);
        param.append("/learn_status?");

        String url = app.bindToken2Url(param.toString(), true);
        ajaxNormalGet(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                if ("\"finished\"".equals(object)) {
                    setLayoutEnable(lesson_status_btn, false);
                    lesson_status_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            cancelLesson(courseId, lessonId);
                        }
                    });
                } else {
                    setLayoutEnable(lesson_status_btn, true);
                    lesson_status_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finishLesson(courseId, lessonId);
                        }
                    });
                }
            }
        });
    }

    private void setLayoutEnable(ViewGroup vg, boolean isEnable) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            vg.getChildAt(i).setEnabled(isEnable);
        }
    }

    private void loadCourseLesson(int courseId)
    {
        String url = app.bindToken2Url(wrapUrl(Const.COURSELESSON, courseId + ""), true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                LinkedHashMap<String, LessonItem> items = app.gson.fromJson(
                        object, new TypeToken<LinkedHashMap<String, LessonItem>>() {
                }.getType());

                if (items != null) {
                    ListView listView = (ListView) menu.findViewById(R.id.menu_listview);
                    final LessonAdapter adapter = new LessonAdapter(
                            mContext,
                            items,
                            R.layout.menu_list_item,
                            mLessonId,
                            new LessonAdapter.SelectLessonCallback() {
                                @Override
                                public void select(LessonItem lesson) {
                                    loadLessonContent(lesson);
                                }
                    });

                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            menu.toggle();
                            Object item = adapterView.getItemAtPosition(i);
                            if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
                                mAudioPlayer.stop();
                            }
                            if (mVideoView != null) {
                                mVideoView.resume();
                            }
                            LessonItem lesson = (LessonItem) item;
                            loadLessonContent(lesson);
                            adapter.setmCurrentLessonId(lesson.id);
                        }
                    });
                }
            }
        });

    }

    private MediaPlayer mAudioPlayer;

    /**
     *
     */
    private void loadLessonContent(LessonItem lesson)
    {
        setLearnStatus(lesson.courseId, lesson.id);

        String url = app.bindToken2Url(
                "courses/" + lesson.courseId + "/lessons/" + lesson.id + "?", true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                LessonInfo items = app.gson.fromJson(
                        object, new TypeToken<LessonInfo>() {
                }.getType());
                if (items == null) {
                    longToast("数据加载异常!");
                    return;
                }

                changeTitle(items.title);
                String content = "";
                switch (CourseLessonType.value(items.type)) {
                    case VIDEO:
                        LessonItem.MediaSourceType mtype = LessonItem.MediaSourceType.cover(items.mediaSource);
                        switch (mtype) {
                            case YOUKU:
                            case TUDOU:
                                content = items.mediaUri;
                                normal_lesson_content.loadUrl(content);
                                return;
                            case SELF:
                                normal_lesson_content.setVisibility(View.GONE);
                                audio_layout.setVisibility(View.GONE);
                                video_layout.setVisibility(View.VISIBLE);
                                normal_lesson_content.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
                                playVideo(items);
                                return;
                        }
                        break;
                    case TEXT:
                        content = items.content;
                        break;
                    case TESTPAPER:
                        content = "暂不支持试卷课程";
                        break;
                    case AUDIO:
                        normal_lesson_content.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.VISIBLE);
                        normal_lesson_content.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
                        playAudio(items);
                        return;
                }
                video_layout.setVisibility(View.GONE);
                audio_layout.setVisibility(View.GONE);
                normal_lesson_content.setVisibility(View.VISIBLE);
                normal_lesson_content.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
            }
        });
    }

    private VideoView mVideoView;

    private void playVideo(LessonInfo lesson) {
        final LoadDialog dlg = LoadDialog.create(mContext);
        if (mVideoView == null) {
            View iframeVideoView = getLayoutInflater().inflate(R.layout.iframe_video, null);
            video_layout.addView(iframeVideoView);
            mVideoView = (VideoView) iframeVideoView.findViewById(R.id.playVideoView);
            MediaController mc = new MediaController(this);
            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    if (dlg != null) {
                        dlg.dismiss();
                    }
                    PopupDialog.createNormal(mContext, "视频错误", "设备不能播放该视频！").show();
                    return true;
                }
            });
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    dlg.dismiss();
                }
            });
            mVideoView.setMediaController(mc);
        }
        mVideoView.setVideoURI(Uri.parse(lesson.mediaUri));
        mVideoView.start();
        dlg.show();
    }


    private Timer audioUpdateTimer = new Timer();

    public static final int UPDATE_PLAY_TIME = 0001;

    private Handler audioPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PLAY_TIME:
                    String currentTime = dateFromat.format(new Date(msg.arg1 + DEFAULT_TIME));
                    audio_play_time.setText(currentTime);
                    audio_progress.setProgress(msg.arg1);
                    break;
            }
        }
    };

    private SeekBar audio_progress;

    private void playAudio(LessonInfo lesson)
    {
        audio_total_time = (TextView) findViewById(R.id.audio_total_time);
        audio_play_time = (TextView) findViewById(R.id.audio_play_time);
        audio_progress = (SeekBar) findViewById(R.id.audio_progress);
        final TextView audio_play_btn = (TextView) findViewById(R.id.audio_play_btn);
        audio_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAudioPlayer == null) {
                    return;
                }
                if (mAudioPlayer.isPlaying()) {
                    mAudioPlayer.pause();
                    audio_play_btn.setText(R.string.font_play_btn);
                } else {
                    mAudioPlayer.start();
                    audio_play_btn.setText(R.string.font_pause_btn);
                }
            }
        });

        if (mAudioPlayer == null) {
            mAudioPlayer = new MediaPlayer();
        }
        mAudioPlayer.reset();
        try {
            String mediaUri = TextUtils.isEmpty(lesson.mediaUri)
                    ? ""
                    : lesson.mediaUri;
            mAudioPlayer.setDataSource(mediaUri);
            mAudioPlayer.prepareAsync();
            mAudioPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    int duaration = mAudioPlayer.getDuration();
                    audio_play_btn.setText(R.string.font_pause_btn);
                    mAudioPlayer.start();
                    audio_total_time.setText(
                            "/" + dateFromat.format(new Date(duaration + DEFAULT_TIME)));
                    audio_progress.setMax(duaration);

                    audioUpdateTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (mAudioPlayer != null) {
                                Message msg = audioPlayHandler.obtainMessage(UPDATE_PLAY_TIME);
                                msg.arg2 = mAudioPlayer.getDuration();
                                msg.arg1 = mAudioPlayer.getCurrentPosition();
                                msg.sendToTarget();
                            }
                        }
                    }, 0, 1000);
                }
            });

            audio_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int current, boolean fromUser) {
                    if (fromUser) {
                        mAudioPlayer.seekTo(current);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        } catch (Exception e) {
            longToast("音频不能播放!");
        }
    }

    @Override
    protected void onDestroy() {
        System.out.println("lesson activity destory");
        super.onDestroy();
        audioUpdateTimer.cancel();
        if (mAudioPlayer != null) {
            if (mAudioPlayer.isPlaying()) {
                mAudioPlayer.stop();
            }
            mAudioPlayer.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            menu.toggle();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;

    public class myWebChromeClient extends WebChromeClient {
        private Bitmap mDefaultVideoPoster;
        private View mVideoProgressView;

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onShowCustomView(View view,CustomViewCallback callback) {

            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            normal_lesson_content.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {

            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(CourseLessonActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();    //To change body of overridden methods use File | Settings | File Templates.
            if (mCustomView == null)
                return;

            normal_lesson_content.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }


    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        normal_lesson_content.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        normal_lesson_content.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (inCustomView()) {
            hideCustomView();
        }
    }
}
