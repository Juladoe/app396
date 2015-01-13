package com.edusoho.kuozhi.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by JesseHuang on 15/1/13.
 */
public class FollowAdapter<T> extends ListBaseAdapter<T> {
    private ActionBarBaseActivity mActivity;

    public FollowAdapter(Context context, int resource, ActionBarBaseActivity activity) {
        super(context, resource);
        mActivity = activity;
    }

    @Override
    public int getCount() {
        if (mList != null && mList.size() > 0) {
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
        return super.getItem(i);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(mResource, null);
            holder = new ViewHolder();
            holder.civ = (CircularImageView) view.findViewById(R.id.ci_follow_pic);
            holder.tvUsername = (TextView) view.findViewById(R.id.tv_username);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        User user = (User) mList.get(position);
        ImageLoader.getInstance().displayImage(user.mediumAvatar, holder.civ, mActivity.app.mOptions);
        holder.tvUsername.setText(user.nickname);

        return view;
    }

    @Override
    public void addItems(ArrayList<T> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public static class ViewHolder {
        public CircularImageView civ;
        public TextView tvUsername;

    }
}
