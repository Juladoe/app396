package com.edusoho.kuozhi.ui.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.FavoriteCourseListAdapter;
import com.edusoho.kuozhi.core.listener.CoreEngineMsgCallback;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseResult;
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

public class FavoriteActivity extends BaseActivity {

    private ViewGroup mFavoirteContent;
    private View mMenuLoadBtn;
    private FavoriteCourseListAdapter mAdapter;
    public static final String REFRESH_DATA = "refresh_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_layout);
        initView();
        app.addMessageListener(REFRESH_DATA, new CoreEngineMsgCallback() {
            @Override
            public void invoke(MessageModel messageModel) {
                if (mAdapter != null) {
                    mAdapter.refreshItem(messageModel.arg, (Boolean)messageModel.obj);
                }
            }
        });
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, FavoriteActivity.class);
        context.startActivityForResult(intent, Const.FAVORITE_REQUEST);
    }

    private void initView() {
        setBackMode("收藏的课程", true, null);
        setMenu(R.layout.menu_load_btn, new MenuListener() {
            @Override
            public void bind(View menuView) {
                mMenuLoadBtn = menuView.findViewById(R.id.menu_load_btn);
                mMenuLoadBtn.setVisibility(View.GONE);
            }
        });
        mFavoirteContent = (ViewGroup) findViewById(R.id.favoirte_content);

        if (app.loginUser == null) {
            LoginActivity.startForResult(mActivity);
            return;
        }

        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        mFavoirteContent.addView(course_content);
        setPagerContent(mFavoirteContent, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    mMenuLoadBtn.setVisibility(View.VISIBLE);
                    setPagerContent(mFavoirteContent, false);
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
            case Const.NORMAL_RESULT_REFRESH:
                break;
        }
    }

    private void setPagerContent(ViewGroup parent, boolean showLoading)
    {
        loadCourse(0, false, showLoading);
    }

    private void loadCourse(int page, final boolean isAppend, final boolean showLoading)
    {
        final PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.course_liseview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        final ListView listView = pullToRefreshListView.getRefreshableView();

        StringBuffer param = new StringBuffer(Const.FAVORITES);
        param.append("?start=").append(page);

        String url = app.bindToken2Url(param.toString(), true);
        ajax(url, new ResultCallback(){
            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(mFavoirteContent, false);
                    }
                });
                findViewById(R.id.load_layout).setVisibility(View.GONE);
            }

            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                mMenuLoadBtn.setVisibility(View.GONE);
                findViewById(R.id.load_layout).setVisibility(View.GONE);
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>(){}.getType());

                if (result == null || result.data.size() == 0) {
                    if (isAppend) {
                        longToast("没有更多收藏课程!");
                        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        return;
                    }
                    showEmptyLayout("暂无收藏课程");
                    return;
                }

                if (!isAppend) {
                    mAdapter = new FavoriteCourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);
                    listView.setAdapter(mAdapter);

                    CourseListScrollListener listener = new CourseListScrollListener(null);
                    listView.setOnItemClickListener(listener);

                    pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            loadCourse(0, false, showLoading);
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            Integer startPage = (Integer) mFavoirteContent.getTag();
                            loadCourse(startPage, true, showLoading);
                        }
                    });

                } else {
                    mAdapter.addItem(result);
                }

                pullToRefreshListView.onRefreshComplete();
                int start = result.start + Const.LIMIT;
                if (start < result.total) {
                    mFavoirteContent.setTag(start);
                } else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                }
            }
        }, false);

    }
}
