package com.edusoho.kuozhi.ui.note;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.androidquery.callback.AjaxStatus;
import com.edusoho.kuozhi.R;
import com.edusoho.kuozhi.adapter.Note.NoteAdapter;
import com.edusoho.kuozhi.core.model.RequestUrl;
import com.edusoho.kuozhi.model.Note.NoteInfo;
import com.edusoho.kuozhi.ui.ActionBarBaseActivity;
import com.edusoho.kuozhi.ui.common.FragmentPageActivity;
import com.edusoho.kuozhi.ui.widget.RefreshGridViewWidget;
import com.edusoho.kuozhi.util.Const;
import com.edusoho.listener.ResultCallback;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import library.PullToRefreshBase;
import library.PullToRefreshScrollView;

/**
 * Created by onewoman on 14-10-9.
 */
public class NoteActivity extends ActionBarBaseActivity {

    private RefreshGridViewWidget mNoteGridView;
    private PullToRefreshScrollView emptyLayout;
    private View mLoadView;
    private NoteAdapter noteAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_layout);
        initView();
    }

    private void initView() {
        setBackMode(BACK, "我的笔记");

        mLoadView = findViewById(R.id.load_layout);
        mNoteGridView = (RefreshGridViewWidget) this.findViewById(R.id.note_gridview);
        mNoteGridView.setMode(PullToRefreshBase.Mode.BOTH);
        mNoteGridView.setEmptyText(new String[] { "暂无笔记" }, R.drawable.icon_note);
        noteAdapter = new NoteAdapter(mContext, R.layout.note_list_item);
        mNoteGridView.setAdapter(noteAdapter);

        mNoteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NoteInfo noteInfo = (NoteInfo) adapterView.getItemAtPosition(i);
                trunToNoteList(noteInfo.courseTitle, noteInfo.coursesId, noteInfo.noteNum);
            }
        });
        setPullToRefreshListener();

        returnObjectFromGson(0);
    }

    private PullToRefreshScrollView initEmptyLayout() {
        PullToRefreshScrollView scrollView = new PullToRefreshScrollView(this);
        scrollView.getRefreshableView().setFillViewport(true);
        scrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View emptyView = getLayoutInflater().inflate(R.layout.course_empty_layout, null);
        TextView text = (TextView) emptyView.findViewById(R.id.list_empty_text);
        text.setText("没有笔记");

        scrollView.addView(emptyView);
        return scrollView;
    }

    private void refreshLayout() {
        ViewGroup view = (ViewGroup) findViewById(R.id.notemain);
        if (noteAdapter.isEmpty()) {
            if (emptyLayout == null) {
                emptyLayout = initEmptyLayout();
                view.addView(emptyLayout);
            }
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            if (emptyLayout != null)
                emptyLayout.setVisibility(View.GONE);
            view.postInvalidate();
        }
    }

    public void setPullToRefreshListener() {
        mNoteGridView.setUpdateListener(new RefreshGridViewWidget.UpdateListener() {
            @Override
            public void update(PullToRefreshBase<GridView> refreshView) {
                returnObjectFromGson(mNoteGridView.getStart());
            }

            @Override
            public void refresh(PullToRefreshBase<GridView> refreshView) {
                returnObjectFromGson(0);
            }
        });
    }

    public void trunToNoteList(
            String courseTitle, int courseId, int total) {
        Bundle bundle = new Bundle();
        bundle.putString(Const.ACTIONBAR_TITLE, courseTitle);
        bundle.putInt(Const.COURSE_ID, courseId);
        bundle.putString(FragmentPageActivity.FRAGMENT, "NoteListFragment");
        bundle.putInt(NoteListFragment.NOTENUM, total);

        app.mEngine.runNormalPluginWithBundle(
                "FragmentPageActivity", mActivity, bundle
        );
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
                super.callback(url, object, ajaxStatus);
                mNoteGridView.onRefreshComplete();
                if (mLoadView.getVisibility() == View.VISIBLE) {
                    mLoadView.setVisibility(View.GONE);
                }
                parasHttpDatas(start, object);
            }
        });
    }

    public void parasHttpDatas(int start, String object) {
        ArrayList<NoteInfo> noteInfos = parseJsonValue(
                object, new TypeToken<ArrayList<NoteInfo>>() {
        });

        if (noteInfos == null) {
            return;
        }

        mNoteGridView.pushData(noteInfos);
        mNoteGridView.setStart(start + Const.LIMIT);
    }
}
