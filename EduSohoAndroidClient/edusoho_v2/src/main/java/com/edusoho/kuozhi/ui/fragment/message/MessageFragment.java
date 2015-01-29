package com.edusoho.kuozhi.ui.fragment.message;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.MessageListAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.BaseResult;
import com.edusoho.kuozhi.model.MessageType;
import com.edusoho.kuozhi.model.Notify;
import com.edusoho.kuozhi.model.WidgetMessage;
import com.edusoho.kuozhi.ui.common.LoginActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;

/**
 * Created by howzhi on 14-9-28.
 */
public class MessageFragment extends BaseFragment {

    private RefreshListWidget mMessageList;
    private View mLoadLayout;

    @Override
    public String getTitle() {
        return "通知";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.message_fragment_layout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (app.loginUser == null) {
            LoginActivity.start(activity);
        }
    }

    @Override
    public void invoke(WidgetMessage message) {
        MessageType messageType = message.type;
        if (Const.LOGING_SUCCESS.equals(messageType.type)) {
            mMessageList.setRefreshing();
        }
    }

    @Override
    public MessageType[] getMsgTypes() {
        MessageType[] messageTypes = new MessageType[]{
                new MessageType(Const.LOGING_SUCCESS)
        };
        return messageTypes;
    }

    @Override
    protected void initView(View view) {
        super.initView(view);

        mLoadLayout = view.findViewById(R.id.load_layout);
        mMessageList = (RefreshListWidget) view.findViewById(R.id.message_list);
        mMessageList.setMode(PullToRefreshBase.Mode.BOTH);
        mMessageList.getRefreshableView().setDividerHeight(20);
        mMessageList.setAdapter(new MessageListAdapter(mContext, mActivity, R.layout.message_list_item));
        mMessageList.setEmptyText(new String[]{"暂无通知"}, R.drawable.icon_notify);

        mMessageList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                Log.d(null, "message->onPullUpToRefresh");
                loadMessage(mMessageList.getStart(), true);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                Log.d(null, "message->refresh");
                loadMessage(0, false);
            }
        });

        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Notify notify = (Notify) adapterView.getItemAtPosition(i);
            }
        });

        loadMessage(0, false);
    }

    private void loadMessage(int start, final boolean isAppend) {
        RequestUrl requestUrl = app.bindUrl(Const.NOTIFICATION, true);
        requestUrl.setParams(new String[]{
                "start", start + "",
                "limit", Const.LIMIT + ""
        });
        mActivity.ajaxPost(requestUrl, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mLoadLayout.setVisibility(View.GONE);
                mMessageList.onRefreshComplete();
                BaseResult<ArrayList<Notify>> notifyBaseResult = mActivity.parseJsonValue(
                        object, new TypeToken<BaseResult<ArrayList<Notify>>>() {
                        });
                if (notifyBaseResult == null) {
                    return;
                }

                mMessageList.pushData(notifyBaseResult.data);
                mMessageList.setStart(notifyBaseResult.start, notifyBaseResult.total);
            }
        });
    }
}
