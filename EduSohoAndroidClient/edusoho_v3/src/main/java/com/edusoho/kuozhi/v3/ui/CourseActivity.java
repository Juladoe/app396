package com.edusoho.kuozhi.v3.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.test.FragmentViewPagerAdapter;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.FragmentUtil;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */
public class CourseActivity extends BaseNoTitleActivity implements View.OnClickListener {
    private HeadStopScrollView mParent;
    private RelativeLayout mHeadRlayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mParent = (HeadStopScrollView) findViewById(R.id.scroll_parent);
        mHeadRlayout = (RelativeLayout) findViewById(R.id.head_rlayout);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mIntroLayout = (RelativeLayout) findViewById(R.id.intro_rlayout);
        mHourLayout = (RelativeLayout) findViewById(R.id.hour_rlayout);
        mReviewLayout = (RelativeLayout) findViewById(R.id.review_rlayout);
        mIntro = findViewById(R.id.intro);
        mHour = findViewById(R.id.hour);
        mReview = findViewById(R.id.review);
        mAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mContentVp.setAdapter(mAdapter);
    }

    private void initEvent() {

    }

    private void initData() {
        mIntroLayout.setOnClickListener(this);
        mHourLayout.setOnClickListener(this);
        mReviewLayout.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.intro_rlayout) {
            mContentVp.setCurrentItem(0);
        } else if (v.getId() == R.id.hour_rlayout) {
            mContentVp.setCurrentItem(1);
        } else if (v.getId() == R.id.review_rlayout) {
            mContentVp.setCurrentItem(2);
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
            case Const.COURSE_SWITCH:
                courseSwitch();
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

        }
    }

    private void changeBar(boolean show) {

    }

    private void courseSwitch() {

    }

    private boolean mIsFullScreen = false;

    private void fullScreen() {
        if (!mIsFullScreen) {
            ViewGroup.LayoutParams params = mHeadRlayout.getLayoutParams();
            params.height = -1;
            params.width = -1;
            mHeadRlayout.setLayoutParams(params);
            mParent.setCanScroll(false);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {

        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{
                new MessageType(Const.SCROLL_STATE_SAVE),
                new MessageType(Const.FULL_SCREEN),
                new MessageType(Const.COURSE_SWITCH),
                new MessageType(Const.COURSE_REFRESH),
                new MessageType(Const.COURSE_SHOW_BAR),
                new MessageType(Const.COURSE_HIDE_BAR)};
    }
}
