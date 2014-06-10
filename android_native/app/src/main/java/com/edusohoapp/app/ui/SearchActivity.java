package com.edusohoapp.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.CourseListAdapter;
import com.edusohoapp.app.model.CourseResult;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.app.view.EdusohoListView;
import com.edusohoapp.app.view.OverScrollView;
import com.edusohoapp.listener.CourseListScrollListener;
import com.edusohoapp.listener.MoveListener;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Map;

public class SearchActivity extends BaseActivity {

    private AQuery aq;
    private View load_layout;
    private AutoCompleteTextView actionbar_search_edt;
    private EdusohoListView listView;
    private OverScrollView scrollView;
    private ViewGroup mSearchContent;

    private static final String SEARCH_HISTORY = "search";
    private ArrayList<String> mSearchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, SearchActivity.class);
        context.startActivity(intent);
    }

    private void initView() {
        aq = new AQuery(this);
        mSearchContent = (ViewGroup) findViewById(R.id.search_content);

        listView = (EdusohoListView) findViewById(R.id.course_liseview);
        actionbar_search_edt = (AutoCompleteTextView) findViewById(R.id.actionbar_search_edt);

        aq.id(R.id.actionbar_back).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        aq.id(R.id.actionbar_search_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchStr = actionbar_search_edt.getText().toString();
                if (TextUtils.isEmpty(searchStr)) {
                    longToast("请输入搜索内容!");
                    return;
                }
                mSearchContent.removeAllViews();
                View course_content = getLayoutInflater().inflate(R.layout.course_content, null);
                mSearchContent.addView(course_content);
                loadSearchList(0, searchStr, false);
                saveSearchHistory(searchStr);
            }
        });

        loadSearchHistory();
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

    private void loadSearchList(int start, final String searchStr, final boolean isAppend)
    {
        StringBuffer param = new StringBuffer(Const.COURSE_LIST);
        param.append("?start=").append(start);
        param.append("&search=").append(searchStr);

        String url = app.bindToken2Url(param.toString(), false);
        load_layout = mSearchContent.findViewById(R.id.load_layout);
        listView = (EdusohoListView) mSearchContent.findViewById(R.id.course_liseview);
        app.query.ajax(
                url, String.class, new AjaxCallback<String>(){
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                super.callback(url, object, status);
                //hide loading layout
                load_layout.setVisibility(View.GONE);
                final CourseResult result = app.gson.fromJson(
                        object, new TypeToken<CourseResult>(){}.getType());

                if (result == null || result.data.length == 0) {
                    showEmptyLayout("没有搜到相关课程，请换个关键词试试！");
                    return;
                }

                if (! isAppend) {
                    CourseListAdapter adapter = new CourseListAdapter(
                            mContext, result, R.layout.course_list_normal_item);
                    listView.setAdapter(adapter);
                    CourseListScrollListener listener = new CourseListScrollListener(mActivity, listView);
                    listView.setOnItemClickListener(listener);

                    scrollView = (OverScrollView) mSearchContent.findViewById(R.id.course_content_scrollview);
                    scrollView.setMoveListener(new MoveListener(){
                        @Override
                        public void moveToBottom() {
                            View course_more_btn = findViewById(R.id.course_more_btn);
                            if (course_more_btn.getVisibility() == View.VISIBLE) {
                                course_more_btn.findViewById(R.id.more_btn_loadbar).setVisibility(View.VISIBLE);
                                loadSearchList((result.start + 1) * Const.LIMIT, searchStr, true);
                            }
                        }
                    });

                } else {
                    CourseListAdapter adapter = (CourseListAdapter) listView.getAdapter();
                    adapter.addItem(result);
                    listView.initListHeight();
                }

                View course_more_btn = findViewById(R.id.course_more_btn);
                int start = (result.start + 1) * Const.LIMIT;
                if (start < result.total) {
                    course_more_btn.setVisibility(View.VISIBLE);
                } else {
                    course_more_btn.setVisibility(View.GONE);
                }
            }
        });
    }
}
