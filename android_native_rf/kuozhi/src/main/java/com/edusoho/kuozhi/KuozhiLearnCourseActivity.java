package com.edusoho.kuozhi;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.adapter.CoursePagerAdapter;
import com.edusoho.kuozhi.adapter.LearnCourseListAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.LearnCourseResult;
import com.edusoho.kuozhi.ui.course.LearningActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-13.
 */
public class KuozhiLearnCourseActivity extends LearningActivity {

    @Override
    protected void hideTabBar() {
        head_radiogroup.setVisibility(View.GONE);
    }

    @Override
    protected void loadCoursePager() {
        mViewList = new ArrayList<View>();
        ViewGroup pager = (ViewGroup)getLayoutInflater().inflate(R.layout.latest_course, null);
        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
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

    @Override
    protected void loadCourse(
            final ViewGroup parent, int page, final boolean isAppend, final boolean showLoading) {

        final PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) parent.findViewById(R.id.course_liseview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        final ListView listView = pullToRefreshListView.getRefreshableView();

        StringBuffer param = new StringBuffer(Const.LEARNING);
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
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity) {
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
