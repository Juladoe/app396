package com.edusoho.kuozhi.adapter.Course;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.RecyclerViewListBaseAdapter;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.util.AppUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by howzhi on 14/12/2.
 */
public class CourseReviewAdapter
        extends RecyclerViewListBaseAdapter<Review, CourseReviewAdapter.ViewHolder> {

    private ActionBarBaseActivity mActivity;
    private DisplayImageOptions mOptions;
    public CourseReviewAdapter(ActionBarBaseActivity activity, int resource)
    {
        super(activity, resource);
        this.mActivity = activity;
        mOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).build();
    }

    @Override
    public void addItem(Review item) {
        if (mList.add(item)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void addItems(List<Review> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }
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
        Review review = mList.get(i);

        viewHolder.mCommitTime.setText(AppUtil.coverTime(review.createdTime));
        viewHolder.mNickname.setText(review.user.nickname);
        viewHolder.mUserMessage.setText(review.content);
        viewHolder.mRating.setRating((float)review.rating);
        ImageLoader.getInstance().displayImage(review.user.avatar, viewHolder.mUserAvatar, mOptions);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mUserMessage;
        public TextView mCommitTime;
        public TextView mNickname;
        public RatingBar mRating;
        public ImageView mUserAvatar;
        public ViewHolder(View view){
            super(view);

            mRating = (RatingBar) view.findViewById(R.id.review_user_rating);
            mCommitTime = (TextView) view.findViewById(R.id.review_user_time);
            mNickname = (TextView) view.findViewById(R.id.review_user_nickname);
            mUserAvatar = (ImageView) view.findViewById(R.id.review_user_face);
            mUserMessage = (TextView) view.findViewById(R.id.course_userinfo_message);
        }
    }
}
