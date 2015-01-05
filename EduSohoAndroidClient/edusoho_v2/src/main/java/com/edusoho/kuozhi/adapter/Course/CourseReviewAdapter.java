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
        extends RecyclerViewListBaseAdapter<Review, CourseReviewAdapter.BaseViewHolder> {

    private ActionBarBaseActivity mActivity;
    private View mFooterView;
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
        if (mFooterView != null) {
            mList.add(null);
            notifyDataSetChanged();
        }
    }

    public void addFooterView(View footerView)
    {
        mFooterView = footerView;
    }

    public void setFooterVisible(int visible)
    {
        mFooterView.setVisibility(visible);
    }

    @Override
    public void addItems(List<Review> list) {
        if (mList.addAll(list)) {
            notifyDataSetChanged();
        }

        if (mFooterView != null) {
            mList.add(null);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mFooterView != null && position == (mList.size() - 1)) {
            return VIEW_TYPE_FOOTER;
        }
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {
        View v = LayoutInflater.from(mContext).inflate(mResource, viewGroup, false);

        switch (type) {
            case VIEW_TYPE_HEADER:
                return new HeadViewHolder(mHeadView);
            case VIEW_TYPE_FOOTER:
                return new HeadViewHolder(mFooterView);
            case VIEW_TYPE_CONTENT:
                return new ViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder vh, int i) {
        if (getItemViewType(i) == VIEW_TYPE_HEADER
                || getItemViewType(i) == VIEW_TYPE_FOOTER) {
            return;
        }
        super.onBindViewHolder(vh, i);
        Review review = mList.get(i);

        ViewHolder viewHolder = (ViewHolder) vh;
        viewHolder.mCommitTime.setText(AppUtil.coverTime(review.createdTime));
        viewHolder.mNickname.setText(review.user.nickname);
        viewHolder.mUserMessage.setText(review.content);
        viewHolder.mRating.setRating((float)review.rating);
        ImageLoader.getInstance().displayImage(review.user.avatar, viewHolder.mUserAvatar, mOptions);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder
    {
        public BaseViewHolder(View view) {
            super(view);
        }
    }

    public class ViewHolder extends BaseViewHolder
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

    public class HeadViewHolder extends BaseViewHolder
    {
        public HeadViewHolder(View view) {
            super(view);
        }
    }
}
