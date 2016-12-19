package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.Course;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseMember;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReview;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReviewDetail;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.edusoho.kuozhi.v3.view.circleImageView.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */

public class CourseDetailFragment extends BaseDetailFragment {

    private String mCourseId;
    private CourseDetail mCourseDetail;
    private List<CourseReview> mReviews = new ArrayList<>();
    private ReviewAdapter mAdapter;

    public CourseDetailFragment() {
    }

    public CourseDetailFragment(String courseId) {
        this.mCourseId = courseId;
    }

    public void setCourseId(String courseId) {
        this.mCourseId = courseId;
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAdapter = new ReviewAdapter();
        mLvReview.setAdapter(mAdapter);
        initEvent();
        initData();
    }


    protected void initData() {
        mLoading.show();
        CourseDetailModel.getCourseDetail(mCourseId, new ResponseCallbackListener<CourseDetail>() {
            @Override
            public void onSuccess(CourseDetail data) {
                mCourseDetail = data;
                refreshView();
                mLoading.dismiss();
            }

            @Override
            public void onFailure(String code, String message) {
                mLoading.dismiss();
                if (message.equals("课程不存在")) {
                    CommonUtil.shortToast(mContext, "课程不存在");
                }
            }
        });
        CourseDetailModel.getCourseReviews(mCourseId, "5", "0",
                new ResponseCallbackListener<CourseReviewDetail>() {
                    @Override
                    public void onSuccess(CourseReviewDetail data) {
                        mReviews.clear();
                        mReviews.addAll(data.getData());
                        mTvReviewMore.setText(String.format("更多评论（%s）", data.getTotal()));
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
        CourseDetailModel.getCourseMember(mCourseId,
                new ResponseCallbackListener<List<CourseMember>>() {
                    @Override
                    public void onSuccess(List<CourseMember> data) {
                        initStudent(data);
                    }

                    @Override
                    public void onFailure(String code, String message) {
                    }
                });
    }

    private void initStudent(List<CourseMember> data) {
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = v.getTag().toString();
                jumpToMember(id);
            }
        };
        for (int i = 0; i < 5; i++) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_detail_avatar, null, false);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, -1);
            params.weight = 1;
            view.setLayoutParams(params);
            ImageView image = (ImageView) view.findViewById(R.id.iv_avatar_icon);
            image.setTag(i);
            image.setOnClickListener(onClickListener);
            TextView txt = (TextView) view.findViewById(R.id.tv_avatar_name);
            txt.setText(data.get(i).user.nickname);
            ImageLoader.getInstance().displayImage(data.get(i).user.avatar, image);
            mStudentIconLayout.addView(view);
        }
    }

    @Override
    protected void refreshView() {
        super.refreshView();
        Course course = mCourseDetail.getCourse();
        mTvTitle.setText(course.title);
        mTvTitleDesc.setText(Html.fromHtml(course.about));
        if (mCourseDetail.getMember() == null) {
            mPriceLayout.setVisibility(View.VISIBLE);
            mVipLayout.setVisibility(View.VISIBLE);
            if (course.price == 0) {
                mTvPriceNow.setText("免费");
                mTvPriceNow.setTextSize(18);
                mTvPriceNow.setTextColor(getResources().getColor(R.color.primary_color));
                mTvPrice1.setVisibility(View.GONE);
            } else {
                mTvPriceNow.setText(String.valueOf(course.price));
                mTvPriceNow.setTextSize(24);
                mTvPriceNow.setTextColor(getResources().getColor(R.color.secondary_color));
                mTvPrice1.setVisibility(View.VISIBLE);
            }
            if (course.originPrice == 0) {
                mTvPriceOld.setVisibility(View.GONE);
            } else {
                mTvPriceOld.setVisibility(View.VISIBLE);
                mTvPriceOld.setText("¥" + course.originPrice);
            }
        } else {
            mPriceLayout.setVisibility(View.GONE);
            mVipLayout.setVisibility(View.GONE);
        }
        mTvTitleStudentNum.setText(String.format("%s名学生",
                course.studentNum));
        mReviewStar.setRating((int) course.rating);
        StringBuilder sb = new StringBuilder();
        int length = course.audiences.length;
        if(length == 0){
            mPeopleLayout.setVisibility(View.GONE);
        }else {
            mPeopleLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < length; i++) {
                sb.append(course.audiences[i]);
                if (i != length - 1) {
                    sb.append("；");
                }
            }
            mTvPeopleDesc.setText(sb.toString());
        }
        if (course.teachers.length == 0) {
            mTeacherLayout.setVisibility(View.GONE);
        } else {
            mTeacherLayout.setVisibility(View.VISIBLE);
            Teacher teacher = course.teachers[0];
            mTeacherId = String.valueOf(teacher.id);
            ImageLoader.getInstance().displayImage(teacher.avatar, mIvTeacherIcon);
            mTvTeacherName.setText(teacher.nickname);
            mTvTeacherDesc.setText(teacher.title);
        }
    }

    @Override
    protected void moreStudent() {

    }

    @Override
    protected void moreReview() {

    }

    @Override
    protected void vipInfo() {

    }

    class ReviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mReviews.size() > 5 ? 5 : mReviews.size();
        }

        @Override
        public Object getItem(int position) {
            return mReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext)
                        .inflate(R.layout.item_detail_review, null, false);
                viewHolder = new ViewHolder();
                viewHolder.mDesc = (TextView) convertView.findViewById(R.id.tv_review_desc);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.tv_review_name);
                viewHolder.mTime = (TextView) convertView.findViewById(R.id.tv_review_time);
                viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.iv_review_icon);
                viewHolder.mStar = (ReviewStarView) convertView.findViewById(R.id.v_review_star);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CourseReview review = mReviews.get(position);
            viewHolder.mDesc.setText(review.getContent());
            viewHolder.mName.setText(review.getUser().nickname);
            viewHolder.mTime.setText(CommonUtil.convertWeekTime(review.getCreatedTime()));
            viewHolder.mStar.setRating((int) Double.parseDouble(review.getRating()));
            ImageLoader.getInstance().displayImage(review.getUser().mediumAvatar, viewHolder.mIcon);
            viewHolder.mIcon.setTag(review.getUser().id);
            viewHolder.mIcon.setOnClickListener(mOnClickListener);
            return convertView;
        }

        private View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = v.getTag().toString();
                jumpToMember(id);
            }
        };
    }

    private static ViewHolder viewHolder;

    class ViewHolder {
        ImageView mIcon;
        TextView mName;
        TextView mTime;
        TextView mDesc;
        ReviewStarView mStar;
    }

    private void jumpToMember(String id){
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/userinfo/%s",
                        id)
        );
        EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity"
                , EdusohoApp.app.mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }
}
