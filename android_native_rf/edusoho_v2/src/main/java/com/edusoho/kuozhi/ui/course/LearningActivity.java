package com.edusoho.kuozhi.ui.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CoursePagerAdapter;
import com.edusoho.kuozhi.adapter.LearnCourseListAdapter;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.EdusohoListView;
import com.edusoho.kuozhi.view.OverScrollView;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.MoveListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

public class LearningActivity extends BaseActivity {

    private ViewPager content_pager;
    private RadioGroup head_radiogroup;
    public static final int LEARNING = 0;
    public static final int LEARNED = 1;
    private ArrayList<View> mViewList;
    private int mSelectType;
    private View mMenuLoadBtn;
    private LearnCourseListAdapter mAdapter;
    public static final String REFRESH_DATA = "learn_refresh_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.learn_layout);
        initView();
        app.addMessageListener(REFRESH_DATA, new CoreEngineMsgCallback() {
            @Override
            public void invoke(MessageModel messageModel) {
                if (mAdapter != null) {
                    mAdapter.refreshItem(0, (Boolean)messageModel.obj);
                }
            }
        });
    }

    public static void start(Activity context) {
        Intent intent = new Intent();
        intent.setClass(context, LearningActivity.class);
        context.startActivityForResult(intent, Const.LEARNING_REQUEST);
    }

    private void selectPage(int index)
    {
        ViewGroup parent = (ViewGroup) mViewList.get(index);
        String tag = parent.getTag().toString();
        if ("false".equals(tag)) {
            parent.setTag("true");
            setPagerContent(parent, false);
        }
    }

    private void loadCoursePager() {
        mViewList = new ArrayList<View>();
        ViewGroup pager = (ViewGroup)getLayoutInflater().inflate(R.layout.latest_course, null);
        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        pager.addView(course_content);
        mViewList.add(pager);

        pager = (ViewGroup)getLayoutInflater().inflate(R.layout.latest_course, null);
        course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        pager.addView(course_content);
        mViewList.add(pager);

        CoursePagerAdapter adapter = new CoursePagerAdapter(mViewList) {
            @Override
            public void onPageSelected(int index) {
                changeContentHead(index);
                selectPage(index);
            }
        };

        content_pager.setAdapter(adapter);
        content_pager.setOnPageChangeListener(adapter);
    }

    private void initView() {
        mSelectType = LEARNING;
        setBackMode("学习计划", true, null);
        setMenu(R.layout.menu_load_btn, new MenuListener() {
            @Override
            public void bind(View menuView) {
                mMenuLoadBtn = menuView.findViewById(R.id.menu_load_btn);
                mMenuLoadBtn.setVisibility(View.GONE);
            }
        });

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        head_radiogroup = (RadioGroup) findViewById(R.id.learn_head_radiogroup);
        content_pager = (ViewPager) findViewById(R.id.content_pager);

        head_radiogroup
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        loadCoursePager();
        changeContentHead(LEARNING);
    }

    private void changeContentHead(int index) {
        mSelectType = index;
        if (index > head_radiogroup.getChildCount()) {
            return;
        }
        RadioButton rb = (RadioButton) head_radiogroup.getChildAt(index);
        rb.setChecked(true);
        content_pager.setCurrentItem(index);
        if (content_pager.getCurrentItem() == index) {
            selectPage(index);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    mMenuLoadBtn.setVisibility(View.VISIBLE);
                    setPagerContent((ViewGroup) mViewList.get(mSelectType), false);
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
            case Const.NORMAL_RESULT_REFRESH:
                break;
        }
    }

    private void setPagerContent(ViewGroup parent, boolean showLoading) {
        loadCourse(parent, 0, false, showLoading);
    }

    private void loadCourse(
            final ViewGroup parent, int page, final boolean isAppend, final boolean showLoading) {

        final PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) parent.findViewById(R.id.course_liseview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        final ListView listView = pullToRefreshListView.getRefreshableView();

        StringBuffer param = new StringBuffer(mSelectType == LEARNING ? Const.LEARNING : Const.LEARNED);
        param.append("?start=").append(page);
        String url = app.bindToken2Url(param.toString(), true);

        ajax(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                //hide loading layout
                mMenuLoadBtn.setVisibility(View.GONE);
                parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
                final LearnCourseResult result = app.gson.fromJson(
                        object, new TypeToken<LearnCourseResult>() {
                }.getType());

                if (result == null || result.data.size() == 0) {
                    if (isAppend) {
                        longToast("没有更多收藏课程!");
                        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        return;
                    }
                    showEmptyLayout("暂无课程");
                    return;
                }

                if (!isAppend) {
                    mAdapter = new LearnCourseListAdapter(
                            mContext, result.data, R.layout.learn_list_item);

                    listView.setAdapter(mAdapter);
                    CourseListScrollListener listener = new CourseListScrollListener(null) {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int index, long arg3) {
                            final Course course = (Course) parent.getItemAtPosition(index);

                            EdusohoApp.app.mEngine.runNormalPluginForResult(
                                    "CourseInfoActivity", mActivity, Const.COURSEINFO_REQUEST, new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    startIntent.putExtra("courseId", course.id);
                                    startIntent.putExtra("largePicture", course.largePicture);
                                    startIntent.putExtra("courseTitle", course.title);
                                    startIntent.putExtra("currentPage", 0);
                                }
                            });
                        }
                    };
                    listView.setOnItemClickListener(listener);

                    pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            loadCourse(parent, 0, false, false);
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            Integer startPage = (Integer) parent.getTag();
                            loadCourse(parent, startPage, true, false);
                        }
                    });

                } else {
                    mAdapter.addItem(result.data);
                }

                pullToRefreshListView.onRefreshComplete();
                int start = result.start + Const.LIMIT;
                if (start < result.total) {
                    parent.setTag(start);
                } else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(parent, false);
                    }
                });
                parent.findViewById(R.id.load_layout).setVisibility(View.GONE);
            }
        }, showLoading);

    }
}
