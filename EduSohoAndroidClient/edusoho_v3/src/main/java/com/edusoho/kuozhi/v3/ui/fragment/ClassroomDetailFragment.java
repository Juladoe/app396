package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.entity.course.ClassroomDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomMember;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomReview;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomReviewDetail;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReview;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.CourseUtil;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */

public class ClassroomDetailFragment extends BaseDetailFragment {

    private String mClassroomId;
    private ClassroomDetail mClassroomDetail;
    private List<ClassroomReview> mReviews = new ArrayList<>();
    private ReviewAdapter mAdapter;

    public ClassroomDetailFragment() {
    }

    public ClassroomDetailFragment(String courseId) {
        this.mClassroomId = courseId;
    }

    public void setClassroomId(String classroomId) {
        this.mClassroomId = classroomId;
        initData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mClassroomId = getArguments().getString("id");
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mAdapter = new ReviewAdapter();
        mVipLayout.setVisibility(View.GONE);
        mLvReview.setAdapter(mAdapter);
        mTvStudent1.setText(R.string.txt_classroom_student);
        mTvReview1.setText(R.string.txt_classroom_review);
        initEvent();
        initData();
    }

    protected void initData() {
        setLoadViewStatus(View.VISIBLE);
        CourseDetailModel.getClassroomDetail(mClassroomId, new ResponseCallbackListener<ClassroomDetail>() {
            @Override
            public void onSuccess(ClassroomDetail data) {
                setLoadViewStatus(View.GONE);
                mClassroomDetail = data;
                refreshView();
            }

            @Override
            public void onFailure(String code, String message) {
                setLoadViewStatus(View.GONE);
            }
        });
        CourseDetailModel.getClassroomReviews(mClassroomId, "5", "0",
                new ResponseCallbackListener<ClassroomReviewDetail>() {
                    @Override
                    public void onSuccess(ClassroomReviewDetail data) {
                        mReviews.clear();
                        int length = data.getData().size();
                        for (int i = 0; i < length; i++) {
                            if (!data.getData().get(i).parentId.equals("0")) {
                                data.getData().remove(i);
                                i--;
                                length--;
                            }
                        }
                        mTvReviewNum.setText(String.format("(%s)", data.getTotal()));
                        if (data.getData().size() == 0) {
                            mReviewNoneLayout.setVisibility(View.VISIBLE);
                            mTvReviewMore.setVisibility(View.GONE);
                        } else {
                            mReviewNoneLayout.setVisibility(View.GONE);
                            mReviews.addAll(data.getData());
                            if (mReviews.size() < 5) {
                                mTvReviewMore.setVisibility(View.GONE);
                            } else {
                                mTvReviewMore.setVisibility(View.VISIBLE);
                                mTvReviewMore.setText("更多评价");
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
        CourseDetailModel.getClassroomMember(mClassroomId,
                new ResponseCallbackListener<List<ClassroomMember>>() {
                    @Override
                    public void onSuccess(List<ClassroomMember> data) {
                        initStudent(data);
                    }

                    @Override
                    public void onFailure(String code, String message) {

                    }
                });
    }

    private void initStudent(List<ClassroomMember> data) {
        View.OnClickListener onClickListener =
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = String.valueOf(v.getTag());
                        jumpToMember(id);
                    }
                };
        if (data.size() == 0) {
            mTvStudentNone.setVisibility(View.VISIBLE);
        } else {
            mTvStudentNone.setVisibility(View.GONE);
        }
        for (int i = 0; i < 5; i++) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_detail_avatar, null, false);
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(0, -1);
            params.weight = 1;
            view.setLayoutParams(params);
            ImageView image = (ImageView) view.findViewById(R.id.iv_avatar_icon);
            TextView txt = (TextView) view.findViewById(R.id.tv_avatar_name);
            if (data.size() > i) {
                image.setTag(data.get(i).userId);
                image.setOnClickListener(onClickListener);
                txt.setText(data.get(i).user.nickname);
                ImageLoader.getInstance().displayImage(data.get(i).user.getAvatar(), image,
                        app.mAvatarOptions);
            } else {
                txt.setText("");
                image.setImageAlpha(0);
            }
            mStudentIconLayout.addView(view);
        }
    }

    @Override
    protected void refreshView() {
        super.refreshView();
        Classroom classRoom = mClassroomDetail.getClassRoom();
        mTvTitle.setText(classRoom.title);
        mTvTitleDesc.setHtml(classRoom.about.toString(), new HtmlHttpImageGetter(mTvTitleDesc));
        mTvStudentNum.setText(String.format("(%s)", mClassroomDetail.getClassRoom().studentNum));
        if (mClassroomDetail.getMember() == null) {
            mPriceLayout.setVisibility(View.VISIBLE);
            mVipLayout.setVisibility(View.GONE);
            if (classRoom.price == 0) {
                mTvPriceNow.setText(R.string.txt_free);
                mTvPriceNow.setTextSize(18);
                mTvPriceNow.setTextColor(getResources().getColor(R.color.primary_color));
                mTvPrice1.setVisibility(View.GONE);
            } else {
                mTvPriceNow.setText(String.valueOf(classRoom.price));
                mTvPriceNow.setTextSize(24);
                mTvPriceNow.setTextColor(getResources().getColor(R.color.secondary_color));
                mTvPrice1.setVisibility(View.VISIBLE);
            }
            mTvPriceOld.setVisibility(View.GONE);
        } else {
            mPriceLayout.setVisibility(View.GONE);
            mVipLayout.setVisibility(View.GONE);
        }
        mTvTitleStudentNum.setText(String.format("%s名学生",
                classRoom.studentNum));
        try {
            mReviewStar.setRating((int) Double.parseDouble(classRoom.rating));
        } catch (Exception e) {

        }
        mPeopleLayout.setVisibility(View.GONE);
        if (classRoom.teachers.length == 0) {
            mTeacherLayout.setVisibility(View.GONE);
        } else {
            mTeacherLayout.setVisibility(View.VISIBLE);
            Teacher teacher = classRoom.teachers[0];
            mTeacherId = String.valueOf(teacher.id);
            ImageLoader.getInstance().displayImage(teacher.getAvatar(), mIvTeacherIcon, app.mAvatarOptions);
            mTvTeacherName.setText(teacher.nickname);
            mTvTeacherDesc.setText(teacher.title);
        }
    }

    @Override
    protected void moreStudent() {
        final String url = String.format(
                Const.MOBILE_APP_URL,
                EdusohoApp.app.schoolHost,
                String.format("main#/studentlist/%s/%s",
                        "classroom", mClassroomId)
        );
        EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity"
                , EdusohoApp.app.mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }

    @Override
    protected void moreReview() {

    }

    @Override
    protected void vipInfo() {
        if (EdusohoApp.app.loginUser == null) {
            CourseUtil.notLogin();
            return;
        }
        final String url = String.format(
                Const.MOBILE_APP_URL,
                app.schoolHost,
                "main#/viplist"
        );
        EdusohoApp.app.mEngine.runNormalPlugin("WebViewActivity"
                , EdusohoApp.app.mActivity, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
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
            ClassroomReview review = mReviews.get(position);
            viewHolder.mDesc.setText(review.getContent());
            viewHolder.mName.setText(review.getUser().nickname);
            viewHolder.mTime.setText(CommonUtil.convertWeekTime(review.getCreatedTime()));
            viewHolder.mStar.setRating((int) Double.parseDouble(review.getRating()));
            ImageLoader.getInstance().displayImage(review.getUser().getMediumAvatar(), viewHolder.mIcon,
                    app.mAvatarOptions);
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

    private void jumpToMember(String id) {
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
