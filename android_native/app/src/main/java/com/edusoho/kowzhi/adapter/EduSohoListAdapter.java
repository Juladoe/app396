package com.edusoho.kowzhi.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import com.edusoho.kowzhi.entity.CourseCommentItem;
import com.edusoho.kowzhi.entity.UserItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by howzhi on 14-5-16.
 */
public class EduSohoListAdapter {
    private HashMap<String, UserItem> mUsers;
    private ArrayList<CourseCommentItem> mList;
    private Context mContext;
    private LayoutInflater inflater;
    private int mResouce;
}
