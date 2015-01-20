package com.edusoho.kuozhi.ui.fragment.message;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.MessageLetterSummaryAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Message.LetterSummaryModel;
import com.edusoho.kuozhi.ui.message.MessageLetterListActivity;
import com.edusoho.kuozhi.ui.fragment.BaseFragment;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;

/**
 * Created by Hby on 14/11/23.
 */
public class LetterFragment extends BaseFragment {
    private static final String TAG = "LetterFragment";
    private static final int RETURN_REFRESH = 0x01;
    private RefreshListWidget mLetterSummaryList;
    private View mLoadingView;
    private int mClickPosition;
    private MessageLetterSummaryAdapter mAdapter;

    @Override
    public String getTitle() {
        return "私信";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContainerView(R.layout.letter_fragment_layout);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mLetterSummaryList = (RefreshListWidget) view.findViewById(R.id.letter_summary_list);
        mLoadingView = view.findViewById(R.id.load_layout);
        mLetterSummaryList.setMode(PullToRefreshBase.Mode.BOTH);
        mLetterSummaryList.setEmptyText(new String[]{"暂无私信"}, R.drawable.icon_discussion);
        mAdapter = new MessageLetterSummaryAdapter(mContext, R.layout.message_letter_item);
        mLetterSummaryList.setAdapter(mAdapter);
        mLetterSummaryList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadLetterSummary(mLetterSummaryList.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadLetterSummary(0);
            }
        });
        mLetterSummaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                LetterSummaryModel model = (LetterSummaryModel) parent.getItemAtPosition(position);
                Bundle bundle = new Bundle();
                if (model != null) {
                    mClickPosition = position;
                    bundle.putInt(MessageLetterListActivity.CONVERSATION_ID, model.id);
                    bundle.putString(MessageLetterListActivity.CONVERSATION_FROM_NAME, model.user.nickname);
                    bundle.putInt(MessageLetterListActivity.CONVERSATION_FROM_ID, model.fromId);
                }
                startActivityWithBundleAndResult("MessageLetterListActivity", RETURN_REFRESH, bundle);
            }
        });

        loadLetterSummary(0);
    }

    private void loadLetterSummary(final int start) {
        RequestUrl requestUrl = app.bindUrl(Const.MESSAGE_LETTER_SUMMARY, true);
        requestUrl.setParams(new String[]{
                "limit", String.valueOf(Const.LIMIT),
                "start", String.valueOf(start)
        });
        final ResultCallback callback = new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                try {
                    mLoadingView.setVisibility(View.GONE);
                    mLetterSummaryList.onRefreshComplete();
                    ArrayList<LetterSummaryModel> result = mActivity.gson.fromJson(object, new TypeToken<ArrayList<LetterSummaryModel>>() {
                    }.getType());
                    if (result == null) {
                        return;
                    }

                    mLetterSummaryList.pushData(result);
                    mLetterSummaryList.setStart(start + Const.LIMIT);
                } catch (Exception ex) {
                    Log.e(TAG, ex.toString());
                }
            }

            @Override
            public void error(String url, AjaxStatus ajaxStatus) {
                super.error(url, ajaxStatus);
            }
        };
        mActivity.ajaxPost(requestUrl, callback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RETURN_REFRESH) {
            mAdapter.setReadMsgNum(mClickPosition - 1);
        }
    }
}
