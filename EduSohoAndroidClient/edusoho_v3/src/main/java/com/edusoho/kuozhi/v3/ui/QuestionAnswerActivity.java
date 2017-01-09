package com.edusoho.kuozhi.v3.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.entity.course.DiscussDetail;
import com.edusoho.kuozhi.v3.entity.lesson.DiscussDetailList;
import com.edusoho.kuozhi.v3.entity.lesson.QuestionAnswerAdapter;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.CommonUtil;
import com.edusoho.kuozhi.v3.util.Const;
import com.edusoho.kuozhi.v3.util.InputUtils;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Collections;
import java.util.Map;

/**
 * Created by DF on 2017/1/5.
 */

public class QuestionAnswerActivity extends ActionBarBaseActivity {

    private TextView mTvKind;
    private ListView mlvDiscuss;
    private View loadView;
    private DiscussDetail.ResourcesBean resourcesBean;
    private DiscussDetailList discussDetailList;
    private QuestionAnswerAdapter questionAnswerAdapter;
    private RelativeLayout mRlReplayEdit;
    private EditText mEtContent;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);
        initView();
        initData();
    }

    private void initView() {
        Bundle bundle = getIntent().getExtras();
        resourcesBean = (DiscussDetail.ResourcesBean) bundle.getSerializable("coursebean");
        loadView = findViewById(R.id.ll_frame_load);
        mTvKind = (TextView) findViewById(R.id.tv_kind);
        mlvDiscuss = (ListView) findViewById(R.id.lv_discuss);
        mRlReplayEdit = (RelativeLayout) findViewById(R.id.rl_replay_edit);
        mEtContent = (EditText) findViewById(R.id.et_content);
        ImageLoader.getInstance().displayImage(resourcesBean.getUser().getAvatar(), ((ImageView) findViewById(R.id.iv_user_icon)));
        ((TextView) findViewById(R.id.tv_user_name)).setText(resourcesBean.getUser().getNickname());
        ((TextView) findViewById(R.id.tv_time)).setText(resourcesBean.getCreatedTime().split("T")[0]);
        ((TextView) findViewById(R.id.tv_title)).setText(resourcesBean.getTitle());
        ((TextView) findViewById(R.id.tv_detail)).setText(resourcesBean.getContent());
        ((TextView) findViewById(R.id.tv_course)).setText(String.format("来自课程《%s》", bundle.getString("title")));
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionAnswerActivity.this.finish();
            }
        });
        findViewById(R.id.rl_replay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlReplayEdit.setVisibility(View.VISIBLE);
                mEtContent.requestFocus();
                InputUtils.showKeyBoard(mEtContent, mContext);
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                mRlReplayEdit.setVisibility(View.GONE);
            }
        });
        findViewById(R.id.tv_issue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEtContent.getText().toString())) {
                    CommonUtil.shortCenterToast(mContext, "内容不可为空");
                    return;
                }
                RequestUrl requestUrl = app.bindNewApiUrl(Const.REPLAY_QUESTION, true);
                Map<String, String> params = requestUrl.getParams();
                params.put("content", mEtContent.getText().toString());
                params.put("courseId", resourcesBean.getCourseId());
                params.put("threadId", resourcesBean.getId());
                params.put("threadType", "course");
                app.postUrl(requestUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        CommonUtil.shortCenterToast(mContext, "发布成功");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        CommonUtil.shortCenterToast(mContext, "发布失败");
                    }
                });
                mRlReplayEdit.setVisibility(View.GONE);
                hideKeyBoard();
                initData();
            }
        });
    }

    private void initData() {
        loadView.setVisibility(View.VISIBLE);
        RequestUrl requestUrl = app.bindNewUrl(String.format(Const.QUESTION_ANSWER, resourcesBean.getId()), true);
        app.getUrl(requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                discussDetailList = app.parseJsonValue(response, new TypeToken<DiscussDetailList>(){});
                Collections.reverse(discussDetailList.getResources());
                if (discussDetailList != null) {
                    if (isFirst) {
                        initFirstList();
                    } else {
                        questionAnswerAdapter.mList = discussDetailList.getResources();
                        questionAnswerAdapter.notifyDataSetChanged();
                        mlvDiscuss.setSelection(discussDetailList.getResources().size());
                        loadView.setVisibility(View.GONE);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadView.setVisibility(View.GONE);
            }
        });
    }

    private void initFirstList() {
        isFirst = false;
        loadView.setVisibility(View.GONE);
        questionAnswerAdapter = new QuestionAnswerAdapter(this, discussDetailList.getResources());
        mlvDiscuss.setAdapter(questionAnswerAdapter);
        mlvDiscuss.setSelection(discussDetailList.getResources().size());
    }

    public void hideKeyBoard(){
        InputMethodManager inputMethodManager = (InputMethodManager) mContext.getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEtContent.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
