package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.FollowFragment;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import ch.boye.httpclientandroidlib.util.TextUtils;

/**
 * Created by Melomelon on 2015/1/4.
 */
public class ProfileAdapter extends ListBaseAdapter<Course> {

    private User mUser;
    private ActionBarBaseActivity mActivity;
    private int mListViewLayoutId;

    public ProfileAdapter(Context context, int resource, User user, ActionBarBaseActivity activity) {
        super(context, resource, true);
        mUser = user;
        mActivity = activity;
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
                mHeaderHolder.mUserLogo = (CircularImageView) v.findViewById(R.id.myinfo_logo);
                mHeaderHolder.mUserName = (TextView) v.findViewById(R.id.tv_nickname);
                mHeaderHolder.mSignature = (TextView) v.findViewById(R.id.myinfo_signature);
                mHeaderHolder.mVip = (TextView) v.findViewById(R.id.vip_icon);
                mHeaderHolder.mTeacherTitle = (TextView) v.findViewById(R.id.teacher_title);
                mHeaderHolder.mSelfIntroduction = (ESTextView) v.findViewById(R.id.tvIntroduction);
                mHeaderHolder.mFollowing = (ESTextView) v.findViewById(R.id.tv_follow_num);
                mHeaderHolder.mFollower = (ESTextView) v.findViewById(R.id.tv_fans_num);
                mHeaderHolder.mDescription = (TextView) v.findViewById(R.id.description);
                mHeaderHolder.mFollowingsLayout = v.findViewById(R.id.ll_followings);
                mHeaderHolder.mFollowersLayout = v.findViewById(R.id.ll_followers);
                mHeaderHolder.mSendMsgLayout = v.findViewById(R.id.ll_send_msg);
                mHeaderHolder.mFollowLayout = v.findViewById(R.id.ll_follow);

                setUserInfo(mHeaderHolder);

                mHeaderHolder.mFollowingsLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mUser.following.equals("0")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Const.ACTIONBAR_TITLE, "关注");
                            bundle.putString(FragmentPageActivity.FRAGMENT, "FollowFragment");
                            bundle.putString(FollowFragment.FOLLOW_TYPE, FollowFragment.FOLLOWING);
                            bundle.putSerializable(FollowFragment.FOLLOW_USER, mUser);
                            mActivity.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                        }
                    }
                });
                mHeaderHolder.mFollowersLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mUser.follower.equals("0")) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Const.ACTIONBAR_TITLE, "粉丝");
                            bundle.putString(FragmentPageActivity.FRAGMENT, "FollowFragment");
                            bundle.putString(FollowFragment.FOLLOW_TYPE, FollowFragment.FOLLOWER);
                            bundle.putSerializable(FollowFragment.FOLLOW_USER, mUser);
                            mActivity.app.mEngine.runNormalPluginWithBundle("FragmentPageActivity", mActivity, bundle);
                        }
                    }
                });
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
                ImageLoader.getInstance().displayImage(course.largePicture, holder.mCourseImage, mActivity.app.mOptions);
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
        if (mUser.id == mActivity.app.loginUser.id) {
            headerHolder.mSendMsgLayout.setVisibility(View.INVISIBLE);
            headerHolder.mFollowLayout.setVisibility(View.INVISIBLE);
        } else {
            headerHolder.mSendMsgLayout.setVisibility(View.VISIBLE);
            headerHolder.mFollowLayout.setVisibility(View.VISIBLE);
        }

        headerHolder.mUserName.setText(mUser.nickname);

        headerHolder.mFollowing.setText(mUser.following);
        headerHolder.mFollower.setText(mUser.follower);

        ImageLoader.getInstance().displayImage(mUser.mediumAvatar, headerHolder.mUserLogo, mActivity.app.mOptions);
        if (TextUtils.isEmpty(mUser.about)) {
            headerHolder.mSelfIntroduction.setText("这家伙很懒，什么都没有留下");
        } else {
            headerHolder.mSelfIntroduction.setText(AppUtil.removeHtmlSpace(Html.fromHtml(AppUtil.removeImgTagFromString(mUser.about)).toString()));
        }

        if (TextUtils.isEmpty(mUser.signature)) {
            headerHolder.mSignature.setText("暂无个性签名");
        } else {
            headerHolder.mSignature.setText(mUser.signature);
        }

        if (isTeacher()) {
            headerHolder.mDescription.setText("在教课程");
        } else {
            headerHolder.mDescription.setText("在学课程");
        }

        headerHolder.mTeacherTitle.setText(mUser.title);
    }


    protected class HeaderHolder {
        public CircularImageView mUserLogo;
        public TextView mUserName;
        public TextView mSignature;
        public TextView mVip;
        public TextView mTeacherTitle;
        public ESTextView mSelfIntroduction;

        public ESTextView mFollowing;
        public ESTextView mFollower;
        public TextView mDescription;

        public View mFollowingsLayout;
        public View mFollowersLayout;

        public View mSendMsgLayout;
        public View mFollowLayout;
    }

    protected class ViewHolder {
        public TextView mCourseTitle;
        public ImageView mCourseImage;
    }
}
