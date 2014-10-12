package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.VipLevel;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsActivity;
import com.edusoho.kuozhi.ui.course.CourseDetailsTabActivity;
import com.edusoho.kuozhi.ui.widget.CourseDetailsGoalsWidget;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLabelWidget;
import com.edusoho.kuozhi.ui.widget.CourseDetailsLessonWidget;
import com.edusoho.kuozhi.ui.widget.CourseDetailsReviewWidget;
import com.edusoho.kuozhi.ui.widget.CourseDetailsTeacherWidget;
import com.edusoho.kuozhi.ui.widget.ScrollWidget;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EduSohoTextBtn;
import com.edusoho.kuozhi.view.EdusohoAnimWrap;
import com.edusoho.listener.LessonItemClickListener;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.Position;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsFragment extends BaseFragment{

    private String mTitle;
    private int mCourseId;
    private Teacher mTeacher;

    private AQuery aQuery;
    private CourseDetailsGoalsWidget mCourseGoalsView;
    private CourseDetailsGoalsWidget mCourseAudiencesView;
    private CourseDetailsGoalsWidget mCourseAboutView;
    private CourseDetailsReviewWidget mCourseReviewView;
    private CourseDetailsTeacherWidget mCourseTeacherView;
    private CourseDetailsLessonWidget mCourseLessonView;

    private EduSohoTextBtn mFavoriteBtn;
    private ViewGroup mLessonLayout;

    private Stack<String> mHeadStack;
    private Stack<CourseDetailsLabelWidget> mLabelsStack;
    private ArrayList<CourseDetailsLabelWidget> mViewList;
    private ScrollWidget mScrollView;
    private View mHeadView;
    private CourseDetailsResult mCourseResult;
    private TextView mHeadTextView;

    private MenuDrawer mMenuDrawer;
    private CourseDetailsActivity mCourseDetailsActivity;

    @Override
    public String getTitle() {
        return "课程详情";
    }

    @Override
    public void invoke(WidgetMessage message) {
        super.invoke(message);
        int type = message.type.code;
        switch (type) {
            case DATA_UPDATE:
                initViewData(mCourseDetailsActivity.getCourseDetailsInfo(), true);
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.course_details_fragment);
        mHeadStack = new Stack<String>();
        mViewList = new ArrayList<CourseDetailsLabelWidget>();
        mLabelsStack = new Stack<CourseDetailsLabelWidget>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(null, "onAttach");
        mCourseDetailsActivity = (CourseDetailsActivity) activity;

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(Const.ACTIONBAT_TITLE);
            mCourseId = bundle.getInt(Const.COURSE_ID);
        }

        mMenuDrawer = mCourseDetailsActivity.getMenuDrawer();
        mCourseLessonView = (CourseDetailsLessonWidget) LayoutInflater.from(activity).inflate(
                R.layout.course_details_lesson_content, null);
        mMenuDrawer.setMenuView(mCourseLessonView);

        mCourseLessonView.initLesson(mCourseId, mCourseDetailsActivity, false);
        mCourseLessonView.onShow();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.course_details_menu, menu);
    }

    @Override
    protected void initView(View view) {
        Log.d(null, "initView->");
        mHeadTextView = (TextView) view.findViewById(R.id.course_details_head_label);
        mLessonLayout = (ViewGroup) view.findViewById(R.id.course_details_lesson_layout);
        mScrollView = (ScrollWidget) view.findViewById(R.id.course_details_scorllview);
        mFavoriteBtn = (EduSohoTextBtn) view.findViewById(R.id.course_details_favorite);
        mCourseReviewView = (CourseDetailsReviewWidget) view.findViewById(R.id.course_details_review);
        mCourseAudiencesView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_audiences);
        mCourseGoalsView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_goals);
        mCourseAboutView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_about);
        mCourseTeacherView = (CourseDetailsTeacherWidget) view.findViewById(R.id.course_details_teacher);
        mHeadView = view.findViewById(R.id.course_details_header);

        mViewList.add(mCourseGoalsView);
        mViewList.add(mCourseAudiencesView);
        mViewList.add(mCourseAboutView);
        mViewList.add(mCourseReviewView);
        mViewList.add(mCourseTeacherView);

        aQuery = new AQuery(view);
        initViewData(mCourseDetailsActivity.getCourseDetailsInfo(), false);

        mScrollView.setHeadView(mHeadView);
        bindListener();
    }

    private void bindListener()
    {
        mLessonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMenuDrawer.openMenu();
            }
        });

        mScrollView.setScrollChangeListener(new ScrollWidget.ScrollChangeListener() {
            @Override
            public void onBottom() {
                Log.d(null, "bottom->");
                showMoreView();
            }

            @Override
            public void onScroll(int l, int t, int oldl, int oldt) {
                setHeadTitle(t);
            }
        });

        mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (app.loginUser == null) {
                    mActivity.longToast("请先登录！");
                    LoginActivity.startForResult(mActivity);
                    return;
                }

                showProgress(true);
                mFavoriteBtn.setEnabled(false);
                int courseId = mCourseResult.course.id;
                if (mCourseResult.userFavorited) {
                    unFavoriteCourse(courseId);
                } else {
                    favoriteCourse(courseId);
                }
            }
        });
    }

    private void showHeadViewByAnim()
    {
        EdusohoAnimWrap animWrap = new EdusohoAnimWrap(mHeadView);
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator widthAnim = ObjectAnimator.ofFloat(animWrap, "scaleX", 3.0f, 1.0f);
        widthAnim.setDuration(400);
        widthAnim.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator heightAnim = ObjectAnimator.ofFloat(animWrap, "scaleY", 3.0f, 1.0f);
        widthAnim.setDuration(400);

        widthAnim.setInterpolator(new AccelerateInterpolator());
        set.playTogether(
                widthAnim,
                heightAnim
        );
        set.start();
    }

    private void showMoreView()
    {
        if (mLabelsStack.empty()) {
            return;
        }
        CourseDetailsLabelWidget view = mLabelsStack.pop();
        view.onShow();
    }

    private void unFavoriteCourse(int courseId)
    {
        RequestUrl url = app.bindUrl(Const.UNFAVORITE, true);
        url.setParams(new String[] {
                "courseId", courseId + ""
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                showProgress(false);
                mFavoriteBtn.setEnabled(true);
                Boolean result = mActivity.parseJsonValue(
                        object, new TypeToken<Boolean>(){});
                if (result) {
                    mCourseResult.userFavorited = false;
                    mFavoriteBtn.setText("收藏", R.string.font_favoirte);
                    mFavoriteBtn.setTextColor(getResources().getColor(R.color.system_normal_text));
                }
            }
        });
    }


    private void favoriteCourse(int courseId)
    {
        RequestUrl url = app.bindUrl(Const.FAVORITE, true);
        url.setParams(new String[] {
                "courseId", courseId + ""
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                showProgress(false);
                mFavoriteBtn.setEnabled(true);
                Boolean result = mActivity.parseJsonValue(object, new TypeToken<Boolean>(){});
                if (result) {
                    mCourseResult.userFavorited = true;
                    mFavoriteBtn.setText("已收藏", R.string.font_favorited);
                    mFavoriteBtn.setTextColor(getResources().getColor(R.color.course_favorited));
                }
            }
        });
    }

    private void setHeadTitle(int t)
    {
        if (!mHeadStack.isEmpty()) {
            String head = mHeadStack.peek();
            mHeadTextView.setVisibility(View.VISIBLE);
            mHeadTextView.setText(head);
        } else {
            mHeadTextView.setVisibility(View.GONE);
        }

        for (CourseDetailsLabelWidget view : mViewList) {
            Object tag = view.getTag();
            if (tag == null && view.isMoveToTop(t)) {
                view.setTag(true);
                mHeadStack.add(view.getTitle());
                return;
            }
            if (tag != null && view.isLeaveTop(t)) {
                view.setTag(null);
                mHeadStack.pop();
                return;
            }
        }
    }

    private void setCourseStatus()
    {
        if (mCourseResult.userFavorited) {
            mFavoriteBtn.setText("已收藏", R.string.font_favorited);
            mFavoriteBtn.setTextColor(getResources().getColor(R.color.course_favorited));
        }

        Course course = mCourseResult.course;
        Bundle bundle =  new Bundle();
        bundle.putInt("vipLevelId", course.vipLevelId);
        bundle.putDouble("price", course.price);
        bundle.putString("vipLevelName", getVipLevelName(
                course.vipLevelId, mCourseResult.vipLevels));
        app.sendMsgToTarget(
                CourseDetailsActivity.SET_LEARN_BTN, bundle, CourseDetailsActivity.class);
    }

    private String getVipLevelName(int level, VipLevel[] vipLevels)
    {
        for(VipLevel vipLevel : vipLevels) {
            if (level == vipLevel.id) {
                return vipLevel.name;
            }
        }

        return "";
    }

    private void addHeadImageView()
    {
        View coursePic = mCourseDetailsActivity.getCoursePic();
        ViewGroup coursePicParent = (ViewGroup) coursePic.getParent();
        if (coursePicParent != null) {
            coursePicParent.removeView(coursePic);
        }
        ViewGroup childGroup = (ViewGroup) mScrollView.getChildAt(0);
        childGroup.addView(coursePic);
    }

    private void initViewData(CourseDetailsResult result, boolean isUpdate)
    {
        mCourseResult = result;
        Course course = result.course;
        aQuery.id(R.id.course_details_info_title).text(course.title);
        aQuery.id(R.id.course_details_info_subtitle).text(course.subtitle);

        mTeacher = null;
        if (course.teachers != null && course.teachers.length > 0) {
            mTeacher = course.teachers[0];
            aQuery.id(R.id.course_details_info_teacher).text("教师:" + mTeacher.nickname);
        }

        setCourseStatus();
        addHeadImageView();

        aQuery.id(R.id.course_details_rating).rating((float)course.rating);
        String price = course.price <= 0 ? "免费" : "￥" + course.price;
        aQuery.id(R.id.course_details_price).text(price);
        if (course.expiryDay > 0) {
            aQuery.id(R.id.course_details_info_expiry).text("有效期:" + course.expiryDay + "天");
        }
        aQuery.id(R.id.course_details_studentNum).text(course.studentNum + "学员");

        mCourseGoalsView.setText(AppUtil.goalsToStr(course.goals));
        mCourseAudiencesView.setText(AppUtil.audiencesToStr(course.audiences));
        mCourseAboutView.setText(AppUtil.coverCourseAbout(course.about));
        mCourseTeacherView.initUser(mTeacher.id, mActivity);
        mCourseReviewView.initReview(course.id, mActivity, false);

        if (!isUpdate) {
            showCourseMoreInfoListener();
        }
    }

    private String getFragmetName(int id)
    {
        if (id == R.id.course_details_teacher) {
            return "TeacherInfoFragment";
        } else if (id == R.id.course_details_review) {
            return "ReviewInfoFragment";
        }

        return "CourseInfoFragment";
    }

    private void showCourseMoreInfoListener()
    {
        mCourseTeacherView.setShowMoreBtn(getClickListener("TeacherInfoFragment"));
        mCourseAboutView.setShowMoreBtn(getClickListener("CourseInfoFragment"));
        mCourseReviewView.setShowMoreBtn(getClickListener("ReviewInfoFragment"));
    }

    private View.OnClickListener getClickListener(final String name)
    {
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                app.mEngine.runNormalPluginWithBundle(
                        "CourseDetailsTabActivity",
                        mActivity,
                        getFragmentBundle(name)
                );
            }
        };
    }
    private Bundle getFragmentBundle(String fragmentName)
    {
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putIntArray(
                TeacherInfoFragment.TEACHER_ID, AppUtil.getTeacherIds(mCourseResult.course.teachers));
        fragmentBundle.putSerializable(CourseInfoFragment.COURSE, mCourseResult.course);
        fragmentBundle.putInt(Const.COURSE_ID, mCourseResult.course.id);

        Bundle bundle = new Bundle();
        bundle.putBundle(CourseDetailsTabActivity.FRAGMENT_DATA, fragmentBundle);
        bundle.putString(Const.ACTIONBAT_TITLE, "课程详情");
        bundle.putStringArray(CourseDetailsTabActivity.LISTS, Const.COURSE_INFO_FRAGMENT);
        bundle.putStringArray(CourseDetailsTabActivity.TITLES, Const.COURSE_INFO_TITLE);
        bundle.putString(
                CourseDetailsTabActivity.FRAGMENT, fragmentName);

        return bundle;
    }
}
