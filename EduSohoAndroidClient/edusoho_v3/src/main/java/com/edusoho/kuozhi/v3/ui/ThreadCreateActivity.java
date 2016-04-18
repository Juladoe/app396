package com.edusoho.kuozhi.v3.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.v3.core.MessageEngine;
import com.edusoho.kuozhi.v3.listener.NormalCallback;
import com.edusoho.kuozhi.v3.model.provider.CourseProvider;
import com.edusoho.kuozhi.v3.ui.base.ActionBarBaseActivity;
import java.util.LinkedHashMap;
import cn.trinea.android.common.util.ToastUtils;

/**
 * Created by 菊 on 2016/4/11.
 */
public class ThreadCreateActivity extends ActionBarBaseActivity {

    public static final String TARGET_ID = "targetId";
    public static final String TARGET_TYPE = "targetType";
    public static final String THREAD_TYPE = "threadType";
    public static final String TYPE = "type";

    private int mTargetId;
    private String mCreateType;
    private String mTargetType;
    private String mThreadType;
    private EditText mTitleEdt;
    private EditText mContenteEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTargetId = getIntent().getIntExtra(TARGET_ID, 0);
        mCreateType = getIntent().getStringExtra(TYPE);
        mTargetType = getIntent().getStringExtra(TARGET_TYPE);
        mThreadType = getIntent().getStringExtra(THREAD_TYPE);
        if (TextUtils.isEmpty(mCreateType)) {
            mCreateType = "question";
        }

        setBackMode(BACK, "question".equals(mCreateType) ? "提问题" : "发话题");
        setContentView(R.layout.activity_thread_create_layout);
        mTitleEdt = (EditText) findViewById(R.id.tc_title);
        mContenteEdt = (EditText) findViewById(R.id.tc_conten);
    }

    private void createThread() {

        String title = mTitleEdt.getText().toString();
        String content = mTitleEdt.getText().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            ToastUtils.show(getBaseContext(), "问答标题或内容不能为空!!");
            return;
        }
        CourseProvider courseProvider = new CourseProvider(getBaseContext());
        courseProvider.createThread(mTargetId, mTargetType, mThreadType, mCreateType, title, content)
                .success(new NormalCallback<LinkedHashMap>() {
                    @Override
                    public void success(LinkedHashMap result) {
                        if (result != null && result.containsKey("threadId")) {
                            createSuccess();
                        }
                    }
        });
    }

    private void createSuccess() {
        ToastUtils.show(getBaseContext(), "发表成功!");
        finish();
        Bundle bundle = new Bundle();
        bundle.putString("event", "createThreadEvent");
        MessageEngine.getInstance().sendMsg(WebViewActivity.SEND_EVENT, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_thread_create) {
            createThread();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.thread_create_menu, menu);
        return true;
    }
}
