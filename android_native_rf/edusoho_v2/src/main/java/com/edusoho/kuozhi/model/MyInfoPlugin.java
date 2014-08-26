package com.edusoho.kuozhi.model;

import android.graphics.Bitmap;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;

import java.util.ArrayList;

/**
 * Created by howzhi on 14-8-15.
 */
public class MyInfoPlugin {

    public Bitmap icon;
    public String action;
    public String name;

    public MyInfoPlugin(){}

    public MyInfoPlugin(Bitmap icon, String action, String name)
    {
        this.action = action;
        this.icon = icon;
        this.name = name;
    }

    public static ArrayList<MyInfoPlugin> createNormalList(ActionBarBaseActivity mActivity)
    {
        ArrayList<MyInfoPlugin> list = new ArrayList<MyInfoPlugin>();

        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_course), "my_course", "我的课程"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_question), "my_question", "我的回答"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_discuss), "my_discuss", "我的讨论"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_test), "my_test", "我的考试"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_note), "my_note", "我的笔记"));

        return list;
    }
}
