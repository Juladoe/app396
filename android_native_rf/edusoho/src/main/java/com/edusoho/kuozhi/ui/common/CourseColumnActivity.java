package com.edusoho.kuozhi.ui.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.CourseMenuItemAdapter;
import com.edusoho.kuozhi.core.model.MessageModel;
import com.edusoho.kuozhi.model.CourseMenu;
import com.edusoho.kuozhi.model.CourseMenuResult;
import com.edusoho.kuozhi.ui.BaseActivity;
import com.edusoho.kuozhi.ui.DefaultPageActivity;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class CourseColumnActivity extends BaseActivity {

    private ListView course_menu_list;

    public static final String LOAD_COURSE_BY_COLUMN = "loadCourseByColumn";
    public static final String COLUMN_PARENT = "columnParent";

    public static final String TYPE = "type";
    private CourseMenu mCurrentCourseMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_column_layout);
        app.addTask("CourseColumnActivity", this);
        initView();
    }

    private void setWidth()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int)(display.getWidth() * 0.4f);
        layoutParams.alpha = 0.5f;
        window.setAttributes(layoutParams);
    }

    private void initView() {
        setBackMode("课程分类-所有", false, null);
        Intent dataIntent = getIntent();
        String currentType = dataIntent.hasExtra(COLUMN_PARENT)
                ? dataIntent.getStringExtra(COLUMN_PARENT) : "";

        mCurrentCourseMenu = new CourseMenu(currentType, "", "true");

        course_menu_list = (ListView) findViewById(R.id.course_menu_list);
        loadContent(mCurrentCourseMenu.type);
    }

    private void loadContent(String channel)
    {
        StringBuilder stringBuilder = new StringBuilder(Const.COURSE_COLUMN);
        stringBuilder.append("?channel=").append(channel);
        String url = app.bindToken2Url(stringBuilder.toString(), false);
        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                CourseMenuResult result = app.gson.fromJson(
                        object, new TypeToken<CourseMenuResult>() {
                }.getType());

                if (result == null) {
                    return;
                }

                setListData(result);
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

    private void setListData(CourseMenuResult result)
    {
        ArrayList<CourseMenu> list = result.data;
        CourseMenu parent = result.parent;
        if (list.isEmpty()) {
            goBack(mCurrentCourseMenu);
            return;
        }
        list.add(0, new CourseMenu(mCurrentCourseMenu, "false", CourseMenu.ALL));
        if (parent != null) {
            list.add(0, new CourseMenu(parent, "true", CourseMenu.BACK));
        }

        CourseMenuItemAdapter arrayAdapter = new CourseMenuItemAdapter(
                mContext, list, R.layout.course_mentu_item_layout);
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

        if ("true".equals(courseMenu.parentId)) {
            mCurrentCourseMenu = courseMenu;
            changeTitle("课程分类-" + mCurrentCourseMenu.name);
            loadContent(courseMenu.type);
            return;
        }
    }

    private void goBack(CourseMenu courseMenu)
    {
        app.sendMessage(LOAD_COURSE_BY_COLUMN, new MessageModel(courseMenu));
        app.sendMessage(DefaultPageActivity.COLUMN_MENU, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
