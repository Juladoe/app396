package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseListAdapter;
import com.edusoho.kuozhi.model.CourseResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.CourseListScrollListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends BaseActivity {

    private AQuery aq;
    private View load_layout;
    private AutoCompleteTextView actionbar_search_edt;
    private PullToRefreshListView pullToRefreshListView;
    private CourseListAdapter mAdapter;
    private ViewGroup mSearchContent;

    private static final String SEARCH_HISTORY = "search";
    private ArrayList<String> mSearchList;
    protected int mPageLimit = 10;
    protected int mPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        initParams();
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        context.startActivity(intent);
    }

    protected void initParams(){}

    private void initView() {
        aq = new AQuery(this);
        mSearchContent = (ViewGroup) findViewById(R.id.search_content);
        actionbar_search_edt = (AutoCompleteTextView) findViewById(R.id.actionbar_search_edt);

        aq.id(R.id.actionbar_back).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        actionbar_search_edt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                searchCourseList();
                return true;
            }
        });

        aq.id(R.id.actionbar_search_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchCourseList();
            }
        });

        loadSearchHistory();
    }

    private void searchCourseList()
    {
        String searchStr = actionbar_search_edt.getText().toString();
        if (TextUtils.isEmpty(searchStr)) {
            longToast("请输入搜索内容!");
            return;
        }
        mSearchContent.removeAllViews();
        View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
        mSearchContent.addView(course_content);

        pullToRefreshListView = (PullToRefreshListView) course_content.findViewById(R.id.course_liseview);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);

        loadSearchList(mPage, searchStr, false);
        saveSearchHistory(searchStr);
    }

    private void saveSearchHistory(String text)
    {
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        Map<String, ?> allHistory = sp.getAll();
        int size = allHistory != null ? allHistory.size() : 0;
        editor.putString(size + "", text);
        editor.commit();
    }

    private void loadSearchHistory()
    {
        mSearchList = new ArrayList<String>();
        SharedPreferences sp = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE);
        Map<String, ?> schools = sp.getAll();
        for (String key : schools.keySet()) {
            mSearchList.add(schools.get(key).toString());
        }

        ArrayAdapter adapter = new ArrayAdapter(
                mContext, R.layout.search_dropdown_item, mSearchList);

        actionbar_search_edt.setAdapter(adapter);
    }

    public StringBuffer getSearchUrl(int start, String searchStr)
    {
        StringBuffer param = new StringBuffer(Const.COURSES);
        param.append("?start=").append(start);
        param.append("&search=").append(searchStr);
        return param;
    }

    private void loadSearchList(int start, final String searchStr, final boolean isAppend)
    {
        StringBuffer param = getSearchUrl(start, searchStr);

        String url = app.bindToken2Url(param.toString(), false);
        load_layout = mSearchContent.findViewById(R.id.load_layout);
        final ListView listView = pullToRefreshListView.getRefreshableView();

        ajaxNormalGet(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                super.callback(url, object, ajaxStatus);
                //hide loading layout
                load_layout.setVisibility(View.GONE);
                pullToRefreshListView.onRefreshComplete();
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>() {
                }.getType());
                if (result == null || result.data.size() == 0) {
                    if (isAppend) {
                        longToast("没有更多相关课程了");
                        pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                        return;
                    }
                    showEmptyLayout("没有搜到相关课程，请换个关键词试试！");
                    return;
                }

                if (!isAppend) {
                    mAdapter = new CourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);
                    listView.setAdapter(mAdapter);
                    CourseListScrollListener listener = new CourseListScrollListener(null);
                    listView.setOnItemClickListener(listener);

                    pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            loadSearchList(0, searchStr, true);
                        }

                        @Override
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            Integer startPage = (Integer) mSearchContent.getTag();
                            loadSearchList(startPage, searchStr, true);
                        }
                    });

                } else {
                    mAdapter.addItem(result);
                }

                int start = result.start + mPageLimit;
                if (start < result.total) {
                    mSearchContent.setTag(start);
                } else {
                    pullToRefreshListView.setMode(PullToRefreshBase.Mode.DISABLED);
                }
            }
        });
    }
}
