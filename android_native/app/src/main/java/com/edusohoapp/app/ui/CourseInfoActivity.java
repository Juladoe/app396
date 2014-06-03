package com.edusohoapp.app.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewStub.OnInflateListener;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.CourseCommentListAdapter;
import com.edusohoapp.app.adapter.CourseLessonListAdapter;
import com.edusohoapp.app.adapter.CoursePagerAdapter;
import com.edusohoapp.app.entity.CommentResult;
import com.edusohoapp.app.model.Course;
import com.edusohoapp.app.model.CourseInfoResult;
import com.edusohoapp.app.model.LessonItem;
import com.edusohoapp.app.model.Review;
import com.edusohoapp.app.model.Teacher;
import com.edusohoapp.app.util.AppUtil;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.CommentPopupDialog;
import com.edusohoapp.app.view.EduSohoList;
import com.edusohoapp.app.view.ExitCoursePopupDialog;
import com.edusohoapp.app.view.PopupDialog;
import com.edusohoapp.handler.ProgressBarHandler;
import com.edusohoapp.listener.NormalCallback;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

/**
 * @author howzhi
 */
public class CourseInfoActivity extends BaseActivity {
    private ViewPager content_pager;
    private RadioGroup head_radiogroup;
    private Handler workHandler;
    private String mCourseId;
    //滑动页
    private HashMap<String, View> mPagerMap;
    private AQuery aq;
    private CourseInfoResult mCourseInfoResult;

    private TextView course_learn_btn;
    private Course mCourseInfo;

