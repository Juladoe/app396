package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.shard.ShardDialog;
import com.edusoho.kuozhi.v3.adapter.test.FragmentViewPagerAdapter;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDetailFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SystemBarTintManager;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */
public class CourseActivity extends BaseNoTitleActivity implements View.OnClickListener {
    public static final String COURSE_ID = "course_id";
    private HeadStopScrollView mParent;
    private RelativeLayout mHeadRlayout;
    private ImageView mIvShare;
    private ImageView mIvGrade;
    private RelativeLayout mMediaRlayout;
    private ViewPager mContentVp;
    private RelativeLayout mIntroLayout;
    private View mIntro;
    private RelativeLayout mHourLayout;
    private View mHour;
    private RelativeLayout mReviewLayout;
    private View mReview;
    private List<Fragment> mFragments = new ArrayList<>();
    private FragmentViewPagerAdapter mAdapter;
    private int mCheckNum = 0;
    private int[] mScrollY = new int[3];
    private boolean[] mCanScroll = {true, true, true};
    public static final int MEDIA_VIEW_HEIGHT = 210;
    private String mCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(Color.parseColor("#00000000"));
        }

        Intent intent = getIntent();
        mCourseId = intent.getStringExtra(COURSE_ID);
        if (mCourseId == null || mCourseId.trim().length() == 0) {
            finish();
        }
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mParent = (HeadStopScrollView) findViewById(R.id.scroll_parent);
        mHeadRlayout = (RelativeLayout) findViewById(R.id.head_rlayout);
        mMediaRlayout = (RelativeLayout) findViewById(R.id.media_rlayout);
        mIvGrade = (ImageView) findViewById(R.id.iv_grade);
        mIvShare = (ImageView) findViewById(R.id.iv_share);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mIntroLayout = (RelativeLayout) findViewById(R.id.intro_rlayout);
        mHourLayout = (RelativeLayout) findViewById(R.id.hour_rlayout);
        mReviewLayout = (RelativeLayout) findViewById(R.id.review_rlayout);
        mIntro = findViewById(R.id.intro);
        mHour = findViewById(R.id.hour);
        mReview = findViewById(R.id.review);
        mFragments.add(new CourseDetailFragment(mCourseId));
        mAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mContentVp.setAdapter(mAdapter);
        mParent.setFirstViewHeight(AppUtil.dp2px(this, 260));
        ViewGroup.LayoutParams params = mContentVp.getLayoutParams();
        if (params != null) {
            params.height = AppUtil.getHeightPx(this);
            mContentVp.setLayoutParams(params);
        }
    }

    private void initEvent() {
        mIntroLayout.setOnClickListener(this);
        mHourLayout.setOnClickListener(this);
        mReviewLayout.setOnClickListener(this);
        mIvShare.setOnClickListener(this);
        mIvGrade.setOnClickListener(this);
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
                mCanScroll[mCheckNum] = mParent.isCanScroll();
                mScrollY[mCheckNum] = t;
            }
        });
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_rlayout) {
            mContentVp.setCurrentItem(0);
        } else if (v.getId() == R.id.hour_rlayout) {
            mContentVp.setCurrentItem(1);
        } else if (v.getId() == R.id.review_rlayout) {
            mContentVp.setCurrentItem(2);
        } else if (v.getId() == R.id.iv_grade) {

        } else if (v.getId() == R.id.iv_share) {
            new ShardDialog(this).show();
        }

    }

    private void checkTab(int num) {
        mCheckNum = num;
        mIntro.setVisibility(View.GONE);
        mHour.setVisibility(View.GONE);
        mReview.setVisibility(View.GONE);
        mParent.setCanScroll(mCanScroll[num]);
        mParent.scrollTo(0, mScrollY[num]);
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
            case Const.SCROLL_STATE_SAVE:
                String clazz = bundle.getString("class");
                if (clazz != null && clazz.equals(getClass().getSimpleName())) {
                    mCanScroll[mCheckNum] = true;
                    mParent.setCanScroll(true);
                }
                break;
            case Const.FULL_SCREEN:
                fullScreen();
                break;
            case Const.COURSE_START:
                courseStart();
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
            case Const.FILL_BANNER:
                fillBanner();
        }
    }

    private void fillBanner() {

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
//            if(mIsFullScreen){
//                fullScreen();
//            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeBar(boolean show) {
        if (show) {
            mHeadRlayout.setVisibility(View.GONE);
        } else {
            mHeadRlayout.setVisibility(View.VISIBLE);
        }
    }

    private void courseStart() {
        /**
         * todo 切换课程
         */
    }

    private boolean mIsFullScreen = false;

    private void fullScreen() {
        ViewGroup.LayoutParams params = mMediaRlayout.getLayoutParams();
        if (!mIsFullScreen) {
            mIsFullScreen = true;
            params.height = -1;
            params.width = -1;
            mMediaRlayout.setLayoutParams(params);
            mParent.setCanScroll(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            mIsFullScreen = false;
            params.width = -1;
            params.height = AppUtil.dp2px(this, MEDIA_VIEW_HEIGHT);
            mMediaRlayout.setLayoutParams(params);
            mParent.setCanScroll(mCanScroll[mCheckNum]);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{
                new MessageType(Const.SCROLL_STATE_SAVE),
                new MessageType(Const.FULL_SCREEN),
                new MessageType(Const.FILL_BANNER),
                new MessageType(Const.COURSE_START),
                new MessageType(Const.COURSE_REFRESH),
                new MessageType(Const.COURSE_SHOW_BAR),
                new MessageType(Const.SCREEN_LOCK),
                new MessageType(Const.COURSE_HIDE_BAR)};
    }
}
