package com.edusoho.kuozhi.clean.module.course.task.menu.discuss;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.clean.module.base.BaseActivity;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.core.CoreEngine;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.ThreadCreateActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by DF on 2017/4/24.
 */

public class DiscussActivity extends BaseActivity<DiscussContract.Presenter>
                                implements DiscussContract.View, View.OnClickListener{

    private static final String COURSE_PROJECT_ID = "course_project_id";

    private PopupWindow mPopupWindow;
    private View mTopic;
    private View mQuestion;
    private View mEmpty;
    private Toolbar mToolbar;
    private RecyclerView mContent;
    private TextView mEditTopic;
    private SwipeRefreshLayout mRefresh;

    private boolean mIsAdd;
    private int mCourseProjectId;
    private DiscussAdapter mAdapter;

    private DiscussContract.Presenter mPresenter;

    public static void launch(Context context, int courseProjectId){
        Intent intent = new Intent();
        intent.putExtra(COURSE_PROJECT_ID, courseProjectId);
        intent.setClass(context, DiscussActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);

        mCourseProjectId = getIntent().getIntExtra(COURSE_PROJECT_ID, 0);
        if (mCourseProjectId == 0) {
            showToast(R.string.discuss_no_exist);
            finish();
            return;
        }
        initView();
        initEvent();
    }

    private void initView() {
        mEmpty = findViewById(R.id.ll_discuss_empty);
        mRefresh = (SwipeRefreshLayout) findViewById(R.id.sl_refresh);
        mContent = (RecyclerView) findViewById(R.id.rv);
        mEditTopic = (TextView) findViewById(R.id.tv_edit_topic);
        mToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        mContent.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DiscussAdapter(this);
        mContent.setAdapter(mAdapter);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        mRefresh.setColorSchemeResources(R.color.primary_color);
        mRefresh.setRefreshing(true);

        mPresenter = new DiscussPresenter(this, mCourseProjectId);
        mPresenter.subscribe();
    }

    private void initEvent() {
        mEditTopic.setOnClickListener(this);
        mToolbar.setNavigationOnClickListener(this);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

            }
        });
    }

    @Override
    public void setEmptyView(boolean isShow) {
        mEmpty.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setSwipeView(boolean isShow) {
        mRefresh.setRefreshing(isShow);
    }

    @Override
    public void showCompleteView(List<DiscussDetail.ResourcesBean> list) {
        mAdapter.setDataAndNotifyData(list);
    }

    private void goToThreadCreateActivity(String type) {
//        if (mCourseDetail != null && validCourseIsExpird(mCourseDetail.getMember())) {
//            showCourseExpireDlg();
//            return;
//        }
        Bundle bundle = new Bundle();
        bundle.putInt(ThreadCreateActivity.TARGET_ID, mCourseProjectId);
        bundle.putString(ThreadCreateActivity.TARGET_TYPE, "");
        bundle.putString(ThreadCreateActivity.TYPE, "question".equals(type) ? "question" : "discussion");
        bundle.putString(ThreadCreateActivity.THREAD_TYPE, "course");
        ((EdusohoApp) getApplication()).mEngine.runNormalPluginWithBundle("ThreadCreateActivity", this, bundle);
    }

    @Override
    public void goToDiscussDetailActivity(DiscussDetail.ResourcesBean resourcesBean) {
            Bundle bundle = new Bundle();
            bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, "course");
            bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, Integer.parseInt(resourcesBean.getCourseId()));
            bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(resourcesBean.getId()));
            bundle.putString(AbstractIMChatActivity.TARGET_TYPE, resourcesBean.getType());
            CoreEngine.create(this).runNormalPluginWithBundle("DiscussDetailActivity", this, bundle);
    }

    private void showEditPop() {
        MobclickAgent.onEvent(this, "courseDetailsPage_Q&A_launchButton");
        if (!mIsAdd) {
            mIsAdd = true;
            View popupView = getLayoutInflater().inflate(R.layout.dialog_discuss_publish, null);
            mPopupWindow = new PopupWindow(popupView, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            mTopic = popupView.findViewById(R.id.tv_topic);
            mTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(DiscussActivity.this, "courseDetailsPage_Q&A_topic");
                    goToThreadCreateActivity("discussion");
                    mPopupWindow.dismiss();
                }
            });
            mQuestion = popupView.findViewById(R.id.tv_question);
            mQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(DiscussActivity.this, "courseDetailsPage_questionsAnswers");
                    goToThreadCreateActivity("question");
                    mPopupWindow.dismiss();
                }
            });
            popupView.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPopupWindow.dismiss();
                }
            });
        }
        mPopupWindow.showAsDropDown(mEditTopic, 0, -AppUtil.dp2px(this, 204));
        startAnimation();
    }

    public void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mQuestion, "translationY", 0, -AppUtil.dp2px(DiscussActivity.this, 73));
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mTopic, "translationY", 0, -AppUtil.dp2px(DiscussActivity.this, 146));
        animator.setInterpolator(new LinearInterpolator());
        animator1.setInterpolator(new LinearInterpolator());
        animator.setDuration(150);
        animator1.setDuration(300);
        animator.start();
        animator1.start();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tv_edit_topic) {
            showEditPop();
        } else if(id == R.id.tb_toolbar){
            finish();
        }
    }
}
