package com.edusoho.kuozhi.adapter.lesson;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.ListBaseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Question.QuestionDetailModel;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by MyPC on 14-11-10.
 */
public class LessonQuestionListAdapter<T> extends ListBaseAdapter<T> {
    private static final String TAG = "LessonQuestionListAdapter";
    private DisplayImageOptions mOptions;
    private SparseArray<User> mUserList;
    private ActionBarBaseActivity mActivity;

    public LessonQuestionListAdapter(ActionBarBaseActivity activity, int layoutId) {
        super(activity, layoutId);
        mActivity = activity;
        mUserList = new SparseArray<User>();
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

            User user = mUserList.get(model.userId);
            if (user == null) {
                loadUserFromNet(model.userId, holder);
            } else {
                holder.tvPostName.setText(user.nickname);
                ImageLoader.getInstance().displayImage(user.mediumAvatar, holder.civ, mOptions);
            }
            holder.tvTitle.setText(model.title);
            holder.tvPostTime.setText(AppUtil.getPostDays(model.createdTime));
            holder.tvMsgs.setText(String.valueOf(model.postNum));
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        return convertView;
    }

    private void loadUserFromNet(
            int userId, final ViewHolder holder)
    {
        RequestUrl url = mActivity.app.bindUrl(Const.USERINFO, false);
        url.setParams(new String[] {
                "userId", String.valueOf(userId)
        });
        mActivity.ajaxPost(url, new ResultCallback(){
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                User user = mActivity.parseJsonValue(object, new TypeToken<User>(){});
                if (user != null) {
                    mUserList.put(user.id, user);
                    holder.tvPostName.setText(user.nickname);
                    ImageLoader.getInstance().displayImage(user.mediumAvatar, holder.civ, mOptions);
                }
            }
        });
    }

    private static class ViewHolder {
        public CircularImageView civ;
        public TextView tvTitle;
        public TextView tvPostName;
        public TextView tvPostTime;
        public TextView tvMsgs;
    }
}
