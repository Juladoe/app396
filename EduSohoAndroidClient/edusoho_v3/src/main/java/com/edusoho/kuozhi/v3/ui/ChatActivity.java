package com.edusoho.kuozhi.v3.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.adapter.MessageAdapter;
import com.edusoho.kuozhi.v3.model.InitModelTool;
import com.edusoho.kuozhi.v3.model.bal.news.NewsItem;
import com.edusoho.kuozhi.v3.model.sys.MessageType;
import com.edusoho.kuozhi.v3.model.sys.RequestUrl;
import com.edusoho.kuozhi.v3.model.sys.WidgetMessage;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import com.edusoho.kuozhi.v3.util.Const;

import java.util.HashMap;

/**
 * Created by JesseHuang on 15/6/3.
 */
public class ChatActivity extends ActionBarBaseActivity {

    public static final int COURSE_CHAT = 0x01;
    public static final String CHAT_DATA = "chat_data";
    public static final String COURSE_ID = "course_id";
    public NewsItem mNewsItem;

    private EditText etSend;
    private ListView lvMessage;
    private TextView tvSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setBackMode(BACK, "suju");
        initView();
    }

    private void initView() {
        etSend = (EditText) findViewById(R.id.et_send_content);
        tvSend = (TextView) findViewById(R.id.tv_send);
        tvSend.setOnClickListener(mSendClickListener);
        lvMessage = (ListView) findViewById(R.id.lv_messages);
        MessageAdapter adapter = new MessageAdapter(mContext, InitModelTool.initMessageList());
        lvMessage.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if (intent != null) {
            mNewsItem = (NewsItem) intent.getSerializableExtra(CHAT_DATA);
//            String courseId = intent.getStringExtra(COURSE_ID);
//            CommonUtil.longToast(mActivity, courseId);
        }
    }

    View.OnClickListener mSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String url = "http://192.168.10.125/mapi_v2/User/sendPushMsg";
            RequestUrl requestUrl = new RequestUrl(url);
            HashMap<String, String> params = requestUrl.getParams();
            params.put("toId", String.valueOf(mNewsItem.title));
            params.put("title", String.valueOf(mNewsItem.title));
            params.put("content", etSend.getText().toString());
            mActivity.ajaxPost(requestUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, null);
        }
    };

    void getNewOneMessage(Bundle bundle) {

    }


}
