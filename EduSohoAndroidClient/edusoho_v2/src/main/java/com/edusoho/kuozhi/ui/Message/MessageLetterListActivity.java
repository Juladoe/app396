package com.edusoho.kuozhi.ui.message;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Message.LetterListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Message.LetterModel;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;

import cn.trinea.android.common.util.ToastUtils;
import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 14/11/24.
 * 私信详细信息查看界面
 */
public class MessageLetterListActivity extends ActionBarBaseActivity implements View.OnClickListener {
    private RefreshListWidget mLetterList;
    private EditText etSendContent;
    private View btnSendLetter;
    public static final String CONVERSATION_ID = "conversation_Id";
    public static final String CONVERSATION_FROM_NAME = "conversation_with";
    public static final String CONVERSATION_FROM_ID = "conversation_from_Id";

    private int mStart;
    private int mConversationId;
    private String mConversationName;
    private int mFromId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_list_activity_layout);
        initData();
        initViews();
    }

    private void initData() {
        mConversationId = getIntent().getIntExtra(CONVERSATION_ID, 0);
        mConversationName = getIntent().getStringExtra(CONVERSATION_FROM_NAME);
        mFromId = getIntent().getIntExtra(CONVERSATION_FROM_ID, 0);
    }

    private void initViews() {
        setBackMode(BACK, mConversationName);
        mLetterList = (RefreshListWidget) findViewById(R.id.letter_list);
        btnSendLetter = findViewById(R.id.btn_send_letter);
        etSendContent = (EditText) findViewById(R.id.et_send_content);
        mLetterList.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mLetterList.setAdapter(new LetterListAdapter(mContext, app.loginUser));
        mLetterList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                //LoadLetterListData(mStart, false);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                LoadLetterListData(mStart, false);
            }
        });
        LoadLetterListData(0, true);
        btnSendLetter.setOnClickListener(this);
    }

    private void LoadLetterListData(final int start, final boolean isPullToBottom) {
        if (mConversationId != 0) {
            RequestUrl url = app.bindUrl(Const.MESSAGE_LIST, true);
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("limit", String.valueOf(Const.LIMIT));
            params.put("start", String.valueOf(start));
            params.put("conversationId", String.valueOf(mConversationId));
            url.setParams(params);
            ResultCallback callback = new ResultCallback() {
                @Override
                public void callback(String url, String object, AjaxStatus ajaxStatus) {
                    mLetterList.onRefreshComplete();
                    ArrayList<LetterModel> result = gson.fromJson(object, new TypeToken<ArrayList<LetterModel>>() {
                    }.getType());

                    if (result == null) {
                        return;
                    }
                    if (result.size() != 0) {
                        LetterListAdapter adapter = (LetterListAdapter) mLetterList.getAdapter();
                        adapter.addItemsToBottom(result);
                        if (isPullToBottom) {
                            mLetterList.setSelection(result.size());
                        }

                        mStart = start + mLetterList.getAdapter().getCount();
                    }
                }

                @Override
                public void error(String url, AjaxStatus ajaxStatus) {
                    super.error(url, ajaxStatus);
                }
            };

            this.ajaxPost(url, callback);
        }
    }

    @Override
    public void onClick(View v) {
        String content = etSendContent.getText().toString();
        if (TextUtils.isEmpty(content)) {
            longToast("请输入私信内容!");
            return;
        }
        RequestUrl url = app.bindUrl(Const.SEND_LETTER, true);
        url.setParams(new String[]{
                "conversationId", String.valueOf(mConversationId),
                "fromId", String.valueOf(mFromId),
                "content", etSendContent.getText().toString()
        });
        ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                if (object != null) {
                    LetterModel result = gson.fromJson(object, new TypeToken<LetterModel>() {
                    }.getType());
                    LetterListAdapter adapter = ((LetterListAdapter) mLetterList.getAdapter());
                    adapter.addItem(result);
                    mLetterList.setSelection(adapter.getCount());
                    etSendContent.getText().clear();
                    mStart++;
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                if (ajaxStatus.getCode() != 200) {
                    ToastUtils.show(mContext, "发送失败");
                }
            }
        };

        this.ajaxPost(url, callback);

    }
}
