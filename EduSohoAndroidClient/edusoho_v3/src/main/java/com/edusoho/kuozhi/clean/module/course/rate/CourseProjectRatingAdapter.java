package com.edusoho.kuozhi.clean.module.course.rate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.bean.Review;
import com.edusoho.kuozhi.clean.utils.TimeUtils;
import com.edusoho.kuozhi.clean.utils.StringUtils;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.view.circleImageView.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JesseHuang on 2017/4/4.
 */

public class CourseProjectRatingAdapter extends RecyclerView.Adapter<CourseProjectRatingAdapter.ViewHolder> {
    private Context mContext;
    private List<Review> mReviews = new ArrayList<>();

    public CourseProjectRatingAdapter(Context context) {
        this.mContext = context;
    }

    public void addDatas(List<Review> reviews) {
        mReviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @Override
    public CourseProjectRatingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_course_project_rate, parent, false);
        return new CourseProjectRatingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CourseProjectRatingAdapter.ViewHolder holder, int position) {
        Review review = mReviews.get(position);
        ImageLoader.getInstance().displayImage(review.user.userAvatar.medium, holder.userAvatar, EdusohoApp.app.mAvatarOptions);
        holder.username.setText(review.user.nickname);
        holder.courseRating.setRating(review.rating);
        String postTime = TimeUtils.getPostDays(StringUtils.isEmpty(review.updatedTime) ? review.createdTime : review.updatedTime);
        holder.postTime.setText(postTime);
        holder.ratingContent.setText(review.content);
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView userAvatar;
        public TextView username;
        public RatingBar courseRating;
        public TextView postTime;
        public TextView ratingContent;

        public ViewHolder(View view) {
            super(view);
            userAvatar = (CircularImageView) view.findViewById(R.id.civ_user_avatar);
            username = (TextView) view.findViewById(R.id.tv_user_name);
            courseRating = (RatingBar) view.findViewById(R.id.rb_rating);
            postTime = (TextView) view.findViewById(R.id.tv_post_time);
            ratingContent = (TextView) view.findViewById(R.id.tv_rate_content);
        }
    }
}
