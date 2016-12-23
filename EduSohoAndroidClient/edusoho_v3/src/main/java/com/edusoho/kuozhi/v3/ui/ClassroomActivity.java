package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.ClassroomDetail;
import com.edusoho.kuozhi.v3.entity.lesson.LessonItem;
import com.edusoho.kuozhi.v3.listener.PluginFragmentCallback;
import com.edusoho.kuozhi.v3.listener.PluginRunCallback;
import com.edusoho.kuozhi.v3.listener.ResponseCallbackListener;
import com.edusoho.kuozhi.v3.model.bal.Member;
import com.edusoho.kuozhi.v3.model.bal.Teacher;
import com.edusoho.kuozhi.v3.model.bal.course.CourseDetailModel;
import com.edusoho.kuozhi.v3.plugin.ShareTool;
import com.edusoho.kuozhi.v3.ui.fragment.ClassCatalogFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.ClassroomUtil;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
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
            return;
        }
        mMediaViewHeight = AppUtil.px2dp(this, (float) AppUtil.getWidthPx(this) / 4f * 3f);
        initView();
        initEvent();
        initData();
    }

    @Override
    protected void initView() {
        super.initView();
        mCollect.setVisibility(View.GONE);
        mPlayButtonLayout.setVisibility(View.GONE);
        mPlayLayout2.setVisibility(View.GONE);
        mTvInclass.setVisibility(View.GONE);
        mTvAdd.setText(R.string.txt_add_class);
        mTvCatalog.setText(R.string.class_catalog);
    }

    @Override
    protected void initFragment(List<Fragment> fragments) {
        Fragment fragment = app.mEngine.runPluginWithFragment("ClassroomDetailFragment", this, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString("id", mClassroomId);
            }
        });
        fragments.add(fragment);
        Fragment catafragment = app.mEngine.runPluginWithFragment("ClassCatalogFragment", this, new PluginFragmentCallback() {
            @Override
            public void setArguments(Bundle bundle) {
                bundle.putString("id", mClassroomId);
            }
        });
        fragments.add(catafragment);
    }

    protected void initEvent() {
        super.initEvent();
    }

    protected void initData() {
        if (mClassroomId != null) {
            setLoadStatus(View.VISIBLE);
            CourseDetailModel.getClassroomDetail(mClassroomId,
                    new ResponseCallbackListener<ClassroomDetail>() {
                        @Override
                        public void onSuccess(ClassroomDetail data) {
                            setLoadStatus(View.GONE);
                            mClassroomDetail = data;
                            if (mFragments.size() >= 2 && mFragments.get(1) != null
                                    && mFragments.get(1) instanceof ClassCatalogFragment) {
                                if (mClassroomDetail.getMember() == null) {
                                    ((ClassCatalogFragment) mFragments.get(1)).reFreshView(false);
                                } else {
                                    ((ClassCatalogFragment) mFragments.get(1)).reFreshView(true);
                                    tabPage(300);
                                }
                            }
                            refreshView();
                        }

                        @Override
                        public void onFailure(String code, String message) {
                            setLoadStatus(View.GONE);
                            if (message != null && message.equals("班级不存在")) {
                                CommonUtil.shortToast(ClassroomActivity.this, "班级不存在");
                                finish();
                            }
                        }
                    });
        }
    }

    @Override
    protected void refreshView() {
        DisplayImageOptions imageOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.default_classroom)
                .showImageOnFail(R.drawable.default_classroom)
                .showImageOnLoading(R.drawable.default_classroom)
                .build();
        ImageLoader.getInstance().displayImage(
                mClassroomDetail.getClassRoom().getLargePicture(),
                mIvMediaBackground, imageOptions);
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
            initViewPager();
        }
    }

    @Override
    protected void goClass() {
        app.mEngine.runNormalPlugin("ClassroomDiscussActivity", mContext, new PluginRunCallback() {
            @Override
            public void setIntentDate(Intent startIntent) {
                startIntent.putExtra(ClassroomDiscussActivity.FROM_ID, Integer.parseInt(mClassroomId));
                startIntent.putExtra(ClassroomDiscussActivity.FROM_NAME, mClassroomDetail.getClassRoom().title);
            }
        });
    }

    @Override
    protected void consult() {
        Teacher[] teachers = mClassroomDetail.getClassRoom().teachers;
        final Teacher teacher;
        if (teachers.length > 0) {
            teacher = teachers[0];
        } else {
            CommonUtil.shortToast(this, "班级目前没有老师");
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
            showProcessDialog();
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
                            hideProcesDialog();
                            CommonUtil.shortToast(ClassroomActivity.this, getResources()
                                    .getString(R.string.success_add_classroom));
                            initData();
                        }

                        @Override
                        public void onAddClassroomError(String error) {
                            hideProcesDialog();
                        }
                    });
        }
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
    protected void courseChange(LessonItem lessonItem) {
    }
}
