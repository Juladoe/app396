package com.edusoho.kuozhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseDetailsResult;
import com.edusoho.kuozhi.model.CourseInfoResult;
import com.edusoho.kuozhi.model.GsonType;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.VipLevel;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
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
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Created by howzhi on 14-8-26.
 */
public class CourseDetailsFragment extends BaseFragment {

    private String mTitle;
    private String mCourseId;
    private Teacher mTeacher;

    private AQuery aQuery;
    private Handler handler;
    private CourseDetailsGoalsWidget mCourseGoalsView;
    private CourseDetailsGoalsWidget mCourseAudiencesView;
    private CourseDetailsGoalsWidget mCourseAboutView;
    private CourseDetailsReviewWidget mCourseReviewView;
    private CourseDetailsTeacherWidget mCourseTeacherView;
    private CourseDetailsLessonWidget mCourseLessonView;

    private EduSohoTextBtn mFavoriteBtn;

    private Stack<String> mHeadStack;
    private ArrayList<CourseDetailsLabelWidget> mViewList;
    private ScrollWidget mScrollView;
    private View mCourseInfoLayout;
    private View mHeadView;

    private CourseDetailsResult mCourseResult;

    @Override
    public String getTitle() {
        return "课程详情";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setContainerView(R.layout.course_details_fragment);
        handler = new Handler();
        mHeadStack = new Stack<String>();
        mViewList = new ArrayList<CourseDetailsLabelWidget>();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.course_details_menu, menu);
    }


    @Override
    protected void initView(View view) {
        mScrollView = (ScrollWidget) view.findViewById(R.id.course_details_scorllview);
        mCourseInfoLayout = view.findViewById(R.id.course_details_info_layout);
        mFavoriteBtn = (EduSohoTextBtn) view.findViewById(R.id.course_details_favorite);
        mCourseReviewView = (CourseDetailsReviewWidget) view.findViewById(R.id.course_details_review);
        mCourseLessonView = (CourseDetailsLessonWidget) view.findViewById(R.id.course_details_lesson);
        mCourseAudiencesView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_audiences);
        mCourseGoalsView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_goals);
        mCourseAboutView = (CourseDetailsGoalsWidget) view.findViewById(R.id.course_details_about);
        mCourseTeacherView = (CourseDetailsTeacherWidget) view.findViewById(R.id.course_details_teacher);
        mHeadView = view.findViewById(R.id.course_details_header);

        mViewList.add(mCourseGoalsView);
        mViewList.add(mCourseAudiencesView);
        mViewList.add(mCourseAboutView);

        mViewList.add(mCourseLessonView);
        mViewList.add(mCourseReviewView);
        mViewList.add(mCourseTeacherView);

        aQuery = new AQuery(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(CourseDetailsActivity.TITLE);
            mCourseId = bundle.getString(CourseDetailsActivity.COURSE_ID);
        }

        CourseDetailsActivity activity = (CourseDetailsActivity) getActivity();
        initViewData(activity.getCourseDetailsInfo());

        mScrollView.setHeadView(mHeadView);
        mScrollView.setScrollChangeListener(new ScrollWidget.ScrollChangeListener() {
            @Override
            public void onBottom() {
                Log.d(null, "bottom->");
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
                String courseId = mCourseResult.course.id;
                if (mCourseResult.userFavorited) {
                    unFavoriteCourse(courseId);
                } else {
                    favoriteCourse(courseId);
                }
            }
        });
    }

    private void unFavoriteCourse(String courseId)
    {
        String url = app.bindUrl(Const.UNFAVORITE);
        HashMap<String, String> params = app.initParams(new String[] {
                "courseId", courseId
        });
        mActivity.ajaxPost(url, params, new ResultCallback(){
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


    private void favoriteCourse(String courseId)
    {
        String url = app.bindUrl(Const.FAVORITE);
        HashMap<String, String> params = app.initParams(new String[] {
                "courseId", courseId
        });
        mActivity.ajaxPost(url, params, new ResultCallback(){
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
            Bundle bundle =  new Bundle();
            bundle.putString("text", head);
            app.sendMsgToTarget(
                    CourseDetailsActivity.SHOWHEAD, bundle, CourseDetailsActivity.class);
        } else {
            app.sendMsgToTarget(
                    CourseDetailsActivity.HIDEHEAD, null, CourseDetailsActivity.class);
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

    private void initViewData(CourseDetailsResult result)
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

        aQuery.id(R.id.course_details_rating).rating((float)course.rating);
        String price = course.price <= 0 ? "免费" : "￥" + course.price;
        aQuery.id(R.id.course_details_price).text(price);
        aQuery.id(R.id.course_details_info_expiry).text("有效期:" + course.expiryDay + "天");
        aQuery.id(R.id.course_details_studentNum).text(course.studentNum + "学员");

        mCourseGoalsView.setText(AppUtil.goalsToStr(course.goals));
        mCourseAudiencesView.setText(AppUtil.audiencesToStr(course.audiences));
        mCourseAboutView.setText(AppUtil.coverCourseAbout(course.about));
        mCourseTeacherView.initUser(mTeacher.id, mActivity);
        mCourseReviewView.initReview(course.id, mActivity, false);
        mCourseLessonView.initLesson(course.id, mActivity);

        showCourseMoreInfoListener();
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
        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                app.mEngine.runNormalPlugin(
                        "CourseDetailsTabActivity", mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(TeacherInfoFragment.TEACHER_ID, mTeacher.id);
                        bundle.putSerializable(CourseInfoFragment.COURSE, mCourseResult.course);
                        bundle.putString(ReviewInfoFragment.COURSE_ID, mCourseResult.course.id);

                        startIntent.putExtra(CourseDetailsTabActivity.FRAGMENT_DATA, bundle);
                        startIntent.putExtra(CourseDetailsTabActivity.TITLE, "课程详情");
                        startIntent.putExtra(CourseDetailsTabActivity.LISTS, Const.COURSE_INFO_FRAGMENT);
                        startIntent.putExtra(CourseDetailsTabActivity.TITLES, Const.COURSE_INFO_TITLE);
                        startIntent.putExtra(
                                CourseDetailsTabActivity.FRAGMENT, getFragmetName(view.getId()));

                    }
                });
            }
        };

        mCourseTeacherView.setOnClickListener(clickListener);
        mCourseAboutView.setOnClickListener(clickListener);
        mCourseReviewView.setOnClickListener(clickListener);
    }
}
