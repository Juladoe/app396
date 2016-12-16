package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.test.FragmentViewPagerAdapter;
import com.edusoho.kuozhi.v3.entity.coursedetail.CourseDetail;
import com.edusoho.kuozhi.v3.entity.coursedetail.Member;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.ui.fragment.CourseCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.CourseDetailFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.util.SystemBarTintManager;
import com.edusoho.kuozhi.v3.view.HeadStopScrollView;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by Zhang on 2016/12/8.
 */
public class CourseActivity extends BaseNoTitleActivity implements View.OnClickListener {
    public static final int MEDIA_VIEW_HEIGHT = 210;
    public static final String COURSE_ID = "course_id";
    private HeadStopScrollView mParent;
    private RelativeLayout mHeadRlayout;
    private RelativeLayout mHeadRlayout2;
    private View mIvShare;
    private View mIvShare2;
    private View mIvGrade;
    private View mIvGrade2;
    private View mPlayLayout;
    private View mPlayLayout2;
    private View mBottomLayout;
    private View mConsult;
    private View mCollect;
    private View mBack2;
    private View mTvInclass;
    private TextView mTvCollect;
    private View mAddCourse;
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
    private String mCourseId;
    private String mClassroomId;
    private boolean mIsFavorite = false;
    private boolean mIsPlay = false;
    private CourseDetail mCourseDetail;
    private int mTitleBarHeight;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mTitleBarHeight = 20;
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
        mContentVp = (ViewPager) findViewById(R.id.vp_content);
        mIntroLayout = (RelativeLayout) findViewById(R.id.intro_rlayout);
        mHourLayout = (RelativeLayout) findViewById(R.id.hour_rlayout);
        mReviewLayout = (RelativeLayout) findViewById(R.id.review_rlayout);
        mIntro = findViewById(R.id.intro);
        mHour = findViewById(R.id.hour);
        mReview = findViewById(R.id.review);
        mBack2 = findViewById(R.id.back2);
        mTvInclass = findViewById(R.id.tv_inclass);
        mFragments.add(new CourseDetailFragment(mCourseId));
        mFragments.add(new CourseCatalogFragment(mCourseId));
        mAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mFragments);
        mContentVp.setAdapter(mAdapter);
        mParent.setFirstViewHeight(AppUtil.dp2px(this,
                MEDIA_VIEW_HEIGHT - 43 - mTitleBarHeight));
        mBottomLayout = findViewById(R.id.bottom_layout);
        mCollect = findViewById(R.id.collect_layout);
        mTvCollect = (TextView) findViewById(R.id.tv_collect);
        mConsult = findViewById(R.id.consult_layout);
        mAddCourse = findViewById(R.id.tv_add);
        initViewPager();
        ViewGroup.LayoutParams headParams =
                mHeadRlayout2.getLayoutParams();
        headParams.height = AppUtil.dp2px(this, 43 + mTitleBarHeight);
        mHeadRlayout2.setLayoutParams(headParams);
        mHeadRlayout2.setPadding(0, AppUtil.dp2px(this, mTitleBarHeight), 0, 0);
    }

    private void initEvent() {
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
        mAddCourse.setOnClickListener(this);
        mConsult.setOnClickListener(this);
        mBack2.setOnClickListener(this);
        mTvInclass.setOnClickListener(this);
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
                if (!mParent.isCanScroll() && t != 0) {
                    mHeadRlayout.setVisibility(View.GONE);
                    mHeadRlayout2.setVisibility(View.VISIBLE);
                    mParent.scrollTo(0, AppUtil.dp2px(CourseActivity.this,
                            MEDIA_VIEW_HEIGHT - 43 - mTitleBarHeight));
                } else {
                    mHeadRlayout.setVisibility(View.VISIBLE);
                    mHeadRlayout2.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initData() {
        CourseDetailModel.getCourseDetail(mCourseId,
                new ResponseCallbackListener<CourseDetail>() {
                    @Override
                    public void onSuccess(CourseDetail data) {
                        mCourseDetail = data;
                        refreshView();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                        if (message.equals("课程不存在")) {
                            CommonUtil.shortToast(CourseActivity.this, "课程不存在");
                            finish();
                        }
                    }
                });
    }

    private void refreshView() {
        mIsFavorite = mCourseDetail.isUserFavorited();
        if (mIsFavorite) {
            mTvCollect.setText(getResources().getString(R.string.new_font_collected));
        } else {
            mTvCollect.setText(getResources().getString(R.string.new_font_collect));
        }
        Member member = mCourseDetail.getMember();
        if (member == null) {
            mBottomLayout.setVisibility(View.VISIBLE);
            mIvGrade.setVisibility(View.GONE);
            mIvGrade2.setVisibility(View.GONE);
            mTvInclass.setVisibility(View.GONE);
            initViewPager();
        } else {
            mBottomLayout.setVisibility(View.GONE);
            mIvGrade.setVisibility(View.VISIBLE);
            mIvGrade2.setVisibility(View.VISIBLE);
            mTvInclass.setVisibility(View.VISIBLE);
            initViewPager();
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
        }

    }

    private void consult() {
        List<Teacher> teachers = mCourseDetail.getCourse().getTeachers();
        final Teacher teacher;
        if (teachers.size() > 0) {
            teacher = teachers.get(0);
        } else {
            /**
             * todo 老师为空的时候
             */
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ImChatActivity.FROM_NAME, teacher.nickname);
        bundle.putInt(ImChatActivity.FROM_ID, teacher.id);
        bundle.putString(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
        app.mEngine.runNormalPlugin("ImChatActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    private void add() {
        if (mCourseId != null) {
            CourseUtil.addCourse(new CourseUtil.CourseParamsBuilder()
                            .setCouponCode("")
                            .setPayment("")
                            .setPayPassword("")
                            .setTargetId(mCourseDetail.getCourse().getId())
                            .setTargetType("course")
                            .setTotalPrice(mCourseDetail.getCourse().getPrice())
                    , new CourseUtil.OnAddCourseListener() {
                        @Override
                        public void onAddCourseSuccee(String response) {
                            CommonUtil.shortToast(CourseActivity.this, getResources()
                                    .getString(R.string.success_add_course));
                            initData();
                        }

                        @Override
                        public void onAddCourseError(String error) {

                        }
                    });
        }
    }

    private void collect() {
        if (mIsFavorite) {
            CourseUtil.uncollectCourse(mCourseId, new CourseUtil.OnCollectSucceeListener() {
                @Override
                public void onCollectSuccee() {
                    mTvCollect.setText(getResources().getString(R.string.new_font_collect));
                }
            });
        } else {
            CourseUtil.collectCourse(mCourseId, new CourseUtil.OnCollectSucceeListener() {
                @Override
                public void onCollectSuccee() {
                    mTvCollect.setText(getResources().getString(R.string.new_font_collected));
                }
            });
        }
    }

    private void share() {
        if (mCourseDetail == null) {
            return;
        }
        final ShareTool shareTool =
                new ShareTool(this
                        , app.host + "/course/" + mCourseDetail.getCourse().getId()
                        , mCourseDetail.getCourse().getTitle()
                        , mCourseDetail.getCourse().getAbout().length() > 20 ?
                        mCourseDetail.getCourse().getAbout().substring(0, 20)
                        : mCourseDetail.getCourse().getAbout()
                        , mCourseDetail.getCourse().getLargePicture());
        new Handler((mActivity.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
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
                if (mIsPlay) {
                    break;
                }
                String clazz = bundle.getString("class");
                if (clazz != null && clazz.equals(getClass().getSimpleName())) {
                    mCanScroll[mCheckNum] = true;
                    mParent.setCanScroll(true);
                    mParent.scrollTo(0, mParent.getScrollY() - 2);
                }
                break;
            case Const.FULL_SCREEN:
                fullScreen();
                break;
            case Const.COURSE_START:
                /**
                 * todo 获得课程相关信息
                 */
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
        }
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
         * todo 播放课程
         */
        if (!mIsFullScreen) {
            mParent.smoothScrollTo(0, 0);
            mParent.setCanScroll(false);
            mCanScroll[mCheckNum] = false;
            ViewGroup.LayoutParams params = mContentVp.getLayoutParams();
            if (params != null) {
                int bottom = AppUtil.dp2px(this, 50 + MEDIA_VIEW_HEIGHT);
                if (mBottomLayout.getVisibility() != View.GONE) {
                    bottom += AppUtil.dp2px(this, 50);
                }
                params.height = AppUtil.getHeightPx(this) - bottom;
                mContentVp.setLayoutParams(params);
            }
        }
        mPlayLayout.setVisibility(View.GONE);
        mIsPlay = true;
    }

    private void initViewPager() {
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

    private void coursePause() {
        if (!mIsFullScreen) {
            mParent.setCanScroll(true);
            mCanScroll[mCheckNum] = true;
            initViewPager();
        }
        mPlayLayout.setVisibility(View.VISIBLE);
        mIsPlay = false;
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
                new MessageType(Const.COURSE_START),
                new MessageType(Const.COURSE_REFRESH),
                new MessageType(Const.COURSE_SHOW_BAR),
                new MessageType(Const.COURSE_PAUSE),
                new MessageType(Const.SCREEN_LOCK),
                new MessageType(Const.COURSE_HIDE_BAR)};
    }
}
