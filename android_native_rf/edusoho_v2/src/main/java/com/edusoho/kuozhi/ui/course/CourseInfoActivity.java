package com.edusoho.kuozhi.ui.course;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseCommentListAdapter;
import com.edusoho.kuozhi.adapter.CourseLessonListAdapter;
import com.edusoho.kuozhi.adapter.CoursePagerAdapter;
import com.edusoho.kuozhi.adapter.LessonPinnedAdapter;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.entity.CourseInfoViewPagerItem;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseInfoResult;
import com.edusoho.kuozhi.model.LessonItem;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.ReviewResult;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.ui.common.AlipayActivity;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.lesson.CourseLessonActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoListView;
import com.edusoho.kuozhi.view.dialog.CommentPopupDialog;
import com.edusoho.kuozhi.view.EduSohoList;
import com.edusoho.kuozhi.view.dialog.ExitCoursePopupDialog;
import com.edusoho.kuozhi.view.dialog.PopupDialog;
import com.edusoho.handler.ProgressBarHandler;
import com.edusoho.listener.MoveListener;
import com.edusoho.listener.NormalCallback;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.hb.views.PinnedSectionListView;

/**
 * @author howzhi
 */
public class CourseInfoActivity extends BaseActivity {
    private ViewPager content_pager;
    private RadioGroup head_radiogroup;
    private Handler workHandler;
    private String mCourseId;
    //滑动页
    private HashMap<Integer, CourseInfoViewPagerItem> mPagerMap;
    private AQuery aq;
    private CourseInfoResult mCourseInfoResult;

    private TextView course_learn_btn;
    private Course mCourseInfo;
    private int mCurrentPage;

    private Handler UIRefreshHandler;

