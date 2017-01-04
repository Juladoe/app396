package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.test.FragmentViewPagerAdapter;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SystemBarTintManager;
import com.edusoho.kuozhi.v3.util.WeakReferenceHandler;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;
import com.edusoho.kuozhi.v3.view.dialog.LoadDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */
public abstract class DetailActivity extends BaseNoTitleActivity
        implements View.OnClickListener, Handler.Callback {

    public static final int RESULT_REFRESH = 0x111;
    public static final int RESULT_LOGIN = 0x222;
    protected HeadStopScrollView mParent;
    protected RelativeLayout mHeadRlayout;
    protected RelativeLayout mHeadRlayout2;
    protected View mIvShare;
    protected View mIvShare2;
    protected View mIvGrade;
    protected View mIvGrade2;
    protected View mPlayLayout;
    protected View mPlayLayout2;
    protected View mBottomLayout;
    protected View mConsult;
    protected View mCollect;
    protected View mBack2;
    protected View mTvInclass;
    protected View mPlayLastLayout;
    protected TextView mTvLastTitle;
    protected TextView mTvCollect;
    protected TextView mTvCollectTxt;
    protected TextView mTvPlay;
    protected TextView mTvPlay2;
    protected TextView mTvAdd;
    protected View mPlayButtonLayout;
    protected RelativeLayout mMediaRlayout;
    protected ImageView mIvMediaBackground;
    protected ViewPager mContentVp;
    protected RelativeLayout mIntroLayout;
    protected View mIntro;
    protected RelativeLayout mHourLayout;
    protected View mHour;
    protected RelativeLayout mReviewLayout;
    protected View mReview;
    protected View mMenu;
    protected List<Fragment> mFragments = new ArrayList<>();
    protected FragmentViewPagerAdapter mAdapter;
    protected int mCheckNum = 0;
    protected boolean mIsPlay = false;
    protected boolean mIsMemder = false;
    private int mTitleBarHeight;
    public int mMediaViewHeight = 210;
    private SystemBarTintManager tintManager;
    protected View mLoadingView;
    protected LoadDialog mProcessDialog;
    protected MenuPop mMenuPop;
    protected View mTabLayout;
    protected TextView mTvCatalog;
    protected static final int TAB_PAGE = 0;
    protected static final int LOADING_END = 1;
    protected WeakReferenceHandler mHandler = new WeakReferenceHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(Color.parseColor("#00000000"));
        }
    }

    public MenuPop getMenu() {
        if (isFinishing()) {
            return null;
        } else {
            return mMenuPop;
        }
    }

    @Override
    protected void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarHeight = 25;
        }
        mParent = (HeadStopScrollView) findViewById(R.id.scroll_parent);
        mHeadRlayout = (RelativeLayout) findViewById(R.id.head_rlayout);
        mHeadRlayout2 = (RelativeLayout) findViewById(R.id.head_rlayout2);
        mMediaRlayout = (RelativeLayout) findViewById(R.id.media_rlayout);
        mIvGrade = findViewById(R.id.iv_grade);
        mIvGrade2 = findViewById(R.id.iv_grade2);
        mIvShare = findViewById(R.id.iv_share);
        mIvShare2 = findViewById(R.id.iv_share2);
        mPlayLayout2 = findViewById(R.id.play_layout2);
        mPlayLayout = findViewById(R.id.play_layout);
        mPlayButtonLayout = findViewById(R.id.layout_play_button);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mIntroLayout = (RelativeLayout) findViewById(R.id.intro_rlayout);
        mHourLayout = (RelativeLayout) findViewById(R.id.hour_rlayout);
        mReviewLayout = (RelativeLayout) findViewById(R.id.review_rlayout);
        mIntro = findViewById(R.id.intro);
        mHour = findViewById(R.id.hour);
        mReview = findViewById(R.id.review);
        mBack2 = findViewById(R.id.back2);
        mMenu = findViewById(R.id.layout_menu);
        mTvPlay = (TextView) findViewById(R.id.tv_play);
        mTvPlay2 = (TextView) findViewById(R.id.tv_play2);
        mTvInclass = findViewById(R.id.tv_inclass);
        mLoadingView = findViewById(R.id.ll_frame_load);
        mTvCatalog = (TextView) findViewById(R.id.textView);
        mPlayLastLayout = findViewById(R.id.layout_play_last);
        mTvLastTitle = (TextView) findViewById(R.id.tv_last_title);
        mIvMediaBackground = (ImageView) findViewById(R.id.iv_media_background);
        mTabLayout = findViewById(R.id.tab_rlayout);

        initFragment(mFragments);
        mAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mContentVp.setAdapter(mAdapter);
        ViewGroup.LayoutParams params = mMediaRlayout.getLayoutParams();
        if (params != null) {
            params.height = AppUtil.dp2px(this, mMediaViewHeight);
            mMediaRlayout.setLayoutParams(params);
        }
        mParent.setFirstViewHeight(AppUtil.dp2px(this,
                mMediaViewHeight - 43 - mTitleBarHeight));
        mBottomLayout = findViewById(R.id.bottom_layout);
        mCollect = findViewById(R.id.collect_layout);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mTvCollectTxt = (TextView) findViewById(R.id.tv_collect_txt);
        mConsult = findViewById(R.id.consult_layout);
        mTvAdd = (TextView) findViewById(R.id.tv_add);
        initViewPager();
        ViewGroup.LayoutParams headParams =
                mHeadRlayout2.getLayoutParams();
        headParams.height = AppUtil.dp2px(this, 44 + mTitleBarHeight);
        mHeadRlayout2.setLayoutParams(headParams);
        mHeadRlayout2.setPadding(0, AppUtil.dp2px(this, mTitleBarHeight), 0, 0);
        mMenuPop = new MenuPop(this, mMenu);
        setLoadStatus(View.VISIBLE);
    }

    protected abstract void initFragment(List<Fragment> fragments);

    protected void initEvent() {
        mIntroLayout.setOnClickListener(this);
        mHourLayout.setOnClickListener(this);
        mReviewLayout.setOnClickListener(this);
        mIvShare.setOnClickListener(this);
        mIvShare2.setOnClickListener(this);
        mIvGrade.setOnClickListener(this);
        mIvGrade2.setOnClickListener(this);
        mPlayLayout2.setOnClickListener(this);
        mPlayLayout.setOnClickListener(this);
        mCollect.setOnClickListener(this);
        mTvAdd.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mBack2.setOnClickListener(this);
        mTvInclass.setOnClickListener(this);
        mMenu.setOnClickListener(this);
        mContentVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                checkTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mParent.setOnScrollChangeListener(new HeadStopScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (!mParent.isCanScroll() && t != 0) {
                    mHeadRlayout.setVisibility(View.GONE);
                    mHeadRlayout2.setVisibility(View.VISIBLE);
//                    mParent.scrollTo(0, AppUtil.dp2px(DetailActivity.this,
//                            mMediaViewHeight - 43 - mTitleBarHeight));
                } else if (mParent.getScrollY() < mParent.getFirstViewHeight() - 2) {
                    mHeadRlayout.setVisibility(View.VISIBLE);
                    mHeadRlayout2.setVisibility(View.GONE);
                }
            }
        });
    }

    protected void showProcessDialog() {
        if (mProcessDialog == null) {
            mProcessDialog = LoadDialog.create(this);
        }
        mProcessDialog.show();
    }

    protected void hideProcesDialog() {
        if (mProcessDialog == null) {
            return;
        }
        if (mProcessDialog.isShowing()) {
            mProcessDialog.dismiss();
        }
    }

    protected void setLoadStatus(int visibility) {
        mLoadingView.setVisibility(visibility);
    }

    protected abstract void initData();

    protected abstract void refreshView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProcessDialog != null) {
            if (mProcessDialog.isShowing()) {
                mProcessDialog.dismiss();
            }
            mProcessDialog = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_rlayout) {
            mContentVp.setCurrentItem(0);
        } else if (v.getId() == R.id.hour_rlayout) {
            mContentVp.setCurrentItem(1);
        } else if (v.getId() == R.id.review_rlayout) {
            mContentVp.setCurrentItem(2);
        } else if (v.getId() == R.id.iv_grade ||
                v.getId() == R.id.iv_grade2) {
        } else if (v.getId() == R.id.iv_share ||
                v.getId() == R.id.iv_share2) {
            share();
        } else if (v.getId() == R.id.collect_layout) {
            collect();
        } else if (v.getId() == R.id.tv_add) {
            add();
        } else if (v.getId() == R.id.play_layout2) {
            courseStart();
        } else if (v.getId() == R.id.play_layout) {
            courseStart();
        } else if (v.getId() == R.id.consult_layout) {
            consult();
        } else if (v.getId() == R.id.back2) {
            finish();
        } else if (v.getId() == R.id.layout_menu) {
            mMenuPop.showAsDropDown(mMenu, -AppUtil.dp2px(this, 6), AppUtil.dp2px(this, 10));
        } else if (v.getId() == R.id.tv_inclass) {
            goClass();
        }
    }

    protected abstract void goClass();

    protected abstract void consult();

    protected abstract void add();

    protected void collect() {
    }

    protected abstract void share();

    private void checkTab(int num) {
        mCheckNum = num;
        mIntro.setVisibility(View.GONE);
        mHour.setVisibility(View.GONE);
        mReview.setVisibility(View.GONE);
//        mParent.setCheckNum(num);
        switch (num) {
            case 0:
                mIntro.setVisibility(View.VISIBLE);
                break;
            case 1:
                mHour.setVisibility(View.VISIBLE);
                break;
            case 2:
                mReview.setVisibility(View.VISIBLE);
                break;
        }
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
                changeBar(true);
                break;
            case Const.COURSE_HIDE_BAR:
                changeBar(false);
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
        }
    }

    protected void courseHastrial(String state, LessonItem lessonItem) {
    }

    /**
     * todo 获得课程相关信息
     */
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

    private void changeBar(boolean show) {
        if (show) {
            mHeadRlayout.setVisibility(View.VISIBLE);
        } else {
            mHeadRlayout.setVisibility(View.GONE);
        }
    }

    protected void courseStart() {
        if (!mIsFullScreen) {
            mParent.smoothScrollTo(0, 0);
            mParent.setCanScroll(false);
            ViewGroup.LayoutParams params = mContentVp.getLayoutParams();
            if (params != null) {
                int bottom = AppUtil.dp2px(this, 50 + mMediaViewHeight);
                if (mBottomLayout.getVisibility() != View.GONE) {
                    bottom += AppUtil.dp2px(this, 50);
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    bottom += AppUtil.dp2px(this, 25);
                }
                params.height = AppUtil.getHeightPx(this) - bottom;
                mContentVp.setLayoutParams(params);
            }
        }
        mPlayButtonLayout.setVisibility(View.GONE);
        mIsPlay = true;
        mParent.setStay(true);
    }

    protected void initViewPager() {
        ViewGroup.LayoutParams params = mContentVp.getLayoutParams();
        if (params != null) {
            int bottom = AppUtil.dp2px(this, 50 + 43 + mTitleBarHeight);
            if (mBottomLayout.getVisibility() != View.GONE) {
                bottom += AppUtil.dp2px(this, 50);
            }
            params.height = AppUtil.getHeightPx(this) - bottom;
            mContentVp.setLayoutParams(params);
        }
    }

    protected void coursePause() {
        if (!mIsFullScreen) {
            mParent.setCanScroll(true);
            initViewPager();
        }
        mIsPlay = false;
        mParent.setStay(false);
        mPlayButtonLayout.setVisibility(View.VISIBLE);
    }

    protected boolean mIsFullScreen = false;

    private void fullScreen() {
        ViewGroup.LayoutParams params = mMediaRlayout.getLayoutParams();
        if (!mIsFullScreen) {
            mParent.scrollTo(0, 0);
            getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
            mIsFullScreen = true;
            params.height = AppUtil.getWidthPx(this);
            params.width = -1;
            mMediaRlayout.setLayoutParams(params);
            mParent.setScrollStay(true);
            mBottomLayout.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.GONE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            mIsFullScreen = false;
            params.width = -1;
            params.height = AppUtil.dp2px(this, mMediaViewHeight);
            mMediaRlayout.setLayoutParams(params);
            mParent.setScrollStay(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            if (!mIsMemder) {
                mBottomLayout.setVisibility(View.VISIBLE);
            } else {
                if (this instanceof CourseActivity) {
                    mTvInclass.setVisibility(View.VISIBLE);
                }
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
                new MessageType(Const.COURSE_HIDE_BAR)};
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
                if (mContentVp != null) {
                    mContentVp.setCurrentItem(1);
                    tabLoadingGone();
                }
                break;
            case LOADING_END:
                setLoadStatus(View.GONE);
                break;
        }
        return false;
    }
}
