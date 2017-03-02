package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.lesson.TeachLesson;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MineFragment;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyTeachFragment;
import com.edusoho.kuozhi.v3.util.Const;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DF on 2017/2/28.
 */

public class MyTeachAdapter extends RecyclerView.Adapter {
    private Context mContext;

    private static final int EMPTY = 0;
    private static final int NOT_EMPTY = 1;
    private int mCurrentDataStatus;

    private List<TeachLesson> mCourseList;
    private TeachLesson mTeachLesson;

    public MyTeachAdapter(Context context) {
        this.mContext = context;
        mCourseList = new ArrayList<>();
    }

    public void setData(List<TeachLesson> list) {
        mCourseList.clear();
        mCourseList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mCurrentDataStatus;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_study, parent, false);
            return new MyTeachFragment.CourseTeachViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_empty, parent, false);
            return new MineFragment.EmptyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mCurrentDataStatus == NOT_EMPTY) {
            MyTeachFragment.CourseTeachViewHolder courseTeachViewHolder = (MyTeachFragment.CourseTeachViewHolder) holder;
            courseTeachViewHolder.layoutClass.setVisibility(View.GONE);
            courseTeachViewHolder.layoutLive.setVisibility(View.GONE);
            courseTeachViewHolder.tvMore.setVisibility(View.GONE);
            courseTeachViewHolder.tvStudyState.setText("");
            mTeachLesson = mCourseList.get(position);
            ImageLoader.getInstance().displayImage(mTeachLesson.getSmallPicture()
                    , courseTeachViewHolder.ivPic, EdusohoApp.app.mOptions);
            courseTeachViewHolder.tvTitle.setText(String.valueOf(mTeachLesson.getTitle()));
            if (mTeachLesson.getType().equals("live")) {
                courseTeachViewHolder.layoutLive.setVisibility(View.VISIBLE);
                courseTeachViewHolder.tvLive.setText("直播");
                courseTeachViewHolder.tvLiveIcon.setVisibility(View.GONE);
            }
            courseTeachViewHolder.tvStudyState.setText(String.format("参与人数  %s", mTeachLesson.getStudentNum()));
            courseTeachViewHolder.rLayoutItem.setTag(mTeachLesson);
            courseTeachViewHolder.rLayoutItem.setOnClickListener(getTeachCourseViewClickListener());
        }
    }

    private View.OnClickListener getTeachCourseViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final TeachLesson teachLesson =  ((TeachLesson) v.getTag());
                EdusohoApp.app.mEngine.runNormalPlugin("TeachActivity"
                        , mContext, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.COURSE_ID, teachLesson.getId());
                            }
                        });
            }
        };
    }

    @Override
    public int getItemCount() {
        if (mCourseList != null && mCourseList.size() != 0) {
            mCurrentDataStatus = NOT_EMPTY;
            return mCourseList.size();
        }
        mCurrentDataStatus = EMPTY;
        return 1;
    }
}
