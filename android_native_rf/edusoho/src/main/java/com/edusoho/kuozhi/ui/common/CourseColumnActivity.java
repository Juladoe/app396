package com.edusoho.kuozhi.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseMenuItemAdapter;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.AboutResult;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.CourseMenu;
import com.edusoho.kuozhi.model.CourseMenuResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class CourseColumnActivity extends BaseActivity {

    private ListView course_menu_list;
    private ViewGroup nav_column_btn;

    public static final String LOAD_COURSE_BY_COLUMN = "loadCourseByColumn";
    public static final String COLUMN_PARENT = "columnParent";

    public static final String TYPE = "type";
    private ArrayList<CourseMenu> lastData;
    private String mCourseMenuParent;
    private Stack<CourseMenu[]> historyStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_column_layout);
        app.addTask("CourseColumnActivity", this);
        initView();
    }

    private void initView() {
        setBackMode("课程分类", false, null);
        Intent dataIntent = getIntent();
        mCourseMenuParent = dataIntent.hasExtra(COLUMN_PARENT) ? dataIntent.getStringExtra(COLUMN_PARENT) : "";

        nav_column_btn = (ViewGroup) findViewById(R.id.nav_column_btn);
        course_menu_list = (ListView) findViewById(R.id.course_menu_list);
        lastData = new ArrayList<CourseMenu>();
        historyStack = new Stack<CourseMenu[]>();
    }

    private void loadContent(String channel)
    {
        StringBuilder stringBuilder = new StringBuilder(Const.COURSE_COLUMN);
        stringBuilder.append("?channel=").append(channel);
        String url = app.bindToken2Url(stringBuilder.toString(), false);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                CourseMenu[] result = app.gson.fromJson(
                        object, new TypeToken<CourseMenu[]>(){}.getType());

                if (result == null) {
                    return;
                }

                setListData(createArrayList(result));
                historyStack.push(result);
            }
        });
    }

    private ArrayList<CourseMenu> createArrayList(CourseMenu[] courseMenus)
    {
        ArrayList<CourseMenu> temp = new ArrayList<CourseMenu>();
        for (CourseMenu courseMenu : courseMenus) {
            temp.add(courseMenu);
        }
        return temp;
    }

    private void setListData(ArrayList<CourseMenu> result)
    {
        if(!historyStack.empty()) {
            result.add(0, new CourseMenu(mCourseMenuParent, "返回", "back"));
        }

        CourseMenuItemAdapter arrayAdapter = new CourseMenuItemAdapter(
                mContext, result, R.layout.course_mentu_item_layout);
        course_menu_list.setAdapter(arrayAdapter);

        course_menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int positon, long l) {
                CourseMenu courseMenu = (CourseMenu) adapterView.getItemAtPosition(positon);
                selectCourseMenu(courseMenu);
            }
        });
    }

    private void selectCourseMenu(CourseMenu courseMenu)
    {
        if ("false".equals(courseMenu.parentId)) {
            goBack(courseMenu);
            return;
        }

        if ("back".equals(courseMenu.parentId)) {
            historyStack.pop();
            CourseMenu[] courseMenus = historyStack.size() > 1 ? historyStack.pop() : historyStack.peek();
            setListData(createArrayList(courseMenus));
            return;
        }

        mCourseMenuParent = courseMenu.type;
        if ("true".equals(courseMenu.parentId)) {
            changeTitle("课程分类－" + courseMenu.name);
            loadContent(courseMenu.type);
            return;
        }
    }

    private void goBack(CourseMenu courseMenu)
    {
        app.sendMessage(LOAD_COURSE_BY_COLUMN, new MessageModel(courseMenu));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent(mCourseMenuParent);
    }
}
