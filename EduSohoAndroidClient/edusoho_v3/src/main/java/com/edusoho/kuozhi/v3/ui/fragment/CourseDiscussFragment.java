package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.handler.CourseStateCallback;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
import com.edusoho.kuozhi.v3.ui.WebViewActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.ui.chat.AbstractIMChatActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.RefreshListView;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2017/1/4.
 */

public class CourseDiscussFragment extends BaseFragment {

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

    public CourseDiscussFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.fragment_discuss);
        mCouseId = getArguments().getString("id");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCourseStateCallback = (CourseStateCallback) activity;
    }


    protected void initView(View view) {
        super.initView(view);
        mUnJoinView = (LinearLayout) view.findViewById(R.id.ll_course_catalog_empty);
        mLvDiscuss = (RefreshListView) view.findViewById(R.id.lv_discuss);
        mLoadView = view.findViewById(R.id.ll_frame_load);
        mEmpty = view.findViewById(R.id.ll_discuss_empty);
        mTvEmpty = (TextView) view.findViewById(R.id.tv_empty);
        if (TextUtils.isEmpty(app.token)) {
            mUnJoinView.setVisibility(View.VISIBLE);
        } else {
            initData();
        }

    }

    // TODO: 17/1/18 @杜樊
    public void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        RequestUrl requestUrl = app.bindNewUrl(String.format(getActivity() instanceof CourseActivity ? Const.LESSON_DISCUSS : Const.CLASS_DISCUSS, mCouseId, mCouseId,0), true);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                discussDetail = ((BaseActivity) getActivity()).parseJsonValue(response, new TypeToken<DiscussDetail>() {});
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

    public void invoke(WidgetMessage message) {
        super.invoke(message);
        if (WebViewActivity.SEND_EVENT.equals(message.type.type)) {
            i = 1;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (i == 1) {
            initData();
            mEmpty.setVisibility(View.GONE);
        }
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
            app.mEngine.runNormalPluginWithBundleForResult("DiscussDetailActivity", mActivity, bundle, 0);
        } else {
            CommonUtil.shortCenterToast(mContext, getString(R.string.discuss_join_look_hint));
        }
    }

}
