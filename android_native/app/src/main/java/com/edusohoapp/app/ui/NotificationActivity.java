package com.edusohoapp.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusohoapp.app.R;
import com.edusohoapp.app.adapter.NotificationListAdapter;
import com.edusohoapp.app.model.Notify;
import com.edusohoapp.app.util.Const;
import com.edusohoapp.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class NotificationActivity extends BaseActivity {

    private ListView notification_list;

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

    private void showEmptyLayout(final String text)
    {
        ViewStub emptyLayout = (ViewStub) findViewById(R.id.list_empty_layout);
        emptyLayout.setOnInflateListener(new ViewStub.OnInflateListener() {
            @Override
            public void onInflate(ViewStub viewStub, View view) {
                TextView emptyText = (TextView) view;
                emptyText.setText(text);
            }
        });
        emptyLayout.inflate();
        return;
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

        notification_list = (ListView) findViewById(R.id.notification_list);

        loadNotificationList();
    }

    private void loadNotificationList() {
        if (app.loginUser == null) {
            LoginActivity.startForResult(this);
            return;
        }
        String url = app.bindToken2Url(Const.NOTICE, true);

        ajaxGetString(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus status) {
                ArrayList<Notify> result = app.gson.fromJson(
                        object, new TypeToken<ArrayList<Notify>>(){}.getType());
                if (result == null || result.size() == 0) {
                    showEmptyLayout("暂无系统通知");
                }
                if (result != null) {
                    NotificationListAdapter adapter = new NotificationListAdapter(
                            mContext, result, R.layout.notification_list_item);
                    notification_list.setAdapter(adapter);
                } else {
                    longToast("加载系统通知失败！请检查网络状态或者登录状态");
                }
            }
        });
    }
}
