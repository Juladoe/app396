package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.discuss.CourseDiscussAdapter;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.CourseDiscussProvider;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.ui.course.CourseStudyDetailActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.RefreshRecycleView;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by DF on 2017/1/4.
 */

public class CourseDiscussFragment extends Fragment implements MessageEngine.MessageCallback, SwipeRefreshLayout.OnRefreshListener{

    public View mLoadView;
    public String title;
    public CourseDiscussAdapter catalogueAdapter;
    private Queue<WidgetMessage> mUIMessageQueue;
    private int mRunStatus;
    private int mCourseId ;
    private RefreshRecycleView mLvDiscuss;
    private View mEmpty;
    private boolean isJoin;
    private boolean isHave = true;
    private LinearLayout mUnJoinView;
    private int i = 0;
    private int start = 20;
    private CourseStateCallback mCourseStateCallback;
    private SwipeRefreshLayout mSwipe;

    public CourseDiscussFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIMessageQueue = new ArrayDeque<>();
        ((EdusohoApp) getActivity().getApplication()).registMsgSource(this);
        mCourseId = getArguments().getInt(Const.COURSE_ID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCourseStateCallback = (CourseStateCallback) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discuss, null);
        initView(view);
        return view;
    }

    protected void initView(View view) {
        mUnJoinView = (LinearLayout) view.findViewById(R.id.ll_course_catalog_empty);
        mLvDiscuss = (RefreshRecycleView) view.findViewById(R.id.lv_discuss);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mEmpty = view.findViewById(R.id.ll_discuss_empty);
        mSwipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mSwipe.setOnRefreshListener(this);
        if (TextUtils.isEmpty(((EdusohoApp) getActivity().getApplication()).token)) {
            mUnJoinView.setVisibility(View.VISIBLE);
        } else {
            initData();
        }

    }

    public void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        new CourseDiscussProvider(getContext()).getCourseDiscuss(getActivity() instanceof CourseStudyDetailActivity, mCourseId, 0)
        .success(new NormalCallback<DiscussDetail>() {
            @Override
            public void success(DiscussDetail discussDetail) {
                if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                    initDiscuss(discussDetail);
                } else {
                    initDiscuss(null);
                }
            }
        }).fail(new NormalCallback<VolleyError>() {
            @Override
            public void success(VolleyError obj) {
                mLoadView.setVisibility(View.GONE);
                setLessonEmptyViewVisibility(View.VISIBLE);
            }
        });
    }

    private void initDiscuss(final DiscussDetail discussDetail) {
        catalogueAdapter = new CourseDiscussAdapter(discussDetail == null ? null : discussDetail.getResources(), getActivity());
        mLvDiscuss.setLayoutManager(new LinearLayoutManager(getActivity()));
        mLvDiscuss.setAdapter(catalogueAdapter);
        mLoadView.setVisibility(View.GONE);
        if (discussDetail == null) {
            mLvDiscuss.isHave = false;
            mLoadView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
            return;
        }
        mLvDiscuss.setMyRecyclerViewListener(new RefreshRecycleView.MyRecyclerViewListener() {
            @Override
            public void onLoadMore() {
                if (!isHave) {
                    CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_load_data_finish));
                    mLvDiscuss.setLoadMoreComplete();
                    return;
                }
                new CourseDiscussProvider(getContext()).getCourseDiscuss(getActivity() instanceof CourseStudyDetailActivity, mCourseId, start)
                .success(new NormalCallback<DiscussDetail>() {
                    @Override
                    public void success(DiscussDetail discussDetail1) {
                        start += 20;
                        if (discussDetail1.getResources().size() != 0) {
                            catalogueAdapter.mList = discussDetail.getResources();
                            catalogueAdapter.notifyDataSetChanged();
                        } else {
                            isHave = false;
                            mLvDiscuss.isHave = false;
                            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_load_data_finish));
                        }
                        mLvDiscuss.setLoadMoreComplete();
                    }
                }).fail(new NormalCallback<VolleyError>() {
                    @Override
                    public void success(VolleyError obj) {
                        mLvDiscuss.setLoadMoreComplete();
                    }
                });
            }
        });
        catalogueAdapter.setOnItemClickListener(new CourseDiscussAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, DiscussDetail.ResourcesBean resourcesBean) {
                if (mCourseStateCallback.isExpired()) {
                    mCourseStateCallback.handlerCourseExpired();
                    return;
                }
                startThreadActivity(resourcesBean);
            }
        });
    }

    public void setLessonEmptyViewVisibility(int visibility) {
        mEmpty.setVisibility(visibility);
    }

    public void reFreshView(boolean isJoin) {
        this.isJoin = isJoin;
    }

    @Override
    public MessageType[] getMsgTypes() {
        return new MessageType[]{new MessageType(WebViewActivity.SEND_EVENT)};
    }

    @Override
    public int getMode() {
        return 0;
    }

    public void invoke(WidgetMessage message) {
        if (WebViewActivity.SEND_EVENT.equals(message.type.type)) {
            i = 1;
        }
    }

    public void setSwipeToRefreshEnabled(boolean enabled) {
        if (mSwipe != null) {
            mSwipe.setEnabled(enabled);
        }
    }

    public void startThreadActivity(DiscussDetail.ResourcesBean resourcesBean){
        if (isJoin) {
            Bundle bundle = new Bundle();
            bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, getActivity() instanceof CourseStudyDetailActivity ? "course" : "classroom");
            bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, getActivity() instanceof CourseStudyDetailActivity ? Integer.parseInt(resourcesBean.getCourseId())
                                        : Integer.parseInt(resourcesBean.getTargetId()));
            bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(resourcesBean.getId()));
            bundle.putString(AbstractIMChatActivity.TARGET_TYPE, resourcesBean.getType());
            ((EdusohoApp) getActivity().getApplication()).mEngine.runNormalPluginWithBundleForResult("DiscussDetailActivity", getActivity(), bundle, 0);
        } else {
            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_join_look_hint));
        }
    }

    public void onRefresh() {
        new CourseDiscussProvider(getContext()).getCourseDiscuss(getActivity() instanceof CourseStudyDetailActivity, mCourseId, 0)
                .success(new NormalCallback<DiscussDetail>() {
                    @Override
                    public void success(DiscussDetail discussDetail) {
                        mSwipe.setRefreshing(false);
                        if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0 && catalogueAdapter != null) {
                            catalogueAdapter.mList.clear();
                            catalogueAdapter.mList = discussDetail.getResources();
                            catalogueAdapter.notifyDataSetChanged();
                        }
                    }
                }).fail(new NormalCallback<VolleyError>() {
                    @Override
                    public void success(VolleyError obj) {
                        mSwipe.setRefreshing(false);
                        setLessonEmptyViewVisibility(View.VISIBLE);
                    }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mRunStatus = MSG_RESUME;
        if (i == 1) {
            initData();
            mEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mRunStatus = MSG_PAUSE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((EdusohoApp) getActivity().getApplication()).unRegistMsgSource(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        i = 0;
    }
}
