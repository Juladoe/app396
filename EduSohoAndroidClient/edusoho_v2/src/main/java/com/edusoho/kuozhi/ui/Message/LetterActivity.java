package com.edusoho.kuozhi.ui.message;

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
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import cn.trinea.android.common.util.ToastUtils;
import library.PullToRefreshBase;

/**
 * Created by JesseHuang on 14/12/31.
 */
public class LetterActivity extends ActionBarBaseActivity {
    private static final String TAG = "LetterFragment";
    private static final int RETURN_REFRESH = 0;
    private RefreshListWidget mLetterSummaryList;
    private View mLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letter_fragment_layout);
        initView();
    }

    private void initView() {
        setBackMode(BACK, "私信");
        mLetterSummaryList = (RefreshListWidget) findViewById(R.id.letter_summary_list);
        mLoadingView = findViewById(R.id.load_layout);
        mLetterSummaryList.setMode(PullToRefreshBase.Mode.BOTH);

        mLetterSummaryList.setEmptyText(new String[]{"暂无私信"});
        mLetterSummaryList.setAdapter(new MessageLetterSummaryAdapter(
                mContext, R.layout.message_letter_item));
        mLetterSummaryList.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                loadLetterSummary(mLetterSummaryList.getStart(), false);
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                loadLetterSummary(0, true);
            }
        });
        mLetterSummaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                mActivity.app.mEngine.runNormalPluginForResult("MessageLetterListActivity", mActivity, RETURN_REFRESH, new PluginRunCallback() {
                    @Override
                    public void setIntentDate(Intent startIntent) {
                        LetterSummaryModel model = (LetterSummaryModel) parent.getItemAtPosition(position);
                        if (model != null) {
                            startIntent.putExtra(MessageLetterListActivity.CONVERSATION_ID, model.id);
                            startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_NAME, model.user.nickname);
                            startIntent.putExtra(MessageLetterListActivity.CONVERSATION_FROM_ID, model.fromId);
                        }
                    }
                });
            }
        });
        loadLetterSummary(0, false);
    }

    private void loadLetterSummary(final int start, final boolean isRefresh) {
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
}
