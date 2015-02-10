package com.edusoho.kuozhi.adapter.Course;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.util.html.EduHtml;
import com.edusoho.kuozhi.util.html.EduImageGetterHandler;
import com.edusoho.kuozhi.util.html.EduTagHandler;
import com.edusoho.listener.IconClickListener;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseTeacherAdapter
        extends RecyclerViewListBaseAdapter<Teacher, CourseTeacherAdapter.ViewHolder> {

    private ActionBarBaseActivity mActivity;
    private DisplayImageOptions mOptions;

    public CourseTeacherAdapter(ActionBarBaseActivity activity, int resource) {
        super(activity, resource);
        this.mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItem(Teacher item) {
        mList.add(item);
        notifyItemInserted(mList.size() - 1);
    }

    @Override
    public void addItems(List<Teacher> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - 1 - list.size(), mList.size() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        Teacher teacher = mList.get(i);
        viewHolder.mNickname.setText(teacher.nickname);
        viewHolder.mUserInfo.setText(teacher.title);
        ImageLoader.getInstance().displayImage(teacher.avatar, viewHolder.mUserFace, mOptions);

        RequestUrl requestUrl = mActivity.app.bindUrl(Const.USERINFO, false);
        requestUrl.setParams(new String[]{
                "userId", String.valueOf(teacher.id)
        });
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                User user = mActivity.parseJsonValue(object, new TypeToken<User>() {
                });
                if (user != null) {
                    Spanned spanned = Html.fromHtml(
                            TextUtils.isEmpty(user.about) ? "" : user.about,
                            new EduImageGetterHandler(mContext, viewHolder.mContent),
                            new EduTagHandler()
                    );
                    viewHolder.mContent.setText(spanned);
                    viewHolder.mUserFace.setOnClickListener(new IconClickListener(mActivity, user));
                }

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mUserInfo;
        public TextView mContent;
        public TextView mNickname;
        public ImageView mUserFace;

        public ViewHolder(View view) {
            super(view);

            mUserInfo = (TextView) view.findViewById(R.id.teacher_info);
            mNickname = (TextView) view.findViewById(R.id.teacher_nickname);
            mUserFace = (ImageView) view.findViewById(R.id.teacher_face);
            mContent = (TextView) view.findViewById(R.id.teacher_content);
        }
    }
}
