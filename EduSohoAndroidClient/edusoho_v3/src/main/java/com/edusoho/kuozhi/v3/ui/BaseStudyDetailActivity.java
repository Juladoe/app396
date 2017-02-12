package com.edusoho.kuozhi.v3.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.SectionsPagerAdapter;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SystemBarTintManager;
import com.edusoho.kuozhi.v3.util.WeakReferenceHandler;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayDeque;
import java.util.Queue;

import extensions.PagerSlidingTabStrip;

/**
 * Created by DF on 2017/2/8.
 */

public abstract class BaseStudyDetailActivity extends AppCompatActivity
        implements View.OnClickListener, Handler.Callback, MessageEngine.MessageCallback {

    protected MenuPop mMenuPop;
    protected int mRunStatus;
    protected ViewPager mViewPager;
    protected Toolbar mToolbar;
    protected TextView mShareView;
    public AppBarLayout mAppBarLayout;
    protected ViewGroup mParentLayout;
    protected RelativeLayout mMediaLayout;
    protected ImageView mIvBackGraound;
    protected RelativeLayout mPlayButtonLayout;
    protected TextView mTvLast;
    protected ViewGroup mPlayLayout;
    protected TextView mTvPlay;
    protected ViewGroup mBottomLayout;
    protected ViewGroup mConsult;
    protected ViewGroup mCollect;
    protected TextView mTvCollectTxt;
    protected TextView mTvAdd;
    protected TextView mTvInclass;
    protected PagerSlidingTabStrip mTabLayout;
    protected SystemBarTintManager tintManager;
    protected Queue<WidgetMessage> mUIMessageQueue;
    protected View mMenu;
    protected TextView mIvGrade;
    protected View mLoadingView;
    protected TextView mTvEditTopic;
    protected LoadDialog mProcessDialog;
    protected PopupWindow mPopupWindow;
    protected TextView tvTopic;
    protected TextView tvQuestion;
    protected TextView mTvCollect;
    protected ViewGroup mAddLayout;
    protected ViewGroup mPlayLastLayout;
    protected SectionsPagerAdapter mSectionsPagerAdapter;
    public static final int RESULT_REFRESH = 0x111;
    public static final int RESULT_LOGIN = 0x222;
    protected WeakReferenceHandler mHandler = new WeakReferenceHandler(this);
    protected int mCheckNum = 0;
    protected boolean mIsPlay = false;
    protected boolean mIsMemder = false;
    protected String mTitle;
    private int mTitleBarHeight;
    public int mMediaViewHeight = 210;
    protected static final int TAB_PAGE = 0;
    protected static final int LOADING_END = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_study_layout);
        mUIMessageQueue = new ArrayDeque<>();
        ((EdusohoApp) getApplication()).registMsgSource(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.transparent));
        }
    }

    public MenuPop getMenu() {
        if (isFinishing()) {
            return null;
        } else {
            return mMenuPop;
        }
    }

    protected void initView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarHeight = 25;
        }
        mTvEditTopic = (TextView) findViewById(R.id.tv_edit_topic);
        mParentLayout = (ViewGroup) findViewById(R.id.parent_rlayout);
        mMediaLayout = (RelativeLayout) findViewById(R.id.media_rlayout);
        mIvBackGraound = (ImageView) findViewById(R.id.iv_media_background);
        mPlayButtonLayout = (RelativeLayout) findViewById(R.id.layout_play_button);
        mPlayLastLayout = (ViewGroup) findViewById(R.id.layout_play_last);
        mTvLast = (TextView) findViewById(R.id.tv_last_title);
        mPlayLayout = (ViewGroup) findViewById(R.id.play_layout);
        mTvPlay = (TextView) findViewById(R.id.tv_play);
        mBottomLayout = (ViewGroup) findViewById(R.id.bottom_layout);
        mAddLayout = (ViewGroup) findViewById(R.id.bottom_add_layout);
        mConsult = (ViewGroup) findViewById(R.id.consult_layout);
        mCollect = (ViewGroup) findViewById(R.id.collect_layout);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mTvCollectTxt = (TextView) findViewById(R.id.tv_collect_txt);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        mTvInclass = (TextView) findViewById(R.id.tv_inclass);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        mIvGrade = (TextView) findViewById(R.id.iv_grade);
        mShareView = (TextView) findViewById(R.id.iv_share);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mMenu = findViewById(R.id.layout_menu);
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mProcessDialog = new LoadDialog(this);
        mLoadingView = findViewById(R.id.ll_frame_load);
        setSupportActionBar(mToolbar);
        mTabLayout.setIndicatorColor(R.color.primary_color);
        mMenuPop = new MenuPop(this, mMenu);
        mMenuPop.setOnBindViewVisibleChangeListener(
                new MenuPop.OnBindViewVisibleChangeListener() {
                    @Override
                    public void onVisibleChange(boolean show) {
                        if (show) {
                            mIvGrade.setVisibility(View.GONE);
                        } else {
                            mIvGrade.setVisibility(View.VISIBLE);
                        }
                    }
                });

        setLoadStatus(View.VISIBLE);
        initEvent();
        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                getBaseContext(),
                getTitleArray(),
                getFragmentArray(),
                getIntent().getExtras()
        );
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setViewPager(mViewPager);
    }


    protected String[] getTitleArray() {
        return new String [] {
                "课程", "目录", "问答"
        };
    }

    protected abstract String[] getFragmentArray();

    private void initEvent() {
        mShareView.setOnClickListener(this);
        mIvGrade.setOnClickListener(this);
        mPlayLayout.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mTvInclass.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mTvEditTopic.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                checkTab(position);
//                setBottomLayoutVisible(position, mIsMemder);
//                showEditTopic(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_share) {
            share();
        } else if (v.getId() == R.id.iv_grade) {
            grade();
        } else if (v.getId() == R.id.layout_menu) {
            mMenuPop.showAsDropDown(mMenu, -AppUtil.dp2px(this, 6), AppUtil.dp2px(this, 10));
        } else if (v.getId() == R.id.play_layout) {
            courseStart();
        } else if (v.getId() == R.id.collect_layout) {
            collect();
        } else if (v.getId() == R.id.tv_add) {
            add();
        } else if (v.getId() == R.id.consult_layout) {
            consult();
        } else if (v.getId() == R.id.tv_inclass) {
            goClass();
        } else if (v.getId() == R.id.tv_edit_topic) {
            showEditPop();
        }
    }

    protected void grade() {}

    protected abstract void goClass();

    protected abstract void consult();

    protected abstract void add();

    protected void collect() {
    }

    protected abstract void share();

    protected abstract void refreshView();

    protected abstract void initData();

    protected void setLoadStatus(int visibility) {
        mLoadingView.setVisibility(visibility);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunStatus = MSG_RESUME;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRunStatus = MSG_PAUSE;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((EdusohoApp) getApplication()).unRegistMsgSource(this);
    }

    @Override
    public void invoke(WidgetMessage message) {
        Bundle bundle = message.data;
        switch (message.type.type) {
            case Const.FULL_SCREEN:
                fullScreen();
                break;
            case Const.COURSE_START:
                courseStart();
                break;
            case Const.COURSE_PAUSE:
                coursePause();
                break;
            case Const.COURSE_REFRESH:
                initData();
                break;
            case Const.COURSE_SHOW_BAR:
//                changeBar(true);
                break;
            case Const.COURSE_HIDE_BAR:
//                changeBar(false);
                break;
            case Const.SCREEN_LOCK:
                screenLock();
                break;
            case Const.COURSE_CHANGE:
                courseChange((LessonItem) bundle.getSerializable(Const.COURSE_CHANGE_OBJECT));
                break;
            case Const.COURSE_HASTRIAL:
                courseHastrial(
                        bundle.getString(Const.COURSE_CHANGE_STATE),
                        (LessonItem) bundle.getSerializable(Const.COURSE_CHANGE_OBJECT)
                );
                break;
            case Const.PAY_SUCCESS:
                if (mRunStatus == MSG_RESUME) {
                    saveMessage(message);
                    return;
                }
                initData();
        }
    }

    protected void courseHastrial(String state, LessonItem lessonItem) {
    }

    protected void courseChange(LessonItem lessonItem) {
    }

    private boolean isScreenLock = false;

    private void screenLock() {
        if (isScreenLock) {
            isScreenLock = false;
        } else {
            isScreenLock = true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (isScreenLock) {
                return true;
            }
            if (mIsFullScreen) {
                fullScreen();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void courseStart() {
        if (!mIsFullScreen) {
//            mParent.smoothScrollTo(0, 0);
//            mParent.setCanScroll(false);
            ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
            if (params != null) {
                int bottom = AppUtil.dp2px(this, 50 + mMediaViewHeight);
                if (mBottomLayout.getVisibility() != View.GONE) {
                    bottom += AppUtil.dp2px(this, 50);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    bottom += AppUtil.dp2px(this, 25);
                }
                params.height = AppUtil.getHeightPx(this) - bottom;
                mViewPager.setLayoutParams(params);
            }
        }
        mPlayButtonLayout.setVisibility(View.GONE);
        mIsPlay = true;
//        mParent.setStay(true);
    }

    protected void initViewPager() {
        ViewGroup.LayoutParams params = mViewPager.getLayoutParams();
        if (params != null) {
            int bottom = AppUtil.dp2px(this, 50 + 43 + mTitleBarHeight);
            if (mBottomLayout.getVisibility() != View.GONE) {
                bottom += AppUtil.dp2px(this, 50);
            }
            params.height = AppUtil.getHeightPx(this) - bottom;
            mViewPager.setLayoutParams(params);
        }
    }

    protected void coursePause() {
        if (!mIsFullScreen) {
//            mParent.setCanScroll(true);
            initViewPager();
        }
        mIsPlay = false;
//        mParent.setStay(false);
        mPlayButtonLayout.setVisibility(View.VISIBLE);
//        changeBar(true);
    }

    protected boolean mIsFullScreen = false;

    private void fullScreen() {
        ViewGroup.LayoutParams params = mMediaLayout.getLayoutParams();
        if (!mIsFullScreen) {
//            mParent.scrollTo(0, 0);
            getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
            mIsFullScreen = true;
            params.height = AppUtil.getWidthPx(this);
            params.width = -1;
            mMediaLayout.setLayoutParams(params);
//            mParent.setScrollStay(true);
            mBottomLayout.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mIsFullScreen = false;
            params.width = -1;
            params.height = AppUtil.dp2px(this, mMediaViewHeight);
            mMediaLayout.setLayoutParams(params);
//            mParent.setScrollStay(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (!mIsMemder) {
                mBottomLayout.setVisibility(View.GONE);
            } else {
                mBottomLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{
                new MessageType(Const.SCROLL_STATE_SAVE),
                new MessageType(Const.COURSE_HASTRIAL),
                new MessageType(Const.FULL_SCREEN),
                new MessageType(Const.COURSE_START),
                new MessageType(Const.COURSE_CHANGE),
                new MessageType(Const.COURSE_REFRESH),
                new MessageType(Const.COURSE_SHOW_BAR),
                new MessageType(Const.COURSE_PAUSE),
                new MessageType(Const.SCREEN_LOCK),
                new MessageType(Const.COURSE_HIDE_BAR),
                new MessageType(Const.PAY_SUCCESS, MessageType.UI_THREAD)
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REFRESH) {
            setLoadStatus(View.GONE);
            hideProcesDialog();
            initData();
        }
        if (requestCode == RESULT_LOGIN) {
            setLoadStatus(View.GONE);
            hideProcesDialog();
            initData();
        }
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    protected void tabPage(final int sleepTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(sleepTime);
                    mHandler.sendEmptyMessage(TAB_PAGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void tabLoadingGone() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                    mHandler.sendEmptyMessage(LOADING_END);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case TAB_PAGE:
                if (mViewPager != null) {
                    mViewPager.setCurrentItem(1);
                    setBottomLayoutVisible(1, mIsMemder);
                    tabLoadingGone();
                }
                break;
            case LOADING_END:
                setLoadStatus(View.GONE);
                break;
        }
        return false;
    }

    protected void showEditTopic(int position) {
        if (position == 2 && mIsMemder) {
            mTvEditTopic.setVisibility(View.VISIBLE);
        } else {
            mTvEditTopic.setVisibility(View.GONE);
        }
    }

    protected abstract void showThreadCreateView(String type);

    private boolean isAdd;

    private void showEditPop() {
        if (!isAdd) {
            isAdd = true;
            View popupView = getLayoutInflater().inflate(R.layout.dialog_discuss_publish, null);
            mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            tvTopic = (EduSohoNewIconView) popupView.findViewById(R.id.tv_topic);
            tvTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showThreadCreateView("discussion");
                    mPopupWindow.dismiss();
                }
            });
            tvQuestion = (EduSohoNewIconView) popupView.findViewById(R.id.tv_question);
            tvQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showThreadCreateView("question");
                    mPopupWindow.dismiss();
                }
            });
            popupView.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
        }
        mPopupWindow.showAsDropDown(mTvEditTopic, 0, -AppUtil.dp2px(this, 204));
        startAnimation();
    }

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(tvQuestion, "translationY", 0, -AppUtil.dp2px(BaseStudyDetailActivity.this, 73));
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(tvTopic, "translationY", 0, -AppUtil.dp2px(BaseStudyDetailActivity.this, 146));
        animator.setInterpolator(new LinearInterpolator());
        animator1.setInterpolator(new LinearInterpolator());
        animator.setDuration(150);
        animator1.setDuration(300);
        animator.start();
        animator1.start();
    }

    public void setBottomLayoutVisible(int curFragment, boolean isMember) {
        if (getIntent().getBooleanExtra(Const.IS_CHILD_COURSE, false)) {
            mBottomLayout.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.GONE);
        } else {
            if (curFragment == 0) {
                if (isMember) {
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mTvInclass.setVisibility(View.VISIBLE);
                } else {
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mTvInclass.setVisibility(View.GONE);
                }
            } else {
                if (!isMember) {
                    mBottomLayout.setVisibility(View.VISIBLE);
                    mTvInclass.setVisibility(View.GONE);
                } else {
                    mBottomLayout.setVisibility(View.GONE);
                    mTvInclass.setVisibility(View.GONE);
                }
            }
        }

    }

    @Override
    public int getMode() {
        return 0;
    }

    protected void saveMessage(WidgetMessage message) {
        mUIMessageQueue.add(message);
    }

    protected void invokeUIMessage() {
        WidgetMessage message;
        while ((message = mUIMessageQueue.poll()) != null) {
            invoke(message);
        }
    }
}
