package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.util.Log;
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
import com.edusoho.kuozhi.view.plugin.CircularImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Melomelon on 2015/1/4.
 */
public class PersonalDetailAdapter extends ListBaseAdapter<Course> {

    private DisplayImageOptions mOptions;
    private User mUser;
    private ActionBarBaseActivity mActivity;
    private int mListViewLayoutId;
    private View mCacheHeader;
    private View mCacheView;
    public PersonalDetailAdapter(Context context, int resource,User user,ActionBarBaseActivity activity){
        super(context, resource);
        mUser=user;
        mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    public void setListViewLayout(int layoutId){
        mListViewLayoutId=layoutId;
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
        return mList.size()+1;
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
        Log.d(null, "getview--->");
        ViewHolder holder;
        HeaderHolder mHeaderHolder;

        if (i == 0) {
            if (view == null) {
                view = inflater.inflate(mResource, null);
                mHeaderHolder = new HeaderHolder();
                mHeaderHolder.mUserLogo = (CircularImageView) view.findViewById(R.id.myinfo_logo);
                mHeaderHolder.mUserName = (TextView) view.findViewById(R.id.myinfo_name);
                mHeaderHolder.mSignature = (TextView) view.findViewById(R.id.myinfo_signature);
                mHeaderHolder.mVip = (TextView) view.findViewById(R.id.vip_icon);
                mHeaderHolder.mUserLayout = view.findViewById(R.id.myinfo_user_layout);
                mHeaderHolder.mTeacherTitle = (TextView) view.findViewById(R.id.teacher_title);
                mHeaderHolder.mSelfIntroduction = (TextView) view.findViewById(R.id.self_introduction);

                mHeaderHolder.mConcern = (LinearLayout) view.findViewById(R.id.concern);
                mHeaderHolder.mConcernNum = (TextView) view.findViewById(R.id.concern_num);
                mHeaderHolder.mFans = (LinearLayout) view.findViewById(R.id.fans);
                mHeaderHolder.mFansNum = (TextView) view.findViewById(R.id.fans_num);
                mHeaderHolder.mMessage = (LinearLayout) view.findViewById(R.id.message);
                mHeaderHolder.mAddConcern = (LinearLayout) view.findViewById(R.id.add_concern);

                mHeaderHolder.mNotice = (TextView) view.findViewById(R.id.notice);
                mHeaderHolder.mDescription = (TextView) view.findViewById(R.id.description);

                view.setTag(mHeaderHolder);
                mCacheHeader=view;
            } else {
                if(view.getTag() instanceof ViewHolder){
                    view=mCacheHeader;
                }
                mHeaderHolder = (HeaderHolder) view.getTag();

            }
            setUserInfo(mHeaderHolder);
            isTeacher(mHeaderHolder);
        } else {
            if (view == null) {
                view = inflater.inflate(mListViewLayoutId, null);
                holder = new ViewHolder();
                holder.mCourseImage = (ImageView) view.findViewById(R.id.course_image);
                holder.mCourseTitle = (TextView) view.findViewById(R.id.course_title);
                if(mCacheView==null){
                    mCacheView=view;
                }
                view.setTag(holder);
            } else {
                if(view.getTag() instanceof HeaderHolder){
                    view=mCacheView;
                }
                holder = (ViewHolder) view.getTag();
            }
            Course course = mList.get(i-1);
            holder.mCourseTitle.setText(course.title);
            ImageLoader.getInstance().displayImage(course.largePicture, holder.mCourseImage, mOptions);

        }
        return view;

    }

    public void isTeacher(HeaderHolder headerHolder){
        //        判断是学生还是老师 设置相应信息
        for (UserRole role : mUser.roles) {
            if(role == UserRole.ROLE_TEACHER){
                headerHolder.mDescription.setText("在教课程");
                return;
            }
        }

    }
    public void setUserInfo(HeaderHolder headerHolder){

        if(mUser.vip != null){
            headerHolder.mVip.setVisibility(View.VISIBLE);
        }
        headerHolder.mUserName.setText(mUser.nickname);
        headerHolder.mSignature.setText(mUser.signature);


        AQuery aQuery = new AQuery(mActivity);
        aQuery.id(headerHolder.mUserLogo).image(
                mUser.mediumAvatar, false, true, 200, R.drawable.myinfo_default_face);
        headerHolder.mSelfIntroduction.setText(mUser.about);
        headerHolder.mTeacherTitle.setText(mUser.title);
    }



    protected class HeaderHolder{
        public CircularImageView mUserLogo;
        public TextView mUserName;
        public TextView mSignature;
        public TextView mVip;
        public TextView mTeacherTitle;
        public View mUserLayout;
        public TextView mSelfIntroduction;

        public LinearLayout mConcern;
        public TextView mConcernNum;
        public LinearLayout mFans;
        public TextView mFansNum;
        public LinearLayout mMessage;
        public LinearLayout mAddConcern;

        public TextView mNotice;
        public TextView mDescription;
    }
    protected class ViewHolder {
        public TextView mCourseTitle;
        public ImageView mCourseImage;
    }
}
