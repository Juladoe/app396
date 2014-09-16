package com.edusoho.kuozhi.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.MessageEngine;
import com.edusoho.kuozhi.core.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Member;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.PayStatus;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.ObjectAnimator;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsActivity extends ActionBarBaseActivity
        implements MessageEngine.MessageCallback {

    public static final String CACHE = "cache";
    public static final String COURSE_PIC = "picture";
    public static final String TAG = "CourseDetailsActivity";
    public static final int HIDE_COURSE_PIC = 0001;
    public static final int SET_LEARN_BTN = 0003;

    private String mTitle;
    private String mCourseId;
    private String mCoursePic;
    private CourseDetailsResult mCourseDetailsResult;
    private String mCurrentFragment;
    private Class mCurrentFragmentClass;

    private View mBtnLayout;
    private View mLoadView;
    private ImageView mCoursePicView;
    private FrameLayout mFragmentLayout;
    private Button mVipLearnBtn;
    private Button mLearnBtn;
    private int mVipLevelId;
    private double mPrice;

    protected MenuDrawer mMenuDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMenuDrawer();
        initView();
        app.registMsgSource(this);
    }

    private void initMenuDrawer()
    {
        mMenuDrawer = MenuDrawer.attach(
                mActivity, MenuDrawer.Type.OVERLAY, Position.RIGHT, MenuDrawer.MENU_DRAG_WINDOW);
        mMenuDrawer.setContentView(R.layout.course_details);
        mMenuDrawer.setMenuSize(EdusohoApp.screenW);
        mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);

        mMenuDrawer.setOnDrawerStateChangeListener(new MenuDrawer.OnDrawerStateChangeListener() {
            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                if (newState == MenuDrawer.STATE_OPEN) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);
                } else if (newState == MenuDrawer.STATE_CLOSED) {
                    mMenuDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_NONE);
                }
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
            }
        });
    }

    public MenuDrawer getMenuDrawer()
    {
        return mMenuDrawer;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.course_details_menu_shard) {
            Log.d(null, "shard->");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mMenuDrawer.isMenuVisible()) {
            mMenuDrawer.closeMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void invoke(WidgetMessage message) {
        int type = message.type.code;
        Bundle data = message.data;
        switch (type) {
            case HIDE_COURSE_PIC:
                Log.d(null, "hide course pic->");
                AppUtil.animForHeight(
                        new EdusohoAnimWrap(mCoursePicView), mCoursePicView.getHeight(), 0, 500);
                break;
            case SET_LEARN_BTN:
                mVipLevelId = data.getInt("vipLevelId", 0);
                mPrice = data.getDouble("price", 0);
                String vipLevelName = data.getString("vipLevelName");

                if (mPrice <= 0) {
                    mLearnBtn.setText("加入学习");
                }
                if (mVipLevelId != 0) {
                    mVipLearnBtn.setText(vipLevelName + "免费学");
                } else {
                    mVipLearnBtn.setVisibility(View.GONE);
                }

                showBtnLayout();
                break;
        }
    }

    private void showBtnLayout()
    {
        mBtnLayout.measure(0, 0);
        int height = mBtnLayout.getMeasuredHeight();
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                new EdusohoAnimWrap(mBtnLayout), "height", 0, height);
        objectAnimator.setDuration(240);
        objectAnimator.setInterpolator(new AccelerateInterpolator());
        objectAnimator.start();
    }

    @Override
    public MessageType[] getMsgTypes() {
        String source = this.getClass().getSimpleName();
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(SET_LEARN_BTN, source),
                new MessageType(HIDE_COURSE_PIC, source)
        };
        return messageTypes;
    }

    private void initView()
    {
        Intent data = getIntent();
        if (data != null) {
            mTitle = data.getStringExtra(Const.ACTIONBAT_TITLE);
            mCoursePic = data.getStringExtra(COURSE_PIC);
            mCourseId = data.getStringExtra(Const.COURSE_ID);
        }

        setBackMode(BACK, mTitle);

        mCoursePicView = (ImageView) findViewById(R.id.course_details_header);
        mFragmentLayout = (FrameLayout) findViewById(android.R.id.list);
        mBtnLayout = findViewById(R.id.course_details_btn_layouts);
        mVipLearnBtn = (Button) findViewById(R.id.course_details_vip_learnbtn);
        mLearnBtn = (Button) findViewById(R.id.course_details_learnbtn);

        loadCoursePic();
        loadCourseInfo();
        bindBtnClick();
    }

    public View getCoursePic()
    {
        return mCoursePicView;
    }

    private void loadCoursePic()
    {
        AQuery aQuery = new AQuery(mCoursePicView);
        aQuery.image(mCoursePic, false, true, 0, R.drawable.noram_course);
    }

    private void loadCourseInfo()
    {
        mLoadView = getLoadView();
        mFragmentLayout.addView(mLoadView);

        RequestUrl url = app.bindUrl(Const.COURSE, true);
        url.setParams(new String[] {
                "courseId", mCourseId
        });

        setProgressBarIndeterminateVisibility(true);
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != Const.CACHE_CODE) {
                    setProgressBarIndeterminateVisibility(false);
                }
                parseRequestData(object);
            }

            @Override
            public void update(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                updateRequestData(object);
            }
        });
    }

    private void updateRequestData(String object)
    {
        mCourseDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<CourseDetailsResult>() {
        });
        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        String fragment = mCourseDetailsResult.userIsStudent
                ? "CourseLearningFragment" : "CourseDetailsFragment";

        if (fragment.equals(mCurrentFragment)) {
            app.sendMsgToTarget(BaseFragment.DATA_UPDATE, null, mCurrentFragmentClass);
            return;
        }
        loadCoureDetailsFragment(fragment);
    }

    private void parseRequestData(String object)
    {
        mLoadView.setVisibility(View.GONE);
        mCourseDetailsResult = mActivity.parseJsonValue(
                object, new TypeToken<CourseDetailsResult>() {
        });
        if (mCourseDetailsResult == null || mCourseDetailsResult.course == null) {
            longToast("加载课程信息出现错误！请尝试重新打开课程！");
            return;
        }

        Member member = mCourseDetailsResult.member;
        String fragment = member != null
                ? "CourseLearningFragment" : "CourseDetailsFragment";
        loadCoureDetailsFragment(fragment);
    }

    private void bindBtnClick()
    {
        mLearnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPrice > 0) {
                    app.mEngine.runNormalPlugin(
                            "PayCourseActivity",
                            mActivity,
                            new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra("price", mPrice);
                                    startIntent.putExtra("title", mTitle);
                                    startIntent.putExtra("courseId", mCourseId);
                                }
                    });
                } else {
                    if (app.loginUser == null) {
                        LoginActivity.start(mActivity);
                        return;
                    }
                    learnCourse();
                }
            }
        });
    }

    private void learnCourse()
    {
        setProgressBarIndeterminateVisibility(true);
        RequestUrl url = app.bindUrl(Const.PAYCOURSE, true);
        url.setParams(new String[] {
                "payment", "alipay",
                "courseId", mCourseId
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                setProgressBarIndeterminateVisibility(false);
                PayStatus payStatus = parseJsonValue(
                        object, new TypeToken<PayStatus>(){});

                if (payStatus == null) {
                    longToast("加入学习失败！");
                    return;
                }

                if (!Const.RESULT_OK.equals(payStatus.status)) {
                    longToast(payStatus.message);
                    return;
                }

                if (payStatus.paid) {
                    //免费课程
                    loadCoureDetailsFragment("CourseLearningFragment");
                }
            }
        });
    }

    public CourseDetailsResult getCourseDetailsInfo()
    {
        return mCourseDetailsResult;
    }

    private View getLoadView()
    {
        View loadView = LayoutInflater.from(mContext).inflate(R.layout.loading_layout, null);
        loadView.findViewById(R.id.load_text).setVisibility(View.GONE);
        return loadView;
    }

    private void loadCoureDetailsFragment(String fragmentName)
    {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = app.mEngine.runPluginWithFragment(
                fragmentName, mActivity, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString(Const.COURSE_ID, mCourseId);
                bundle.putString(Const.ACTIONBAT_TITLE, mTitle);
            }
        });
        fragmentTransaction.replace(android.R.id.list, fragment);
        fragmentTransaction.commit();

        mCurrentFragment = fragmentName;
        mCurrentFragmentClass = fragment.getClass();
    }
}
