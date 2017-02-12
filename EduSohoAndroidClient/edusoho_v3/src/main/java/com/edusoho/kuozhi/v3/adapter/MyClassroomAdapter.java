package com.edusoho.kuozhi.v3.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.ui.ClassroomActivity;
import com.edusoho.kuozhi.v3.ui.fragment.mine.MyStudyFragment;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/2/10.
 */

public class MyClassroomAdapter extends RecyclerView.Adapter<MyStudyFragment.ClassroomViewHolder> {
    private Context mContext;

    private List<Classroom> mClassroomList;

    public MyClassroomAdapter(Context context) {
        this.mContext = context;
        mClassroomList = new ArrayList<>();
    }

    public void setClassrooms(List<Classroom> list) {
        mClassroomList = list;
        notifyDataSetChanged();
    }

    @Override
    public MyStudyFragment.ClassroomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_classroom, parent, false);
        return new MyStudyFragment.ClassroomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyStudyFragment.ClassroomViewHolder viewHolder, int position) {
        final Classroom classroom = mClassroomList.get(position);
        viewHolder.tvTitle.setText(String.valueOf(classroom.title));
        ImageLoader.getInstance().displayImage(classroom.getLargePicture(), viewHolder.ivPic,
                EdusohoApp.app.mOptions);
        viewHolder.rLayoutItem.setTag(classroom.id);
        viewHolder.rLayoutItem.setOnClickListener(getClassroomViewClickListener());
    }

    @Override
    public int getItemCount() {
        return mClassroomList != null ? mClassroomList.size() : 0;
    }

    private View.OnClickListener getClassroomViewClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int classroomId = (int) v.getTag();
                CoreEngine.create(mContext).runNormalPlugin("ClassroomActivity", mContext, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(ClassroomActivity.CLASSROOM_ID, String.valueOf(classroomId));
                    }
                });
            }
        };
    }
}
