package com.edusoho.kuozhi.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.model.Review;
import com.edusoho.kuozhi.model.Teacher;
import com.edusoho.kuozhi.util.AppUtil;

import java.util.ArrayList;

public class ReviewListAdapter extends ListBaseAdapter<Review> {

	public ReviewListAdapter(Context context,  int resource) {
		super(context, resource);
	}

    @Override
    public void addItems(ArrayList<Review> list) {
        mList.addAll(list);
        notifyDataSetChanged();
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
			view = inflater.inflate(mResource, null);
			holder = new ViewHolder();
			holder.course_comment_rating = (RatingBar) view.findViewById(R.id.review_user_rating);
			holder.course_comment_user_time = (TextView) view.findViewById(R.id.review_user_time);
			holder.course_comment_user_nickname = (TextView) view.findViewById(R.id.review_user_nickname);
			holder.course_comment_user_avatar = (ImageView) view.findViewById(R.id.review_user_face);
			holder.course_comment_user_message = (TextView) view.findViewById(R.id.course_userinfo_message);
			holder.aq = new AQuery(view);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
        Review item = mList.get(index);
		Teacher user = item.user;

		holder.course_comment_user_time.setText(AppUtil.coverTime(item.createdTime));
		holder.course_comment_user_message.setText(item.content);
		holder.course_comment_user_nickname.setText(user.nickname);
		holder.course_comment_rating.setRating((float) item.rating);
		holder.aq.id(R.id.review_user_face).image(
                user.avatar, false, true, 0, R.drawable.course_teacher_avatar);

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
