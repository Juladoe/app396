package com.edusoho.kowzhi.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kowzhi.R;
import com.edusoho.kowzhi.adapter.NotificationListAdapter;
import com.edusoho.kowzhi.model.Notify;
import com.edusoho.kowzhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class NotificationActivity extends BaseActivity {
    private ViewGroup mNotifyContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_layout);
        initView();
    }

    public static void start(Activity context)
    {
        Intent intent = new Intent();
        intent.setClass(context, NotificationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case LoginActivity.OK:
                if (app.loginUser != null) {
                    loadNotificationList();
                }
                break;
            case LoginActivity.EXIT:
                finish();
                break;
        }
    }

    private void initView() {
        setBackMode("系统通知", true, null);

        setMenu(R.layout.about_menu, new MenuListener() {
            @Override
            public void bind(View menuView) {
                View refreshBtn = menuView.findViewById(R.id.about_refresh_btn);
                refreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        loadNotificationList();
                    }
                });
            }
        });

        mNotifyContent = (ViewGroup) findViewById(R.id.notify_content);

        setPagerContent(mNotifyContent);
    }

    private void setPagerContent(ViewGroup parent)
    {
        parent.removeAllViews();
        View course_content = getLayoutInflater().inflate(R.layout.normal_content, null);
        parent.addView(course_content);
        loadNotificationList();
    }

    private void loadNotificationList() {
        if (app.loginUser == null) {
            LoginActivity.startForResult(this);
            return;
        }
        String url = app.bindToken2Url(Const.NOTICE, true);

        final ListView listView = (ListView) findViewById(R.id.normal_listview);
        ajaxGetString(url, new ResultCallback() {
            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
                findViewById(R.id.course_content_scrollview).setVisibility(View.GONE);
                showErrorLayout("网络数据加载错误！请重新尝试刷新。", new ListErrorListener() {
                    @Override
                    public void error(View errorBtn) {
                        setPagerContent(mNotifyContent);
                    }
                });
                findViewById(R.id.load_layout).setVisibility(View.GONE);
            }

            @Override
            public void callback(String url, String object, AjaxStatus status) {
                findViewById(R.id.load_layout).setVisibility(View.GONE);
                ArrayList<Notify> result = app.gson.fromJson(
                        object, new TypeToken<ArrayList<Notify>>(){}.getType());
                if (result == null || result.size() == 0) {
                    showEmptyLayout("暂无系统通知");
                }

                NotificationListAdapter adapter = new NotificationListAdapter(
                        mContext, result, R.layout.notification_list_item);
                listView.setAdapter(adapter);
            }
        });
    }
}
