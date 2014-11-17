package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MyPC on 14-11-10.
 */
public class LessonQuestionListAdapter<T> extends ListBaseAdapter<T> {
    private static final String TAG = "LessonQuestionListAdapter";
    private DisplayImageOptions mOptions;

    public LessonQuestionListAdapter(Context mContext, int layoutId) {
        super(mContext, layoutId);
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return super.getItemId(i);
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public void addItems(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        try {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(mResource, null);
                holder = new ViewHolder();
                holder.civ = (CircularImageView) convertView.findViewById(R.id.civ_reply_pic);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_thread_title);
                holder.tvPostName = (TextView) convertView.findViewById(R.id.tv_thread_post_name);
                holder.tvPostTime = (TextView) convertView.findViewById(R.id.tv_thread_post_time);
                holder.tvMsgs = (TextView) convertView.findViewById(R.id.tv_msgs);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            QuestionDetailModel model = (QuestionDetailModel) mList.get(position);
            ImageLoader.getInstance().displayImage(model.user.mediumAvatar, holder.civ, mOptions);
            holder.tvTitle.setText(model.title);
            holder.tvPostName.setText(model.user.nickname);
            holder.tvPostTime.setText(AppUtil.getPostDays(model.createdTime));
            holder.tvMsgs.setText(model.postNum);


        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return convertView;
    }

    private static class ViewHolder {
        public CircularImageView civ;
        public TextView tvTitle;
        public TextView tvPostName;
        public TextView tvPostTime;
        public TextView tvMsgs;
    }
}