    private static final int lessons = 0;
    private static final int info = 1;
    private static final int comment = 2;
    private static final String VIEW_LESSON = "view_lesson";
    private static final int PAGER_LOAD_SUCCESS = 0001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_info);

        workHandler = new ProgressBarHandler(this);
        initView();

        app.addTask("CourseInfoActivity", this);
    }

    public static void start(Activity context) {
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
            public void onPageSelected(final int index) {
                changeContentHead(index);
                final View parent = mViewList.get(index);
                if (!mPagerMap.containsKey(index)) {
                    CourseInfoViewPagerItem pagerItem = new CourseInfoViewPagerItem();
                    pagerItem.pager = parent;
                    pagerItem.data = mCourseInfoResult;
                    mPagerMap.put(index, pagerItem);
                }

                final View load_layout;
                switch (index) {
                    case lessons:
                        load_layout = parent.findViewById(R.id.load_layout);
                        UIRefreshHandler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                loadLessonLayout(mCourseInfoResult);
                                load_layout.setVisibility(View.GONE);
                            }
                        }, android.os.SystemClock.uptimeMillis() + 500);
                        break;
                    case info:
                        load_layout = parent.findViewById(R.id.load_layout);
                        UIRefreshHandler.postAtTime(new Runnable() {
                            @Override
                            public void run() {
                                loadCourseInfoLayout(mCourseInfoResult);
                                load_layout.setVisibility(View.GONE);
                            }
                        }, android.os.SystemClock.uptimeMillis() + 500);
                        break;
                    case comment:
                        loadCommentLayout(mCourseInfoResult);
                        break;
                }
            }
        };
        content_pager.setAdapter(adapter);
        content_pager.setOnPageChangeListener(adapter);
    }

    private void refresh() {
        getCourseInfoFromNet(mCourseId, new NormalCallback<CourseInfoResult>() {
            @Override
            public void success(CourseInfoResult result) {
                mCourseInfoResult = result;
                mFavoriteStatus = result.userFavorited;
                mIsStudent = result.userIsStudent;
                setPagerData(mCourseInfoResult);
                loadCourseInfoLayout(result);
                loadCommentLayout(result);
                loadLessonLayout(result);
            }
        }, false);
    }

    /**
     * @param courseInfoResult
     */
    private void setPagerData(CourseInfoResult courseInfoResult) {
        for (Integer index : mPagerMap.keySet()) {
            CourseInfoViewPagerItem pagerItem = mPagerMap.get(index);
            pagerItem.data = courseInfoResult;
        }
    }

    /**
     * 退出课程
     *
     * @param courseId
     */
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
                                    mCourseInfoResult.userIsStudent = false;
                                    setPagerData(mCourseInfoResult);
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
        if (app.loginUser != null && mIsStudent) {
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
        CourseInfoViewPagerItem pagerItem = mPagerMap.get(info);
        if (pagerItem == null || pagerItem.data == null) {
            return;
        }

        AQuery aquery = new AQuery(pagerItem.pager);
        mCourseInfo = result.course;
        Teacher teacher = null;
        if (result.course.teachers !=null && result.course.teachers.length > 0) {
            teacher = result.course.teachers[0];
        }

        aquery.id(R.id.course_info_title).text(mCourseInfo.title);
        aquery.id(R.id.course_rating).rating((float) mCourseInfo.rating);

        if ("opened".equals(mCourseInfo.showStudentNumType)) {
            aquery.id(R.id.course_student_num).text("学员数:" + mCourseInfo.studentNum);
        }

        aquery.id(R.id.course_free).text(mCourseInfo.price > 0 ? mCourseInfo.price + "元" : "免费");

        course_learn_btn = (TextView) pagerItem.pager.findViewById(R.id.course_learn_btn);

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
            aquery.id(R.id.course_about_content).text(
                    AppUtil.coverCourseAbout(mCourseInfo.about));
        }

        if (mCourseInfo.goals.length == 0) {
            aquery.id(R.id.course_goals_layout).visibility(View.GONE);
        } else {
            aquery.id(R.id.course_goals_content).text(
                    AppUtil.goalsToStr(mCourseInfo.goals));
        }

        if (mCourseInfo.audiences.length == 0) {
            aquery.id(R.id.course_audiences_layout).visibility(View.GONE);
        } else {
            aquery.id(R.id.course_audiences_content).text(
                    AppUtil.audiencesToStr(mCourseInfo.audiences));
        }

        aquery.id(R.id.course_info_layout).visible();
        pagerItem.clear();
    }

    private void joinLearnCourse(Course courseInfo) {
        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        String message = courseInfo.price <= 0
                ? "此课程为免费课程，无需购买，可直接加入学习"
                : "课程价格:" + courseInfo.price + "元\r\n支付方式：支付宝";

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
                        mCourseInfoResult.userIsStudent = true;
                        setPagerData(mCourseInfoResult);
                        setLearnBtnStatus(true, mCourseInfo);
                        content_pager.setCurrentItem(lessons);
                        app.sendMessage(VIEW_LESSON, null);
                        app.delMessageListener(VIEW_LESSON);
                    } else {
                        String payurl = result.get("payUrl");
                        AlipayActivity.startForResult(mActivity, payurl);
                    }

                } else if (Const.RESULT_ERROR.equals(status)) {
                    String message = result.get("message");

                    PopupDialog.createNormal(
                            mContext,
                            "课程提示",
                            message != null ? message : "加入课程失败,请联系网站管理员!"
                    ).show();
                } else {
                    longToast("加入课程失败,请联系网站管理员!");
                }
            }
        });
    }

    public void hideEmptyLayout(View inflated) {
        View emptyLayout = inflated.findViewById(R.id.list_empty_text);
        if (emptyLayout != null) {
            emptyLayout.setVisibility(View.GONE);
        }
    }

    private void showEmptyLayout(View inflated, final String text) {
        ViewStub emptyLayout = (ViewStub) inflated.findViewById(R.id.list_empty_stub);
        if (emptyLayout == null) {
            return;
        }
        emptyLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView emptyText = (TextView) view.findViewById(R.id.list_empty_text);
                emptyText.setText(text);
            }
        });
        emptyLayout.inflate();
    }

    private void setLessonItemData(PinnedSectionListView listView, CourseInfoResult result) {
        LessonPinnedAdapter adapter = new LessonPinnedAdapter(
                mContext,
                result.items,
                result.userLearns,
                R.layout.course_lesson_item_list_item
        );

        listView.setAdapter(adapter);
    }

    private PinnedSectionListView mLessonListView;

    /**
     * @param result
     */
    private void loadLessonLayout(CourseInfoResult result) {
        CourseInfoViewPagerItem pagerItem = mPagerMap.get(lessons);
        if (pagerItem == null || pagerItem.data == null) {
            return;
        }

        if (result.items.size() == 0) {
            showEmptyLayout(pagerItem.pager, "课程暂无课时内容");
            return;
        }

        mLessonListView = (PinnedSectionListView) pagerItem.pager.findViewById(R.id.course_lesson_listview);
        setLessonItemData(mLessonListView, result);

        mLessonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final LessonItem lesson = (LessonItem) adapterView.getItemAtPosition(i);
                if (app.loginUser == null || !(mIsStudent || mIsTeacher)) {
                    app.addMessageListener(VIEW_LESSON, new CoreEngineMsgCallback() {
                        @Override
                        public void invoke(MessageModel obj) {
                            viewLessonByLesson(lesson);
                        }
                    });
                    joinLearnCourse(mCourseInfo);
                    return;
                }

                if (!"lesson".equals(lesson.itemType)) {
                    return;
                }
                viewLessonByLesson(lesson);
            }
        });

        pagerItem.clear();
    }

    private void viewLessonByLesson(LessonItem lesson) {
        Intent lessonIntent = new Intent();
        lessonIntent.setClass(mContext, CourseLessonActivity.class);
        lessonIntent.putExtra("courseId", lesson.courseId);
        lessonIntent.putExtra("lessonId", lesson.id);
        lessonIntent.putExtra("lessonTitle", lesson.title);
        startActivityForResult(lessonIntent, Const.COURSELESSON_REQUEST);
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

    public void showCommentDlg(Review loginUserComment, final View parent) {
        CommentPopupDialog dlg = CommentPopupDialog.create(mContext);
        dlg.showDlg(loginUserComment, new CommentPopupDialog.PopupClickListener() {
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

    private void loadCommentStatus(final View parent) {
        View commentBtn = parent.findViewById(R.id.course_comment_btn);
        if (app.loginUser != null && (mIsStudent || mIsTeacher)) {
            commentBtn.setVisibility(View.VISIBLE);
            commentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCommentDlg(cllAdapter == null ? null : cllAdapter.loginUserComment, parent);
                }
            });
        } else {
            commentBtn.setVisibility(View.GONE);
        }
    }

    private void loadCommentLayout(CourseInfoResult result) {
        final CourseInfoViewPagerItem pagerItem = mPagerMap.get(comment);
        if (pagerItem == null || pagerItem.data == null) {
            return;
        }

        listCommentView = (EduSohoList) pagerItem.pager.findViewById(R.id.course_comment_listview);
        getComments(pagerItem.pager, 0, false, true);
        pagerItem.clear();
    }

    public void addComment(String message, float rating, final NormalCallback callBack) {
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
                    CourseInfoViewPagerItem pagerItem = mPagerMap.get(comment);
                    pagerItem.data = review;
                    loadCommentLayout(mCourseInfoResult);
                    longToast("评论成功!");
                    callBack.success(null);
                } else {
                    longToast("评论失败!");
                }
            }
        });
    }

    private void getComments(
            final View parent, int start, final boolean isAppend, final boolean showLoading) {

        StringBuilder params = new StringBuilder(wrapUrl(Const.COMMENTLIST, mCourseId));
        params.append("?start=").append(start);
        if (showLoading) {
            View load_layout = parent.findViewById(R.id.load_layout);
            load_layout.setVisibility(View.VISIBLE);
        }

        String url = app.bindToken2Url(params.toString(), true);
        ajax(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                loadCommentStatus(parent);
                //hide loading layout
                if (showLoading) {
                    parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
                }
                final ReviewResult result = app.gson.fromJson(
                        object, new TypeToken<ReviewResult>() {
                }.getType());

                if (result == null || result.data.length == 0) {
                    showEmptyLayout("课程暂无评价内容");
                    return;
                }
                hideEmptyLayout(parent);

                if (!isAppend) {
                    cllAdapter = new CourseCommentListAdapter(
                            mContext,
                            result.data,
                            R.layout.course_comment_item_list_item
                    );

                    listCommentView.setAdapter(cllAdapter);

                    listCommentView.setMoveListener(new MoveListener() {
                        @Override
                        public void moveToBottom() {
                            if (listCommentView.isShowMoreBtn()) {
                                return;
                            }

                            int startPage = parent.getTag() == null ? 0 : (Integer) parent.getTag();
                            if (startPage > result.total) {
                                return;
                            }

                            listCommentView.showMoreBtn();
                            getComments(parent, startPage, true, false);
                        }
                    });

                } else {
                    cllAdapter = (CourseCommentListAdapter) listCommentView.getAdapter();
                    cllAdapter.addItem(result.data);
                }

                int start = result.start + Const.LIMIT;
                parent.setTag(start);

                listCommentView.hideMoreBtn();
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                longToast("网络数据加载错误！请重新尝试刷新");
                findViewById(R.id.load_layout).setVisibility(View.GONE);
            }
        }, false);

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
                    favoriteBtn.setEnabled(true);
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
                    favoriteBtn.setEnabled(true);
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
        mPagerMap = new HashMap<Integer, CourseInfoViewPagerItem>();
        Intent dataIntent = getIntent();
        if (!dataIntent.hasExtra("courseId")) {
            return;
        }

        UIRefreshHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PAGER_LOAD_SUCCESS:
                        break;
                }
            }
        };

        mCurrentPage = dataIntent.getIntExtra("currentPage", 1);
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
                        favoriteBtn.setEnabled(false);
                        favoriteCourse();
                    }
                });
            }
        });

        aq = new AQuery(this);
        String largePic = dataIntent.getStringExtra("largePicture");
        aq.id(R.id.courseInfo_pic).image(
                largePic, false, true, (int) (app.screenW * 0.9f), R.drawable.noram_course);

        aq.id(R.id.courseInfo_pic).height(AppUtil.getCourseCorverHeight(app.screenW), false);

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
                changeContentHead(info);
                if (mCurrentPage != info) {
                    changeContentHead(mCurrentPage);
                }
            }
        }, true);
    }

    private void getCourseInfoFromNet(
            String courseId, final NormalCallback<CourseInfoResult> callback, boolean showLoading) {
        String url = app.bindToken2Url(Const.COURSE + courseId + "?", true);
        ajax(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                CourseInfoResult result = app.gson.fromJson(
                        object, new TypeToken<CourseInfoResult>() {
                }.getType());
                if (result != null) {
                    callback.success(result);
                }
            }
        }, showLoading);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Create the search view
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        if (mCourseInfo != null) {
            //app.sendMessage(FavoriteActivity.REFRESH_DATA, new MessageModel(mCourseInfo.id, mFavoriteStatus));
            //app.sendMessage(LearningActivity.REFRESH_DATA, new MessageModel(mCourseInfo.id, mIsStudent));
        }
        app.delMessageListener(VIEW_LESSON);
        super.onDestroy();
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
                }, false);
                break;
            case AlipayActivity.ALIPAY_SUCCESS:
            case AlipayActivity.ALIPAY_EXIT:
                content_pager.setCurrentItem(lessons);
                showPayResultDlg(resultCode);
                break;
        }

    }

    /**
     * @param resultCode
     */
    private void showPayResultDlg(final int resultCode) {
        //支付成功
        PopupDialog.createMuilt(
                mContext, "支付完成", "确定支付是否成功?", new PopupDialog.PopupClickListener() {
            @Override
            public void onClick(int button) {
                if (resultCode == AlipayActivity.ALIPAY_SUCCESS) {
                    mIsStudent = true;
                    mCourseInfoResult.userIsStudent = true;
                    setPagerData(mCourseInfoResult);
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
                            mCourseInfoResult.userIsStudent = true;
                            setPagerData(mCourseInfoResult);
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
