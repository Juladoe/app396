package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Classroom;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomReview;
import com.edusoho.kuozhi.v3.model.bal.course.ClassroomReviewDetail;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReview;
import com.edusoho.kuozhi.v3.model.bal.course.CourseReviewDetail;
import com.edusoho.kuozhi.v3.ui.base.BaseNoTitleActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.SystemBarTintManager;
import com.edusoho.kuozhi.v3.view.ReviewStarView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by remilia on 2017/1/11.
 */
public class AllReviewActivity extends BaseNoTitleActivity {

    private ListView mLvContent;
    private List<CourseReview> mCourseReviews = new ArrayList<>();
    private List<ClassroomReview> mClassroomReviews = new ArrayList<>();
    private CourseReviewAdapter mCourseReviewAdapter;
    private ClassroomReviewAdapter mClassroomReviewAdapter;
    private int mType;
    public static final String TYPE = "type";
    public static final String ID = "id";
    public static final int TYPE_COURSE = 0;
    public static final int TYPE_CLASSROOM = 1;
    private int mPage;
    private int mId;
    private boolean mCanLoad = false;
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(Color.parseColor("#00000000"));
        }

        Intent intent = getIntent();
        mType = intent.getIntExtra(TYPE, TYPE_COURSE);
        mId = intent.getIntExtra(ID, -1);

        initView();
        initData();
    }

    private void initData() {
        mPage = 0;
        if (mType == TYPE_COURSE) {
            CourseDetailModel.getCourseReviews(mId, String.valueOf(10)
                    , String.valueOf(0), new ResponseCallbackListener<CourseReviewDetail>() {
                        @Override
                        public void onSuccess(CourseReviewDetail data) {
                            if (data.getData().size() < 10) {
                                mCanLoad = true;
                            } else {
                                mCanLoad = false;
                            }
                            mCourseReviews.clear();
                            mCourseReviews.addAll(data.getData());
                            mCourseReviewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String code, String message) {

                        }
                    });
        } else {
            CourseDetailModel.getClassroomReviews(String.valueOf(mId), String.valueOf(10)
                    , String.valueOf(0), new ResponseCallbackListener<ClassroomReviewDetail>() {
                        @Override
                        public void onSuccess(ClassroomReviewDetail data) {
                            if (data.getData().size() < 10) {
                                mCanLoad = true;
                            } else {
                                mCanLoad = false;
                            }
                            mClassroomReviews.clear();
                            mClassroomReviews.addAll(data.getData());
                            mClassroomReviewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String code, String message) {

                        }
                    });
        }
    }

    private void addData() {
        if (mType == TYPE_COURSE) {
            CourseDetailModel.getCourseReviews(mId, String.valueOf(10)
                    , String.valueOf(mPage*10), new ResponseCallbackListener<CourseReviewDetail>() {
                        @Override
                        public void onSuccess(CourseReviewDetail data) {
                            if (data.getData().size() < 10) {
                                mCanLoad = true;
                            } else {
                                mCanLoad = false;
                            }
                            mCourseReviews.addAll(data.getData());
                            mCourseReviewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String code, String message) {

                        }
                    });
        } else {
            CourseDetailModel.getClassroomReviews(String.valueOf(mId), String.valueOf(10)
                    , String.valueOf(mPage*10), new ResponseCallbackListener<ClassroomReviewDetail>() {
                        @Override
                        public void onSuccess(ClassroomReviewDetail data) {
                            if (data.getData().size() < 10) {
                                mCanLoad = true;
                            } else {
                                mCanLoad = false;
                            }
                            mClassroomReviews.addAll(data.getData());
                            mClassroomReviewAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String code, String message) {

                        }
                    });
        }
    }

    @Override
    protected void initView() {
        super.initView();
        mLvContent = (ListView) findViewById(R.id.lv_content);
        if (mType == TYPE_COURSE) {
            mCourseReviewAdapter = new CourseReviewAdapter();
            mLvContent.setAdapter(mCourseReviewAdapter);
        } else {
            mClassroomReviewAdapter = new ClassroomReviewAdapter();
            mLvContent.setAdapter(mClassroomReviewAdapter);
        }
    }


    class CourseReviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCourseReviews.size();
        }

        @Override
        public Object getItem(int position) {
            return mCourseReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mCanLoad && position == getCount() - 1) {
                mPage++;
                mCanLoad = false;
                addData();
            }
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
            CourseReview review = mCourseReviews.get(position);
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

    class ClassroomReviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mClassroomReviews.size();
        }

        @Override
        public Object getItem(int position) {
            return mClassroomReviews.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mCanLoad && position == getCount() - 1) {
                mPage++;
                mCanLoad = false;
                addData();
            }
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
            ClassroomReview review = mClassroomReviews.get(position);
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
        CoreEngine.create(mContext).runNormalPlugin("WebViewActivity"
                , this, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        startIntent.putExtra(Const.WEB_URL, url);
                    }
                });
    }
}
