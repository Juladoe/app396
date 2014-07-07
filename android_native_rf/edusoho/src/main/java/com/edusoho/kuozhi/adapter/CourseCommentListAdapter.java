package com.edusoho.kuozhi.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.EdusohoApp;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;

public class CourseCommentListAdapter extends BaseAdapter {

    public Review loginUserComment;
    public int mLoginUserReviewIndex = -1;
	private LayoutInflater inflater;
	private int mResouce;
	private Context mContext;
	private ArrayList<Review> mList;

	public CourseCommentListAdapter(Context context, Review[] list, int resource) {
		mList = new ArrayList<Review>();
		listAddItem(list);
		mContext = context;
		mResouce = resource;
		inflater = LayoutInflater.from(context);
	}

    public void setData(Review[] list)
    {
        mList.clear();
        listAddItem(list);
    }

    public void setLoginUserComment(Review review)
    {
        if (mLoginUserReviewIndex != -1) {
            mList.remove(mLoginUserReviewIndex);
        } else {
            mLoginUserReviewIndex = 0;
        }
        mList.add(mLoginUserReviewIndex, review);
        notifyDataSetChanged();
    }

	/**
	 * 
	 *
	*/
	private void listAddItem(Review[] courseCommentItems)
	{
		for (Review item : courseCommentItems) {
			mList.add(item);
		}
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int index) {
		return mList.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int index, View view, ViewGroup vg) {
		ViewHolder holder;
		if (view == null) {
			view = inflater.inflate(mResouce, null);
			holder = new ViewHolder();
			holder.course_comment_rating = (RatingBar) view.findViewById(R.id.course_comment_rating);
			holder.course_comment_user_time = (TextView) view.findViewById(R.id.course_comment_user_time);
			holder.course_comment_user_nickname = (TextView) view.findViewById(R.id.course_comment_user_nickname);
			holder.course_comment_user_avatar = (ImageView) view.findViewById(R.id.course_comment_user_avatar);
			holder.course_comment_user_message = (TextView) view.findViewById(R.id.course_comment_user_message);
			holder.aq = new AQuery(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
        Review item = mList.get(index);
		Teacher user = item.user;

        if (EdusohoApp.app.loginUser != null
                &&user.id == EdusohoApp.app.loginUser.id) {
            loginUserComment = item;
            mLoginUserReviewIndex = index;
        }

		holder.course_comment_user_time.setText(AppUtil.coverTime(item.createdTime));
		holder.course_comment_user_message.setText(item.content);
		holder.course_comment_user_nickname.setText(user.nickname);
		holder.course_comment_rating.setRating((float) item.rating);
		
		holder.aq.id(R.id.course_comment_user_avatar).image(user.avatar, false, true);

		return view;
	}

	private class ViewHolder {
		public AQuery aq;
		public TextView course_comment_user_message;
		public TextView course_comment_user_time;
		public TextView course_comment_user_nickname;
		public RatingBar course_comment_rating;
		public ImageView course_comment_user_avatar;
	}

}
