package com.edusoho.kuozhi.ui.note;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;


import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteAdapter;
import com.edusoho.kuozhi.core.listener.PluginRunCallback;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshListWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshScrollView;


/**
 * Created by Melomelon on 2014/11/28.
 */
public class NoteListActivity extends ActionBarBaseActivity {
    private static final String TAG = "NoteListActivity";
    private RefreshListWidget mNoteListView;
    private PullToRefreshScrollView emptyLayout;
    private View mLoadView;
    private NoteAdapter noteAdapter;

    private static final int NOTERESULT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_list_layout);
        initView();
    }

    private void initView() {
        setBackMode(BACK, "我的笔记");
        mLoadView = findViewById(R.id.load_layout);
        mNoteListView = (RefreshListWidget) this.findViewById(R.id.note_list_view);
        mNoteListView.setMode(PullToRefreshBase.Mode.BOTH);
        mNoteListView.setEmptyText(new String[]{"暂无笔记"}, R.drawable.icon_note);
        noteAdapter = new NoteAdapter(mContext, R.layout.note_list_item);
        mNoteListView.setAdapter(noteAdapter);
        mNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    final NoteInfo noteInfo = (NoteInfo) adapterView.getItemAtPosition(i);

                    app.mEngine.runNormalPluginForResult("FragmentPageActivity", mActivity, NOTERESULT, new PluginRunCallback() {
                        @Override
                        public void setIntentDate(Intent startIntent) {
                            startIntent.putExtra(Const.ACTIONBAR_TITLE, noteInfo.lessonTitle);
                            startIntent.putExtra(FragmentPageActivity.FRAGMENT, "NoteContentFragment");
                            startIntent.putExtra(NoteContentFragment.CONTENT, noteInfo.content);
                            startIntent.putExtra(Const.LESSON_ID, noteInfo.lessonId);
                            startIntent.putExtra(Const.COURSE_ID, noteInfo.coursesId);
                            startIntent.putExtra(Const.LESSON_NAME, noteInfo.lessonTitle);
                            startIntent.putExtra(Const.LEARN_STATUS, noteInfo.learnStatus);
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
        });
        setPullToRefreshListener();
        returnObjectFromGson(0);
    }

    public void setPullToRefreshListener() {
        mNoteListView.setUpdateListener(new RefreshListWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(mNoteListView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<ListView> refreshView) {
                returnObjectFromGson(0);
            }
        });
    }

    public void returnObjectFromGson(final int start) {
        RequestUrl url = app.bindUrl(Const.USER_NOTES, true);
        url.setParams(new String[]{
                "start", String.valueOf(start),
                "limit", String.valueOf(Const.LIMIT)
        });
        ajaxPost(url, new ResultCallback() {
            @Override
            public void callback(String url, String object, AjaxStatus ajaxStatus) {
                mNoteListView.onRefreshComplete();
                mLoadView.setVisibility(View.GONE);
                parasHttpDatas(start, object);
            }
        });
    }

    public void parasHttpDatas(int start, String object) {
        ArrayList<NoteInfo> noteInfos = parseJsonValue(object, new TypeToken<ArrayList<NoteInfo>>() {
        });
        if (noteInfos == null) {
            return;
        }
        mNoteListView.pushData(noteInfos);
        mNoteListView.setStart(start + Const.LIMIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NOTERESULT) {
            mNoteListView.setRefreshing();
        }
    }
}
