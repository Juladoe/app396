package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.EdusohoApp;
import com.edusoho.kuozhi.v3.adapter.CatalogueAdapter;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.RefreshListView;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by DF on 2017/1/4.
 */

public class CourseDiscussFragment extends Fragment implements MessageEngine.MessageCallback{

    public View mLoadView;
    public String title;
    public DiscussDetail discussDetail;
    public CatalogueAdapter catalogueAdapter;
    private String mCouseId ;
    private View mView;
    private RefreshListView mLvDiscuss;
    private View mEmpty;
    private boolean isJoin;
    private TextView mTvEmpty;
    private LinearLayout mUnJoinView;
    private int i = 0;
    private CourseStateCallback mCourseStateCallback;
    protected Queue<WidgetMessage> mUIMessageQueue;
    protected int mRunStatus;

    public CourseDiscussFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIMessageQueue = new ArrayDeque<>();
        EdusohoApp.app.registMsgSource(this);
        EdusohoApp.app.registMsgSource(this);
        mCouseId = getArguments().getString("id");
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
        mLvDiscuss = (RefreshListView) view.findViewById(R.id.lv_discuss);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mEmpty = view.findViewById(R.id.ll_discuss_empty);
        mTvEmpty = (TextView) view.findViewById(R.id.tv_empty);
        if (TextUtils.isEmpty(EdusohoApp.app.token)) {
            mUnJoinView.setVisibility(View.VISIBLE);
        } else {
            initData();
        }

    }

    // TODO: 17/1/18 @杜樊
    public void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        RequestUrl requestUrl = EdusohoApp.app.bindNewUrl(String.format(getActivity() instanceof CourseActivity ? Const.LESSON_DISCUSS : Const.CLASS_DISCUSS, mCouseId, mCouseId,0), true);
        EdusohoApp.app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                discussDetail = EdusohoApp.app.parseJsonValue(response, new TypeToken<DiscussDetail>() {});
                if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                    if (discussDetail.getResources().size() < 20) {
                        mLvDiscuss.setRequest(false);
                    }
                    initDiscuss();
                } else {
                    mLoadView.setVisibility(View.GONE);
                    mEmpty.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mLoadView.setVisibility(View.GONE);
                setLessonEmptyViewVisibility(View.VISIBLE);
            }
        });
    }

    private void initDiscuss() {
        mLoadView.setVisibility(View.GONE);
        catalogueAdapter = new CatalogueAdapter(discussDetail.getResources(), getActivity());
        mLvDiscuss.initWithContext(((BaseActivity) getActivity()), this , mCouseId);
        mLvDiscuss.setAdapter(catalogueAdapter);
        mLvDiscuss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCourseStateCallback.isExpired()) {
                    mCourseStateCallback.handlerCourseExpired();
                    return;
                }
                startThreadActivity(position);
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
        EdusohoApp.app.unRegistMsgSource(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        i = 0;
    }

    public void startThreadActivity(int position){
        if (isJoin) {
            Bundle bundle = new Bundle();
            bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, getActivity() instanceof CourseActivity ? "course" : "classroom");
            bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, getActivity() instanceof CourseActivity ? Integer.parseInt(discussDetail.getResources().get(position).getCourseId())
                                        : Integer.parseInt(discussDetail.getResources().get(position).getTargetId()));
            bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(discussDetail.getResources().get(position).getId()));
            bundle.putString(AbstractIMChatActivity.TARGET_TYPE, discussDetail.getResources().get(position).getType());
            EdusohoApp.app.mEngine.runNormalPluginWithBundleForResult("DiscussDetailActivity", getActivity(), bundle, 0);
        } else {
            CommonUtil.shortCenterToast(getContext(), getString(R.string.discuss_join_look_hint));
        }
    }

}
