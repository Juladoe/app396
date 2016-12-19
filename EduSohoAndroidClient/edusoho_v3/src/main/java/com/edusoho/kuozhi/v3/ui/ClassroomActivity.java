package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.ClassroomDetail;
import com.edusoho.kuozhi.v3.entity.lesson.CourseCatalogue;
import com.edusoho.kuozhi.v3.entity.course.CourseDetail;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.ClassCatalogFragment;
import com.edusoho.kuozhi.v3.ui.fragment.ClassroomDetailFragment;
import com.edusoho.kuozhi.v3.util.ClassroomUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Zhang on 2016/12/8.
 */
public class ClassroomActivity extends DetailActivity implements View.OnClickListener {
    public static final String CLASSROOM_ID = "Classroom_id";
    private String mClassroomId;
    private ClassroomDetail mClassroomDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mClassroomId = intent.getStringExtra(CLASSROOM_ID);
        if (mClassroomId == null || mClassroomId.trim().length() == 0) {
            finish();
        }
        mMediaViewHeight = 281;
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mCollect.setVisibility(View.GONE);
        mPlayLayout.setVisibility(View.GONE);
        mTvInclass.setVisibility(View.GONE);
        mPlayLayout2.setVisibility(View.GONE);
    }

    @Override
    protected void initFragment(List<Fragment> fragments) {
        fragments.add(new ClassroomDetailFragment(mClassroomId));
        fragments.add(new ClassCatalogFragment(mClassroomId));
    }

    protected void initEvent() {
        super.initEvent();
    }

    protected void initData() {
        if (mClassroomId != null) {
            mLoading.show();
            CourseDetailModel.getClassroomDetail(mClassroomId,
                    new ResponseCallbackListener<ClassroomDetail>() {
                        @Override
                        public void onSuccess(ClassroomDetail data) {
                            mLoading.dismiss();
                            mClassroomDetail = data;
                            refreshView();
                        }

                        @Override
                        public void onFailure(String code, String message) {
                            mLoading.dismiss();
                            if (message != null && message.equals("班级不存在")) {
                                CommonUtil.shortToast(ClassroomActivity.this, "班级不存在");
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    protected CourseDetail getCourseDetail() {
        return null;
    }

    @Override
    protected void refreshView() {
        ImageLoader.getInstance().displayImage(
                mClassroomDetail.getClassRoom().largePicture,
                mIvMediaBackground);
        Member member = mClassroomDetail.getMember();
        if (member == null) {
            mIsMemder = false;
            mBottomLayout.setVisibility(View.VISIBLE);
            mIvGrade.setVisibility(View.GONE);
            mIvGrade2.setVisibility(View.GONE);
            initViewPager();
        } else {
            mIsMemder = true;
            mBottomLayout.setVisibility(View.GONE);
            mIvGrade.setVisibility(View.VISIBLE);
            mIvGrade2.setVisibility(View.VISIBLE);
            initViewPager();
        }
    }
    @Override
    protected void consult() {
        Teacher[] teachers = mClassroomDetail.getClassRoom().teachers;
        final Teacher teacher;
        if (teachers.length > 0) {
            teacher = teachers[0];
        } else {
            CommonUtil.shortToast(this,"班级目前没有老师");
            return;
        }
        app.mEngine.runNormalPlugin("ImChatActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    @Override
    protected void add() {
        if (mClassroomId != null) {
            mLoading.show();
            ClassroomUtil.addClassroom(new ClassroomUtil.ClassroomParamsBuilder()
                            .setCouponCode("")
                            .setPayment("")
                            .setPayPassword("")
                            .setTargetId(String.valueOf(mClassroomDetail.getClassRoom().id))
                            .setTargetType("Classroom")
                            .setTotalPrice(String.valueOf(mClassroomDetail.getClassRoom().price))
                    , new ClassroomUtil.OnAddClassroomListener() {
                        @Override
                        public void onAddClassroomSuccee(String response) {
                            mLoading.dismiss();
                            CommonUtil.shortToast(ClassroomActivity.this, getResources()
                                    .getString(R.string.success_add_classroom));
                            initData();
                        }

                        @Override
                        public void onAddClassroomError(String error) {
                            mLoading.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void collect() {
        Teacher[] teachers = mClassroomDetail.getClassRoom().teachers;
        final Teacher teacher;
        if (teachers.length > 0) {
            teacher = teachers[0];
        } else {
            /**
             * todo 老师为空的时候
             */
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString(ImChatActivity.FROM_NAME, teacher.nickname);
        bundle.putInt(ImChatActivity.FROM_ID, teacher.id);
        bundle.putString(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
        app.mEngine.runNormalPlugin("ImChatActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ImChatActivity.FROM_NAME, teacher.nickname);
                startIntent.putExtra(ImChatActivity.FROM_ID, teacher.id);
                startIntent.putExtra(ImChatActivity.HEAD_IMAGE_URL, teacher.avatar);
            }
        });
    }

    @Override
    protected void share() {
        if (mClassroomDetail == null) {
            return;
        }
        final ShareTool shareTool =
                new ShareTool(this
                        , app.host + "/Classroom/" + mClassroomDetail.getClassRoom().id
                        , mClassroomDetail.getClassRoom().title
                        , mClassroomDetail.getClassRoom().about.toString().length() > 20 ?
                        mClassroomDetail.getClassRoom().about.toString().substring(0, 20)
                        : mClassroomDetail.getClassRoom().about.toString()
                        , mClassroomDetail.getClassRoom().largePicture);
        new Handler((mActivity.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                shareTool.shardCourse();
            }
        });
    }

    @Override
    protected void courseStart() {
        super.courseStart();
    }

    @Override
    protected void courseChange(CourseCatalogue.LessonsBean lesson) {

    }
}
