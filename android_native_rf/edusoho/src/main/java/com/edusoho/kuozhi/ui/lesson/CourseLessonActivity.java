package com.edusoho.kuozhi.ui.lesson;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.LessonAdapter;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.entity.CourseLessonType;
import com.edusoho.kuozhi.model.LessonInfo;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.dialog.LoadDialog;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.edusoho.plugin.photo.ViewPagerActivity;

import com.edusoho.plugin.video.EduSohoVideoFragment;
import com.edusoho.plugin.video.EdusohoVideoManagerActivity;
import com.edusoho.plugin.video.VideoPlayerCallback;
import com.edusoho.plugin.video.WebVideoActivity;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CourseLessonActivity extends BaseActivity {

    private WebView normal_lesson_content;
    private View audio_layout;
    private ViewGroup video_layout;
    private TextView audio_play_time;
    private TextView audio_total_time;
    private ViewGroup lesson_status_btn;
    private View lesson_status_layout;
    private View lesson_content;
    protected LessonAdapter mLessonAdapter;

    private ViewGroup mLesson_layout;
    private Handler webViewHandler;
    private CourseLessonType mCurrentLessonType;

    private static final int PLAY_VIDEO = 0001;
    private static final int SHOW_IMAGES = 0002;

    private static final int FULLSCREEN = 0011;
    private static final int NORMALSCREEN = 0012;

    private static final int LOAD_LESSON_ITEM = 0100;

    private static final String ANDROID_UA = "Mozilla/5.0 (Linux; Android 4.4.4; Nexus 5 Build/KTU84P) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/33.0.0.0 Mobile Safari/537.36";

    private static final String IOS_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 6_0 like Mac OS X) AppleWebKit" +
            "/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A403 Safari/8536.25";
    private String mDefaultUA = "";
    private boolean isShowVideo;
    private boolean mIsPlayerVideo;

    //时间从1970-0-0 08:00:00开始
    private static final int DEFAULT_TIME = 3600 * 1000 * 16;
    private static SimpleDateFormat dateFromat = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShowVideo = true;
        setContentView(R.layout.course_lesson);
        initView();
    }

    private SlidingMenu menu;
    private int mLessonId;

    private void initView() {
        Intent dataIntent = getIntent();
        if (!dataIntent.hasExtra("courseId")) {
            return;
        }

        mLessonId = dataIntent.getIntExtra("lessonId", 0);
        setBackMode(dataIntent.getStringExtra("lessonTitle"), true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

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

        webViewHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case PLAY_VIDEO:
                        synchronized (mContext) {
                            if (!mIsPlayerVideo) {
                                mIsPlayerVideo = true;
                                releaseWebView();
                                audio_layout.setVisibility(View.GONE);
                                normal_lesson_content.setVisibility(View.GONE);
                                video_layout.setVisibility(View.VISIBLE);
                                playVideo(msg.obj.toString());
                                log("%s", "releaseWebView");
                            }
                        }
                        break;
                    case SHOW_IMAGES:
                        ViewPagerActivity.start(mContext, msg.arg1, (String[]) msg.obj);
                        break;
                    case FULLSCREEN:
                        hideMenuAndTools();
                        break;
                    case NORMALSCREEN:
                        showMenuAndTools();
                        break;
                    case LOAD_LESSON_ITEM:
                        LessonItem lessonItem = (LessonItem) msg.obj;
                        selectLessonItem(lessonItem);
                        break;
                }
            }
        };

        mWebLoadingView = findViewById(R.id.load_layout);
        mLesson_layout = (ViewGroup) findViewById(R.id.lesson_layout);
        lesson_content = findViewById(R.id.lesson_content);
        mActionBar = (ViewGroup) findViewById(R.id.edusoho_actionbar);

        lesson_status_layout = findViewById(R.id.lesson_status_layout);
        lesson_status_btn = (ViewGroup) findViewById(R.id.lesson_status_btn);
        video_layout = (ViewGroup) findViewById(R.id.video_layout);
        audio_layout = findViewById(R.id.audio_layout);

        normal_lesson_content = (WebView) findViewById(R.id.normal_lesson_content);
        setWebView(normal_lesson_content);

        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.RIGHT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

        menu.setBehindWidth(getResources().getDimensionPixelSize(R.dimen.slidingMenuWidth));
        menu.setShadowDrawable(R.drawable.card_bg);

        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW, true);
        menu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {
                video_layout.setLayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
        });
        menu.setMenu(R.layout.lesson_menu);

        mLesson_layout.removeView(lesson_content);
        mLesson_layout.addView(lesson_content, 0);
        loadCourseLesson(dataIntent.getIntExtra("courseId", 0));
    }

    private void setWebView(WebView webView) {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webView.addJavascriptInterface(new JavaScriptObj(), "jsobj");
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //mDefaultUA = webView.getSettings().getUserAgentString();
        mWebChromeClient = new myWebChromeClient();
        mWebViewClient = new myWebViewClient();
        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(mWebViewClient);
    }

    /**
     * js注入对象
     */
    public class JavaScriptObj {
        @JavascriptInterface
        public void showHtml(String src) {
            if (src != null && !"".equals(src)) {
                log("src-->%s", src);
                webViewHandler.obtainMessage(PLAY_VIDEO, src).sendToTarget();
            }
        }

        @JavascriptInterface
        public void showImages(String index, String[] imageArray) {
            Message msg = webViewHandler.obtainMessage(SHOW_IMAGES);
            msg.obj = imageArray;
            msg.arg1 = Integer.parseInt(index);
            msg.sendToTarget();
        }

        @JavascriptInterface
        public void show(String html) {
            log("html-->%s", html);
        }
    }

    private void hideMenuAndTools() {
        isShowVideo = false;
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mActionBar.setVisibility(View.GONE);
        lesson_status_layout.setVisibility(View.GONE);
    }

    private void showMenuAndTools() {
        isShowVideo = true;
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        mActionBar.setVisibility(View.VISIBLE);
        lesson_status_layout.setVisibility(View.VISIBLE);
    }

    private void finishLesson(final int courseId, final int lessonId) {
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
                    autoNextLesson();
                    setLearnBtnClick(courseId, lessonId, true);
                    setLayoutEnable(lesson_status_btn, false);
                } else {
                    setLayoutEnable(lesson_status_btn, true);
                }
            }
        });
    }

    /**
     * @param courseId
     * @param lessonId
     * @param isLearn
     */
    private void setLearnBtnClick(
            final int courseId, final int lessonId, boolean isLearn) {
        if (isLearn) {
            lesson_status_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelLesson(courseId, lessonId);
                }
            });
        } else {
            lesson_status_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishLesson(courseId, lessonId);
                }
            });
        }
    }

    protected void autoNextLesson(){
        final LessonItem lessonItem = mLessonAdapter.getNextLessonItem();
        if (!app.config.isAutoLearn) {
            return;
        }

        if (lessonItem != null) {
            LoadDialog loadDialog = LoadDialog.create(mActivity, new LoadDialog.LoadingCompleCallback() {
                @Override
                public void success() {
                    Message message = webViewHandler.obtainMessage(LOAD_LESSON_ITEM);
                    message.obj = lessonItem;
                    message.sendToTarget();
                }
            });
            loadDialog.setAutoLoadTime(500);
            loadDialog.showAutoHide("正在跳转下一课时...");
        }
    }

    private void cancelLesson(final int courseId, final int lessonId) {
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
                    setLearnBtnClick(courseId, lessonId, false);
                    setLayoutEnable(lesson_status_btn, true);
                } else {
                    setLayoutEnable(lesson_status_btn, false);
                }
            }
        });
    }

    private void setLearnStatus(final int courseId, final int lessonId) {
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
                    setLearnBtnClick(courseId, lessonId, true);
                } else {
                    setLayoutEnable(lesson_status_btn, true);
                    setLearnBtnClick(courseId, lessonId, false);
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

    private int findLessonIndex(
            LinkedHashMap<String, LessonItem> items, int lessonId) {
        int index = 0;
        String value = "lesson-" + lessonId;
        for (String key : items.keySet()) {
            if (value.equals(key)) {
                break;
            }
            index++;
        }
        return index;
    }

    private void loadCourseLesson(int courseId) {
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
                    mLessonAdapter = new LessonAdapter(
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

                    listView.setAdapter(mLessonAdapter);
                    int index = findLessonIndex(items, mLessonId);
                    listView.setSelection(index);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            menu.toggle();
                            final LessonItem lesson = (LessonItem) adapterView.getItemAtPosition(i);
                            selectLessonItem(lesson);
                        }
                    });
                }
            }
        });
    }

    protected void selectLessonItem(final LessonItem lesson)
    {
        if (LessonItem.ItemType.LESSON != LessonItem.ItemType.cover(lesson.itemType)) {
            return;
        }
        if (mAudioPlayer != null && mAudioPlayer.isPlaying()) {
            mAudioPlayer.stop();
        }
        clearVideoPlayer(new NormalCallback() {
            @Override
            public void success(Object obj) {
                loadLessonContent(lesson);
                mLessonAdapter.setmCurrentLessonId(lesson.id);
            }
        });
    }

    private MediaPlayer mAudioPlayer;

    /**
     *
     */
    private void loadLessonContent(LessonItem lesson) {
        setLearnStatus(lesson.courseId, lesson.id);
        String url = app.bindToken2Url(
                "courses/" + lesson.courseId + "/lessons/" + lesson.id + "?", true);

        ajaxNormalGet(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                LessonInfo items = app.gson.fromJson(
                        object, new TypeToken<LessonInfo>() {
                }.getType());
                if (items == null) {
                    longToast("获取数据异常!");
                    return;
                }

                changeTitle(items.title);
                switchLessonToShow(items);
            }
        });
    }

    private void switchLessonToShow(LessonInfo items) {
        String content = "";
        mCurrentLessonType = CourseLessonType.value(items.type);
        switch (mCurrentLessonType) {
            case VIDEO:
                releaseWebView();
                audio_layout.setVisibility(View.GONE);
                switchPlayVideo(items);
                return;
            case TEXT:
                content = items.content;
                content = AppUtil.coverLessonContent(content);
                break;
            case AUDIO:
                playAudio(items);
                return;
            case TESTPAPER:
                content = "暂不支持试卷功能";
                break;
            default:
                content = "暂不支持此功能";
                break;
        }

        if (!Const.PUBLISHED.equals(items.status)) {
            content = "当前课时正在编辑中，暂时无法观看。";
        }

        video_layout.setVisibility(View.GONE);
        audio_layout.setVisibility(View.GONE);
        normal_lesson_content.setVisibility(View.VISIBLE);
        normal_lesson_content.loadDataWithBaseURL(null, wrapWebViewContent(content), "text/html", "UTF-8", null);
    }

    private String wrapWebViewContent(String content) {
        StringBuilder stringBuilder = new StringBuilder("<html><head><meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, target-densitydpi=device-dpi,initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0\">" +
                "<style>body{font-size: 120%}</style></head><body>");
        stringBuilder.append(content);
        stringBuilder.append("<script type='text/javascript'>function getScreenWidth(){var width=window.screen.width;switch(window.orientation){case 0:width=window.screen.width;break;case 90:case-90:width=window.screen.height;break}width=width*0.98;return width}function zoomImage(img,width){var oldH=img.height;var oldW=img.width;img.width=width;img.height=width/oldW*oldH}function adaptationImage(){var width=getScreenWidth();var imgs=document.getElementsByTagName('img');for(var i=0;i<imgs.length;i++){zoomImage(imgs[i],width)}}var imageArray=new Array();var imgs=document.getElementsByTagName('img');for(var i=0;i<imgs.length;i++){var img=imgs[i];img.addEventListener('load',function(){var width=getScreenWidth();zoomImage(this,width)});img.alt=i;imageArray.push(img.src);img.addEventListener('click',function(){window.jsobj.showImages(this.alt,imageArray)})}window.addEventListener('orientationchange',function(){adaptationImage()},false);</script></body></html>");
        return stringBuilder.toString();
    }

    /**
     * @param items
     */
    private void switchPlayVideo(final LessonInfo items) {
        String content = "";
        LessonItem.MediaSourceType mtype = LessonItem.MediaSourceType.cover(items.mediaSource);
        switch (mtype) {
            case YOUKU:
            case TUDOU:
            case QQVIDEO:
                content = items.mediaUri;
                video_layout.setVisibility(View.VISIBLE);
                normal_lesson_content.setVisibility(View.GONE);
                playWebVideo(content, false, mtype);
                return;
            case NETEASEOPENCOURSE:
                content = items.mediaUri;
                video_layout.setVisibility(View.VISIBLE);
                normal_lesson_content.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT < 16) {
                    playWebVideo(items.swfUrl, true, mtype);
                } else {
                    playWebVideo(content, true, mtype);
                }
                break;
            case SELF:
                normal_lesson_content.setVisibility(View.GONE);
                video_layout.setVisibility(View.VISIBLE);
                playVideo(items.mediaUri);
                return;
            case FALLBACK:
            default:
                video_layout.setVisibility(View.GONE);
                normal_lesson_content.setVisibility(View.VISIBLE);
                normal_lesson_content.loadDataWithBaseURL(null, "客户端暂不支持该课程播放浏览功能", "text/html", "UTF-8", null);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            showMenuAndTools();
        } else {
            hideMenuAndTools();
        }
    }

    private void clearVideoPlayer(final NormalCallback clearFinishCallback) {
        VideoPlayerCallback videoplayer = (VideoPlayerCallback) getLocalActivityManager()
                .getActivity("videoplayer");

        mIsPlayerVideo = false;
        if (videoplayer == null) {
            clearFinishCallback.success(null);
            return;
        }
        videoplayer.clear(new NormalCallback() {
            @Override
            public void success(Object obj) {
                getLocalActivityManager().removeAllActivities();
                video_layout.removeAllViews();
                clearFinishCallback.success(null);
            }
        });
    }

    private void playWebVideo(String url, boolean isAutoScreen, LessonItem.MediaSourceType type) {
        Intent intent = new Intent(mContext, WebVideoActivity.class);
        if (isAutoScreen) {
            intent.putExtra(WebVideoActivity.AUTO_SCREEN, isAutoScreen);
        }

        intent.putExtra("url", url);
        intent.putExtra("MediaSourceType", type);
        Window videoWindow = getLocalActivityManager().startActivity(
                "videoplayer", intent);
        View rootView = videoWindow.getDecorView();

        video_layout.addView(rootView);

        app.addMessageListener(WebVideoActivity.MESSAGE_ID, new CoreEngineMsgCallback() {
            @Override
            public void invoke(MessageModel messageModel) {
                switch (messageModel.what) {
                    case WebVideoActivity.MESSAGE_OPEN_FULL:
                        webViewHandler.obtainMessage(FULLSCREEN).sendToTarget();
                        toggleScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case WebVideoActivity.MESSAGE_CLOSE_FULL:
                        webViewHandler.obtainMessage(NORMALSCREEN).sendToTarget();
                        toggleScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
            }
        });
    }

    private void toggleScreenOrientation(int screenOrientation) {
        //水平
        int oldScreenOrientation = mActivity.getRequestedOrientation();
        if (screenOrientation == oldScreenOrientation) {
            return;
        }
        mActivity.setRequestedOrientation(screenOrientation);
    }

    private void playVideo(String mediaUri) {
        Intent intent = new Intent(mContext, EdusohoVideoManagerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("url", mediaUri);
        Window videoWindow = getLocalActivityManager().startActivity(
                "videoplayer", intent);
        View rootView = videoWindow.getDecorView();

        video_layout.addView(rootView);
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

    private void playAudio(LessonInfo lesson) {
        video_layout.setVisibility(View.GONE);
        normal_lesson_content.setVisibility(View.GONE);
        audio_layout.setVisibility(View.VISIBLE);

        audio_total_time = (TextView) findViewById(R.id.audio_total_time);
        audio_play_time = (TextView) findViewById(R.id.audio_play_time);
        audio_progress = (SeekBar) findViewById(R.id.audio_progress);
        final CheckBox audio_play_btn = (CheckBox) findViewById(R.id.audio_play_btn);
        audio_play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAudioPlayer == null) {
                    return;
                }
                if (mAudioPlayer.isPlaying()) {
                    mAudioPlayer.pause();
                    audio_play_btn.setChecked(true);
                } else {
                    mAudioPlayer.start();
                    audio_play_btn.setChecked(false);
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

            mAudioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.seekTo(0);
                    audio_play_btn.setChecked(true);
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
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
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
            goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goBack() {
        releaseWebView();
        setResult(Const.NORMAL_RESULT_REFRESH);
        clearVideoPlayer(new NormalCallback() {
            @Override
            public void success(Object obj) {
                finish();
            }
        });
    }

    private void releaseWebView() {
        normal_lesson_content.stopLoading();
        normal_lesson_content.loadData("<a></a>", "text/html", "utf-8");
    }

    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;
    private View mWebLoadingView;

    public class myWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

            if (mWebLoadingView.getVisibility() == View.GONE) {
                mWebLoadingView.setVisibility(View.VISIBLE);
            }
            if (newProgress == 100) {
                mWebLoadingView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            mCustomView = view;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;
            mCustomView = null;
        }

    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
            /*
            if (mCurrentLessonType == CourseLessonType.VIDEO) {
                view.loadUrl("javascript:" +
                        "var video = document.getElementsByTagName('video')[0];" +
                        "window.jsobj.showHtml(video.src);" +
                        "video.addEventListener('play', function(media){" +
                        "video.pause();" +
                        "window.jsobj.showHtml(video.src);" +
                        "video.src = '';});");
            }
            */
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
