package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Melomelon on 2015/1/4.
 */
public class ProfileAdapter extends ListBaseAdapter<Course> {

    private DisplayImageOptions mOptions;
    private User mUser;
    private ActionBarBaseActivity mActivity;
    private int mListViewLayoutId;

    public ProfileAdapter(Context context, int resource, User user, ActionBarBaseActivity activity) {
        super(context, resource, true);
        mUser = user;
        mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public void setListViewLayout(int layoutId) {
        mListViewLayoutId = layoutId;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList.size() + 1;
    }

    @Override
    public void addItem(Course item) {
        mList.add(item);
        notifyDataSetChanged();
    }

    @Override
    public void addItems(ArrayList<Course> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;
        if (i == 0) {
            HeaderHolder mHeaderHolder;
            if (cacheArray.get(0) == null) {
                v = inflater.inflate(mResource, null);
                mHeaderHolder = new HeaderHolder();
                mHeaderHolder.mHeader = v.findViewById(R.id.header);
                mHeaderHolder.mUserLogo = (CircularImageView) v.findViewById(R.id.myinfo_logo);
                mHeaderHolder.mUserName = (TextView) v.findViewById(R.id.tv_nickname);
                mHeaderHolder.mSignature = (TextView) v.findViewById(R.id.myinfo_signature);
                mHeaderHolder.mVip = (TextView) v.findViewById(R.id.vip_icon);
                mHeaderHolder.mTeacherTitle = (TextView) v.findViewById(R.id.teacher_title);
                mHeaderHolder.mSelfIntroduction = (ESTextView) v.findViewById(R.id.self_introduction);
                mHeaderHolder.mFollowing = (ESTextView) v.findViewById(R.id.tv_follow_num);
                mHeaderHolder.mFollower = (ESTextView) v.findViewById(R.id.tv_fans_num);
                mHeaderHolder.mDescription = (TextView) v.findViewById(R.id.description);

                setUserInfo(mHeaderHolder);
                if (isTeacher()) {
                    mHeaderHolder.mDescription.setText("在教课程");
                } else {
                    mHeaderHolder.mDescription.setText("在学课程");
                }
                mHeaderHolder.mHeader.setEnabled(false);
                setCacheView(0, v);
            } else {
                v = getCacheView(0);
            }
        } else {
            ViewHolder holder;
            if (cacheArray.get(i) == null) {
                v = inflater.inflate(mListViewLayoutId, null);
                holder = new ViewHolder();
                holder.mCourseImage = (ImageView) v.findViewById(R.id.course_image);
                holder.mCourseTitle = (TextView) v.findViewById(R.id.course_title);
                Course course = mList.get(i - 1);
                holder.mCourseTitle.setText(course.title);
                ImageLoader.getInstance().displayImage(course.largePicture, holder.mCourseImage, mOptions);
                setCacheView(i, v);
            } else {
                v = getCacheView(i);
            }
        }
        return v;
    }

    public boolean isTeacher() {
        for (UserRole role : mUser.roles) {
            if (role == UserRole.ROLE_TEACHER) {
                return true;
            }
        }
        return false;
    }

    public void setUserInfo(HeaderHolder headerHolder) {
        if (mUser.vip != null) {
            headerHolder.mVip.setVisibility(View.VISIBLE);
        } else {
            headerHolder.mVip.setVisibility(View.GONE);
        }
        headerHolder.mUserName.setText(mUser.nickname);
        headerHolder.mSignature.setText(mUser.signature);
        headerHolder.mFollowing.setText(mUser.following);
        headerHolder.mFollower.setText(mUser.follower);

        AQuery aQuery = new AQuery(mActivity);
        aQuery.id(headerHolder.mUserLogo).image(
                mUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);
        headerHolder.mSelfIntroduction.setText(AppUtil.removeHtmlSpace(Html.fromHtml(AppUtil.removeImgTagFromString(mUser.about)).toString()));
        headerHolder.mTeacherTitle.setText(mUser.title);
    }


    protected class HeaderHolder {
        public View mHeader;
        public CircularImageView mUserLogo;
        public TextView mUserName;
        public TextView mSignature;
        public TextView mVip;
        public TextView mTeacherTitle;
        public ESTextView mSelfIntroduction;

        public ESTextView mFollowing;
        public ESTextView mFollower;
        public TextView mDescription;
    }

    protected class ViewHolder {
        public TextView mCourseTitle;
        public ImageView mCourseImage;
    }
}
