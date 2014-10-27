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
    public PluginEnum action;
    public String name;

    public MyInfoPlugin(){}

    public MyInfoPlugin(Bitmap icon, PluginEnum action, String name)
    {
        this.action = action;
        this.icon = icon;
        this.name = name;
    }

    public static ArrayList<MyInfoPlugin> createNormalList(ActionBarBaseActivity mActivity)
    {
        ArrayList<MyInfoPlugin> list = new ArrayList<MyInfoPlugin>();

        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_course), PluginEnum.COURSE, "我的课程"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_question), PluginEnum.QUESTION, "我的回答"));
        //list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_discuss), PluginEnum.DISCUSS, "我的讨论"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_test), PluginEnum.TEST, "我的考试"));
        list.add(new MyInfoPlugin(mActivity.getBitmap(R.drawable.myinfo_note), PluginEnum.NOTE, "我的笔记"));

        return list;
    }

    public static enum PluginEnum
    {
        COURSE, QUESTION, DISCUSS, TEST, NOTE
    }
}