    private static final int popular = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_info);

        workHandler = new ProgressBarHandler(this);
        initView();

        app.addTask("CourseInfoActivity", this);
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, CourseInfoActivity.class);
        context.startActivityForResult(intent, Const.COURSEINFO_REQUEST);
    }

    /**
     * 载入课程页面
     */
    private void loadCourseInfoPager() {
        final ArrayList<View> mViewList = new ArrayList<View>();
        View pager = getLayoutInflater().inflate(R.layout.course_lesson_item, null);
        mViewList.add(pager);

        pager = getLayoutInflater().inflate(R.layout.course_info_item, null);
        mViewList.add(pager);

        pager = getLayoutInflater().inflate(R.layout.course_comment_item, null);
        mViewList.add(pager);

        CoursePagerAdapter adapter = new CoursePagerAdapter(mViewList) {
            @Override
            public void onPageSelected(int index) {
                changeContentHead(index);
                View parent = mViewList.get(index);
                ViewStub vStub = (ViewStub) parent
                        .findViewById(R.id.course_info_content_vs);
                if (vStub != null) {
                    vStub.setOnInflateListener(new OnInflateListener() {
                        @Override
                        public void onInflate(ViewStub stub, View inflated) {

                            switch (stub.getLayoutResource()) {
                                //页面详情
                                case R.layout.course_info_item_content:
                                    mPagerMap.put("info", inflated);
                                    loadCourseInfoLayout(mCourseInfoResult);
                                    break;
                                case R.layout.course_comment_item_content:
                                    mPagerMap.put("comment", inflated);
                                    loadCommentLayout(mCourseInfoResult);
                                    break;
                                case R.layout.course_lesson_item_content:
                                    mPagerMap.put("lesson", inflated);
                                    loadLessonLayout(mCourseInfoResult);
                                    break;
                            }

                        }
                    });
                    vStub.inflate();
                }
                switch (index) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        loadCommentStatus(parent);
                        break;
                }
            }
        };
        content_pager.setAdapter(adapter);
        content_pager.setOnPageChangeListener(adapter);
    }

    private void refresh()
    {
        getCourseInfoFromNet(mCourseId, new NormalCallback<CourseInfoResult>() {
            @Override
            public void success(CourseInfoResult result) {
                mCourseInfoResult = result;
                mFavoriteStatus = result.userFavorited;
                mIsStudent = result.userIsStudent;
                loadCourseInfoLayout(result);
                loadCommentLayout(result);
                loadLessonLayout(result);
            }
        });
    }

    private void exitCourse(final String courseId) {
        ExitCoursePopupDialog.create(
                mContext, new ExitCoursePopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button, String selStr) {
                if (button == ExitCoursePopupDialog.OK) {
                    StringBuilder param = new StringBuilder(wrapUrl(Const.REFUNDCOURSE, courseId));
                    param.append("?reason=").append(selStr);

                    String url = app.bindToken2Url(param.toString(), true);
                    ajaxGetString(url, new ResultCallback() {
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            HashMap<String, String> result = app.gson.fromJson(
                                    object, new TypeToken<HashMap<String, String>>() {
                            }.getType());

                            if (result != null) {
                                String status = result.get("status");
                                if (Const.RESULT_SUCCESS.equals(status)) {
                                    mIsStudent = false;
                                    setLearnBtnStatus(false, mCourseInfo);
                                } else {
                                    longToast("退出学习失败");
                                }
                            }
                        }
                    });
                }
                ;
            }
        }).show();
    }

    private void setLearnBtnStatus(boolean isStudent, Course course) {
        if (mIsStudent) {
            course_learn_btn.setText("退出学习");
            course_learn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    exitCourse(mCourseId);
                }
            });

        } else {
            course_learn_btn.setText(
                    course.price > 0 ? "购买课程" : "加入学习"
            );
            course_learn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    joinLearnCourse(mCourseInfo);
                }
            });
        }
    }

    /**
     * @param teachers
     * @return
     */
    private boolean isCourseTeacher(Teacher[] teachers) {
        if (app.loginUser == null) {
            return false;
        }
        for (Teacher teacher : teachers) {
            if (teacher.id == app.loginUser.id) {
                return true;
            }
        }
        return false;
    }

    private boolean mIsTeacher = false;

    /**
     * @param result
     */
    private void loadCourseInfoLayout(CourseInfoResult result) {
        View inflated = mPagerMap.get("info");
        if (inflated == null) {
            return;
        }
        AQuery aquery = new AQuery(inflated);
        mCourseInfo = result.course;
        Teacher teacher = result.course.teachers[0];
        aquery.id(R.id.course_info_title).text(mCourseInfo.title);
        aquery.id(R.id.course_rating).rating((float) mCourseInfo.rating);

        aquery.id(R.id.course_student_num).text("学员数:" + mCourseInfo.studentNum);

        aquery.id(R.id.course_free).text(mCourseInfo.price > 0 ? mCourseInfo.price + "元" : "免费");

        course_learn_btn = (TextView) inflated.findViewById(R.id.course_learn_btn);

        if (mFavoriteStatus) {
            favoriteBtn.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
        }

        mIsTeacher = isCourseTeacher(result.course.teachers);
        if (mIsTeacher) {
            course_learn_btn.setVisibility(View.GONE);
        } else {
            setLearnBtnStatus(mIsStudent, mCourseInfo);
        }

        if (teacher != null) {
            aquery.id(R.id.course_teacher).text("教师:" + teacher.nickname);
        }

        if (TextUtils.isEmpty(mCourseInfo.about)) {
            aquery.id(R.id.course_about_layout).visibility(View.GONE);
        } else {
            aquery.id(R.id.course_about_content).text(AppUtil.coverCourseAbout(mCourseInfo.about));
        }

        if (mCourseInfo.goals.length == 0) {
            aquery.id(R.id.course_goals_layout).visibility(View.GONE);
        } else {
            aquery.id(R.id.course_goals_content).text(AppUtil.goalsToStr(mCourseInfo.goals));
        }

        if (mCourseInfo.audiences.length == 0) {
            aquery.id(R.id.course_audiences_layout).visibility(View.GONE);
        } else {
            aquery.id(R.id.course_audiences_content).text(AppUtil.audiencesToStr(mCourseInfo.audiences));
        }

    }

    private void joinLearnCourse(Course courseInfo) {
        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        String message = courseInfo.price <= 0
                ? "此课程为免费课程，无需购买，可直接加入学习"
                : "课程价格:" + courseInfo.price + " \r\n 支付方式：支付宝";

        PopupDialog.createMuilt(
                mContext, "加入学习",
                message,
                new PopupDialog.PopupClickListener() {
                    @Override
                    public void onClick(int button) {
                        if (button == PopupDialog.OK) {
                            buyCourse(mCourseInfo);
                        }
                    }
                }).show();
    }

    private void buyCourse(Course course) {
        StringBuilder param = new StringBuilder(wrapUrl(Const.PAYCOURSE, course.id + ""));
        param.append("?payment=alipay");

        String url = app.bindToken2Url(param.toString(), true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                HashMap<String, String> result = app.gson.fromJson(
                        object, new TypeToken<HashMap<String, String>>() {
                }.getType());

                String status = result.get("status");
                if (Const.RESULT_OK.equals(status)) {
                    String paid = result.get("paid");
                    if ("true".equals(paid)) {
                        mIsStudent = true;
                        setLearnBtnStatus(true, mCourseInfo);
                    } else {
                        String payurl = result.get("payUrl");
                        AlipayActivity.startForResult(mActivity, payurl);
                    }

                } else {
                    longToast("加入课程失败");
                }
            }
        });
    }

    private void hideEmptyLayout(View inflated) {
        View emptyLayout = inflated.findViewById(R.id.list_empty_text);
        if (emptyLayout != null) {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyLayout(View inflated, final String text) {
        ViewStub emptyLayout = (ViewStub) inflated.findViewById(R.id.list_empty_layout);
        if (emptyLayout != null) {
            emptyLayout.setOnInflateListener(new OnInflateListener() {
                @Override
                public void onInflate(ViewStub viewStub, View view) {
                    TextView emptyText = (TextView) view;
                    emptyText.setText(text);
                }
            });
            emptyLayout.inflate();
        }
    }

    private void setLessonItemData(EduSohoList listView, CourseInfoResult result)
    {
        CourseLessonListAdapter adapter = new CourseLessonListAdapter(
                mContext,
                result.items,
                result.userLearns,
                R.layout.course_lesson_item_list_item
        );

        listView.setAdapter(adapter);
    }

    private EduSohoList mLessonListView;
    /**
     *
     * @param result
     */
    private void loadLessonLayout(CourseInfoResult result) {
        View inflated = mPagerMap.get("lesson");
        if (inflated == null) {
            return;
        }
        if (result.items.size() == 0) {
            showEmptyLayout(inflated, "课程暂无课时内容");
            return;
        }

        mLessonListView = (EduSohoList) inflated.findViewById(R.id.course_lesson_listview);
        setLessonItemData(mLessonListView, result);

        mLessonListView.setOnItemClickListener(new EduSohoList.EduSohoItemClickListener() {
            @Override
            public void onItemClick(Object item, int index, View view) {

                if (app.loginUser == null || !(mIsStudent || mIsTeacher)) {
                    PopupDialog.createNormal(mContext, "课程提示", "你不是该课程学员或教师").show();
                    return;
                }
                LessonItem lesson = (LessonItem) item;
                if (!"lesson".equals(lesson.itemType)) {
                    return;
                }
                Intent lessonIntent = new Intent();
                lessonIntent.setClass(mContext, CourseLessonActivity.class);
                lessonIntent.putExtra("courseId", lesson.courseId);
                lessonIntent.putExtra("lessonId", lesson.id);
                lessonIntent.putExtra("lessonTitle", lesson.title);
                startActivityForResult(lessonIntent, Const.COURSELESSON_REQUEST);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(Const.NORMAL_RESULT_REFRESH);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private CourseCommentListAdapter cllAdapter;
    private EduSohoList listCommentView;

    private void loadCommentStatus(final View parent) {
        if (cllAdapter.getCount() == 0) {
            showEmptyLayout(parent, "课程暂无评价内容");
        } else {
            hideEmptyLayout(parent);
        }
        View commentBtn = parent.findViewById(R.id.course_comment_btn);
        if (app.loginUser != null && (mIsStudent || mIsTeacher)) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommentPopupDialog dlg = CommentPopupDialog.create(mContext);
                    dlg.showDlg(cllAdapter.loginUserComment, new CommentPopupDialog.PopupClickListener() {
                        @Override
                        public void onClick(int button, String message, float rating) {
                            switch (button) {
                                case CommentPopupDialog.OK:
                                    addComment(message, rating, new NormalCallback() {
                                        @Override
                                        public void success(Object obj) {
                                            hideEmptyLayout(parent);
                                        }
                                    });
                                    break;
                            }
                        }
                    });
                }
            });
        } else {
            commentBtn.setVisibility(View.GONE);
        }
    }

    /**
     * @param result
     */
    private void loadCommentLayout(CourseInfoResult result) {
        View inflated = mPagerMap.get("comment");
        if (inflated == null) {
            return;
        }
        listCommentView = (EduSohoList) inflated.findViewById(R.id.course_comment_listview);
        cllAdapter = new CourseCommentListAdapter(
                mContext,
                result.reviews,
                R.layout.course_comment_item_list_item
        );

        listCommentView.setAdapter(cllAdapter);
    }

    private int viewPageHeight = 0;

    private void setViewPagerHeight(View inflated) {
        inflated.measure(0, 0);
        ViewGroup.LayoutParams lp = content_pager.getLayoutParams();
        int height = inflated.getMeasuredHeight();
        if (height > viewPageHeight) {
            viewPageHeight = height;
            lp.height = viewPageHeight;
            content_pager.setLayoutParams(lp);
        }
    }

    private void addComment(String message, float rating, final NormalCallback callBack) {
        StringBuilder params = new StringBuilder(wrapUrl(Const.ADDCOMMENT, mCourseId));
        params.append("?rating=").append(rating);
        params.append("&content=").append(message);

        String url = app.bindToken2Url(params.toString(), true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                Review review = app.gson.fromJson(
                        object, new TypeToken<Review>() {
                }.getType());
                if (review != null) {
                    cllAdapter.setLoginUserComment(review);
                    longToast("评论成功!");
                    callBack.success(null);
                } else {
                    longToast("评论失败!");
                }
            }
        });
    }

    private void getComments() {
        StringBuilder params = new StringBuilder(Const.COMMENTLIST);
        params.append(mCourseId);

        String url = app.bindToken2Url(params.toString(), true);
        ajaxNormalGet(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                if (status.getCode() != Const.OK) {
                    longToast("网络异常！");
                    return;
                }
                CommentResult result = app.gson.fromJson(
                        object, new TypeToken<CommentResult>() {
                }.getType());
                if (result != null && Const.RESULT_SUCCESS.equals(result.status)) {
                    cllAdapter.setData(null);
                    listCommentView.refresh();
                } else {
                    longToast("评论提示,课程不存在或已关闭!");
                }
            }
        });
    }

    /**
     * @param index
     */
    private void changeContentHead(int index) {
        if (index > head_radiogroup.getChildCount()) {
            return;
        }
        RadioButton rb = (RadioButton) head_radiogroup.getChildAt(index);
        rb.setChecked(true);
        content_pager.setCurrentItem(index);
    }

    protected TextView favoriteBtn;

    private void favoriteCourse() {
        StringBuilder param = new StringBuilder();
        if (mFavoriteStatus) {
            param.append(wrapUrl(Const.UNFAVORITE, mCourseId));

            String url = app.bindToken2Url(param.toString(), true);

            ajaxNormalGet(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {

                    if ("true".equals(object)) {
                        longToast("取消收藏成功！");
                        favoriteBtn.setTextColor(getResources().getColor(R.color.nav_btn_normal));
                        mFavoriteStatus = !mFavoriteStatus;
                    }
                }
            });
        } else {
            param.append(wrapUrl(Const.FAVORITE, mCourseId));
            String url = app.bindToken2Url(param.toString(), true);
            ajaxNormalGet(url, new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus status) {
                    if ("true".equals(object)) {
                        longToast("收藏成功！");
                        favoriteBtn.setTextColor(getResources().getColor(R.color.nav_btn_pressed));
                        mFavoriteStatus = !mFavoriteStatus;
                    }
                }
            });
        }
    }

    /**
     *
     */
    private void initView() {
        mPagerMap = new HashMap<String, View>();
        Intent dataIntent = getIntent();
        if (!dataIntent.hasExtra("courseId")) {
            return;
        }

        mCourseId = dataIntent.getStringExtra("courseId");
        setBackMode(dataIntent.getStringExtra("courseTitle"), true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Const.NORMAL_RESULT_REFRESH);
                finish();
            }
        });
        setMenu(R.layout.courseinfo_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                favoriteBtn = (TextView) menuView.findViewById(R.id.sch_favorite_btn);
                //收藏
                favoriteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (app.loginUser == null) {
                            LoginActivity.start(mActivity);
                            return;
                        }
                        favoriteCourse();
                    }
                });
            }
        });

        aq = new AQuery(this);
        String largePic = dataIntent.getStringExtra("largePicture");
        if (TextUtils.isEmpty(largePic)) {
            aq.id(R.id.courseInfo_pic).image(R.drawable.noram_course);
        } else {
            aq.id(R.id.courseInfo_pic).image(dataIntent.getStringExtra("largePicture"), false, true);
        }

        head_radiogroup = (RadioGroup) findViewById(R.id.head_radiogroup);
        head_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int count = group.getChildCount();
                for (int i = 0; i < count; i++) {
                    RadioButton rb = (RadioButton) group.getChildAt(i);
                    if (rb.getId() == checkedId) {
                        changeContentHead(i);
                        break;
                    }
                }
            }
        });
        content_pager = (ViewPager) findViewById(R.id.content_pager);

        loadCourseInfo(mCourseId);
    }

    protected boolean mFavoriteStatus;
    protected boolean mIsStudent;

    private void loadCourseInfo(String courseId) {
        getCourseInfoFromNet(courseId, new NormalCallback<CourseInfoResult>() {
            @Override
            public void success(CourseInfoResult result) {
                mFavoriteStatus = result.userFavorited;
                mIsStudent = result.userIsStudent;
                mCourseInfoResult = result;
                loadCourseInfoPager();
                changeContentHead(popular);
            }
        });
    }

    private void getCourseInfoFromNet(String courseId, final NormalCallback<CourseInfoResult> callback)
    {
        String url = app.bindToken2Url(Const.COURSE + courseId + "?", true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                CourseInfoResult result = app.gson.fromJson(
                        object, new TypeToken<CourseInfoResult>() {
                }.getType());
                if (result != null) {
                    callback.success(result);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the search view
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {

        switch (resultCode) {
            case LoginActivity.OK:
                refresh();
                break;
            case Const.NORMAL_RESULT_REFRESH:
                getCourseInfoFromNet(mCourseId, new NormalCallback<CourseInfoResult>() {
                    @Override
                    public void success(CourseInfoResult result) {
                        setLessonItemData(mLessonListView, result);
                    }
                });
                break;
            case AlipayActivity.ALIPAY_SUCCESS:
            case AlipayActivity.ALIPAY_EXIT:
                showPayResultDlg(resultCode);
                break;
        }

    }

    /**
     *
     * @param resultCode
     */
    private void showPayResultDlg(final int resultCode)
    {
        //支付成功
        PopupDialog.createMuilt(
                mContext, "支付完成", "确定支付是否成功?", new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (resultCode == AlipayActivity.ALIPAY_SUCCESS) {
                    mIsStudent = true;
                    setLearnBtnStatus(true, mCourseInfo);
                    longToast("支付成功");
                    return;
                }
                String url = app.bindToken2Url(
                        wrapUrl(Const.CHECKORDER, mCourseId), true);
                ajaxGetString(url, new ResultCallback() {
                    @Override
                    public void callback(String url, String object, AjaxStatus ajaxStatus) {
                        HashMap<String, String> result = app.gson.fromJson(
                                object, new TypeToken<HashMap<String, String>>() {
                        }.getType());

                        String status = result.get("status");
                        if (Const.RESULT_OK.equals(status)) {
                            mIsStudent = true;
                            setLearnBtnStatus(true, mCourseInfo);
                            longToast("支付成功");
                        } else {
                            longToast(result.get("message"));
                        }
                    }
                });
            }
        }).show();
    }
}
