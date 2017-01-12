package com.edusoho.kuozhi.v3.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.edusoho.kuozhi.v3.adapter.CatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.CourseActivity;
import com.edusoho.kuozhi.v3.ui.DiscussDetailActivity;
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

public class DiscussFragment extends BaseFragment {

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

    public DiscussFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_discuss, container, false);
        mCouseId = getArguments().getString("id");
        initWidget();
        if (TextUtils.isEmpty(app.token)) {
            mUnJoinView.setVisibility(View.VISIBLE);
        } else {
            initData();
        }
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void initWidget() {
        mUnJoinView = (LinearLayout) mView.findViewById(R.id.ll_course_catalog_empty);
        mLvDiscuss = (RefreshListView) mView.findViewById(R.id.lv_discuss);
        mLoadView = mView.findViewById(R.id.ll_frame_load);
        mEmpty = mView.findViewById(R.id.ll_discuss_empty);
        mTvEmpty = (TextView) mView.findViewById(R.id.tv_empty);
    }

    private void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        RequestUrl requestUrl;
        if (getActivity() instanceof CourseActivity) {
            requestUrl = app.bindNewUrl(String.format(Const.LESSON_DISCUSS, mCouseId, mCouseId, 0), true);
        } else {
            requestUrl = app.bindNewUrl(String.format(Const.CLASS_DISCUSS, mCouseId, mCouseId, 0), true);
        }
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                discussDetail = ((BaseActivity) getActivity()).parseJsonValue(response, new TypeToken<DiscussDetail>() {});
                if (discussDetail.getResources() != null && discussDetail.getResources().size() != 0) {
                    initDiscuss();
                } else {
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
                startThreadActivity(position);
            }
        });
    }

    public void setLessonEmptyViewVisibility(int visibility) {
        mEmpty.setVisibility(visibility);
    }

    public void reFreshView(boolean isJoin, String title) {
        this.isJoin = isJoin;
        this.title = title;
    }

    public void startThreadActivity(int position){
        if (isJoin) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("coursebean", discussDetail.getResources().get(position));
            bundle.putString("title", title);
            bundle.putString(DiscussDetailActivity.THREAD_TYPE, discussDetail.getResources().get(position).getType());
            bundle.putString(DiscussDetailActivity.THREAD_TARGET_TYPE, getActivity() instanceof CourseActivity ? "course" : "classroom");
            bundle.putInt(DiscussDetailActivity.THREAD_TARGET_ID, Integer.parseInt(discussDetail.getResources().get(position).getId()));
            if (getActivity() instanceof CourseActivity) {
                bundle.putInt(DiscussDetailActivity.LESSON_ID, Integer.parseInt(discussDetail.getResources().get(position).getLessonId()));
            } else {
                bundle.putInt(DiscussDetailActivity.LESSON_ID, Integer.parseInt(discussDetail.getResources().get(position).getTargetId()));
            }
            bundle.putInt(AbstractIMChatActivity.FROM_ID, Integer.parseInt(discussDetail.getResources().get(position).getId()));
            bundle.putString(AbstractIMChatActivity.FROM_NAME, discussDetail.getResources().get(position).getUser().getNickname());
            bundle.putString(AbstractIMChatActivity.TARGET_TYPE, discussDetail.getResources().get(position).getType());
            bundle.putString(AbstractIMChatActivity.CONV_NO, discussDetail.getResources().get(position).getId());
            Intent intent = new Intent(mActivity, DiscussDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            CommonUtil.shortCenterToast(mContext, "加入课程后才能查看详情");
        }
    }
}
