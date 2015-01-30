package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Course;
import com.edusoho.kuozhi.model.Message.ConversationModel;
import com.edusoho.kuozhi.model.User;
import com.edusoho.kuozhi.model.UserRole;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.fragment.FollowFragment;
import com.edusoho.kuozhi.ui.fragment.ProfileFragment;
import com.edusoho.kuozhi.ui.message.MessageLetterListActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.kuozhi.view.ESExpandableTextView;
import com.edusoho.kuozhi.view.ESTextView;
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;

import ch.boye.httpclientandroidlib.util.TextUtils;

/**
 * Created by Melomelon on 2015/1/4.
 */
public class ProfileAdapter extends ListBaseAdapter<Course> {

    private User mUser;
    private ActionBarBaseActivity mActivity;
    private int mListViewLayoutId;
    private String mType = "";
    private boolean bResult;

    public ProfileAdapter(Context context, int resource, User user, ActionBarBaseActivity activity, String type) {
        super(context, resource, true);
        mUser = user;
        mActivity = activity;
        mType = (type == null ? "" : type);
    }

    public void updateUserInfo() {
        RequestUrl url = mActivity.app.bindUrl(Const.USERINFO, false);
        HashMap<String, String> params = url.getParams();
        params.put("userId", mUser.id + "");
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (object != null) {
                    mUser = mActivity.parseJsonValue(object, new TypeToken<User>() {
                    });
                    notifyDataSetChanged();
                }
            }
        });

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
            final HeaderHolder mHeaderHolder;
            if (cacheArray.get(0) == null) {
                v = inflater.inflate(mResource, null);
                mHeaderHolder = new HeaderHolder();
                mHeaderHolder.mUserLogo = (CircularImageView) v.findViewById(R.id.myinfo_logo);
                mHeaderHolder.mUserName = (TextView) v.findViewById(R.id.tv_nickname);
                mHeaderHolder.mSignature = (TextView) v.findViewById(R.id.myinfo_signature);
                mHeaderHolder.mVip = (TextView) v.findViewById(R.id.vip_icon);
                mHeaderHolder.mTeacherTitle = (TextView) v.findViewById(R.id.teacher_title);
                mHeaderHolder.expandableTextView = (ESExpandableTextView) v.findViewById(R.id.expand_text_view);
                mHeaderHolder.mFollowing = (ESTextView) v.findViewById(R.id.tv_follow_num);
                mHeaderHolder.mFollower = (ESTextView) v.findViewById(R.id.tv_fans_num);
                mHeaderHolder.mDescription = (TextView) v.findViewById(R.id.description);
                mHeaderHolder.mFollowingsLayout = v.findViewById(R.id.ll_followings);
                mHeaderHolder.mFollowersLayout = v.findViewById(R.id.ll_followers);
                mHeaderHolder.mSendMsgLayout = v.findViewById(R.id.ll_send_msg);
                mHeaderHolder.mFollowLayout = v.findViewById(R.id.ll_follow);
                mHeaderHolder.tvFollow = (TextView) v.findViewById(R.id.tv_Follow);
                v.setTag(mHeaderHolder);
                setCacheView(0, v);
            } else {
                v = getCacheView(0);
                mHeaderHolder = (HeaderHolder) v.getTag();
            }
            setHeaderInfo(mHeaderHolder);

            mHeaderHolder.mFollowingsLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mUser.following.equals("0")) {
                        mActivity.app.mEngine.runNormalPluginForResult("FragmentPageActivity", mActivity, ProfileFragment.PROFILEFRAGMENT_REFRESH, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, "关注");
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FollowFragment");
                                startIntent.putExtra(FollowFragment.FOLLOW_TYPE, FollowFragment.FOLLOWING);
                                startIntent.putExtra(FollowFragment.FOLLOW_USER, mUser);
                            }
                        });
                    }
                }
            });
            mHeaderHolder.mFollowersLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mUser.follower.equals("0")) {
                        mActivity.app.mEngine.runNormalPluginForResult("FragmentPageActivity", mActivity, ProfileFragment.PROFILEFRAGMENT_REFRESH, new PluginRunCallback() {
                            @Override
                            public void setIntentDate(Intent startIntent) {
                                startIntent.putExtra(Const.ACTIONBAR_TITLE, "粉丝");
                                startIntent.putExtra(FragmentPageActivity.FRAGMENT, "FollowFragment");
                                startIntent.putExtra(FollowFragment.FOLLOW_TYPE, FollowFragment.FOLLOWER);
                                startIntent.putExtra(FollowFragment.FOLLOW_USER, mUser);
                            }
                        });
                    }
                }
            });
            mHeaderHolder.mFollowLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mHeaderHolder.mFollowLayout.setEnabled(false);
                    mHeaderHolder.tvFollow.setEnabled(false);
                    String url;
                    final String changeText;
                    if (mHeaderHolder.tvFollow.getText().equals("关注")) {
                        url = Const.FOLLOW;
                        changeText = "取消关注";
                    } else {
                        url = Const.UNFOLLOW;
                        changeText = "关注";
                    }
                    RequestUrl requestUrl = mActivity.app.bindUrl(url, true);
                    HashMap<String, String> params = requestUrl.getParams();
                    params.put("toId", mUser.id + "");
                    mActivity.ajaxPost(requestUrl, new ResultCallback() {
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            mHeaderHolder.mFollowLayout.setEnabled(true);
                            mHeaderHolder.tvFollow.setEnabled(true);
                            if (object != null) {
                                mHeaderHolder.tvFollow.setText(changeText);
                            }
                        }

                        @Override
                        public void error(String url, AjaxStatus ajaxStatus) {
                            mHeaderHolder.mFollowLayout.setEnabled(true);
                            mHeaderHolder.tvFollow.setEnabled(true);
                        }
                    });
                }
            });
            mHeaderHolder.mSendMsgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RequestUrl url = mActivity.app.bindUrl(Const.GET_CONVERSATION, true);
                    HashMap<String, String> params = url.getParams();
                    params.put("fromId", mUser.id + "");
                    params.put("toId", mActivity.app.loginUser.id + "");
                    mActivity.ajaxPost(url, new ResultCallback() {
                        @Override
                        public void callback(String url, String object, AjaxStatus ajaxStatus) {
                            final ConversationModel model = mActivity.parseJsonValue(object, new TypeToken<ConversationModel>() {
                            });
                            PluginRunCallback pluginRunCallback = new PluginRunCallback() {
                                @Override
                                public void setIntentDate(Intent startIntent) {
                                    if (model != null) {
                                        startIntent.putExtra(MessageLetterListActivity.CONVERSATION_ID, model.id);
                                    }
                                    startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_NAME, mUser.nickname);
                                    startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_ID, mUser.id);
                                }
                            };
                            mActivity.app.mEngine.runNormalPlugin("MessageLetterListActivity", mActivity, pluginRunCallback);
                        }
                    });
                }
            });
        } else {
            ViewHolder holder;
            if (cacheArray.get(i) == null) {
                v = inflater.inflate(mListViewLayoutId, null);
                holder = new ViewHolder();
                holder.mCourseImage = (ImageView) v.findViewById(R.id.course_image);
                holder.mCourseTitle = (TextView) v.findViewById(R.id.course_title);
                v.setTag(holder);
                setCacheView(i, v);
            } else {
                v = getCacheView(i);
                holder = (ViewHolder) v.getTag();
            }
            Course course = mList.get(i - 1);
            holder.mCourseTitle.setText(course.title);
            ImageLoader.getInstance().displayImage(course.largePicture, holder.mCourseImage, mActivity.app.mOptions);
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

    /**
     * 粉丝列表中点击用户，设置关注按钮
     *
     * @return
     */
    private void isFollowed(final HeaderHolder headerHolder) {
        RequestUrl url = mActivity.app.bindUrl(Const.IS_FOLLOWED, false);
        HashMap<String, String> params = url.getParams();
        params.put("userId", mActivity.app.loginUser.id + "");
        params.put("toId", mUser.id + "");
        mActivity.ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (object != null) {
                    if (object.equals("true")) {
                        headerHolder.tvFollow.setText("取消关注");
                    } else {
                        headerHolder.tvFollow.setText("关注");
                    }
                }
            }
        });
    }

    public void setHeaderInfo(HeaderHolder headerHolder) {
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
//            if (mType.equals(FollowFragment.FOLLOWING)) {
//                headerHolder.tvFollow.setText("取消关注");
//            } else {
//                isFollowed(headerHolder);
//            }
            isFollowed(headerHolder);
        }

        headerHolder.mUserName.setText(mUser.nickname);

        headerHolder.mFollowing.setText(mUser.following);
        headerHolder.mFollower.setText(mUser.follower);

        ImageLoader.getInstance().displayImage(mUser.mediumAvatar, headerHolder.mUserLogo, mActivity.app.mOptions);
        if (TextUtils.isEmpty(mUser.about)) {
            headerHolder.expandableTextView.setMyText("这家伙很懒，什么都没有留下");
        } else {
            headerHolder.expandableTextView.setMyText(AppUtil.removeHtmlSpace(Html.fromHtml(AppUtil.removeImgTagFromString(mUser.about)).toString()));
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
//
//    private boolean isFollowed() {
//        RequestUrl requestUrl = mActivity.app.bindUrl(Const.FOLLOWING, false);
//        HashMap<String, String> params = requestUrl.getParams();
//        params.put("userId", app.loginUser.id + "");
//    }


    protected class HeaderHolder {
        public CircularImageView mUserLogo;
        public TextView mUserName;
        public TextView mSignature;
        public TextView mVip;
        public TextView mTeacherTitle;

        public ESExpandableTextView expandableTextView;

        public ESTextView mFollowing;
        public ESTextView mFollower;
        public TextView mDescription;
        public TextView tvFollow;

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
