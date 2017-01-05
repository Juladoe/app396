package com.edusoho.kuozhi.v3.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.CatalogueAdapter;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.BaseActivity;
import com.edusoho.kuozhi.v3.ui.base.BaseFragment;
import com.edusoho.kuozhi.v3.util.AppUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.view.EduSohoNewIconView;
import com.edusoho.kuozhi.v3.view.FixCourseListView;
import com.google.gson.reflect.TypeToken;

/**
 * Created by DF on 2017/1/4.
 */

public class DiscussFragment extends BaseFragment {


    private String mCouseId = "55";
    private View mView;
    private FixCourseListView mLvDiscuss;
    private EduSohoNewIconView mTvEdit;
    private DiscussDetail discussDetail;
    private View mLoadView;
    private View mEmpty;
    private CatalogueAdapter catalogueAdapter;
    private EduSohoNewIconView tvTopic;
    private Dialog dialog;

    public DiscussFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_discuss, container, false);
        mCouseId = getArguments().getString("id");
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWidget();
        initData();
    }

    private void initWidget() {
        dialog = new Dialog(getActivity(), R.style.DiscussDialog);
        mLvDiscuss = (FixCourseListView) mView.findViewById(R.id.lv_discuss);
        mTvEdit = (EduSohoNewIconView) mView.findViewById(R.id.tv_edit_topic);
        mLoadView = mView.findViewById(R.id.ll_frame_load);
        mEmpty = mView.findViewById(R.id.ll_discuss_empty);
        mTvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
    }

    private void initData() {
        mLoadView.setVisibility(View.VISIBLE);
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.LESSON_DISCUSS, mCouseId, mCouseId), true);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("test", response);
                discussDetail = ((BaseActivity) getActivity()).parseJsonValue(response, new TypeToken<DiscussDetail>() {});
                if (discussDetail.getResources() != null) {
                    initDiscuss();
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
        mLvDiscuss.setAdapter(catalogueAdapter);
        mLvDiscuss.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void setLessonEmptyViewVisibility(int visibility) {
        mEmpty.setVisibility(visibility);
    }

    public boolean isAdd;
    private void showPopup() {
        if (!isAdd) {
            isAdd = true;
            View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_discuss_publish, null);
            tvTopic = (EduSohoNewIconView) dialogView.findViewById(R.id.tv_topic);
            tvTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.tv_question).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(dialogView);
            dialog.setCanceledOnTouchOutside(false);
            Window mWindow = dialog.getWindow();
            mWindow .setGravity(Gravity.LEFT | Gravity.TOP);
            WindowManager.LayoutParams lp = mWindow.getAttributes();
            lp.x = (int) mTvEdit.getX();
            lp.y = (int) (mTvEdit.getY() - AppUtil.dp2px(getActivity(), 103));
            mWindow.setAttributes(lp);
        }
        dialog.show();
    }
}
